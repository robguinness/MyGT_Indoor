package org.mygeotrust.indoor.tasks.loadMap;

/**
 * this callback method notifies the client on the Oseris map
 * load status to the client.
 *
 * Created by Dr. Mahbubul Syeed on 15.6.2016.
 */
public interface IMapLoader {
    void onMapLoaded(Boolean status, String message);
}
