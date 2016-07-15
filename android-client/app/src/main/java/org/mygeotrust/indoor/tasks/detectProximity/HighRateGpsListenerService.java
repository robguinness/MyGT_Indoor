package org.mygeotrust.indoor.tasks.detectProximity;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

public class HighRateGpsListenerService extends Service implements LocationListener {

	private static final boolean DEBUG_ON = false;
	private static final String TAG = "GpsListenerService";
	private static final String GPS_VALUE = "com.contextawareness.determineindooroutdoor.GPS_VALUE";
	private static final String GPS_UPDATE = "com.contextawareness.determineindooroutdoor.GPS_UPDATE";
	private LocationManager locationManager;
	private Intent intentCurrentStatus;
	private Intent intentGpsUpdate;

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		Toast.makeText(this, "GPS Service Created", Toast.LENGTH_LONG).show();
		if (DEBUG_ON) Log.d(TAG, "onCreate");
		//Intent intentCurrentStatus = new Intent(CURRENT_STATUS_UPDATE);

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		if (DEBUG_ON) Log.d(TAG, "Location updates requested");
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

		intentCurrentStatus = new Intent(GPS_UPDATE);
		intentGpsUpdate = new Intent(GPS_UPDATE);

		//new Thread(new GpsRunnable()).start();

	}

	@Override
	public void onDestroy() {
		Toast.makeText(this, "GPS Service Stopped", Toast.LENGTH_LONG).show();
		if (DEBUG_ON) Log.d(TAG, "onDestroy");
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
		if (DEBUG_ON) Log.d(TAG, "Location update requests have been removed.");
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Toast.makeText(this, "GPS Service Started", Toast.LENGTH_LONG).show();
		if (DEBUG_ON) Log.d(TAG, "onStart");		
		
	    
	    // We want this service to continue running until it is explicitly
	    // stopped, so return sticky.
	    return START_STICKY;
	}
	
	
	private class GpsRunnable implements Runnable {

		public void run() {
			//Intent intentCurrentStatus = new Intent(GPS_UPDATE);
			for (int i=0; i<50;i++){
				if (DEBUG_ON) Log.d("run", "i = " + i);
				if (i%2 == 0){
					intentCurrentStatus.putExtra(GPS_VALUE, "outdoor");
				}
				else{
					intentCurrentStatus.putExtra(GPS_VALUE, "indoor");
				}
				sendBroadcast(intentCurrentStatus);
				SystemClock.sleep(1000);
			}
			
		}
		
	}

	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		if (DEBUG_ON) Log.d(TAG, "Location update received.");
		intentGpsUpdate.putExtra(GPS_VALUE, location);
		sendBroadcast(intentGpsUpdate);
	}

	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		
	}
}
