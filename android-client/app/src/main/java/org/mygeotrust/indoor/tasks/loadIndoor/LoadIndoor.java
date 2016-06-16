package org.mygeotrust.indoor.tasks.loadIndoor;

import com.fhc25.percepcion.osiris.mapviewer.common.ICallback;
import com.fhc25.percepcion.osiris.mapviewer.common.errors.Failure;
import com.fhc25.percepcion.osiris.mapviewer.common.tools.PolygonCreator;
import com.fhc25.percepcion.osiris.mapviewer.manager.ApplicationManager;
import com.fhc25.percepcion.osiris.mapviewer.model.indoor.BuildingGroup;
import com.fhc25.percepcion.osiris.mapviewer.model.location.MetaData;
import com.fhc25.percepcion.osiris.mapviewer.model.states.api.IInternalStateManager;

/**
 * Created by mis on 6/16/2016.
 */
public class LoadIndoor {
    private static final String TAG = LoadIndoor.class.toString();
    private IIndoorLoader observer;
    private ApplicationManager applicationManager;
    private IInternalStateManager internalStateManager;
    private MetaData metaData;

    //return values for callback
    Boolean mapLoadStatus;
    String message = "";  // should hold success or failure message as appropriate

    public LoadIndoor(IIndoorLoader observer, ApplicationManager applicationManager,
                      IInternalStateManager internalStateManager) {
        //register the client to send update
        this.observer = observer;
        this.applicationManager = applicationManager;
        this.internalStateManager = internalStateManager;

        //try to load map..
        loadIndoorLayout();

        //TODO: remove these.. only for testing
        mapLoadStatus = true;
        message = "successfully loaded the indoors!";

        //notify the observer
        notifyObserver();
    }

    private final void loadIndoorLayout()
    {
        applicationManager.getMetadataRepository().getMetadata(new ICallback<MetaData>() {
            @Override
            public void onFinish(Failure error, MetaData data) {
                //TODO check Failure
                LoadIndoor.this.metaData = data;

                applicationManager.getBuildingsManager().getBuildings(PolygonCreator.createPolygon(metaData), new ICallback<BuildingGroup>() {
                    @Override
                    public void onFinish(Failure error, BuildingGroup data) {
                        //TODO check Failure
                        observer.initFromViewState(internalStateManager.getViewState());
                    }
                });
            }
        });
    }

    public void notifyObserver()
    {
        observer.onIndoorLayoutLoad(mapLoadStatus, message);
    }
}
