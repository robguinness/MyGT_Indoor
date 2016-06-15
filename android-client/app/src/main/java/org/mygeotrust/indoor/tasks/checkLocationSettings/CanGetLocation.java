package org.mygeotrust.indoor.tasks.checkLocationSettings;

import android.app.Activity;
import android.app.LauncherActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.Toast;

import org.mygeotrust.indoor.utils.dialogue.CustomDialog;
import org.mygeotrust.service.initializer.MyGtServiceBinder;
import org.mygeotrust.service.manager.Listeners.IMyGtGPSOptionListener;
import org.mygeotrust.service.manager.MyGtLocationManager;

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

            //TODO:REmove (only for testing)
            notifyObserver();

            //TODO:now check device settings for the GPS
        }


        //if location service (GPS) is not allowed in the profile
        else if (!MyGtLocationManager.isLocationAllowed()) {

            //show dialogue and launch the service to turn it on with user consent.
            CustomDialog.showDialog((Activity)observer, "Launch MyGeoTrust", "GPS is not allowed in Current Profile. Press OK to launch the Stack for changing the settings..", CustomDialog.YES_NO, new DialogInterface.OnClickListener() {
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
        if(b)
        {
            Log.e(TAG, "GPS is turned on in the profile!");
        }
        //if not
        else
        {

        }
    }



    /**
     * ----------------------------------------------------
     * Following checks if the GPS is allowed in the device
     * ----------------------------------------------------
     */





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
