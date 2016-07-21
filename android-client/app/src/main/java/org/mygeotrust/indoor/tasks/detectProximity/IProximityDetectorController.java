package org.mygeotrust.indoor.tasks.detectProximity;

/**
 * Created by Dr. Mahbubul Syeed on 21.7.2016.
 */
public interface IProximityDetectorController {
    void onIndoorOutdoorStatusChanged(ProximityDetectorController.LocationStatus locationStatus, String probability, String power, String noOfAccessPoints);
}
