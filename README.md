# MyGT_Indoor

## Directory Strcture


    |MyGT_Indoor
      |_____ProximityDetection
      |_____android-client
      |_____resources
      

* **ProximityDetection** contains an Android project which demonstrate how to detect indoor or outdoor space based on user's current location. This indoor/outdoor detection is implemented using an algorithm provided by Rob.
* **android-client** contains the client Android application, which is using the Osiris server provided by osiris. The client code is modified by FGI developers.
* **resources** folder where inportant resource files are located, such as important instructions to use osiris REST APIs, FGI floor images, and OSM of FGI floor plans. The OSM of FGI floor plans are drawn using JOSM tool.

## 


    |android-client
      |_____app
      |_____demo
      |_____maplib
      
* **app** module contains the inital code base of osiris client provided by osiris. FGI developers have modified the client code to add some more features, such as detect indoor/outdoor based on current location, create and draw poi on map using osiris rest apis, show openstreet map as the base map
* **demo** module contains a simple demonstration of how to use the **maplib** developed by FGI developers.
* **maplib** is a submodule which contains all necessary codes to draw base map, communicate with server to fetch indoor information and draw indoor layout on top of the base map. [Github - maplib](https://github.com/robguinness/maplib): `https://github.com/robguinness/maplib`

## Server Preparation

The details of setting and running osiris server can be found here: [https://github.com/osiris-indoor/osiris](https://github.com/osiris-indoor/osiris)

### Server Build Steps

* Clone the osiris project in the server
* Build the project from osiris folder `./build.sh`
* Import and process the osm map (prepared using JOSM tool) under **bin** folder, `sudo ./import_map.sh building_key /path_to_my_map/map.osm`
* Run the osiris server, `./osiris.sh`

the `building_key` can be anything and associated with the `map.osm` file. So, please keep the `building_key` in mind, because the key will be used in the client code to fetch indoor data from server and load indoor layout on the map.

### Create an Indoor map (osm format) using JOSM

The following link describes how to prepare an indoor layout in JOSM tool: [https://github.com/osiris-indoor/sample-maps/wiki/How-to-map-a-building](https://github.com/osiris-indoor/sample-maps/wiki/How-to-map-a-building)

## Server Configuration

Osiris server has been installed in linode under **indoor** folder. There are two folders, i) osiris, contains the osiris server core and ii) maps, contains the indoor maps. Maven, mongodb and other necessary tools are installed. The linode server credentials are:

**user:** mislam@139.162.157.15, **pass:** xxxxxxxx (confidential)

To run the osiris server: `cd indoor/osiris/bin/` and then `./osiris.sh`

The indoor map of FGI has been already prepared and imported in the linode. The indoor map is `fgi_building_all_floors.osm` and the key of the map is `fgi_all`.

## How to Use maplib to create a simple indoor application in android

Please look at the example code: [https://github.com/robguinness/MyGT_Indoor/tree/dev/android-client/demo](https://github.com/robguinness/MyGT_Indoor/tree/dev/android-client/demo)

Specially, following files:

`MainActivity.Java` https://github.com/robguinness/MyGT_Indoor/blob/dev/android-client/demo/src/main/java/org/mygeotrust/indoor/demo/MainActivity.java

`activity_main.xml` https://github.com/robguinness/MyGT_Indoor/blob/dev/android-client/demo/src/main/res/layout/activity_main.xml

`build.gradle` https://github.com/robguinness/MyGT_Indoor/blob/dev/android-client/demo/build.gradle


If you would like to develop the demo application from scratch, then please add the `maplib` as submodule in your project's root folder and import maplib as module. The add `maplib` as a dependency in your application's `build.gradle` file.

If you want to change the server configuration, please change in following file: https://github.com/robguinness/maplib/blob/master/src/main/java/fi/fgi/navi/imgc/maplib/network/config/ServerEndPoints.java
