package org.mygeotrust.indoor.ui;

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
import org.mygeotrust.indoor.tasks.checkLocationSettings.ICanGetLocation;
import org.mygeotrust.indoor.tasks.loadIndoor.IIndoorLoader;
import org.mygeotrust.indoor.tasks.loadIndoor.LoadIndoor;
import org.mygeotrust.indoor.tasks.loadMap.IMapLoader;
import org.mygeotrust.indoor.tasks.loadMap.LoadMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LandingActivity extends AppCompatActivity implements IBindService,
        IMapLoader,
        IIndoorLoader,
        ICanGetLocation {

    private static final String TAG = LandingActivity.class.toString();
    private MapsforgeMapView mapsforgeMapView;

    private ApplicationManager applicationManager;
    private IInternalStateManager internalStateManager;

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
            initFromViewState(internalStateManager.getViewState());
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
    public void onSaveInstanceState(Bundle savedInstanceState, PersistableBundle outPersistentState) {
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

    @Override
    public void onPause() {
        super.onPause();

        internalStateManager.persistInternalStateVariable();
        mapsforgeMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mapsforgeMapView.destroy();
        mapsforgeOsirisOverlayManager.destroy();
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
        floorSelectorView = (FloorSelectorView) findViewById(R.id.floor_radio_group);

        mapsforgeOsirisOverlayManager = new MapsforgeOsirisOverlayManager(getResources(), mapsforgeMapView.getMapView(),
                new VisualTheme(this));

        floorSelectorViewController = new FloorSelectorViewController(floorSelectorView, mapsforgeOsirisOverlayManager);

        floorSelectorView.addObserver(floorSelectorViewController);
        mapsforgeMapView.addObserver(floorSelectorViewController);
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
            new LoadIndoor(this, applicationManager, internalStateManager);
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
    public MapsforgeMapView getMapsforgeMapView() {
        return mapsforgeMapView;
    }

    @Override
    public FloorSelectorView getFloorSelectorView() {
        return floorSelectorView;
    }

    @Override
    public OsirisOverlayManager getOsirisOverlayManager() {
        return mapsforgeOsirisOverlayManager;
    }

    @Override
    public FloorSelectorViewController getFloorSelectorViewController() {
        return floorSelectorViewController;
    }

    private void initFromBuildings(BuildingGroup buildingGroup) {

        if (buildingGroup.getBuildings().size() == 1) {
            Building building = buildingGroup.getAllBuildings().iterator().next();

            final List<String> levels = new ArrayList<String>(building.getLevels());
            Collections.sort(levels, Collections.reverseOrder());

            floorSelectorView.post(new Runnable() {
                @Override
                public void run() {
                    floorSelectorView.load(levels);
                }
            });

        } else if (buildingGroup.getBuildings().size() == 2 && buildingGroup.getBuildings().containsKey("none")) {

            for (Building building : buildingGroup.getAllBuildings()) {

                if (!building.getName().equals("none")) {
                    final List<String> levels = new ArrayList<String>(building.getLevels());
                    Collections.sort(levels, Collections.reverseOrder());

                    floorSelectorView.post(new Runnable() {
                        @Override
                        public void run() {
                            floorSelectorView.load(levels);
                        }
                    });
                }
            }
        } else {
            Lgr.e(TAG, "Floor selector is not prepared for managing more than one building");
        }
    }

    @Override
    public void initFromViewState(IInternalViewState internalViewState) {
        mapsforgeOsirisOverlayManager.buildFromViewState(internalViewState);

        BuildingGroup buildingGroup = internalViewState.getBuildingGroup();
        initFromBuildings(buildingGroup);
    }

    @Override
    public void onGetLocationStatus(Boolean status, String message) {
        Log.e(TAG, "Location status: " + status + " Message: " + message);
    }


}
