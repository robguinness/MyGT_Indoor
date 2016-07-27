package org.mygeotrust.indoor.tasks.detectIndoorOutdoor.algorithm;

import android.Manifest;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.Iterator;

public class HighRateGpsStatusListenerService extends Service implements LocationListener {

	private static final boolean DEBUG_ON = false;
	private static final String TAG = "GpsStatusListenerService";
	private static final String CURRENT_STATUS_VALUE = "com.contextawareness.determineindooroutdoor.CURRENT_STATUS_VALUE";
	private static final String CURRENT_STATUS_PROB = "com.contextawareness.determineindooroutdoor.CURRENT_STATUS_PROB";
	private static final String CURRENT_STATUS_UPDATE = "com.contextawareness.determineindooroutdoor.CURRENT_STATUS_UPDATE";
	private static final String WIFI_POWER_LOW_EVENT = "com.contextawareness.determineindooroutdoor.WIFI_POWER_LOW_EVENT";
	private static final String GPS_STATUS_UPDATE = "com.contextawareness.determineindooroutdoor.GPS_STATUS_UPDATE";
	private static final String SAT_COUNT = "com.contextawareness.determineindooroutdoor.SAT_COUNT";
	private static final String SAT_FIX_COUNT = "com.contextawareness.determineindooroutdoor.SAT_FIX_COUNT";
	private static final String MIN_SNR = "com.contextawareness.determineindooroutdoor.MIN_SNR";
	private static final String MAX_SNR = "com.contextawareness.determineindooroutdoor.MAX_SNR";
	private static final String MEAN_SNR = "com.contextawareness.determineindooroutdoor.MEAN_SNR";

	private LocationManager locationManager;
	private BroadcastReceiver currentStatusReceiver;
	private BroadcastReceiver wifiPowerLowReceiver;

	private boolean indoorScanMode = false;
	private boolean wifiPowerLowTimeout = false;
	private boolean wifiPowerLowRecentlyTriggered = false;


	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private final Handler handler = new Handler();
	;
	private Handler mHandler;

	@Override

	public void onCreate() {
		Toast.makeText(this, "GPS Status Service Created", Toast.LENGTH_SHORT).show();
		if (DEBUG_ON) Log.d(TAG, "onCreate");

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			// TODO: Consider calling
			//    ActivityCompat#requestPermissions
			// here to request the missing permissions, and then overriding
			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
			//                                          int[] grantResults)
			// to handle the case where the user grants the permission. See the documentation
			// for ActivityCompat#requestPermissions for more details.
			return;
		}
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
		locationManager.addGpsStatusListener(mGpsStatusListener);

		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				String i = (String) msg.obj;
				Toast.makeText(getBaseContext(), i, Toast.LENGTH_SHORT).show();
			}
		};


		currentStatusReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent)//this method receives broadcast messages. Be sure to modify AndroidManifest.xml file in order to enable message receiving
			{
				String currentStatus = intent.getStringExtra(CURRENT_STATUS_VALUE);
				int statusProb = intent.getIntExtra(CURRENT_STATUS_PROB, 0);
				//If high probability, switch scanning modes
				if (statusProb >= 75) {
					// switch to indoorScanMode
					if (currentStatus.equals("indoor") && !indoorScanMode && !wifiPowerLowTimeout) {
						if (DEBUG_ON) Log.d(TAG, "---------------SWITCHING TO INDOOR MODE!");
						Toast.makeText(getBaseContext(), "Entering indoor scan mode", Toast.LENGTH_SHORT).show();
						Toast.makeText(getBaseContext(), "Indoor: Removing updates", Toast.LENGTH_SHORT).show();
						if (ActivityCompat.checkSelfPermission(HighRateGpsStatusListenerService.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(HighRateGpsStatusListenerService.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
							// TODO: Consider calling
							//    ActivityCompat#requestPermissions
							// here to request the missing permissions, and then overriding
							//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
							//                                          int[] grantResults)
							// to handle the case where the user grants the permission. See the documentation
							// for ActivityCompat#requestPermissions for more details.
							return;
						}
						locationManager.removeUpdates(HighRateGpsStatusListenerService.this);
						indoorScanMode = true;
						lowRateStopped = false;
						Runnable lowRateUpdates = new LowRateUpdates();
						new Thread(lowRateUpdates).start();


					} else if (currentStatus.equals("outdoor") && indoorScanMode) {
						if (DEBUG_ON)
							Log.d(TAG, "-------------------------Switching to OUTDOOR mode!");
						Toast.makeText(getBaseContext(), "Entering outdoor scan mode", Toast.LENGTH_SHORT).show();
						indoorScanMode = false;
						wifiPowerLowRecentlyTriggered = false;
						lowRateStopped = true;
						//locationManager.removeUpdates(HighRateGpsStatusListenerService.this);
						locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, HighRateGpsStatusListenerService.this);
					}
				} else if (indoorScanMode) {
					if (DEBUG_ON)
						Log.d(TAG, "-------------------------Switching to OUTDOOR mode due to low prob.");
					Toast.makeText(getBaseContext(), "Detection probability low", Toast.LENGTH_SHORT).show();
					indoorScanMode = false;
					lowRateStopped = true;
					locationManager.removeUpdates(HighRateGpsStatusListenerService.this);
					locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, HighRateGpsStatusListenerService.this);
				}
			}
		};
		wifiPowerLowReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent)//this method receives broadcast messages. Be sure to modify AndroidManifest.xml file in order to enable message receiving
			{

				if (indoorScanMode && !wifiPowerLowRecentlyTriggered) {
					wifiPowerLowRecentlyTriggered = true;
					Runnable wifiPowerLowTriggerTimeoutRunnable = new Runnable() {

						public void run() {
							SystemClock.sleep(300000);
							wifiPowerLowRecentlyTriggered = false;
						}

					};
					new Thread(wifiPowerLowTriggerTimeoutRunnable).start();


					if (DEBUG_ON)
						Log.d(TAG, "-------------------------Switching to OUTDOOR mode due to low wifi power.");
					Toast.makeText(getBaseContext(), "Low wifi power detected!!!", Toast.LENGTH_SHORT).show();
					Toast.makeText(getBaseContext(), "Switching to outdoor scan mode.", Toast.LENGTH_SHORT).show();
					wifiPowerLowTimeout = true;
					indoorScanMode = false;
					lowRateStopped = true;

					if (ActivityCompat.checkSelfPermission(HighRateGpsStatusListenerService.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(HighRateGpsStatusListenerService.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
						// TODO: Consider calling
						//    ActivityCompat#requestPermissions
						// here to request the missing permissions, and then overriding
						//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
						//                                          int[] grantResults)
						// to handle the case where the user grants the permission. See the documentation
						// for ActivityCompat#requestPermissions for more details.
						return;
					}
					locationManager.removeUpdates(HighRateGpsStatusListenerService.this);
					locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, HighRateGpsStatusListenerService.this);
					Runnable wifiPowerLowTimeoutRunnable = new Runnable() {

						public void run() {
							SystemClock.sleep(15000);
							wifiPowerLowTimeout = false;
						}

					};
					new Thread(wifiPowerLowTimeoutRunnable).start();
				}
			}
		};
		IntentFilter currentStatusFilter = new IntentFilter(CURRENT_STATUS_UPDATE);
		registerReceiver(currentStatusReceiver, currentStatusFilter);
		IntentFilter wifiPowerLowFilter = new IntentFilter(WIFI_POWER_LOW_EVENT);
		registerReceiver(wifiPowerLowReceiver, wifiPowerLowFilter);
	}

	private boolean lowRateStopped;


	private class LowRateUpdates implements Runnable {

		public void run() {
			while (indoorScanMode) {
				// do real work here
				SystemClock.sleep(105000);
				if (ActivityCompat.checkSelfPermission(HighRateGpsStatusListenerService.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(HighRateGpsStatusListenerService.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
					// TODO: Consider calling
					//    ActivityCompat#requestPermissions
					// here to request the missing permissions, and then overriding
					//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
					//                                          int[] grantResults)
					// to handle the case where the user grants the permission. See the documentation
					// for ActivityCompat#requestPermissions for more details.
					return;
				}
				locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, HighRateGpsStatusListenerService.this, handler.getLooper());
				SystemClock.sleep(15000);
				if (!lowRateStopped) {
					Message msg = new Message();
					msg.obj = "Run: removeUpdates";
					mHandler.sendMessage(msg);

					locationManager.removeUpdates(HighRateGpsStatusListenerService.this);
				}
			}

		}

		public void stop() {
			lowRateStopped = true;
		}

	}

	@Override
	public void onDestroy() {
		Toast.makeText(this, "GPS Status Service Stopped", Toast.LENGTH_SHORT).show();
		if (DEBUG_ON) Log.d(TAG, "onDestroy");
		locationManager.removeGpsStatusListener(mGpsStatusListener);
		Toast.makeText(this, "onDestroy: removing updates", Toast.LENGTH_SHORT).show();
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			// TODO: Consider calling
			//    ActivityCompat#requestPermissions
			// here to request the missing permissions, and then overriding
			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
			//                                          int[] grantResults)
			// to handle the case where the user grants the permission. See the documentation
			// for ActivityCompat#requestPermissions for more details.
			return;
		}
		locationManager.removeUpdates(this);
		unregisterReceiver(currentStatusReceiver);
		unregisterReceiver(wifiPowerLowReceiver);
		
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Toast.makeText(this, "GPS Status Service Started", Toast.LENGTH_SHORT).show();
		if (DEBUG_ON) Log.d(TAG, "onStart");		
		
	    
	    // We want this service to continue running until it is explicitly
	    // stopped, so return sticky.
	    return START_STICKY;
	}
	
	// Define a listener that responds to GpsStatus updates
	private GpsStatus.Listener mGpsStatusListener = new GpsStatus.Listener() {
		
		public void onGpsStatusChanged(int event) {
			if (DEBUG_ON) Log.d(TAG, "GPS status update received.");
			GpsStatus latestGpsStatus = locationManager.getGpsStatus(null);
			if(latestGpsStatus != null) {
				Iterable<GpsSatellite>satellites = latestGpsStatus.getSatellites();
				Iterator<GpsSatellite>sat = satellites.iterator();
		           int count=0;
		           int fixCount = 0;
		           int minSNR = 1000;
		           int maxSNR = 0;
		           float meanSNR = 0;
		           while (sat.hasNext() ) { 
		        	   GpsSatellite iSat = sat.next();
		               //boolean usedInFix = iSat.usedInFix();
		        	   count++;
		        	   //find minimum SNR
		        	   if (iSat.getSnr() < minSNR){
		        		   minSNR = (int) iSat.getSnr();
		        	   }
		        	   if (iSat.getSnr() > maxSNR){
		        		   maxSNR = (int) iSat.getSnr();
		        	   }
		        	   meanSNR += iSat.getSnr();
		        	   if (iSat.usedInFix()){
		        		   fixCount++;
		        	   }
		        	   
		        	   // This is just an experiment to see if this value ever returns true!
		        	   boolean hasAlmanac = iSat.hasAlmanac();
		        	   //Log.d(TAG, "hasAlmanac: " + hasAlmanac);
		           }
		           if (count>0){
		        	   meanSNR = meanSNR/count;
		           }
		           
		           
		           if (DEBUG_ON) Log.d(TAG, "Sat count: " + count);
		           Intent intentGpsStatus = new Intent(GPS_STATUS_UPDATE);
		           intentGpsStatus.putExtra(SAT_COUNT, count);
		           intentGpsStatus.putExtra(SAT_FIX_COUNT, fixCount);		           
		           intentGpsStatus.putExtra(MIN_SNR, minSNR);
		           intentGpsStatus.putExtra(MAX_SNR, minSNR);
		           intentGpsStatus.putExtra(MEAN_SNR, meanSNR);
		           sendBroadcast(intentGpsStatus);
			}
		};
	};

	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		
	}


	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}


	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}


	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
	


}
