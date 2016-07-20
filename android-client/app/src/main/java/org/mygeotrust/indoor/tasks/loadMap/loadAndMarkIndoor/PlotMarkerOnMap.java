package org.mygeotrust.indoor.tasks.loadMap.loadAndMarkIndoor;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.fhc25.percepcion.osiris.mapviewer.R;
import com.fhc25.percepcion.osiris.mapviewer.ui.views.indoor.MapsforgeMapView;

import org.mapsforge.core.graphics.Bitmap;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.layer.overlay.Marker;
import org.mygeotrust.indoor.tasks.loadMap.IMapLoader;
import org.mygeotrust.indoor.ui.LandingActivity;
import org.mygeotrust.indoor.utils.dataStore.IndoorLocationData;

/**
 * This method plot marker on the map.
 * Each marker reprsent the indoor location that is already mapped in our server.
 *
 * Created by Dr. Mahbubul Syeed on 15.7.2016.
 */
public class PlotMarkerOnMap {

    private final String TAG = PlotMarkerOnMap.class.getName();

    private static PlotMarkerOnMap ourInstance = new PlotMarkerOnMap();

    public static PlotMarkerOnMap getInstance() {
        return ourInstance;
    }

    private PlotMarkerOnMap() {
    }

    public final void  plotMarker(IMapLoader observer, MapsforgeMapView mapsforgeMapView)
    {
        Context c = ((LandingActivity) observer).getApplicationContext();
        Drawable d = c.getResources().getDrawable(R.drawable.apartment);

        Bitmap bitmap = AndroidGraphicFactory.convertToBitmap(d);

        for (int i = 0; i < IndoorLocationData.getInstance().getIndoorLocationDataList().size(); i++) {
            IndoorLocationData.PropertiesAndGeometryDTO propertiesAndGeometryDTO = IndoorLocationData.getInstance().getIndoorLocationDataList().get(i);
            Log.e(TAG, propertiesAndGeometryDTO.getName() + "-" + propertiesAndGeometryDTO.getLatitude() + ":" + propertiesAndGeometryDTO.getLongitude());

            Marker m = new Marker(new LatLong(Double.parseDouble(propertiesAndGeometryDTO.getLatitude()), Double.parseDouble(propertiesAndGeometryDTO.getLongitude())), bitmap, 0, 0);

            mapsforgeMapView.getMapView().getLayerManager().getLayers().add(m);
        }
    }
}
