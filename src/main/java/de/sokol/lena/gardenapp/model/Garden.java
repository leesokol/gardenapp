package de.sokol.lena.gardenapp.model;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import de.sokol.lena.gardenapp.model.Layer.GardenLayer;

/**
 * Garden contains the different layers of elements describing the garden.
 * Created by Lena on 15.02.2015.
 */
public class Garden {
    //=Member variables=
    //BoundingBox of the area that contains the garden
    BoundaryBox boundary;
    //Layers of the Garden
    List<GardenLayer> layers;

    public Garden() {
        this.layers = new ArrayList<>();
    }

    public Garden(BoundaryBox boundingBox) {
        this.boundary = boundingBox;
        this.layers = new ArrayList<>();
    }

    public BoundaryBox getBoundary() {
        return boundary;
    }

    public void setBoundary(BoundaryBox boundary) {
        this.boundary = boundary;
        Log.d("BOUNDARY",boundary.toString());
        GardenListeners.getInstance().informListenerAddedBoundary();
    }

    public Iterable<GardenLayer> getLayers() {
        return layers;
    }

    public boolean deleteLayer(GardenLayer layer) {
        boolean result = this.layers.remove(layer);
        if (result) {
            GardenListeners.getInstance().informListenerDeletedLayer();
        }
        return result;
    }

    public boolean addLayer(GardenLayer layer){
        boolean result = false;
        if (!containsLayer(layer.getName())){
            result= layers.add(layer);
            GardenListeners.getInstance().informListenerAddedLayer();
        }
        return result;
    }

    public boolean containsLayer(String layername){
        boolean result = false;
        for (GardenLayer layer: getLayers()){
            if (layer.getName().equals(layername)){
                return true;
            }
        }
        return result;
    }

    public boolean moveLayerUp(GardenLayer layer) {
        boolean result = false;
        int index = layers.indexOf(layer);
        if (index < layers.size() - 1) {
            layers.remove(index);
            layers.add(index + 1, layer);
            result = true;
            GardenListeners.getInstance().informListenerMovedLayer();
        }
        return result;
    }

    public boolean moveLayerDown(GardenLayer layer) {
        boolean result = false;
        int index = layers.indexOf(layer);
        if (0 < index) {
            layers.remove(index);
            layers.add(index - 1, layer);
            result = true;
            GardenListeners.getInstance().informListenerMovedLayer();
        }
        return result;
    }
}
