package de.sokol.lena.gardenapp.model;

/**
 * Represents a Garden Object that is circle shaped.
 *
 * Created by lena on 30.03.15.
 */
public class GardenObjectCircle extends Point{

    private String gardenObjectName;
    private Float width;

    public GardenObjectCircle(int vectorObjectID){
        super(vectorObjectID);
    }

    public GardenObjectCircle() {
        super();
    }

    public void setGardenObjectName(String gardenObjectName) {
        this.gardenObjectName = gardenObjectName;
    }

    public void setWidth(Float width) {
        this.width = width;
    }

    public Float getWidth() {
        return width;
    }

    public String getGardenObjectName() {
        return gardenObjectName;
    }

    @Override
    public String toString() {
        String result = "Garden Object: ";
        if(gardenObjectName == null){
            return result + getVectorObjectID();
        }
        return result + gardenObjectName;
    }
}
