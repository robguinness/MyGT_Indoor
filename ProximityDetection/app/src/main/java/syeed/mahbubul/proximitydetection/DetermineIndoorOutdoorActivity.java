package syeed.mahbubul.proximitydetection;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class DetermineIndoorOutdoorActivity extends Activity {

	private static final String TAG = "DetermineIndoorOutdoorActivity";
	private static final boolean DEBUG_ON = false;
    private static final String CURRENT_STATUS_VALUE = "com.contextawareness.determineindooroutdoor.CURRENT_STATUS_VALUE";
    private static final String CURRENT_STATUS_PROB = "com.contextawareness.determineindooroutdoor.CURRENT_STATUS_PROB";
    private static final String WIFI_INFO_UPDATE = "com.contextawareness.determineindooroutdoor.WIFI_INFO_UPDATE";
    private static final String WIFI_POWER_LOW_EVENT = "com.contextawareness.determineindooroutdoor.WIFI_POWER_LOW_EVENT";
    private static final String TOTAL_POWER_VALUE = "com.contextawareness.determineindooroutdoor.TOTAL_POWER_VALUE";
    private static final String NUMBER_ACCESS_POINTS = "com.contextawareness.determineindooroutdoor.NUMBER_ACCESS_POINTS";
    private static final String CURRENT_STATUS_UPDATE = "com.contextawareness.determineindooroutdoor.CURRENT_STATUS_UPDATE";
    private static final String GPS_VALUE = "com.contextawareness.determineindooroutdoor.GPS_VALUE";
    private static final String GPS_UPDATE = "com.contextawareness.determineindooroutdoor.GPS_UPDATE";    
	private BroadcastReceiver currentStatusReceiver;
	private BroadcastReceiver wifiInfoReceiver;
	//private BroadcastReceiver gpsReceiver;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (DEBUG_ON) Log.d("onCreate", "onCreate entered");
        setContentView(R.layout.activity_main);

        
        final TextView tvCurrentStatus 		= (TextView) findViewById(R.id.tvCurrentStatusValue);
        final TextView tvCurrentStatusProb 	= (TextView) findViewById(R.id.tvCurrentStatusProb);
        final TextView tvTotalPowerValue 		= (TextView) findViewById(R.id.tvTotalPowerValue);
        final TextView tvNumberAPsValue 	= (TextView) findViewById(R.id.tvNumberAPsValue);
        
        currentStatusReceiver = new BroadcastReceiver() {
        	
            @Override
              public void onReceive(Context context, Intent intent)//this method receives broadcast messages. Be sure to modify AndroidManifest.xml file in order to enable message receiving
              {
          	  	tvCurrentStatus.setText(intent.getStringExtra(CURRENT_STATUS_VALUE));
          	    tvCurrentStatusProb.setText(String.valueOf(intent.getIntExtra(CURRENT_STATUS_PROB,0))); 
              }
        };
        
        wifiInfoReceiver = new BroadcastReceiver() {
        	
            @Override
              public void onReceive(Context context, Intent intent)//this method receives broadcast messages. Be sure to modify AndroidManifest.xml file in order to enable message receiving
              {
            	int totalPower = intent.getIntExtra(TOTAL_POWER_VALUE,0);
            	if (totalPower != 0){
          	  		tvTotalPowerValue.setText(String.valueOf(totalPower));
            	}
            	else {
            		tvTotalPowerValue.setText("low");
            	}
            	
          	    tvNumberAPsValue.setText("(" + String.valueOf(intent.getIntExtra(NUMBER_ACCESS_POINTS,0)) + ")"); 
          	    
              }
        };
        
//        gpsReceiver = new BroadcastReceiver() {
//        	
//            @Override
//              public void onReceive(Context context, Intent intent)//this method receives broadcast messages. Be sure to modify AndroidManifest.xml file in order to enable message receiving
//              {
//            	if (DEBUG_ON) Log.d(TAG,"Location received in Activty.");
//          	  	Location location = intent.getExtras().getParcelable(GPS_VALUE);
//          	  	if (DEBUG_ON) Log.d(TAG,"Location:" + location.toString());
//              }
//        };
        
        IntentFilter currentStatusFilter = new IntentFilter(CURRENT_STATUS_UPDATE);
        registerReceiver(currentStatusReceiver,currentStatusFilter);

        IntentFilter wifiInfoFilter = new IntentFilter(WIFI_INFO_UPDATE);
        registerReceiver(wifiInfoReceiver,wifiInfoFilter);
        
        
        //IntentFilter gpsFilter = new IntentFilter(GPS_UPDATE);
        //registerReceiver(gpsReceiver,gpsFilter);
        
        startService(new Intent(this, DetermineIndoorOutdoorService.class));
        //startService(new Intent(this, GpsListenerService.class));
    }
	
	@Override
	public void onDestroy(){
		super.onDestroy();
        unregisterReceiver(currentStatusReceiver);
        unregisterReceiver(wifiInfoReceiver);
		stopService(new Intent(this, DetermineIndoorOutdoorService.class));    
		//stopService(new Intent(this, GpsListenerService.class)); 
	}
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_determine_indoor_outdoor, menu);
        return true;
    }
    */
    public void buttonClickHandlerIndoor(View target) {
    	if (DEBUG_ON) Log.d("indoorButton", "indoor button clicked");
    	Intent intent = new Intent(CURRENT_STATUS_UPDATE);
        intent.putExtra(CURRENT_STATUS_VALUE, "indoor");       
        intent.putExtra(CURRENT_STATUS_PROB, 99);  
        
        sendBroadcast(intent);
    }
    
    public void buttonClickHandlerOutdoor(View target) {
    	if (DEBUG_ON) Log.d("outdoorButton", "outdoor button clicked");
    	Intent intent = new Intent(CURRENT_STATUS_UPDATE);
        intent.putExtra(CURRENT_STATUS_VALUE, "outdoor");
        intent.putExtra(CURRENT_STATUS_PROB, 70);
        
        sendBroadcast(intent);
    }
    
}
