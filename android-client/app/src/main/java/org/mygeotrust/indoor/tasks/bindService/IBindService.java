package org.mygeotrust.indoor.tasks.bindService;

/**
 * this callback method notifies the client on the bind status
 * with MyGtService Stack.
 *
 * Created by Dr. Mahbubul Syeed on 15.6.2016.
 */
public interface IBindService {
    void onServiceBind(Boolean status);
}
