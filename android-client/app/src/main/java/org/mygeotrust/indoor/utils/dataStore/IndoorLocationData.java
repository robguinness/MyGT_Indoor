package org.mygeotrust.indoor.utils.dataStore;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is the single point storage of indoor location data.
 *
 * Created by Dr. Mahbubul Syeed on 15.7.2016.
 */
public class IndoorLocationData {

    //its a singlaton class
    private static IndoorLocationData ourInstance = new IndoorLocationData();

    public static IndoorLocationData getInstance() {
        return ourInstance;
    }

    private IndoorLocationData() {
    }

    private static List<PropertiesAndGeometryDTO> indoorLocationDataList = new ArrayList<>();


    public final void addIndoorLocationData(String name, String type, String subtype, String description, String geometryType, String latitude, String longitude)
    {
        indoorLocationDataList.add(new PropertiesAndGeometryDTO(name, type, subtype, description, geometryType, latitude, longitude));
    }


    public final List<PropertiesAndGeometryDTO> getIndoorLocationDataList()
    {
        return indoorLocationDataList;
    }


    public class PropertiesAndGeometryDTO{
        String name;
        String type;
        String subtype;
        String description;

        String geometryType;
        String latitude;
        String longitude;

        public PropertiesAndGeometryDTO(String name, String type, String subtype, String description, String geometryType, String latitude, String longitude) {
            this.name = name;
            this.type = type;
            this.subtype = subtype;
            this.description = description;
            this.geometryType = geometryType;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }

        public String getSubtype() {
            return subtype;
        }

        public String getDescription() {
            return description;
        }

        public String getGeometryType() {
            return geometryType;
        }

        public String getLatitude() {
            return latitude;
        }

        public String getLongitude() {
            return longitude;
        }
    }
}
