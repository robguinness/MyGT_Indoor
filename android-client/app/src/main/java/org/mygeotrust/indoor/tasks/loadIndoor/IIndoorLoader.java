package org.mygeotrust.indoor.tasks.loadIndoor;

import android.os.Bundle;

import com.fhc25.percepcion.osiris.mapviewer.model.states.api.IInternalViewState;
import com.fhc25.percepcion.osiris.mapviewer.ui.controllers.FloorSelectorViewController;
import com.fhc25.percepcion.osiris.mapviewer.ui.overlays.OsirisOverlayManager;
import com.fhc25.percepcion.osiris.mapviewer.ui.views.indoor.MapsforgeMapView;
import com.fhc25.percepcion.osiris.mapviewer.ui.views.indoor.level.IFloorSelectorView;

/**
 * Created by mis on 6/16/2016.
 */
public interface IIndoorLoader {

    MapsforgeMapView getMapsforgeMapView();

    IFloorSelectorView getFloorSelectorView();

    OsirisOverlayManager getOsirisOverlayManager();

    FloorSelectorViewController getFloorSelectorViewController();

    void initFromViewState(IInternalViewState internalViewState);

    void onIndoorLayoutLoad(Boolean status, String message);
}
