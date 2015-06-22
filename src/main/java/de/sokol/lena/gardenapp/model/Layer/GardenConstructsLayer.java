package de.sokol.lena.gardenapp.model.Layer;

import java.util.ArrayList;
import java.util.List;

import de.sokol.lena.gardenapp.model.Building;
import de.sokol.lena.gardenapp.model.GardenObjectCircle;
import de.sokol.lena.gardenapp.model.GardenObjectPolygon;
import de.sokol.lena.gardenapp.model.Layer.VectorLayer;

/**
 * A layer only displaying Garten Constructs: GardenObjectPolygon, GardenObjectCircle, Building.
 *
 * Created by Lena on 06.04.2015.
 */
public class GardenConstructsLayer extends VectorLayer {
    public GardenConstructsLayer(String name) {
        super(name);
    }

    @Override
    public List<Class> getAllowedVectorObjects() {
        List<Class> list = new ArrayList<>();
        list.add(GardenObjectPolygon.class);
        list.add(GardenObjectCircle.class);
        list.add(Building.class);
        return list;
    }
}
