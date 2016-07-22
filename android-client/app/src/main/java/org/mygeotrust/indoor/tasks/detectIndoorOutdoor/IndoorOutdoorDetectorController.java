package org.mygeotrust.indoor.tasks.detectIndoorOutdoor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import org.mygeotrust.indoor.tasks.detectIndoorOutdoor.algorithm.DetermineIndoorOutdoorService;
import org.mygeotrust.indoor.tasks.detectIndoorOutdoor.algorithm.WifiReceiver;

/**
 * Created by Dr. Mahbubul Syeed on 21.7.2016.
 */
public class IndoorOutdoorDetectorController {

    private final String TAG = IndoorOutdoorDetectorController.class.getName();

    private static IndoorOutdoorDetectorController ourInstance = new IndoorOutdoorDetectorController();

    public static IndoorOutdoorDetectorController getInstance() {
        return ourInstance;
    }

    private IndoorOutdoorDetectorController() {
    }

    public enum LocationStatus {
        indoor,
        outdoor
    }

    private String indoorOutdoor = "";
    private String probability = "";
    private String powerLevel = "";
    private String noOfAccessPoints = "";

    private BroadcastReceiver currentStatusReceiver;
    private BroadcastReceiver wifiInfoReceiver;

    private static Context activityContext;
    private IindoorOutdoorDetectorController observer;

    public final void startIndoorOutdoorDetector(Context context, IindoorOutdoorDetectorController observer) {
        Log.e(TAG, "Proximity detector started!!");

        //save the context
        activityContext = context;
        this.observer = observer;

        currentStatusReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent)//this method receives broadcast messages. Be sure to modify AndroidManifest.xml file in order to enable message receiving
            {
                indoorOutdoor = intent.getStringExtra(DetermineIndoorOutdoorService.CURRENT_STATUS_VALUE);
                probability = String.valueOf(intent.getIntExtra(DetermineIndoorOutdoorService.CURRENT_STATUS_PROB, 0));

                Log.e(TAG, "indoor/outdoor: " + indoorOutdoor);
                Log.e(TAG, "probability: " + probability);

                notifyObserver();
            }
        };

        wifiInfoReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent)//this method receives broadcast messages. Be sure to modify AndroidManifest.xml file in order to enable message receiving
            {
                int totalPower = intent.getIntExtra(WifiReceiver.TOTAL_POWER_VALUE, 0);
                if (totalPower != 0) {
                    powerLevel = String.valueOf(totalPower);
                    Log.e(TAG, "total power: " + powerLevel);
                } else {
                    powerLevel = "low";
                    Log.e(TAG, "total power: " + powerLevel);
                }

                noOfAccessPoints = String.valueOf(intent.getIntExtra(WifiReceiver.NUMBER_ACCESS_POINTS, 0));
                Log.e(TAG, "total access points: " + noOfAccessPoints);

                notifyObserver();
            }
        };


        IntentFilter currentStatusFilter = new IntentFilter(DetermineIndoorOutdoorService.CURRENT_STATUS_UPDATE);
        context.registerReceiver(currentStatusReceiver, currentStatusFilter);

        IntentFilter wifiInfoFilter = new IntentFilter(WifiReceiver.WIFI_INFO_UPDATE);
        context.registerReceiver(wifiInfoReceiver, wifiInfoFilter);

        context.startService(new Intent(context, DetermineIndoorOutdoorService.class));
    }


    public final void stopIndoorOutdoorDetector() {
        activityContext.unregisterReceiver(currentStatusReceiver);
        activityContext.unregisterReceiver(wifiInfoReceiver);
        activityContext.stopService(new Intent(activityContext, DetermineIndoorOutdoorService.class));
    }


    private void notifyObserver() {
        if (null != observer)
            observer.onIndoorOutdoorStatusChanged(indoorOutdoor.equalsIgnoreCase("indoor")? LocationStatus.indoor : LocationStatus.outdoor, probability, powerLevel, noOfAccessPoints);
    }
}
