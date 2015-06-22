package de.sokol.lena.gardenapp.model;

/**
 * Represents a Building.
 *
 * Created by lena on 30.03.15.
 */
public class Building extends Polygon {

    private String buildingName;

    public Building(int vectorObjectID){
        super(vectorObjectID);
    }

    public Building() {
        super();
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public String getBuildingName() {
        return buildingName;
    }

    @Override
    public String toString() {
        String result = "Building: ";
        if (buildingName == null) {
            return result + getVectorObjectID();
        }
        return result + buildingName;
    }
}
