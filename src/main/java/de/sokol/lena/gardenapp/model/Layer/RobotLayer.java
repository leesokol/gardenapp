package de.sokol.lena.gardenapp.model.Layer;

import java.util.ArrayList;
import java.util.List;

import de.sokol.lena.gardenapp.model.ActionPoint;
import de.sokol.lena.gardenapp.model.Building;
import de.sokol.lena.gardenapp.model.GardenObjectCircle;
import de.sokol.lena.gardenapp.model.GardenObjectPolygon;
import de.sokol.lena.gardenapp.model.Landmark;
import de.sokol.lena.gardenapp.model.LandmarkPolygon;
import de.sokol.lena.gardenapp.model.Layer.VectorLayer;
import de.sokol.lena.gardenapp.model.RFIDLandmark;

/**
 * This Layer can contain all Geoobjects related to robotic localization: Landmark, RFIDLandmark,
 * ActionPoint, GardenObjectPolygon, Building, GardenObjectCircle, LandmarkPolygon.
 *
 * Created by Lena on 06.04.2015.
 */
public class RobotLayer extends VectorLayer {

    public RobotLayer(String name) {
        super(name);
    }

    @Override
    public List<Class> getAllowedVectorObjects() {
        List<Class> list = new ArrayList<>();
        list.add(Landmark.class);
        list.add(RFIDLandmark.class);
        list.add(ActionPoint.class);
        list.add(GardenObjectPolygon.class);
        list.add(Building.class);
        list.add(GardenObjectCircle.class);
        list.add(LandmarkPolygon.class);
        return list;
    }
}
