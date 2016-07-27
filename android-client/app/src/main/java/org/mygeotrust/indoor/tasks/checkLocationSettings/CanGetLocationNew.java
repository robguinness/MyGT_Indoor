package org.mygeotrust.indoor.tasks.checkLocationSettings;

import android.app.Activity;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.fhc25.percepcion.osiris.mapviewer.R;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import org.mygeotrust.indoor.utils.dialogue.CustomDialog;
import org.mygeotrust.indoor.utils.dialogue.Dialogs;
import org.mygeotrust.indoor.utils.dialogue.IDialogs;
import org.mygeotrust.service.initializer.MyGtServiceBinder;
import org.mygeotrust.service.manager.Listeners.IMyGtGPSOptionListener;
import org.mygeotrust.service.manager.MyGtLocationManager;
import org.mygeotrust.service.manager.MyGtOptionListener;
import org.mygeotrust.utils.OptionKeys;

public class CanGetLocationNew extends Activity implements IMyGtGPSOptionListener, IDialogs, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = CanGetLocationNew.class.toString();

    private static ICanGetLocation observer;


    private enum GpsAllowedStatus {
        ALLOWED,
        NOTALLOWED_CURRENT_PROFILE,
        NOTALLOWED_IN_DEVICE;
    }

    //return values for callback
    private boolean canGetLocation;
    private String message;  // holds the explanation of success or failure of the process.


    private GoogleApiClient _client;

    final int REQUEST_CHECK_SETTINGS = 1000;


    public static final void addObserver(ICanGetLocation callbackObserver) {
        observer = callbackObserver;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_can_get_location_new);

        //add this class as a listener to GPS option
        MyGtOptionListener.addOptionListener(OptionKeys.GPS, this);


        checkMyGtStackLocationSettings();


    }


    /**
     * ------------------------------------------------------------------
     * Following checks if the GPS is allowed in current profile settings
     * ------------------------------------------------------------------
     */

    private void checkMyGtStackLocationSettings() {
        //if the location service is already allowed in the profile
        if (MyGtLocationManager.isLocationAllowed()) {
            prepareReturnData(GpsAllowedStatus.ALLOWED);

            //now check device settings for the GPS
            checkDeviceLocationSettings();
        }


        //if location service (GPS) is not allowed in the profile
        else if (!MyGtLocationManager.isLocationAllowed()) {

            Dialogs.getInstance().showStandardDialog(this, "Launch MyGeoTrust", "GPS is not allowed in Current Profile. Press OK to launch the Stack for changing the settings.", "Launch", "Not Now");

        }
    }

    @Override
    public void onDialogOptionSelected(SelectionStatus status) {
        Dialogs.getInstance().unregisterObserver();

        if(status == SelectionStatus.ok_pressed)
        {
            if (!MyGtServiceBinder.launchMyGTApp(getApplicationContext(), Package.getPackage(getPackageName()), CanGetLocation.class)) {
                Toast.makeText(CanGetLocationNew.this, "MyGeoTrust is not installed in your device!", Toast.LENGTH_SHORT).show();
                //finish(); //close this activity.
            }
        }
        else if(status == SelectionStatus.cancel_pressed)
        {
            prepareReturnData(GpsAllowedStatus.NOTALLOWED_CURRENT_PROFILE);
            notifyObserver();
            finish();
        }

    }

    /**
     * Get called when GPS option changes in the stack
     *
     * @param b
     */
    @Override
    public void onGPSOptionChanged(boolean b) {

        //remove listener
        MyGtOptionListener.removeListener(OptionKeys.GPS, this);

        //if user allows GPS in the profile
        if (b) {
            Log.e(TAG, "GPS is allowed in the profile!");
            //now check device settings for the GPS
            checkDeviceLocationSettings();
        }
        //if not
        else {
            //this case will not occur..
            Log.e(TAG, "GPS is not allowed in the profile!");
        }
    }


    /**
     * ----------------------------------------------------
     * Following checks if the GPS is allowed in the device
     * ----------------------------------------------------
     */


    private void checkDeviceLocationSettings() {

        //if the GPS provider is enabled
        if (MyGtLocationManager.isProviderEnabled()) {
            Log.e(TAG, "GPS is already turned on in the device.");

            //so prepare the message and notify client
            prepareReturnData(GpsAllowedStatus.ALLOWED);
            notifyObserver();
            finish();
        }

        //if the GPS is not turned on then.
        else {
            promptUserToTurnOnGPS();
        }
    }


    private void promptUserToTurnOnGPS() {
        if (_client == null) {
            _client = new GoogleApiClient.Builder(this) //getActivity()
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).build();
            _client.connect();

            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            //locationRequest.setInterval(30 * 1000);
            //locationRequest.setFastestInterval(5 * 1000);

            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            //**************************
            builder.setAlwaysShow(true); //this is the key ingredient
            //**************************

            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(_client, builder.build());


            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    final LocationSettingsStates state = result.getLocationSettingsStates();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            // All location settings are satisfied. The client can initialize location
                            // requests here.

                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            Log.e(TAG, "RESOLUTION_REQUIRED..!!!!");
                            // Location settings are not satisfied. But could be fixed by showing the user
                            // a dialog.
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(
                                        CanGetLocationNew.this, REQUEST_CHECK_SETTINGS); //getActivity()
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                }
            });
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        Log.e(TAG, "Is GPS usable: " + states.isGpsUsable());
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // All required changes were successfully made
                        Log.e(TAG, "GPS IS TURNED ON IN THE DEVICE.. YEEEEEE..!!!!");
                        prepareReturnData(GpsAllowedStatus.ALLOWED);
                        notifyObserver();
                        finish();
                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        Log.e(TAG, "GPS IS NOT ALLOWED ....!!!!");
                        prepareReturnData(GpsAllowedStatus.NOTALLOWED_IN_DEVICE);
                        notifyObserver();
                        finish();
                        break;
                    default:
                        break;
                }
                break;
        }
    }


    @Override
    public void onConnected(Bundle bundle) {
        Log.e("TEST: ", "Connected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e("TEST: ", "Connection suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "Connection failed.");
    }


    /**
     * --------------
     * Helper Mehods
     * --------------
     */

    private void notifyObserver() {
        if (observer != null)
            observer.onGetLocationStatus(canGetLocation, message);
    }

    private void prepareReturnData(GpsAllowedStatus status) {
        switch (status) {
            case ALLOWED:
                canGetLocation = true;
                message = "GPS location update is allowed.";
                break;
            case NOTALLOWED_CURRENT_PROFILE:
                canGetLocation = false;
                message = "GPS is not allowed in the current profile settings.";
                break;
            case NOTALLOWED_IN_DEVICE:
                canGetLocation = false;
                message = "GPS is not allowed in the device.";
                break;
        }
    }
}
