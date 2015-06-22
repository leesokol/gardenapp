package de.sokol.lena.gardenapp.model;

import android.content.Context;
import android.graphics.Bitmap;

import de.sokol.lena.gardenapp.model.Layer.SatelliteLayer;
import de.sokol.lena.gardenapp.model.Layer.GardenConstructsLayer;
import de.sokol.lena.gardenapp.model.Layer.GardenLayer;
import de.sokol.lena.gardenapp.model.Layer.GroundLayer;
import de.sokol.lena.gardenapp.model.Layer.PlantLayer;
import de.sokol.lena.gardenapp.model.Layer.ReminderLayer;
import de.sokol.lena.gardenapp.model.Layer.RobotLayer;
import de.sokol.lena.gardenapp.model.Layer.SensorLayer;
import de.sokol.lena.gardenapp.model.Layer.TopologicalMap;
import de.sokol.lena.gardenapp.model.Layer.VectorLayer;

/**
 * A Facade for a garden object for certain functions
 *
 * Created by Lena on 12.03.2015.
 */
public class GardenFacade {

    private Garden garden;
    private Context context;

    public GardenFacade(Garden garden, Context context) {
        this.garden = garden;
        this.context = context;
    }

    public boolean addSateliteLayer(String layerName, Bitmap bitmap){
        return garden.addLayer(new SatelliteLayer(layerName,bitmap.getWidth(),bitmap.getHeight(),bitmap));
    }

    public boolean addLayer(String layerName, String layerType) {
        if (layerType.equals("VectorLayer")) {
            return garden.addLayer(new VectorLayer(layerName));
        }
        if (layerType.equals("PlantLayer")) {
            return garden.addLayer(new PlantLayer(layerName));
        }
        if (layerType.equals("Topological Map")) {
            return garden.addLayer(new TopologicalMap(layerName));
        }
        if (layerType.equals("SensorLayer")) {
            return garden.addLayer(new SensorLayer(layerName));
        }
        if (layerType.equals("GroundLayer")) {
            return garden.addLayer(new GroundLayer(layerName));
        }
        if (layerType.equals("RobotLayer")) {
            return garden.addLayer(new RobotLayer(layerName));
        }
        if (layerType.equals("GardenConstructsLayer")) {
            return garden.addLayer(new GardenConstructsLayer(layerName));
        }
        if (layerType.equals("ReminderLayer")){
            return garden.addLayer(new ReminderLayer(layerName));
        }
        return false;
    }

    public GardenLayer getLayer(String layerName) {
        for (GardenLayer layer : garden.getLayers()) {
            if (layer.getName().equals(layerName)) {
                return layer;
            }
        }
        return null;
    }

    public VectorLayer getVectorLayer(String layerName) {
        GardenLayer layer = getLayer(layerName);
        if (layer instanceof VectorLayer) {
            return (VectorLayer) layer;
        }
        return null;
    }
}
