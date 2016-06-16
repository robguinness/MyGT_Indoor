package org.mygeotrust.indoor.tasks.checkLocationSettings;

import android.app.Activity;
import android.app.LauncherActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
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
public class CanGetLocation extends Activity implements IMyGtGPSOptionListener {

    private static final String TAG = CanGetLocation.class.toString();
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient _client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        _client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        _client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "CanGetLocation Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://org.mygeotrust.indoor.tasks.checkLocationSettings/http/host/path")
        );
        AppIndex.AppIndexApi.start(_client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "CanGetLocation Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://org.mygeotrust.indoor.tasks.checkLocationSettings/http/host/path")
        );
        AppIndex.AppIndexApi.end(_client, viewAction);
        _client.disconnect();
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
            GoogleApiClient googleApiClient = null;

            if (googleApiClient == null) {
                googleApiClient = new GoogleApiClient.Builder(CanGetLocation.this) //getActivity()
                        .addApi(LocationServices.API)
                        .addConnectionCallbacks((GoogleApiClient.ConnectionCallbacks) this)
                        .addOnConnectionFailedListener((GoogleApiClient.OnConnectionFailedListener) this).build();
                googleApiClient.connect();

                LocationRequest locationRequest = LocationRequest.create();
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                locationRequest.setInterval(30 * 1000);
                locationRequest.setFastestInterval(5 * 1000);
                LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                        .addLocationRequest(locationRequest);

                //**************************
                builder.setAlwaysShow(true); //this is the key ingredient
                //**************************

                PendingResult<LocationSettingsResult> result =
                        LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
                result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                    @Override
                    public void onResult(LocationSettingsResult result) {
                        final Status status = result.getStatus();
                        final LocationSettingsStates state = result.getLocationSettingsStates();
                        switch (status.getStatusCode()) {
                            case LocationSettingsStatusCodes.SUCCESS:
                                // All location settings are satisfied. The client can initialize location
                                // requests here.
                                Log.e(TAG, "GPS IS TURNED ON IN THE DEVICE.. YEEEEEE..!!!!");
                                break;
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                // Location settings are not satisfied. But could be fixed by showing the user
                                // a dialog.
                                try {
                                    // Show the dialog by calling startResolutionForResult(),
                                    // and check the result in onActivityResult().
                                    status.startResolutionForResult(
                                            CanGetLocation.this, 1000); //getActivity()
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
