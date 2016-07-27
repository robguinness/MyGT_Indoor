package org.mygeotrust.indoor.tasks.detectIndoorOutdoor;

/**
 * Created by Dr. Mahbubul Syeed on 21.7.2016.
 */
public interface IindoorOutdoorDetectorController {
    void onIndoorOutdoorStatusChanged(IndoorOutdoorDetectorController.LocationStatus locationStatus, String probability, String power, String noOfAccessPoints);
}
