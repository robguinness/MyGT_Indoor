package org.mygeotrust.indoor.tasks.loadMap;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import org.mygeotrust.indoor.tasks.bindService.IBindService;
import org.mygeotrust.service.initializer.MyGtServiceBinder;
import org.mygeotrust.service.manager.Listeners.IMyGtServiceBinder;

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

    //return values for callback
    Boolean mapLoadStatus;
    String message = "";  // should hold success or failure message as appropriate


    public LoadMap(IMapLoader observer) {
        //register the client to send update
        this.observer = observer;

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

    }


   public void notifyObserver()
   {
        observer.onMapLoaded(mapLoadStatus, message);
   }
}
