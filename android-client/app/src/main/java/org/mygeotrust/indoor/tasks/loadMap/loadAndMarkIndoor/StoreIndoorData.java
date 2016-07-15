package org.mygeotrust.indoor.tasks.loadMap.loadAndMarkIndoor;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mygeotrust.indoor.utils.dataStore.IndoorLocationData;

/**
 * This class parse the JSON response representing the indoor location information and
 * store it in the local data store.
 *
 * Created by Dr. Mahbubul Syeed on 15.7.2016.
 */
public class StoreIndoorData {

    private static final String TAG = StoreIndoorData.class.toString();

    private static StoreIndoorData ourInstance = new StoreIndoorData();

    public static StoreIndoorData getInstance() {
        return ourInstance;
    }

    private StoreIndoorData() {
    }

    public final void parseAndStore(String response)
    {
        //parse the json response here
        try {
            //store the value in the local data store
            IndoorLocationData indoorLocationData = IndoorLocationData.getInstance();

            //the returned json is a Json array.
            JSONArray jArray = new JSONArray(response);

            //retrieve each object from the array
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject json = jArray.getJSONObject(i);

                JSONObject properties = (JSONObject) json.get("properties");
                Log.e(TAG, "OBJECTS: " + properties);

                JSONObject geometryDTO = (JSONObject) json.get("geometryDTO");
                Log.e(TAG, "OBJECTS: " + geometryDTO);

                //store the value in the local data store
                indoorLocationData.addIndoorLocationData(properties.getString("name"), properties.getString("type"), properties.getString("subtype"), properties.getString("description"),
                        geometryDTO.getString("type"), geometryDTO.getString("latitude"), geometryDTO.getString("longitude"));
            }

            //TODO: (Remove) test printing
            for (int i = 0; i < indoorLocationData.getIndoorLocationDataList().size(); i++) {
                IndoorLocationData.PropertiesAndGeometryDTO propertiesAndGeometryDTO = indoorLocationData.getIndoorLocationDataList().get(i);
                Log.e(TAG, propertiesAndGeometryDTO.getName() + "-" + propertiesAndGeometryDTO.getLatitude() + ":" + propertiesAndGeometryDTO.getLongitude());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
