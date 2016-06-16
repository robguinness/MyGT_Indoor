package org.mygeotrust.indoor.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.util.Log;

import com.fhc25.percepcion.osiris.mapviewer.R;
import com.fhc25.percepcion.osiris.mapviewer.manager.ApplicationManager;
import com.fhc25.percepcion.osiris.mapviewer.manager.IApplicationManagerProvider;
import com.fhc25.percepcion.osiris.mapviewer.model.states.api.IInternalStateManager;
import com.fhc25.percepcion.osiris.mapviewer.ui.views.indoor.MapsforgeMapView;

import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mygeotrust.indoor.tasks.bindService.IBindService;
import org.mygeotrust.indoor.tasks.bindService.BindToMyGtService;
import org.mygeotrust.indoor.tasks.checkLocationSettings.CanGetLocation;
import org.mygeotrust.indoor.tasks.checkLocationSettings.ICanGetLocation;
import org.mygeotrust.indoor.tasks.loadIndoor.IIndoorLoader;
import org.mygeotrust.indoor.tasks.loadIndoor.LoadIndoor;
import org.mygeotrust.indoor.tasks.loadMap.IMapLoader;
import org.mygeotrust.indoor.tasks.loadMap.LoadMap;

public class LandingActivity extends AppCompatActivity implements IBindService,
        IMapLoader,
        IIndoorLoader,
        ICanGetLocation {

    private static final String TAG = LandingActivity.class.toString();
    private MapsforgeMapView mapsforgeMapView;

    private ApplicationManager applicationManager;
    private IInternalStateManager internalStateManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidGraphicFactory.createInstance(this.getApplication());

        setContentView(R.layout.activity_landing);

        initViews();

        if (savedInstanceState != null) {
            internalStateManager.loadFromBundle(savedInstanceState);
        }

        new BindToMyGtService(getApplicationContext(), this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){
            case R.id.settings:
                break;
            case R.id.about:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();

        internalStateManager.persistInternalStateVariable();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        internalStateManager.saveToBundle(savedInstanceState);
    }

    /**
     *
     */
    private void initViews(){
        //indoor Code
        IApplicationManagerProvider applicationManagerProvider = (IApplicationManagerProvider) getApplication();
        applicationManager = applicationManagerProvider.getApplicationManager();
        internalStateManager = (IInternalStateManager) getApplication();

        // setting the map view
        mapsforgeMapView = (MapsforgeMapView) findViewById(R.id.map_view);
    }


    @Override
    public void onServiceBind(Boolean status) {
        if(status)
        {
            Log.e(TAG, "Bind Status: successful!");
            //now load the map
            new LoadMap(this, mapsforgeMapView);
        }
        else
            Log.e(TAG, "Bind Failed! Please check that MyGeoTrust is properly installed in your device and restart again. Thanks");
    }


    @Override
    public void onMapLoaded(Boolean status, String message) {
        if(status)
        {
            Log.e(TAG, "Map load status: " + message);
            // load the indoor layout from server
            new LoadIndoor(this, applicationManager);
        }
        else
            Log.e(TAG, "Map load Failed! Error Message: " + message);
    }

    @Override
    public void onIndoorLayoutLoad(Boolean status, String message) {
        if(status)
        {
            Log.e(TAG, "Map load status: " + message);
            //now check if location update is possible or not
            new CanGetLocation(getApplicationContext(), this);
        }
        else
            Log.e(TAG, "Map load Failed! Error Message: " + message);
    }


    @Override
    public void onGetLocationStatus(Boolean status, String message) {
        Log.e(TAG, "Location status: " + status + " Message: " + message);
    }


}
