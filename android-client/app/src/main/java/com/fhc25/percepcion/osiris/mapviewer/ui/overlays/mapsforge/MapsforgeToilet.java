package com.fhc25.percepcion.osiris.mapviewer.ui.overlays.mapsforge;

import com.fhc25.percepcion.osiris.mapviewer.model.indoor.Toilet;
import com.fhc25.percepcion.osiris.mapviewer.ui.overlays.themes.IndoorElementTheme;
import com.fhc25.percepcion.osiris.mapviewer.ui.overlays.themes.VisualTheme;

import org.mapsforge.map.layer.Layer;
import org.mapsforge.map.layer.overlay.Polygon;

/**
 * Created by mis on 7/20/2016.
 */
public class MapsforgeToilet extends MapsforgeVisualElement {
    private Polygon polygon;
    private Toilet toilet;

    public MapsforgeToilet(VisualTheme visualTheme, Toilet toilet) {
        this.toilet = toilet;
        IndoorElementTheme elementTheme = visualTheme.getIndoorTheme().getToiletTheme();
        polygon = getPolygon(this.toilet.getGeometry(), elementTheme);
    }

    @Override
    public Layer getLayer() {
        return polygon;
    }

    @Override
    public int getZDepth() {
        return 3;
    }

    @Override
    public void destroy() {
        polygon.onDestroy();
    }
}
