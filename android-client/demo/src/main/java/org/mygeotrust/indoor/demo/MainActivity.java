package org.mygeotrust.indoor.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import org.mapsforge.map.android.graphics.AndroidGraphicFactory;

import fi.fgi.navi.imgc.maplib.common.ICallback;
import fi.fgi.navi.imgc.maplib.model.indoor.BuildingGroup;
import fi.fgi.navi.imgc.maplib.tasks.IIndoorLayoutTask;
import fi.fgi.navi.imgc.maplib.tasks.IndoorLayoutTask;
import fi.fgi.navi.imgc.maplib.views.indoor.level.FloorSelectorView;
import fi.fgi.navi.imgc.maplib.views.indoor.level.FloorSelectorViewController;
import fi.fgi.navi.imgc.maplib.views.mapsforge.MapsforgeMapView;
import fi.fgi.navi.imgc.maplib.views.overlays.mapsforge.MapsforgeOsirisOverlayManager;
import fi.fgi.navi.imgc.maplib.views.overlays.theme.VisualTheme;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.toString();
    private static final String API_KEY = "fgi_all";

    private MapsforgeMapView mapsforgeMapView;
    private MapsforgeOsirisOverlayManager mapsforgeOsirisOverlayManager;
    private FloorSelectorView floorSelectorView;
    private FloorSelectorViewController floorSelectorViewController;

    private Button btnLoadIndoor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidGraphicFactory.createInstance(getApplication());
        setContentView(R.layout.activity_main);

        // init mapsforge map
        initMapsforgeView();
        // init other views
        initIndoorMapControls();

        btnLoadIndoor = (Button) findViewById(R.id.btn_load_indoor);
        btnLoadIndoor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // load indoor layout of FGI building
                loadIndoorMap(API_KEY);
            }
        });
    }

    /**
     *
     */
    private void initMapsforgeView(){

        mapsforgeMapView = (MapsforgeMapView) findViewById(R.id.map_view);

        mapsforgeMapView.initMap();
        mapsforgeMapView.setMapPosition(60.160959, 24.546153);
        mapsforgeMapView.setZoomLevel((byte) 16);

    }

    /**
     *
     */
    private void initIndoorMapControls(){
        // map overlay for indoor layout
        mapsforgeOsirisOverlayManager = new MapsforgeOsirisOverlayManager(getResources(), mapsforgeMapView.getMapView(), new VisualTheme(this));

        // floor selector view initialize
        floorSelectorView = (FloorSelectorView) findViewById(R.id.floor_radio_group);
        floorSelectorViewController = new FloorSelectorViewController(floorSelectorView, mapsforgeOsirisOverlayManager);

        // adding necessary observers
        floorSelectorView.addObserver(floorSelectorViewController);
        mapsforgeMapView.addObserver(floorSelectorViewController);
    }

    /**
     *
     * @param buildingKey
     */
    private void loadIndoorMap(String buildingKey){
        final IIndoorLayoutTask indoorLayoutTask = new IndoorLayoutTask();
        indoorLayoutTask.processIndoorLayer(buildingKey, new ICallback<BuildingGroup>() {
            @Override
            public void onFinish(String error, BuildingGroup data) {
                // create the indoor layout
                mapsforgeOsirisOverlayManager.createIndoorLayer(data);
                // load the indoor layout on map
                indoorLayoutTask.loadIndoorLayerFromBuilding(data, floorSelectorView);
            }
        });
    }
}
