package org.mygeotrust.indoor.tasks.detectProximity.algorithm;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class DetermineIndoorOutdoorService extends Service {
	private static final boolean DEBUG_ON = false;
	private static final String TAG = "DetermineIndoorOutdoorService";	
    public static final String CURRENT_STATUS_VALUE = "com.contextawareness.determineindooroutdoor.CURRENT_STATUS_VALUE";
    public static final String CURRENT_STATUS_PROB = "com.contextawareness.determineindooroutdoor.CURRENT_STATUS_PROB";
    public static final String CURRENT_STATUS_UPDATE = "com.contextawareness.determineindooroutdoor.CURRENT_STATUS_UPDATE";
    private static final String WIFI_INFO_UPDATE = "com.contextawareness.determineindooroutdoor.WIFI_INFO_UPDATE";
    private static final String WIFI_POWER_LOW_EVENT = "com.contextawareness.determineindooroutdoor.WIFI_POWER_LOW_EVENT";
    private static final String TOTAL_POWER_VALUE = "com.contextawareness.determineindooroutdoor.TOTAL_POWER_VALUE";
    private static final String GPS_STATUS_UPDATE = "com.contextawareness.determineindooroutdoor.GPS_STATUS_UPDATE";
    private static final String SAT_COUNT = "com.contextawareness.determineindooroutdoor.SAT_COUNT";
    private static final String SAT_FIX_COUNT = "com.contextawareness.determineindooroutdoor.SAT_FIX_COUNT";
    private static final String MIN_SNR = "com.contextawareness.determineindooroutdoor.MIN_SNR";
    private static final String MAX_SNR = "com.contextawareness.determineindooroutdoor.MAX_SNR";
    private static final String MEAN_SNR = "com.contextawareness.determineindooroutdoor.MEAN_SNR";
	//private static final float indoorOutdoorSNRThreshold = 20;
	private static int thresholdCount;
	private static boolean WIFI_SERVICE_RUNNING = false;
	private WifiReceiver wifiReceiver;
	private WifiManager wifiManager;
	
	private BroadcastReceiver wifiInfoReceiver;

	@Override	
	public IBinder onBind(Intent intent) {	
		if (DEBUG_ON) Log.d(TAG, "DetermineInOut onBind");
		return null;	
		}		
	private Intent intentCurrentStatus = new Intent(CURRENT_STATUS_UPDATE);
	private BroadcastReceiver gpsStatusReceiver;
	@Override	
	public void onCreate() {
		Toast.makeText(this, "DetermineInOut Service Created", Toast.LENGTH_SHORT).show();
		if (DEBUG_ON) Log.d(TAG, "onCreate");	
		
		gpsStatusReceiver = new BroadcastReceiver() {
			
		
            @Override
              public void onReceive(Context context, Intent intent)//this method receives GPS status broadcast messages.
              {
            	if (DEBUG_ON) Log.d(TAG,"GPS status update received in DetermineInOut Service.");
            	new Thread(new IndoorOutdoorRunnable(intent)).start();
            }
        };
		
        IntentFilter gpsStatusFilter = new IntentFilter(GPS_STATUS_UPDATE);
        registerReceiver(gpsStatusReceiver,gpsStatusFilter);
        
		startService(new Intent(this, HighRateGpsStatusListenerService.class));
		
		wifiReceiver = new WifiReceiver();
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
		
        wifiInfoReceiver = new BroadcastReceiver() {
        	
            @Override
              public void onReceive(Context context, Intent intent)//this method receives broadcast messages. Be sure to modify AndroidManifest.xml file in order to enable message receiving
              {
            	int totalPower = intent.getIntExtra(TOTAL_POWER_VALUE,0);
            	
          	    if (totalPower < 70){
          	    	sendBroadcast(new Intent(WIFI_POWER_LOW_EVENT));
          	    }
              }
        };
        
        IntentFilter wifiInfoFilter = new IntentFilter(WIFI_INFO_UPDATE);
        registerReceiver(wifiInfoReceiver,wifiInfoFilter);
        
        
        Intent startingIntent = new Intent(CURRENT_STATUS_UPDATE);
        startingIntent.putExtra(CURRENT_STATUS_VALUE, "outdoor");
        startingIntent.putExtra(CURRENT_STATUS_PROB, 50);
        sendBroadcast(startingIntent);
        new Thread(new IndoorOutdoorRunnable(startingIntent)).start();

		}	
	
	@Override	
	public void onDestroy() {	
		Toast.makeText(this, "DetermineInOut Service Stopped", Toast.LENGTH_SHORT).show();
		if (DEBUG_ON) Log.d(TAG, "onDestroy");
		unregisterReceiver(gpsStatusReceiver);
		unregisterReceiver(wifiReceiver);
        unregisterReceiver(wifiInfoReceiver);
		stopService(new Intent(this, HighRateGpsStatusListenerService.class));
		//stopService(new Intent(this, HighRateGpsStatusListenerService.class));

	}
	

	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Toast.makeText(this, "DetermineInOut Service Started", Toast.LENGTH_SHORT).show();
		if (DEBUG_ON) Log.d(TAG, "onStart");		
		
	    
	    // We want this service to continue running until it is explicitly
	    // stopped, so return sticky.
	    return START_STICKY;
	}
	boolean inOutFlag;
	boolean indoorScanMode = false;
	
	private class IndoorOutdoorRunnable implements Runnable {
		Intent intent;
		
		
		private IndoorOutdoorRunnable(Intent intent){
			this.intent = intent;
		}
		public void run() {
			int satCount = intent.getIntExtra(SAT_COUNT, 999);
        	int satFixCount = intent.getIntExtra(SAT_FIX_COUNT, 999);
        	int minSNR = intent.getIntExtra(MIN_SNR, 999);
        	int maxSNR = intent.getIntExtra(MAX_SNR, 999);
        	float meanSNR = intent.getFloatExtra(MEAN_SNR, 999);
      	  	if (DEBUG_ON) Log.d(TAG,"Sat Count: " + satCount + " Sat Fix: " + satFixCount + " Min SNR: " + minSNR +  " Max SNR: " + 
      	  			maxSNR + " Mean SNR: " + meanSNR + "(in service)");
      	  		
      	  		boolean prevFlag = inOutFlag;
      	  		inOutFlag = false;
      	  		int prob;
      	  		
      	  		switch (satCount) {
      	  		case 0:			prob = 99;
      	  						thresholdCount+=5;
      	  						break;
      	  		case 1:			
				      	  		if (meanSNR <= 5) {
				      	  			prob = 90;
				      	  		}
					        	else if (meanSNR <=10) {
					        		prob = 85;
					        	}
					        	else if (meanSNR <=15) {
					        		prob = 80;
					        	}
					        	else if (meanSNR <=20) {
					        		prob = 75;
					        	}
					        	else if (meanSNR <=25) {
					        		prob = 70;
					        	}
					        	else if (meanSNR <=30) {
					        		prob = 65;
					        	}
					        	else {
					        		prob = 60;
					        	}
      	  						thresholdCount+=3;
								break;	
      	  		case 2:			
				      	  		if (meanSNR <= 5) {
				      	  			prob = 90;
				      	  		}
					        	else if (meanSNR <=10) {
					        		prob = 85;
					        	}
					        	else if (meanSNR <=15) {
					        		prob = 75;
					        	}
					        	else if (meanSNR <=20) {
					        		prob = 70;
					        	}
					        	else if (meanSNR <=25) {
					        		prob = 65;
					        	}
					        	else if (meanSNR <=30) {
					        		prob = 60;
					        	}
					        	else {
					        		prob = 55;
					        	}
								thresholdCount+=3;
								break;   	  		
				case 3:			
				      	  		if (meanSNR <= 5) {
				      	  			prob = 90;
				      	  		}
					        	else if (meanSNR <=10) {
					        		prob = 85;
					        	}
					        	else if (meanSNR <=15) {
					        		prob = 75;
					        	}
					        	else if (meanSNR <=20) {
					        		prob = 60;
					        	}
					        	else if (meanSNR <=25) {
					        		prob = 55;
					        	}
					        	else if (meanSNR <=30) {
					        		prob = 55;
					        		inOutFlag = true;
					        	}
					        	else {
					        		prob = 60;
					        		inOutFlag = true;
					        	}
								thresholdCount+=3;
								break;
      	  		case 4:
				      	  		if (meanSNR <= 5) {
				      	  			prob = 90;
				      	  		}
					        	else if (meanSNR <=10) {
					        		prob = 80;
					        	}
					        	else if (meanSNR <=15) {
					        		prob = 70;
					        	}
					        	else if (meanSNR <=20) {
					        		prob = 55;
					        	}
					        	else if (meanSNR <=25) {
					        		prob = 75;
					        		inOutFlag = true;
					        	}
					        	else if (meanSNR <=30) {
					        		prob = 80;
					        		inOutFlag = true;
					        	}
					        	else {
					        		prob = 85;
					        		inOutFlag = true;
					        	}
								thresholdCount+=3;
								break;
      	  		case 5:
				      	  		if (meanSNR <= 5) {
				      	  			prob = 90;
				      	  		}
					        	else if (meanSNR <=10) {
					        		prob = 80;
					        	}
					        	else if (meanSNR <=15) {
					        		prob = 65;
					        	}
					        	else if (meanSNR <=20) {
					        		prob = 65;
					        		inOutFlag = true;
					        	}
					        	else if (meanSNR <=25) {
					        		prob = 75;
					        		inOutFlag = true;
					        	}
					        	else if (meanSNR <=30) {
					        		prob = 85;
					        		inOutFlag = true;
					        	}
					        	else {
					        		prob = 90;
					        		inOutFlag = true;
					        	}
								thresholdCount+=3;
								break;
      	  		case 6:
				      	  		if (meanSNR <= 5) {
				      	  			prob = 90;
				      	  		}
					        	else if (meanSNR <=10) {
					        		prob = 80;
					        	}
					        	else if (meanSNR <=15) {
					        		prob = 60;
					        	}
					        	else if (meanSNR <=20) {
					        		prob = 70;
					        		inOutFlag = true;
					        	}
					        	else if (meanSNR <=25) {
					        		prob = 85;
					        		inOutFlag = true;
					        	}
					        	else if (meanSNR <=30) {
					        		prob = 90;
					        		inOutFlag = true;
					        	}
					        	else {
					        		prob = 95;
					        		inOutFlag = true;
					        	}
								thresholdCount+=3;
								break;
      	  		case 7:
				      	  		if (meanSNR <= 5) {
				      	  			prob = 90;
				      	  		}
					        	else if (meanSNR <=10) {
					        		prob = 80;
					        	}
					        	else if (meanSNR <=15) {
					        		prob = 60;
					        	}
					        	else if (meanSNR <=20) {
					        		prob = 75;
					        		inOutFlag = true;
					        	}
					        	else if (meanSNR <=25) {
					        		prob = 85;
					        		inOutFlag = true;
					        	}
					        	else if (meanSNR <=30) {
					        		prob = 95;
					        		inOutFlag = true;
					        	}
					        	else {
					        		prob = 99;
					        		inOutFlag = true;
					        	}
								thresholdCount+=3;
								break;
      	  		case 8:
				      	  		if (meanSNR <= 5) {
				      	  			prob = 90;
				      	  		}
					        	else if (meanSNR <=10) {
					        		prob = 80;
					        	}
					        	else if (meanSNR <=15) {
					        		prob = 60;
					        	}
					        	else if (meanSNR <=20) {
					        		prob = 80;
					        		inOutFlag = true;
					        	}
					        	else if (meanSNR <=25) {
					        		prob = 90;
					        		inOutFlag = true;
					        	}
					        	else if (meanSNR <=30) {
					        		prob = 98;
					        		inOutFlag = true;
					        	}
					        	else {
					        		prob = 99;
					        		inOutFlag = true;
					        	}
								thresholdCount+=3;
								break;
      	  		default:
				      	  		if (meanSNR <= 5) {
				      	  			prob = 80;
				      	  		}
					        	else if (meanSNR <=10) {
					        		prob = 75;
					        	}
					        	else if (meanSNR <=15) {
					        		prob = 55;
					        	}
					        	else if (meanSNR <=20) {
					        		prob = 80;
					        		inOutFlag = true;
					        	}
					        	else if (meanSNR <=25) {
					        		prob = 95;
					        		inOutFlag = true;
					        	}
					        	else if (meanSNR <=30) {
					        		prob = 98;
					        		inOutFlag = true;
					        	}
					        	else {
					        		prob = 99;
					        		inOutFlag = true;
					        	}
								thresholdCount+=3;
								break;
      	  		}
      	  	if (DEBUG_ON) Log.d(TAG, "thresholdCount: " + thresholdCount);
      	  		if (inOutFlag){
	        	   if (DEBUG_ON) Log.d(TAG, "OUTDOOR detected by GPS status listener!");
	        	   intentCurrentStatus.putExtra(CURRENT_STATUS_VALUE, "outdoor");
	        	   
	            }
      	  		else{
	        	   if (DEBUG_ON) Log.d(TAG, "INDOOR detected by GPS status listener!");
	        	   intentCurrentStatus.putExtra(CURRENT_STATUS_VALUE, "indoor");
	        	   
	        	   if (!WIFI_SERVICE_RUNNING) {
	   				// Start wifi scan service
	        		if (DEBUG_ON) Log.d(TAG, "Starting wifi service.....");
	   				startService(new Intent(getBaseContext(), WifiScanService.class));
	   				WIFI_SERVICE_RUNNING = true;
	   			}
	        	   //thresholdCount = 0;
	           }
      	  		
	      	   if (thresholdCount <= 10) {
	     		   intentCurrentStatus.putExtra(CURRENT_STATUS_PROB, 50);
	     	   }
	     	   else if (thresholdCount <=15) {
	     		   intentCurrentStatus.putExtra(CURRENT_STATUS_PROB, 55);
	     	   }
	     	   else if (thresholdCount <=20) {
	     		   intentCurrentStatus.putExtra(CURRENT_STATUS_PROB, 60);
	     	   }
	     	   else if (thresholdCount <=25) {
	     		   intentCurrentStatus.putExtra(CURRENT_STATUS_PROB, 65);
	     	   }
	     	   else {
	     		   intentCurrentStatus.putExtra(CURRENT_STATUS_PROB, prob); 
	     	   }
	      	   Log.d(TAG, "Outdoor: " + inOutFlag + " prob " + prob);
	     	   sendBroadcast(intentCurrentStatus);
	     	   
	     	   if (inOutFlag != prevFlag){
	     		   thresholdCount = 0;
	     	   }
	     	   if (thresholdCount > 500) {
	     		   thresholdCount = 50;
	     	   }
	           

			
		}
		
	}

 
}
