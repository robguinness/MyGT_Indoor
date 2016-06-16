package org.mygeotrust.indoor.tasks.loadMap;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.fhc25.percepcion.osiris.mapviewer.R;
import com.fhc25.percepcion.osiris.mapviewer.ui.views.indoor.MapsforgeMapView;

import org.mygeotrust.indoor.tasks.bindService.IBindService;
import org.mygeotrust.service.initializer.MyGtServiceBinder;
import org.mygeotrust.service.manager.Listeners.IMyGtServiceBinder;

import java.io.File;

/**
 * This class is responsible for loading the WorldMap (open street map) and
 * indoor location information (from Oseris server) to be plotted on the
 * world map.
 *
 * Created by Dr. Mahbubul Syeed on 15.6.2016.
 */
public class LoadMap{

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

        //TODO: remove these.. only for testing
        mapLoadStatus = true;
        message = "successfully loaded both the maps!";

        //notify the observer
        notifyObserver();
    }

    private final void loadOpenStreetMap()
    {
        // setting the map view
        mapsforgeMapView.setMapFile(new File(""));
        mapsforgeMapView.setMapPosition(60.161397, 24.738347);
        mapsforgeMapView.setZoomLevel((byte) 16);
    }


   public void notifyObserver()
   {
        observer.onMapLoaded(mapLoadStatus, message);
   }
}