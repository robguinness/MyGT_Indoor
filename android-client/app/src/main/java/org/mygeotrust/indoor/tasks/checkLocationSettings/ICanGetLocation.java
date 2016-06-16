package org.mygeotrust.indoor.tasks.checkLocationSettings;

/**
 * this callback method notifies the client whether GPS location can
 * be acquired by the client or not.
 *
 * Created by Dr. Mahbubul Syeed on 15.6.2016.
 */
public interface ICanGetLocation {
    void onGetLocationStatus(Boolean status, String message);
}
