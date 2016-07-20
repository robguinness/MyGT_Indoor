package org.mygeotrust.indoor.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.fhc25.percepcion.osiris.mapviewer.R;
import com.fhc25.percepcion.osiris.mapviewer.ui.application.OsirisApplication;
import com.fhc25.percepcion.osiris.mapviewer.ui.controllers.FloorSelectorViewController;
import com.fhc25.percepcion.osiris.mapviewer.ui.overlays.mapsforge.MapsforgeOsirisOverlayManager;
import com.fhc25.percepcion.osiris.mapviewer.ui.overlays.themes.VisualTheme;
import com.fhc25.percepcion.osiris.mapviewer.ui.views.indoor.MapsforgeMapView;
import com.fhc25.percepcion.osiris.mapviewer.ui.views.indoor.level.FloorSelectorView;

import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mygeotrust.indoor.tasks.bindService.BindToMyGtService;
import org.mygeotrust.indoor.tasks.bindService.IBindService;
import org.mygeotrust.indoor.tasks.checkLocationSettings.CanGetLocationNew;
import org.mygeotrust.indoor.tasks.checkLocationSettings.ICanGetLocation;
import org.mygeotrust.indoor.tasks.detectProximity.DetermineIndoorOutdoorService;
import org.mygeotrust.indoor.tasks.loadIndoor.IIndoorMapLoader;
import org.mygeotrust.indoor.tasks.loadIndoor.LoadIndoorMap;
import org.mygeotrust.indoor.tasks.loadMap.IMapLoader;
import org.mygeotrust.indoor.tasks.loadMap.LoadMap;


public class LandingActivity extends AppCompatActivity implements ILandingActivity, IBindService, IMapLoader, ICanGetLocation, IIndoorMapLoader {


    private static final String TAG = LandingActivity.class.toString();

    /**
     * --------------------------------------
     * For Oseries indoor mapping
     * --------------------------------------
     */
    private MapsforgeMapView mapsforgeMapView;
    private FloorSelectorView floorSelectorView;
    private MapsforgeOsirisOverlayManager mapsforgeOsirisOverlayManager;
    private FloorSelectorViewController floorSelectorViewController;


    /**
     * -------------------------------
     * For proximity detection
     * -------------------------------
     */
    private static final boolean DEBUG_ON = false;
    private static final String CURRENT_STATUS_VALUE = "com.contextawareness.determineindooroutdoor.CURRENT_STATUS_VALUE";
    private static final String CURRENT_STATUS_PROB = "com.contextawareness.determineindooroutdoor.CURRENT_STATUS_PROB";
    private static final String WIFI_INFO_UPDATE = "com.contextawareness.determineindooroutdoor.WIFI_INFO_UPDATE";
    private static final String WIFI_POWER_LOW_EVENT = "com.contextawareness.determineindooroutdoor.WIFI_POWER_LOW_EVENT";
    private static final String TOTAL_POWER_VALUE = "com.contextawareness.determineindooroutdoor.TOTAL_POWER_VALUE";
    private static final String NUMBER_ACCESS_POINTS = "com.contextawareness.determineindooroutdoor.NUMBER_ACCESS_POINTS";
    private static final String CURRENT_STATUS_UPDATE = "com.contextawareness.determineindooroutdoor.CURRENT_STATUS_UPDATE";
    private static final String GPS_VALUE = "com.contextawareness.determineindooroutdoor.GPS_VALUE";
    private static final String GPS_UPDATE = "com.contextawareness.determineindooroutdoor.GPS_UPDATE";
    private BroadcastReceiver currentStatusReceiver;
    private BroadcastReceiver wifiInfoReceiver;

    //Temporary views to test indoor / outdoor proximity detection
    private TextView tvCurrentStatus;
    private TextView tvCurrentStatusProb;
    private TextView tvTotalPowerValue;
    private TextView tvNumberAPsValue;


    private Button btnLoadIndoor;
    private Button btnLoadIndoor2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidGraphicFactory.createInstance(this.getApplication());

        setContentView(R.layout.activity_landing);

        initViews();


       /*
        * This code is related to lifecycle event of the activity while onCreate is called, e.g., screen orientation change. to load the last known
        * state of the application.
        * Will be used in future.
       if (savedInstanceState != null) {
            LoadIndoorMap.loadFromSaveState(savedInstanceState);
        }*/


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

        //Temporary views to test indoor / outdoor proximity detection
        tvCurrentStatus = (TextView) findViewById(R.id.tvCurrentStatusValue);
        tvCurrentStatusProb = (TextView) findViewById(R.id.tvCurrentStatusProb);
        tvTotalPowerValue = (TextView) findViewById(R.id.tvTotalPowerValue);
        tvNumberAPsValue = (TextView) findViewById(R.id.tvNumberAPsValue);

        btnLoadIndoor = (Button) findViewById(R.id.load_indoor_btn);
        btnLoadIndoor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapsforgeOsirisOverlayManager.getDisplayer().clear();
                String appId = getResources().getString(R.string.osiris_app_id);

                OsirisApplication app = (OsirisApplication) getApplicationContext();
                app.indoorLayoutManager(appId); //initialize with given map id

                LoadIndoorMap.loadMap(LandingActivity.this); // loading corresponding indoor layout
            }
        });

        btnLoadIndoor2 = (Button) findViewById(R.id.load_indoor_btn_2);
        btnLoadIndoor2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapsforgeOsirisOverlayManager.getDisplayer().clear();
                String appId = getResources().getString(R.string.osiris_building_1);

                OsirisApplication app = (OsirisApplication) getApplicationContext();
                app.indoorLayoutManager(appId); //initialize with given map id

                LoadIndoorMap.loadMap(LandingActivity.this); // loading corresponding indoor layout
            }
        });

    }


    @Override
    public void onServiceBind(Boolean status) {
        if (status) {
            Log.d(TAG, "Bind Status: successful!");
            //now load the map
            new LoadMap(this, mapsforgeMapView);

        } else
            Log.e(TAG, "Bind Failed! Please check that MyGeoTrust is properly installed in your device and restart again. Thanks");
    }


    @Override
    public void onMapLoaded(Boolean status, String message) {
        if (status) {
            Log.e(TAG, "Map load status: " + status + ": " + message);

            // Check location update settings
            CanGetLocationNew.addObserver(this);
            Intent intent = new Intent(LandingActivity.this, CanGetLocationNew.class);
            startActivity(intent);
        } else
            Log.e(TAG, "Map load Failed! Error Message: " + message);
    }


    public void onGetLocationStatus(Boolean status, String message) {
        Log.e(TAG, "Location status: " + status + " Message: " + message);
        // load the indoor layout from server
//        LoadIndoorMap.loadMap(this); // TODO: parameter need to be refactored.
    }


    @Override
    public void onIndoorMapLoaded(Boolean status, String message) {
        if (status) {
            Log.e(TAG, "indoor Map load status: " + status + ":  " + message);
            startProximityDetector();

        } else
            Log.e(TAG, "Indoor Map load Failed! Error Message: " + message);
    }


    /**
     * This method starts the proximity detection service(s) upon successful completion
     * of the app lifecycle events.
     */
    private void startProximityDetector() {
        Log.e(TAG, "Proximity detector started!!");

        currentStatusReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent)//this method receives broadcast messages. Be sure to modify AndroidManifest.xml file in order to enable message receiving
            {
                tvCurrentStatus.setText(intent.getStringExtra(CURRENT_STATUS_VALUE));
                tvCurrentStatusProb.setText(String.valueOf(intent.getIntExtra(CURRENT_STATUS_PROB, 0)));
            }
        };

        wifiInfoReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent)//this method receives broadcast messages. Be sure to modify AndroidManifest.xml file in order to enable message receiving
            {
                int totalPower = intent.getIntExtra(TOTAL_POWER_VALUE, 0);
                if (totalPower != 0) {
                    tvTotalPowerValue.setText(String.valueOf(totalPower));
                } else {
                    tvTotalPowerValue.setText("low");
                }

                tvNumberAPsValue.setText("(" + String.valueOf(intent.getIntExtra(NUMBER_ACCESS_POINTS, 0)) + ")");

            }
        };


        IntentFilter currentStatusFilter = new IntentFilter(CURRENT_STATUS_UPDATE);
        registerReceiver(currentStatusReceiver, currentStatusFilter);

        IntentFilter wifiInfoFilter = new IntentFilter(WIFI_INFO_UPDATE);
        registerReceiver(wifiInfoReceiver, wifiInfoFilter);

        startService(new Intent(this, DetermineIndoorOutdoorService.class));
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


    /*
     * This block is to save the state of the application when it get destroyed. so that it can be loaded while reloading, e.g., orientation change.
     * Currently not in use.. but should be used in future.
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState, PersistableBundle outPersistentState) {
        Log.e(TAG, "onSaveInstanceState called!!");
        super.onSaveInstanceState(savedInstanceState, outPersistentState);

        mapsforgeMapView.saveState(savedInstanceState);
        mapsforgeOsirisOverlayManager.saveIntoBundle(savedInstanceState);
        floorSelectorView.saveStateToBundle(savedInstanceState);
    }*/

   /*
    * Code block in the on resume and on pause causing indoor map load failure.
     * We currently dont need this.
    @Override

    public void onResume() {
        super.onResume();

        mapsforgeOsirisOverlayManager.deepUpdate();
    }

   /* @Override
    public void onPause() {
        super.onPause();

        //LoadIndoorMap.persistInternalState();

        mapsforgeMapView.onPause();
    }*/

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //remove the map view and layer manager
        mapsforgeMapView.destroy();
        mapsforgeOsirisOverlayManager.destroy();

        //stop proximity detection services
        unregisterReceiver(currentStatusReceiver);
        unregisterReceiver(wifiInfoReceiver);
        stopService(new Intent(this, DetermineIndoorOutdoorService.class));
    }


}
