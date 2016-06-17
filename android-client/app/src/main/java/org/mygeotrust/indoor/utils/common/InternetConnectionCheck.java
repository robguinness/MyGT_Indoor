package org.mygeotrust.indoor.utils.common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * This helper class checks whether the device has internet connection or not.
 * <p/>
 * Created by Dr. Mahbubul Syeed on 17.6.2016.
 */
public class InternetConnectionCheck {

    private static ConnectivityManager cm;
    private static NetworkInfo activeNetwork;

    /**
     * checks whether the device has internet connection or not
     * True: if connected
     * False: otherwise
     *
     * @param context
     * @return
     */
    public static final boolean isInternetConnectionAvailable(Context context) {
        cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        return isConnected;
    }

    /**
     * Checks whether the device has WiFi connection available
     * True: if connected
     * False: otherwise.
     *
     * @param context
     * @return
     */
    public static final boolean isWiFiConnectionAvailable(Context context) {
        boolean isWiFi = false;
        if (isInternetConnectionAvailable(context))
            isWiFi = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;

        return isWiFi;
    }


    /**
     * Checks whether the device has wiMAX connection available
     * True: if connected
     * False: otherwise.
     *
     * @param context
     * @return
     */
    public static final boolean isWiMAXConnectionAvailable(Context context) {
        boolean isWiMAX = false;
        if (isInternetConnectionAvailable(context))
            isWiMAX = activeNetwork.getType() == ConnectivityManager.TYPE_WIMAX;

        return isWiMAX;
    }




    /**
     * Checks whether the device has mobile data connection connection available
     * True: if connected
     * False: otherwise.
     *
     * @param context
     * @return
     */
    public static final boolean isMobileDataConnectionAvailable(Context context) {
        boolean isMobileData = false;
        if (isInternetConnectionAvailable(context))
            isMobileData = activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE;

        return isMobileData;
    }
}
