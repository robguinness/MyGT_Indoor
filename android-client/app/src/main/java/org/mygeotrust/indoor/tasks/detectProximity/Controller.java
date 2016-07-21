package org.mygeotrust.indoor.tasks.detectProximity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import org.mygeotrust.indoor.tasks.detectProximity.algorithm.DetermineIndoorOutdoorService;
import org.mygeotrust.indoor.tasks.detectProximity.algorithm.WifiReceiver;

/**
 * Created by Dr. Mahbubul Syeed on 21.7.2016.
 */
public class Controller {

    private final String TAG = Controller.class.getName();

    private static Controller ourInstance = new Controller();

    public static Controller getInstance() {
        return ourInstance;
    }

    private Controller() {
    }

    private BroadcastReceiver currentStatusReceiver;
    private BroadcastReceiver wifiInfoReceiver;

    private static Context activityContext;

    public final void startProximityDetector(Context context)
    {
        Log.e(TAG, "Proximity detector started!!");

        //save the context
        activityContext = context;

        currentStatusReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent)//this method receives broadcast messages. Be sure to modify AndroidManifest.xml file in order to enable message receiving
            {
                Log.e(TAG, "indoor/outdoor: " + intent.getStringExtra(DetermineIndoorOutdoorService.CURRENT_STATUS_VALUE));
                Log.e(TAG, "probability: " + String.valueOf(intent.getIntExtra(DetermineIndoorOutdoorService.CURRENT_STATUS_PROB, 0)));
                //tvCurrentStatus.setText(intent.getStringExtra(CURRENT_STATUS_VALUE));
                //tvCurrentStatusProb.setText(String.valueOf(intent.getIntExtra(CURRENT_STATUS_PROB, 0)));
            }
        };

        wifiInfoReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent)//this method receives broadcast messages. Be sure to modify AndroidManifest.xml file in order to enable message receiving
            {
                int totalPower = intent.getIntExtra(WifiReceiver.TOTAL_POWER_VALUE, 0);
                if (totalPower != 0) {
                    //tvTotalPowerValue.setText(String.valueOf(totalPower));
                    Log.e(TAG, "total power: " + String.valueOf(totalPower));
                } else {
                    //tvTotalPowerValue.setText("low");
                    Log.e(TAG, "total power: " + "low");
                }

                //tvNumberAPsValue.setText("(" + String.valueOf(intent.getIntExtra(NUMBER_ACCESS_POINTS, 0)) + ")");
                Log.e(TAG, "total access points: " + String.valueOf(intent.getIntExtra(WifiReceiver.NUMBER_ACCESS_POINTS, 0)));
            }
        };


        IntentFilter currentStatusFilter = new IntentFilter(DetermineIndoorOutdoorService.CURRENT_STATUS_UPDATE);
        context.registerReceiver(currentStatusReceiver, currentStatusFilter);

        IntentFilter wifiInfoFilter = new IntentFilter(WifiReceiver.WIFI_INFO_UPDATE);
        context.registerReceiver(wifiInfoReceiver, wifiInfoFilter);

        context.startService(new Intent(context, DetermineIndoorOutdoorService.class));
    }



    public final void stopProximityDetector()
    {
        activityContext.unregisterReceiver(currentStatusReceiver);
        activityContext.unregisterReceiver(wifiInfoReceiver);
        activityContext.stopService(new Intent(activityContext, DetermineIndoorOutdoorService.class));
    }
}
