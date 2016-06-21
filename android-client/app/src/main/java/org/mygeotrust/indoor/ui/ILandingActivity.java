package org.mygeotrust.indoor.ui;

import com.fhc25.percepcion.osiris.mapviewer.ui.overlays.mapsforge.MapsforgeOsirisOverlayManager;
import com.fhc25.percepcion.osiris.mapviewer.ui.views.indoor.level.FloorSelectorView;

/**
 * Created by Dr. Mahbubul Syeed on 21.6.2016.
 */
public interface ILandingActivity {
    MapsforgeOsirisOverlayManager getMapsforgeOsirisOverlayManager();
    FloorSelectorView getFloorSelectorView();
}
