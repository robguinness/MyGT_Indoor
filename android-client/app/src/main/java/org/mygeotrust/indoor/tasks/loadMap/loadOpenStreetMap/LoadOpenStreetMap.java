package org.mygeotrust.indoor.tasks.loadMap.loadOpenStreetMap;

import com.fhc25.percepcion.osiris.mapviewer.ui.views.indoor.MapsforgeMapView;

import java.io.File;

/**
 * Created by Dr. Mahbubul Syeed on 15.7.2016.
 */
public class LoadOpenStreetMap {
    private static LoadOpenStreetMap ourInstance = new LoadOpenStreetMap();

    public static LoadOpenStreetMap getInstance() {
        return ourInstance;
    }

    private LoadOpenStreetMap() {
    }

    public final void initOpenStreetMap(MapsforgeMapView mapsforgeMapView)
    {
        // setting the map view
        mapsforgeMapView.setMapFile(new File(""));

        mapsforgeMapView.setMapPosition(60.160959, 24.546153);

        mapsforgeMapView.setZoomLevel((byte) 16);
    }
}
