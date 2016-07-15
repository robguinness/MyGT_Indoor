package syeed.mahbubul.proximitydetection;

import java.util.Iterator;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

public class LowRateGpsStatusListenerService extends Service implements LocationListener {

	private static final String TAG = "GpsStatusListenerService";
	private static final String GPS_STATUS_UPDATE = "com.contextawareness.determineindooroutdoor.GPS_STATUS_UPDATE";
	private static final String SAT_COUNT = "com.contextawareness.determineindooroutdoor.SAT_COUNT";
	private static final String SAT_FIX_COUNT = "com.contextawareness.determineindooroutdoor.SAT_FIX_COUNT";
	private static final String MIN_SNR = "com.contextawareness.determineindooroutdoor.MIN_SNR";
	private static final String MAX_SNR = "com.contextawareness.determineindooroutdoor.MAX_SNR";
	private static final String MEAN_SNR = "com.contextawareness.determineindooroutdoor.MEAN_SNR";

	private LocationManager locationManager;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}


	@Override
	public void onCreate() {
		Toast.makeText(this, "GPS Status Service Created", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onCreate");

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

	}

	@Override
	public void onDestroy() {
		Toast.makeText(this, "GPS Status Service Stopped", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onDestroy");
		locationManager.removeGpsStatusListener(mGpsStatusListener);
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

	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Toast.makeText(this, "GPS Status Service Started", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onStart");		
		
	    
	    // We want this service to continue running until it is explicitly
	    // stopped, so return sticky.
	    return START_STICKY;
	}
	
	// Define a listener that responds to GpsStatus updates
	private GpsStatus.Listener mGpsStatusListener = new GpsStatus.Listener() {
		
		public void onGpsStatusChanged(int event) {
			Log.d(TAG, "GPS status update received.");
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
		        	   Log.d(TAG, "hasAlmanac: " + hasAlmanac);
		           }
		           if (count>0){
		        	   meanSNR = meanSNR/count;
		           }
		           
		           
		           Log.d(TAG, "Sat count: " + count);
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
