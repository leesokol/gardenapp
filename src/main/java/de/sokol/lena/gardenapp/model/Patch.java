package de.sokol.lena.gardenapp.model;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a patch in which a usually large amount of smaller plants are placed. For those plants a prepresenting plant is placed inside the patch.s
 *
 * Created by lena on 30.03.15.
 */
public class Patch extends Polygon {

    public Set<Plant> plantrepresentives = new HashSet<>();

    public Patch(int vectorObjectID){
        super(vectorObjectID);
    }

    public Patch(){
        super();
    }

    public void addAPlantReprensentive(Plant plant){
        plantrepresentives.add(plant);
        GardenListeners.getInstance().informListenerChangedObejct();
    }

    public Set<Plant> getPlantrepresentives() {
        return plantrepresentives;
    }

    @Override
    public String toString() {
        return "Patch: " + getVectorObjectID();
    }
}
