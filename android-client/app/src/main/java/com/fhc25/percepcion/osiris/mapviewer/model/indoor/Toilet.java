package com.fhc25.percepcion.osiris.mapviewer.model.indoor;

import com.fhc25.percepcion.osiris.mapviewer.model.location.LineString;

import java.io.Serializable;

/**
 * Created by mis on 7/20/2016.
 */
public class Toilet extends BuildingArea implements Serializable {
    /**
     * Default constructor
     *
     * @param id
     * @param name
     * @param geometry
     * @param level
     */
    public Toilet(Long id, String name, LineString geometry, String level) {
        super(id, name, geometry, level);
    }
}
