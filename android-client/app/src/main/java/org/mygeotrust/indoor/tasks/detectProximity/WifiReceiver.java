package org.mygeotrust.indoor.tasks.detectProximity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class WifiReceiver extends BroadcastReceiver implements Comparator<ScanResult> {

	private static final boolean DEBUG_ON = false;
    private static final String WIFI_INFO_UPDATE = "com.contextawareness.determineindooroutdoor.WIFI_INFO_UPDATE";
    private static final String TOTAL_POWER_VALUE = "com.contextawareness.determineindooroutdoor.TOTAL_POWER_VALUE";
    private static final String NUMBER_ACCESS_POINTS = "com.contextawareness.determineindooroutdoor.NUMBER_ACCESS_POINTS";
    
		//Handler wifiProcessingHandler;
		long scanTime;
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (DEBUG_ON) Log.d("WifiReceiver", "Wifi scan received on Thread " + Thread.currentThread().getId() + " with priority " + Thread.currentThread().getPriority());
			try {
				WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
				List<ScanResult> wifiList = wifiManager.getScanResults();
				scanTime = System.currentTimeMillis();
				ArrayList<ScanResult> wifiArray = new ArrayList<ScanResult>();
				int length = wifiList.size();
				for(int i = 0; i < length; i++){
					wifiArray.add(wifiList.get(i));
				}
				  
				Collections.sort(wifiArray, this);
				
				int highestLevel = 0;
				int totalPower = 0;
				float avgPower = 0;
				int size = wifiArray.size();
				for (int i = 0; i< size ; i++ ){
					int level = wifiArray.get(i).level;
					if (level > -80) {
						totalPower += 100 + wifiArray.get(i).level;
					}
				}
				
				if (size > 0) {
					highestLevel = wifiArray.get(0).level;
					avgPower = totalPower / size;
				}
				
				Intent intentWifiInfo = new Intent(WIFI_INFO_UPDATE);
		        intentWifiInfo.putExtra(TOTAL_POWER_VALUE, totalPower);
		        intentWifiInfo.putExtra(NUMBER_ACCESS_POINTS, size);		           
		        context.sendBroadcast(intentWifiInfo);
				
				if (DEBUG_ON) Log.d("WifiReceiver", "Highest level: " + highestLevel + ", Total level: " + totalPower + ", Average Level: " + avgPower + ", Num: " + size);
				//Thread processWifiThread = new Thread(new ProcessWifiResultRunnable(wifiList, scanTime));
				//processWifiThread.setPriority(Thread.MIN_PRIORITY);
				//processWifiThread.start();
				
//				if (wifiProcessingHandlerThread == null){
//					//Create and start the handler thread - give it a custom name
//					wifiProcessingHandlerThread = new HandlerThread("realTimeProcessingHandlerThread ");
//					wifiProcessingHandlerThread.start();
//					//Get the looper from the handlerThread
//					Looper looper = wifiProcessingHandlerThread.getLooper();
//					//Create a new handler - passing in the looper for it to use
//					wifiProcessingHandler = new Handler(looper);
//				}
//				
//				Runnable processWifiRunnable = new ProcessWifiResultRunnable(wifiList, scanTime);
//				wifiProcessingHandler.post(processWifiRunnable);
//				
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
		}

		public int compare(ScanResult o1, ScanResult o2) {
			return o2.level - o1.level;
		}
	
    	
	}

