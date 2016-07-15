package org.mygeotrust.indoor.tasks.detectProximity;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WifiScanService extends Service {

	private static final boolean DEBUG_ON = false;
	private static final String TAG = "WifiScanService";
    private static final String WIFI_RESULTS = "com.contextawareness.determineindooroutdoor.WIFI_RESULTS";
    private static final String WIFI_RESULT_AVAILABLE = "com.contextawareness.determineindooroutdoor.WIFI_RESULT_AVAILABLE";	
    private static final String ITEM_KEY = "WifiResults";
    private static final int SCAN_INTERVAL = 5000;
    
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override	
	public void onCreate() {
		Toast.makeText(this, "Wifi Scan Service Created", Toast.LENGTH_LONG).show();
		if (DEBUG_ON) Log.d(TAG, "onCreate");	

		new Thread(new WifiScanRunnable()).start();

		}	
	
	@Override	
	public void onDestroy() {	
		Toast.makeText(this, "Wifi Scan Service Stopped", Toast.LENGTH_LONG).show();
		if (DEBUG_ON) Log.d(TAG, "onDestroy");
		try {
			unregisterReceiver(wifiScanReceiver);
		}
		catch (IllegalArgumentException e){
			e.printStackTrace();
		}

	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Toast.makeText(this, "Wifi Scan Service Started", Toast.LENGTH_LONG).show();
		if (DEBUG_ON) Log.d(TAG, "onStart");		
		
	    
	    // We want this service to continue running until it is explicitly
	    // stopped, so return sticky.
	    return START_STICKY;
	}
	
    public static WifiManager wifiManager;
    private static WifiScanReceiver wifiScanReceiver;
    
	private class WifiScanRunnable implements Runnable {
		Long timeOfPrevScan = SystemClock.currentThreadTimeMillis();
		public void run() {
			// Start wifi scanner
			if (wifiManager == null) {
				wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
				if (!wifiManager.isWifiEnabled()){
					wifiManager.setWifiEnabled(true);
				}
				WifiLock scanOnlyLock = wifiManager.createWifiLock(WifiManager.WIFI_MODE_SCAN_ONLY, "scanOnly");      
				scanOnlyLock.acquire(); 
				
				wifiScanReceiver = new WifiScanReceiver();

				HandlerThread handlerThread = new HandlerThread("WifiScanReceiverThread");
				handlerThread.setPriority(Thread.MAX_PRIORITY);
				handlerThread.start();
				// Now get the Looper from the HandlerThread so that we can create a Handler that is attached to
				//  the HandlerThread
				// NOTE: This call will block until the HandlerThread gets control and initializes its Looper
				Looper looper = handlerThread.getLooper();
				// Create a handler for the service
				Handler handler = new Handler(looper);
				
				// Register the broadcast receiver to run on the separate Thread
				registerReceiver(wifiScanReceiver, new IntentFilter(
	    				WifiManager.SCAN_RESULTS_AVAILABLE_ACTION), null, handler);

				//registerReceiver(wifiScanReceiver, new IntentFilter(
	    		//		WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
				
				int i = 0;
				//timeOfScanStart = System.currentTimeMillis();
				
				Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
					while (true) {
						
						// Initiate a wifi scan.
						
						//Log.d(TAG, "Scan " + i + " initiated at " + secondsFormat.format(System.currentTimeMillis()) + " on Thread " + Thread.currentThread().getId() + " with priotity " + Thread.currentThread().getPriority());
						//timeOfScan = SystemClock.currentThreadTimeMillis();
			    		
						boolean scanResult = wifiManager.startScan();
//						if (!scanResult){
//							Log.d(TAG, "SCAN " + i + " FAILED!");
//
//						}
			    		i++;
			    		try {
							Thread.sleep(SCAN_INTERVAL);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					//float elapsedTime = ((float) (System.currentTimeMillis() - timeOfScanStart))/1000;
					//Log.d(TAG, countReceivedScans + " scans received in " + elapsedTime + " seconds.");
					// Get instance of Vibrator from current Context
					//Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
					//v.vibrate(300);
			}
			
		}
		
	}
	Long timeOfScanStart;
	Long timeOfScanReceipt;
	int countReceivedScans = 0;
	SimpleDateFormat secondsFormat = new SimpleDateFormat("HH:mm:ss.SSS");
	Intent intentWifiResults = new Intent(WIFI_RESULT_AVAILABLE);
	
	ArrayList<HashMap<String, ScanResult>> wifiArrayList = new ArrayList<HashMap<String, ScanResult>>();

	private class WifiScanReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			countReceivedScans++;

			//timeOfScanReceipt = SystemClock.currentThreadTimeMillis();
			if (DEBUG_ON) Log.d(TAG, "Wifi scan " + countReceivedScans + " received at " + secondsFormat.format(System.currentTimeMillis()) + " on Thread " + Thread.currentThread().getId() + " with priotity " + Thread.currentThread().getPriority());
			List<ScanResult> wifiList = wifiManager.getScanResults();

		}
	}
}
