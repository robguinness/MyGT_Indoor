package org.mygeotrust.indoor.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.fhc25.percepcion.osiris.mapviewer.R;
import com.fhc25.percepcion.osiris.mapviewer.common.log.Lgr;
import com.fhc25.percepcion.osiris.mapviewer.manager.ApplicationManager;
import com.fhc25.percepcion.osiris.mapviewer.manager.IApplicationManagerProvider;
import com.fhc25.percepcion.osiris.mapviewer.model.indoor.Building;
import com.fhc25.percepcion.osiris.mapviewer.model.indoor.BuildingGroup;
import com.fhc25.percepcion.osiris.mapviewer.model.states.api.IInternalStateManager;
import com.fhc25.percepcion.osiris.mapviewer.model.states.api.IInternalViewState;
import com.fhc25.percepcion.osiris.mapviewer.ui.controllers.FloorSelectorViewController;
import com.fhc25.percepcion.osiris.mapviewer.ui.overlays.OsirisOverlayManager;
import com.fhc25.percepcion.osiris.mapviewer.ui.overlays.mapsforge.MapsforgeOsirisOverlayManager;
import com.fhc25.percepcion.osiris.mapviewer.ui.overlays.themes.VisualTheme;
import com.fhc25.percepcion.osiris.mapviewer.ui.views.indoor.MapsforgeMapView;
import com.fhc25.percepcion.osiris.mapviewer.ui.views.indoor.level.FloorSelectorView;

import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mygeotrust.indoor.tasks.bindService.BindToMyGtService;
import org.mygeotrust.indoor.tasks.bindService.IBindService;
import org.mygeotrust.indoor.tasks.checkLocationSettings.CanGetLocation;
import org.mygeotrust.indoor.tasks.checkLocationSettings.CanGetLocationNew;
import org.mygeotrust.indoor.tasks.checkLocationSettings.ICanGetLocation;
import org.mygeotrust.indoor.tasks.loadIndoor.IIndoorMapLoader;
import org.mygeotrust.indoor.tasks.loadIndoor.LoadIndoorMap;
import org.mygeotrust.indoor.tasks.loadMap.IMapLoader;
import org.mygeotrust.indoor.tasks.loadMap.LoadMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LandingActivity extends AppCompatActivity implements ILandingActivity, IBindService,
        IMapLoader,
        IIndoorMapLoader,
        ICanGetLocation {

    private static final String TAG = LandingActivity.class.toString();
    private MapsforgeMapView mapsforgeMapView;

    //TODO: I commented it.
    /*private ApplicationManager applicationManager;
    private IInternalStateManager internalStateManager;*/

    private FloorSelectorView floorSelectorView;
    private MapsforgeOsirisOverlayManager mapsforgeOsirisOverlayManager;
    private FloorSelectorViewController floorSelectorViewController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidGraphicFactory.createInstance(this.getApplication());

        setContentView(R.layout.activity_landing);

        initViews();

        if (savedInstanceState != null) {
            LoadIndoorMap.loadFromSaveState(savedInstanceState);
        }

        new BindToMyGtService(getApplicationContext(), this);

    }


    /**
     * ----------------------------------------
     * Application Logic flow callback methods
     * ----------------------------------------
     */
    private void initViews() {

        // setting the map view
        mapsforgeMapView = (MapsforgeMapView) findViewById(R.id.map_view);
        //setting the floor selector view
        floorSelectorView = (FloorSelectorView) findViewById(R.id.floor_radio_group);

        mapsforgeOsirisOverlayManager = new MapsforgeOsirisOverlayManager(getResources(), mapsforgeMapView.getMapView(),
                new VisualTheme(this));

        floorSelectorViewController = new FloorSelectorViewController(floorSelectorView, mapsforgeOsirisOverlayManager);

        floorSelectorView.addObserver(floorSelectorViewController);
        mapsforgeMapView.addObserver(floorSelectorViewController);
    }


    @Override
    public void onServiceBind(Boolean status) {
        if (status) {
            Log.e(TAG, "Bind Status: successful!");
            //now load the map
            new LoadMap(this, mapsforgeMapView);
        } else
            Log.e(TAG, "Bind Failed! Please check that MyGeoTrust is properly installed in your device and restart again. Thanks");
    }


    @Override
    public void onMapLoaded(Boolean status, String message) {
        if (status) {
            Log.e(TAG, "Map load status: " + message);
            // load the indoor layout from server
            LoadIndoorMap.loadMap(this, this); // TODO: parameter need to be refactored.
        } else
            Log.e(TAG, "Map load Failed! Error Message: " + message);
    }

    @Override
    public void onIndoorMapLoaded(Boolean status, String message) {
        if (status) {
            Log.e(TAG, "Map load status: " + message);
            //now check if location update is possible or not
            CanGetLocationNew.addObserver(this);
            Intent intent =new Intent(LandingActivity.this, CanGetLocationNew.class);
            LandingActivity.this.startActivity(intent);
        } else
            Log.e(TAG, "Map load Failed! Error Message: " + message);
    }



    @Override
    public void onGetLocationStatus(Boolean status, String message) {
        Log.e(TAG, "Location status: " + status + " Message: " + message);
    }






    /**
     * -------------------------------------------------
     * Getter Methods
     * -------------------------------------------------
     */
    @Override
    public MapsforgeOsirisOverlayManager getMapsforgeOsirisOverlayManager() {
        return mapsforgeOsirisOverlayManager;
    }

    @Override
    public FloorSelectorView getFloorSelectorView() {
        return floorSelectorView;
    }









    /**
     * ----------------------------------------------
     * Activity LIFECYCLE METHODS
     * ----------------------------------------------
     */

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

        switch (id) {
            case R.id.settings:
                break;
            case R.id.about:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState, PersistableBundle outPersistentState) {
        Log.e(TAG, "onSaveInstanceState called!!");
        super.onSaveInstanceState(savedInstanceState, outPersistentState);

        mapsforgeMapView.saveState(savedInstanceState);
        mapsforgeOsirisOverlayManager.saveIntoBundle(savedInstanceState);
        floorSelectorView.saveStateToBundle(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

        mapsforgeOsirisOverlayManager.deepUpdate();
    }



    public void onPause() {
        super.onPause();


        LoadIndoorMap.persistInternalState();
        //LoadIndoorMap.getInternalStateManager().persistInternalStateVariable();
        mapsforgeMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mapsforgeMapView.destroy();
        mapsforgeOsirisOverlayManager.destroy();
    }


}
