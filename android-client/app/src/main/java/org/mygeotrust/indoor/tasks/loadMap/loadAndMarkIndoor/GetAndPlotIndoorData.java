package org.mygeotrust.indoor.tasks.loadMap.loadAndMarkIndoor;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fhc25.percepcion.osiris.mapviewer.R;
import com.fhc25.percepcion.osiris.mapviewer.dto.location.FeatureDTO;
import com.fhc25.percepcion.osiris.mapviewer.ui.views.indoor.MapsforgeMapView;

import org.mapsforge.core.graphics.Bitmap;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.layer.overlay.Marker;
import org.mygeotrust.indoor.network.async.AsyncRequestTask;
import org.mygeotrust.indoor.network.async.IAsyncResponse;
import org.mygeotrust.indoor.network.config.serverConfig.ServerEndPoints;
import org.mygeotrust.indoor.network.remote.Request;
import org.mygeotrust.indoor.network.remote.RequestTypes;
import org.mygeotrust.indoor.tasks.loadMap.IMapLoader;
import org.mygeotrust.indoor.ui.LandingActivity;
import org.mygeotrust.indoor.utils.dataStore.IndoorLocationData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mis on 7/14/2016.
 */
public class GetAndPlotIndoorData {
    private static final String TAG = GetAndPlotIndoorData.class.toString();

    public static void getQueryResult(final IMarkIndoor observer, final IMapLoader activityRef, final MapsforgeMapView mapsforgeMapView) {

        HashMap<String, String> params = new HashMap<>();
        params.put("layer", "FEATURES");
        params.put("pageSize", "2000");

        Request request = new Request(ServerEndPoints.HOST_ADDRESS, ServerEndPoints.PORT_NUMBER, RequestTypes.POST);
        request.setApiName(ServerEndPoints.API_SEARCH_ALL_FEATURE);
        request.setParams(params);

        AsyncRequestTask requestTask = new AsyncRequestTask();
        requestTask.executeTask(request, new IAsyncResponse() {
            @Override
            public void onResultsFailed(String response, int statusCode, Map<String, List<String>> header) {
                Log.e(TAG, response);

                //notify the result to the caller
                observer.onIndoorMarkerPlotted(false, response);
            }

            @Override
            public void onResultsSucceeded(String response, int statusCode, Map<String, List<String>> header) {
                Log.d(TAG, response);


                //parse and store the information
                StoreIndoorData.getInstance().parseAndStore(response);

                //plot marker on the map
                PlotMarkerOnMap.getInstance().plotMarker(activityRef,mapsforgeMapView);

                //notify the result to the caller
                observer.onIndoorMarkerPlotted(true, "Indoor data read and plot successful!");
            }
        });

    }
}
