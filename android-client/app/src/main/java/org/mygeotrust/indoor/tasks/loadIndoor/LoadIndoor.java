package org.mygeotrust.indoor.tasks.loadIndoor;

import android.util.Log;

import com.fhc25.percepcion.osiris.mapviewer.common.ICallback;
import com.fhc25.percepcion.osiris.mapviewer.common.errors.Failure;
import com.fhc25.percepcion.osiris.mapviewer.common.tasks.LinkedTasks;
import com.fhc25.percepcion.osiris.mapviewer.common.tasks.Task;
import com.fhc25.percepcion.osiris.mapviewer.common.tools.PolygonCreator;
import com.fhc25.percepcion.osiris.mapviewer.manager.ApplicationManager;
import com.fhc25.percepcion.osiris.mapviewer.model.indoor.BuildingGroup;
import com.fhc25.percepcion.osiris.mapviewer.model.location.MetaData;

/**
 * Created by mis on 6/16/2016.
 */
public class LoadIndoor {
    private static final String TAG = LoadIndoor.class.toString();
    private IIndoorLoader observer;
    private ApplicationManager applicationManager;
    private MetaData metaData;

    //return values for callback
    Boolean mapLoadStatus;
    String message = "";  // should hold success or failure message as appropriate

    public abstract class StartupManagerTask<ReturnData> extends Task<ReturnData> {

        @Override
        public void onTaskError(Failure error) {
            super.onTaskError(error);
            Log.e(TAG, "Error received:" + error.getMessage());
        }
    }

    private Task<MetaData> metadataTask = new StartupManagerTask<MetaData>() {

        @Override
        public void onMainTask(ICallback<MetaData> callback) {
            applicationManager.getMetadataRepository().getMetadata(callback);
        }

        @Override
        public void onResultTask(MetaData metaData) {
            LoadIndoor.this.metaData = metaData;
        }
    };

    private Task<BuildingGroup> indoorTask = new StartupManagerTask<BuildingGroup>() {

        @Override
        protected void onMainTask(ICallback<BuildingGroup> callback) {
            applicationManager.getBuildingsManager().getBuildings(PolygonCreator.createPolygon(metaData), callback);
        }
    };

    public LoadIndoor(IIndoorLoader observer, ApplicationManager applicationManager) {
        //register the client to send update
        this.observer = observer;
        this.applicationManager = applicationManager;

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
        //indoor Code
        LinkedTasks tasks = new LinkedTasks();

        tasks.then(metadataTask)
                .then(indoorTask);

        tasks.runTask();
    }

    public void notifyObserver()
    {
        observer.onIndoorLayoutLoad(mapLoadStatus, message);
    }
}
