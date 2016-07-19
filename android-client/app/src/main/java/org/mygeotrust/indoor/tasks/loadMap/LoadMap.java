package org.mygeotrust.indoor.tasks.loadMap;


import com.fhc25.percepcion.osiris.mapviewer.ui.views.indoor.MapsforgeMapView;

import org.mygeotrust.indoor.tasks.loadMap.loadAndMarkIndoor.GetAndPlotIndoorData;
import org.mygeotrust.indoor.tasks.loadMap.loadAndMarkIndoor.IMarkIndoor;
import org.mygeotrust.indoor.tasks.loadMap.loadOpenStreetMap.LoadOpenStreetMap;


/**
 * This class is responsible for loading the WorldMap (open street map) and
 * indoor location information (from Oseris server) to be plotted on the
 * world map.
 * <p/>
 * Created by Dr. Mahbubul Syeed on 15.6.2016.
 */
public class LoadMap implements IMarkIndoor {

    private static final String TAG = LoadMap.class.toString();

    private IMapLoader observer;
    private MapsforgeMapView mapsforgeMapView;

    //return values for callback
    Boolean mapLoadStatus = false;
    String message = "Error loading map!";  // should hold success or failure message as appropriate


    public LoadMap(IMapLoader observer, MapsforgeMapView mapsforgeMapView) {
        //register the client to send update
        this.observer = observer;
        this.mapsforgeMapView = mapsforgeMapView;

        //try to load map..
        LoadOpenStreetMap.getInstance().initOpenStreetMap(mapsforgeMapView);

        // "highlight" or "mark" bulidings which are indoor mapped
        GetAndPlotIndoorData.getQueryResult(this, observer, mapsforgeMapView);

    }



    @Override
    public void onIndoorMarkerPlotted(Boolean status, String message) {
        mapLoadStatus = status;
        this.message = message;
        notifyObserver();
    }



    public void notifyObserver() {
        if (null != observer)
            observer.onMapLoaded(mapLoadStatus, message);
    }


}
