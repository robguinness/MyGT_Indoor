package org.mygeotrust.indoor.ui;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.fhc25.percepcion.osiris.mapviewer.R;
import com.fhc25.percepcion.osiris.mapviewer.ui.application.OsirisApplication;
import com.fhc25.percepcion.osiris.mapviewer.ui.controllers.FloorSelectorViewController;
import com.fhc25.percepcion.osiris.mapviewer.ui.overlays.mapsforge.MapsforgeOsirisOverlayManager;
import com.fhc25.percepcion.osiris.mapviewer.ui.overlays.themes.VisualTheme;
import com.fhc25.percepcion.osiris.mapviewer.ui.views.indoor.MapsforgeMapView;
import com.fhc25.percepcion.osiris.mapviewer.ui.views.indoor.level.FloorSelectorView;
import com.yarolegovich.lovelydialog.LovelyInfoDialog;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mygeotrust.indoor.tasks.bindService.BindToMyGtService;
import org.mygeotrust.indoor.tasks.bindService.IBindService;
import org.mygeotrust.indoor.tasks.checkLocationSettings.CanGetLocationNew;
import org.mygeotrust.indoor.tasks.checkLocationSettings.ICanGetLocation;
import org.mygeotrust.indoor.tasks.detectIndoorOutdoor.IindoorOutdoorDetectorController;
import org.mygeotrust.indoor.tasks.detectIndoorOutdoor.IndoorOutdoorDetectorController;
import org.mygeotrust.indoor.tasks.loadIndoor.IIndoorMapLoader;
import org.mygeotrust.indoor.tasks.loadIndoor.LoadIndoorMap;
import org.mygeotrust.indoor.tasks.loadMap.IMapLoader;
import org.mygeotrust.indoor.tasks.loadMap.LoadMap;
import org.mygeotrust.indoor.utils.dialogue.Dialogs;
import org.mygeotrust.indoor.utils.dialogue.IDialogs;


public class LandingActivity extends AppCompatActivity implements ILandingActivity, IBindService, IMapLoader, ICanGetLocation, IIndoorMapLoader, IDialogs, IindoorOutdoorDetectorController {


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

    private ToggleButton btnInOutSet;

    //TODO:Temporary views to test indoor / outdoor proximity detection
    private TextView tvCurrentStatus;
    private TextView tvCurrentStatusProb;
    private TextView tvTotalPowerValue;
    private TextView tvNumberAPsValue;

    //TODO: buttons for testing purpose only
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

        btnInOutSet = (ToggleButton) findViewById(R.id.tgBtnInOutDetect);

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
            //this loads open street map and marks the buildings that have indoor mapping available.
            //this also saves the indoor data (partial) localy for proximity detection.
            new LoadMap(this, mapsforgeMapView);

        } else
            Log.e(TAG, "Bind Failed! Please check that MyGeoTrust is properly installed in your device and restart again. Thanks");
    }


    @Override
    public void onMapLoaded(Boolean status, String message) {
        if (status) {
            Log.e(TAG, "Map load status: " + status + ": " + message);

            //ask user to start indoor/outdoor detector.
            Dialogs.getInstance().showStandardDialog(this, "Start Indoor/Outdoor Detector",
                    "  Would you like to start auto indoor/outdoor detector? " +
                            "\n\n  Pressing Start will ask you to allow GPS and WiFI in Device and in MyGT Profile.", "Start", "Not Now");

        }
        //show error dialog
        else
            Dialogs.getInstance().showInfoDialog(this, "Map load Failed!", "Error: " + message, R.drawable.ic_warning, R.color.colorRed);
    }

    @Override
    public void onDialogOptionSelected(SelectionStatus status) {
        Dialogs.getInstance().unregisterObserver();

        //if user agrees to start the indoor/outdoor detector
        if(status == SelectionStatus.ok_pressed)
        {
            checkAndSetLocationSettings();
        }
    }

    private void checkAndSetLocationSettings() {
        CanGetLocationNew.addObserver(this);
        Intent intent = new Intent(LandingActivity.this, CanGetLocationNew.class);
        startActivity(intent);
    }

    public void onGetLocationStatus(Boolean status, String message) {

        //Start proximity detector
        //TODO: There should an explicit way (e.g., a button) to trun proximity detector on if user does not trun on GPS at this stage.
        //startIndoorOutdoorDetector();

        //TODO: Do we need to check WiFi status as well?!
        //if GPS is allowed in both Profile and in Device then start
        if (status)
            IndoorOutdoorDetectorController.getInstance().startIndoorOutdoorDetector(this, this);
        else {
            Dialogs.getInstance().showInfoDialog(this, " Cannot start indoor/outdoor Detector.", " GPS use Status: " + status + "\n Error: " + message, R.drawable.ic_warning, R.color.colorRed);
        }
    }





    /**
     * this method is invoked while there is a change in any of the following four parameter in determining
     * user location with respect to indoor / outdoor location
     *
     * @param locationStatus
     * @param probability
     * @param power
     * @param noOfAccessPoints
     */
    @Override
    public void onIndoorOutdoorStatusChanged(final IndoorOutdoorDetectorController.LocationStatus locationStatus, final String probability, final String power, final String noOfAccessPoints) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (locationStatus == IndoorOutdoorDetectorController.LocationStatus.outdoor)
                    tvCurrentStatus.setText("Outdoor");
                else if (locationStatus == IndoorOutdoorDetectorController.LocationStatus.indoor)
                    tvCurrentStatus.setText("Indoor");

                tvCurrentStatusProb.setText(probability);
                tvTotalPowerValue.setText(power);
                tvNumberAPsValue.setText(noOfAccessPoints);
            }
        });
    }



    //TODO: the following callback method in test mode now!
    /**
     * This call back method fires when a request for a indoor map loading returns.
     * <p/>
     * NOTE: Indoor map is loaded as per request (e.g., while user taps on a building which is indoor mapped) or
     * the proximity detector detects an indoor location that is already mapped and user wants to view the indoor map.
     *
     * @param status:  success / failure
     * @param message: message associated with the cause of failure or success.
     */
    @Override
    public void onIndoorMapLoaded(Boolean status, String message) {
        if (status) {
            Log.e(TAG, "indoor Map load status: " + status + ":  " + message);


        } else
            Log.e(TAG, "Indoor Map load Failed! Error Message: " + message);
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
     * ---------------
     * Helper methods
     * ---------------
     */


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
    protected void onDestroy() {
        super.onDestroy();

        //remove the map view and layer manager
        mapsforgeMapView.destroy();
        mapsforgeOsirisOverlayManager.destroy();

        //stop proximity detection services
        IndoorOutdoorDetectorController.getInstance().stopIndoorOutdoorDetector();
    }




 /*   private void showStandardDialog(String title, String message, String txtPositiveBtn, String txtNegBtn) {
        new LovelyStandardDialog(this)
                .setTopColorRes(R.color.wallet_holo_blue_light)
                .setButtonsColorRes(R.color.wallet_holo_blue_light)
                .setIcon(R.drawable.ic_info)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(txtPositiveBtn, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Toast.makeText(LandingActivity.this, "Starting Indoor/Outdoor detector...", Toast.LENGTH_SHORT).show();
                        setLocationSettings();
                    }
                })
                .setNegativeButton(txtNegBtn, null)
                .show();
    }*/

    /*private void showInfoDialog(String title, String msg, int icon, int color) {
        new LovelyInfoDialog(this)
                .setTopColorRes(color)
                .setIcon(icon)
                //This will add Don't show again checkbox to the dialog. You can pass any ID as argument
                //.setNotShowAgainOptionEnabled(0)
                .setTitle(title)
                .setMessage(msg)
                .show();
    }*/

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


}
