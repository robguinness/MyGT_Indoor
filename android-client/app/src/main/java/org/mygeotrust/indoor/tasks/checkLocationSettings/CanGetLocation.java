package org.mygeotrust.indoor.tasks.checkLocationSettings;

import android.app.LauncherActivity;
import android.content.Context;
import android.content.DialogInterface;
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
public class CanGetLocation implements IMyGtGPSOptionListener{

    private static boolean canGetLocation;
    private static Context activityContext;

    public static boolean status(Context context) {

        //save the context
        activityContext = context;

        //if GPS is not turned on in the device then prompt the user to trun it on.
        if (!MyGtLocationManager.isProviderEnabled()) {
            //show dialogue to turn on.
            //CustomDialog.showDialog();
        }

        //now check if user turned the GPS on or not
        if (MyGtLocationManager.isProviderEnabled()) {
            //now check if it is enabled in the profile
            if (!MyGtLocationManager.isLocationAllowed()) {
                //show dialogue and launch the service to trun it on with user consent.
                showDialog("Launch MyGeoTrust", "Allow Location Service in the Stack. Would you like to proceed?");
            }

            //now if the service allows location update
            if (MyGtLocationManager.isLocationAllowed())
                canGetLocation = true;
        }

        return canGetLocation;
    }


    private static void showDialog(String title, String message) {

        CustomDialog.showDialog(activityContext, title, message, CustomDialog.YES_NO, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    //launch mygeotrust service
                    if(!MyGtServiceBinder.launchMyGTApp(activityContext, Package.getPackage(activityContext.getPackageName()), LauncherActivity.class)) {
                        Toast.makeText(activityContext, "MyGeoTrust is not installed in your device!", Toast.LENGTH_SHORT).show();
                    }

                } else if (which == DialogInterface.BUTTON_NEGATIVE) {
                    // DO NOTHING
                }
            }
        });
    }

    /**
     * Get called when GPS option changes in the stack
     * @param b
     */
    @Override
    public void onGPSOptionChanged(boolean b) {

    }


}
