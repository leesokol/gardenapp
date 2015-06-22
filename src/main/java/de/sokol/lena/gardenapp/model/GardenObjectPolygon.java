package de.sokol.lena.gardenapp.model;

/**
 * Represents a Garden Object that is shaped polygonal.
 *
 * Created by lena on 30.03.15.
 */
public class GardenObjectPolygon extends Polygon {

    private String gardenObjectName;

    public GardenObjectPolygon(int vectorObjectID){
        super(vectorObjectID);
    }

    public GardenObjectPolygon() {
        super();
    }

    public void setGardenObjectName(String gardenObjectName) {
        this.gardenObjectName = gardenObjectName;
        GardenListeners.getInstance().informListenerChangedObejct();
    }

    public String getGardenObjectName() {
        return gardenObjectName;
    }

    @Override
    public String toString() {
        String result= "Garden Object: ";
        if (gardenObjectName == null){
            return result +getVectorObjectID();
        }
        return result + gardenObjectName;
    }
}
