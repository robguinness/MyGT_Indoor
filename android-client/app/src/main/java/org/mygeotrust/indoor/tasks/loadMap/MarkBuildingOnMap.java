package org.mygeotrust.indoor.tasks.loadMap;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fhc25.percepcion.osiris.mapviewer.R;
import com.fhc25.percepcion.osiris.mapviewer.dto.location.FeatureDTO;
import com.fhc25.percepcion.osiris.mapviewer.dto.location.GeometryDTO;
import com.fhc25.percepcion.osiris.mapviewer.model.location.Feature;
import com.fhc25.percepcion.osiris.mapviewer.model.location.Point;
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
import org.mygeotrust.indoor.ui.LandingActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by mis on 7/14/2016.
 */
public class MarkBuildingOnMap {
    private static final String TAG = MarkBuildingOnMap.class.toString();
    private static List<FeatureDTO> _featureDTOs = new ArrayList<>();

    public static List<FeatureDTO> getQueryResult(final IMapLoader observer, final MapsforgeMapView mapsforgeMapView){

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
            }

            @Override
            public void onResultsSucceeded(String response, int statusCode, Map<String, List<String>> header) {
                Log.d(TAG, response);

                //json string to java object converter
                ObjectMapper mapper = new ObjectMapper();

                try {
                    _featureDTOs = mapper.readValue(response, new TypeReference<List<FeatureDTO>>(){});

                    String jsonString = mapper.writeValueAsString(_featureDTOs);
                    Log.d(TAG, jsonString);

                    Context c = ((LandingActivity)observer).getApplicationContext();
                    Drawable d = c.getResources().getDrawable(R.drawable.userpos_icon);

                    Bitmap bitmap = AndroidGraphicFactory.convertToBitmap(d);

                    for (FeatureDTO obj : _featureDTOs) {
                        /*Iterator<Point> points = obj.getGeometryDTO();
                        while (points.hasNext()){
                            Point p = points.next();

                            Marker m = new Marker(new LatLong(p.getLatitude(), p.getLongitude()), bitmap, 0, 0);

                            mapsforgeMapView.getMapView().getLayerManager().getLayers().add(m);
                        }*/
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        return _featureDTOs;
    }
}
