package org.mygeotrust.indoor.tasks.bindService;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import org.mygeotrust.service.initializer.MyGtServiceBinder;
import org.mygeotrust.service.manager.Listeners.IMyGtServiceBinder;

/**
 * This class bind to the MyGtServie stack and returns the status to
 * the client.
 * <p/>
 * Created by Dr. Mahbubul Syeed on 15.6.2016.
 */
public class BindToMyGtService extends Activity implements IMyGtServiceBinder {

    private static final String TAG = BindToMyGtService.class.toString();

    private Context applicationContext;
    private IBindService observer;

    public BindToMyGtService(Context applicationContext, IBindService observer) {
        //register the client to send update
        this.observer = observer;
        this.applicationContext = applicationContext;
        //try to bind
        bindToMyGtService();
    }

    private final void bindToMyGtService() {
        if (MyGtServiceBinder.isBound()) {
            Log.e(TAG, "YEE!! Already bounded.. I can do my work... :)");
        }
        //if not then bind with the service pls..
        else {
            try {
                //bind response will be received in onServiceBind(..) callback method
                MyGtServiceBinder.BindService(applicationContext, this);
            }
            //this exception is thrown if the context is other than application context
            catch (MyGtServiceBinder.BindServiceException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Returns status of service binding.
     *
     * @param b
     */
    @Override
    public void onServiceBind(boolean b) {
        Log.e(TAG, "Bind status: " + b);
        //notify the observer
        if (null != observer)
            observer.onServiceBind(b);
    }
}
