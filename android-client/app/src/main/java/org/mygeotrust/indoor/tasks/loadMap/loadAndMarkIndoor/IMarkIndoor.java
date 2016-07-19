package org.mygeotrust.indoor.tasks.loadMap.loadAndMarkIndoor;

/**
 * This interface is used by LoadMap class to get update on
 * retrieving indoor map data and plotting them on the map.
 *
 * Created by Dr. Mahbubul Syeed on 15.7.2016.
 */
public interface IMarkIndoor {
    void onIndoorMarkerPlotted(Boolean status, String message);
}
