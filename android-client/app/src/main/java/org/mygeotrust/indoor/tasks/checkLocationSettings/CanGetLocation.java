package org.mygeotrust.indoor.tasks.checkLocationSettings;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

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
import org.mygeotrust.service.initializer.MyGtServiceBinder;
import org.mygeotrust.service.manager.Listeners.IMyGtGPSOptionListener;
import org.mygeotrust.service.manager.MyGtLocationManager;
import org.mygeotrust.service.manager.MyGtOptionListener;
import org.mygeotrust.utils.OptionKeys;

/**
 * This class implements the entire logic to setup the location service permission required
 * to run the application. It checks and prompts user to turn on/allow the location service
 * in device and in the stack. Based on the results it returns whether location service can
 * be used by the application or not.
 * <p/>
 * Created by Dr. Mahbubul Syeed on 14.6.2016.
 */
public class CanGetLocation extends Activity implements IMyGtGPSOptionListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = CanGetLocation.class.toString();
    final int REQUEST_CHECK_SETTINGS = 1000;

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    private enum GpsAllowedStatus {
        ALLOWED,
        NOTALLOWED_CURRENT_PROFILE,
        NOTALLOWED_IN_DEVICE;
    }

    private Context applicationContext;

    private ICanGetLocation observer;


    //return values for callback
    private boolean canGetLocation;
    private String message;  // holds the explanation of success or failure of the process.


    public CanGetLocation(Context applicationContext, ICanGetLocation observer) {

        //add this class as a listener to GPS option
        MyGtOptionListener.addOptionListener(OptionKeys.GPS, this);

        //set the context
        this.applicationContext = applicationContext;



        //set the callback listener
        this.observer = observer;

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

            //show dialogue and launch the service to turn it on with user consent.
            CustomDialog.showDialog((Activity) observer, "Launch MyGeoTrust", "GPS is not allowed in Current Profile. Press OK to launch the Stack for changing the settings..", CustomDialog.YES_NO, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    if (which == DialogInterface.BUTTON_POSITIVE) {
                        //launch mygeotrust service
                        if (!MyGtServiceBinder.launchMyGTApp(applicationContext, Package.getPackage(getPackageName()), CanGetLocation.class)) {
                            Toast.makeText(CanGetLocation.this, "MyGeoTrust is not installed in your device!", Toast.LENGTH_SHORT).show();
                        }

                    }

                    //if user denies to allow GPS in the profile then return and notify the client immediately
                    else if (which == DialogInterface.BUTTON_NEGATIVE) {
                        prepareReturnData(GpsAllowedStatus.NOTALLOWED_CURRENT_PROFILE);
                        notifyObserver();
                    }
                }
            });
        }
    }


    /**
     * Get called when GPS option changes in the stack
     *
     * @param b
     */
    @Override
    public void onGPSOptionChanged(boolean b) {
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
        }

        //if the GPS is not turned on then.
        else {
            promptUserToTurnOnGPS();
        }
    }


    private void promptUserToTurnOnGPS()
    {

        GoogleApiClient _client = null;

        if (_client == null) {
            _client = new GoogleApiClient.Builder(CanGetLocation.this) //getActivity()
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
                            Log.e("Main activity:: ", "RESOLUTION_REQUIRED..!!!!");
                            // Location settings are not satisfied. But could be fixed by showing the user
                            // a dialog.
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(
                                        CanGetLocation.this, REQUEST_CHECK_SETTINGS); //getActivity()
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








    /**
     * --------------
     * Helper Mehods
     * --------------
     */

    private void notifyObserver() {
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
