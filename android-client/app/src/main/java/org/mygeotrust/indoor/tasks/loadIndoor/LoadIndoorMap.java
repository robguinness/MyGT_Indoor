package org.mygeotrust.indoor.tasks.loadIndoor;

import android.app.Activity;
import android.os.Bundle;

import com.fhc25.percepcion.osiris.mapviewer.common.ICallback;
import com.fhc25.percepcion.osiris.mapviewer.common.errors.Failure;
import com.fhc25.percepcion.osiris.mapviewer.common.log.Lgr;
import com.fhc25.percepcion.osiris.mapviewer.common.tools.PolygonCreator;
import com.fhc25.percepcion.osiris.mapviewer.manager.ApplicationManager;
import com.fhc25.percepcion.osiris.mapviewer.manager.IApplicationManagerProvider;
import com.fhc25.percepcion.osiris.mapviewer.model.indoor.Building;
import com.fhc25.percepcion.osiris.mapviewer.model.indoor.BuildingGroup;
import com.fhc25.percepcion.osiris.mapviewer.model.location.MetaData;
import com.fhc25.percepcion.osiris.mapviewer.model.states.api.IInternalStateManager;
import com.fhc25.percepcion.osiris.mapviewer.model.states.api.IInternalViewState;

import org.mygeotrust.indoor.ui.LandingActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by mis on 6/16/2016.
 */
public class LoadIndoorMap {

    private static final String TAG = LoadIndoorMap.class.toString();

    //for registering callback listener
    private static IIndoorMapLoader observer;

    //return values for callback
    private static Boolean mapLoadStatus;
    private static String message = "";  // should hold success or failure message as appropriate

    //reference to UIs
    private static Activity activity;

    //for indoor map
    private static IApplicationManagerProvider applicationManagerProvider;
    private static ApplicationManager applicationManager;
    private static IInternalStateManager internalStateManager;
    private static MetaData metaData;



    public static final void loadMap(IIndoorMapLoader clientObserver, String appId) {
        //register the client to send update
        observer = clientObserver;
        //store the activity context
        activity = (Activity)clientObserver;


        applicationManagerProvider = (IApplicationManagerProvider) activity.getApplication();
        applicationManager = applicationManagerProvider.getApplicationManager();
        applicationManager.setAppId(appId);
        internalStateManager = (IInternalStateManager) activity.getApplication();


        //try to load map..
        loadIndoorLayout();


        //TODO: set load status and message properly.
        mapLoadStatus = true;
        message = "successfully loaded the indoors!";

        //notify the observer
        notifyObserver();
    }


    private static final void loadIndoorLayout() {
        applicationManager.getMetadataRepository().getMetadata(new ICallback<MetaData>() {
            @Override
            public void onFinish(Failure error, MetaData data) {
                //TODO check Failure
                LoadIndoorMap.metaData = data;

                applicationManager.getBuildingsManager().getBuildings(PolygonCreator.createPolygon(metaData), new ICallback<BuildingGroup>() {
                    @Override
                    public void onFinish(Failure error, BuildingGroup data) {
                        //TODO check Failure
                        initFromViewState(internalStateManager.getViewState());
                    }
                });
            }
        });
    }


    public static final void initFromViewState(IInternalViewState internalViewState) {
        ((LandingActivity) observer).getMapsforgeOsirisOverlayManager().buildFromViewState(internalViewState);

        BuildingGroup buildingGroup = internalViewState.getBuildingGroup();
        initFromBuildings(buildingGroup);
    }

    private static void initFromBuildings(BuildingGroup buildingGroup) {

        if (buildingGroup.getBuildings().size() == 1) {
            Building building = buildingGroup.getAllBuildings().iterator().next();

            final List<String> levels = new ArrayList<String>(building.getLevels());
            Collections.sort(levels, Collections.reverseOrder());

            ((LandingActivity) observer).getFloorSelectorView().post(new Runnable() {
                @Override
                public void run() {
                    ((LandingActivity) observer).getFloorSelectorView().load(levels);
                }
            });

        } else if (buildingGroup.getBuildings().size() == 2 && buildingGroup.getBuildings().containsKey("none")) {

            for (Building building : buildingGroup.getAllBuildings()) {

                if (!building.getName().equals("none")) {
                    final List<String> levels = new ArrayList<String>(building.getLevels());
                    Collections.sort(levels, Collections.reverseOrder());

                    ((LandingActivity) observer).getFloorSelectorView().post(new Runnable() {
                        @Override
                        public void run() {
                            ((LandingActivity) observer).getFloorSelectorView().load(levels);
                        }
                    });
                }
            }
        } else {
            Lgr.e(TAG, "Floor selector is not prepared for managing more than one building");
        }
    }


    /**
     * Save map state to persistent memory.
     */
    public static final void persistInternalState()
    {
        if( null != internalStateManager)
        internalStateManager.persistInternalStateVariable();
    }


    /**
     * @param savedInstanceState
     */
    public static final void loadFromSaveState(Bundle savedInstanceState) {
        initFromViewState(internalStateManager.getViewState()); // TODO Is it needed??
        internalStateManager.loadFromBundle(savedInstanceState);
    }


    private static final void notifyObserver() {
        if (null != observer)
            observer.onIndoorMapLoaded(mapLoadStatus, message);

    }

}
