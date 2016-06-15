package org.mygeotrust.indoor.ui;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.fhc25.percepcion.osiris.mapviewer.R;

import org.mygeotrust.indoor.tasks.bindService.IBindService;
import org.mygeotrust.indoor.tasks.bindService.BindToMyGtService;
import org.mygeotrust.indoor.tasks.loadMap.IMapLoader;
import org.mygeotrust.indoor.tasks.loadMap.LoadMap;

public class LandingActivity extends Activity implements IBindService, IMapLoader {

    private static final String TAG = LandingActivity.class.toString();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        //bindToMyGtService();

        new BindToMyGtService(getApplicationContext(), this);

    }


    @Override
    public void onServiceBind(Boolean status) {
        if(status)
        {
            Log.e(TAG, "Bind Status: successful!");
            //now load the map
            new LoadMap(this);
        }
        else
            Log.e(TAG, "Bind Failed! Please check that MyGeoTrust is properly installed in your device and restart again. Thanks");
    }


    @Override
    public void onMapLoaded(Boolean status, String message) {
        if(status)
        {
            Log.e(TAG, "Map load status: " + message);
        }
        else
            Log.e(TAG, "Map load Failed! Error Message: " + message);
    }
}
