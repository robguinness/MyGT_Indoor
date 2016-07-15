package org.mygeotrust.indoor.tasks.loadMap;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;

import com.fhc25.percepcion.osiris.mapviewer.R;
import com.fhc25.percepcion.osiris.mapviewer.dto.location.FeatureDTO;
import com.fhc25.percepcion.osiris.mapviewer.ui.views.indoor.MapsforgeMapView;

import org.mapsforge.core.graphics.Bitmap;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.layer.Layer;
import org.mapsforge.map.layer.Layers;
import org.mapsforge.map.layer.overlay.Marker;
import org.mygeotrust.indoor.ui.LandingActivity;

import java.io.File;
import java.util.List;

/**
 * This class is responsible for loading the WorldMap (open street map) and
 * indoor location information (from Oseris server) to be plotted on the
 * world map.
 * <p/>
 * Created by Dr. Mahbubul Syeed on 15.6.2016.
 */
public class LoadMap {

    private static final String TAG = LoadMap.class.toString();

    private IMapLoader observer;
    private MapsforgeMapView mapsforgeMapView;

    //return values for callback
    Boolean mapLoadStatus;
    String message = "";  // should hold success or failure message as appropriate


    public LoadMap(IMapLoader observer, MapsforgeMapView mapsforgeMapView) {
        //register the client to send update
        this.observer = observer;
        this.mapsforgeMapView = mapsforgeMapView;

        //try to load map..
        loadOpenStreetMap();

        // "highlight" or "mark" bulidings which are indoor mapped
        markIndoorMappedBuilding();

        //TODO: remove these.. only for testing
        mapLoadStatus = true;
        message = "successfully loaded both the maps!";

        //notify the observer
        notifyObserver();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private final void loadOpenStreetMap() {
        // setting the map view
        mapsforgeMapView.setMapFile(new File(""));

        mapsforgeMapView.setMapPosition(60.160959, 24.546153);

        mapsforgeMapView.setZoomLevel((byte) 16);
    }

    private void markIndoorMappedBuilding(){
        MarkBuildingOnMap.getQueryResult(observer, mapsforgeMapView);

    }


    public void notifyObserver() {
        if (null != observer)
            observer.onMapLoaded(mapLoadStatus, message);
    }
}
