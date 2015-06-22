package de.sokol.lena.gardenapp.model;

/**
 * Representing the Attributes of a Plant that change over time
 *
 * Created by Lena on 25.02.2015.
 */
public class PlantTimeValue extends TimeValue {

    private String stateDescription;
    private Float height;
    private Float width;

    public PlantTimeValue(){
        super();
    }

    public Float getHeight() {
        return height;
    }

    public Float getWidth() {
        return width;
    }

    public void setWidth(Float width) {
        this.width = width;
    }

    public String getStateDescription() {
        return stateDescription;
    }

    public void setHeight(Float height) {
        this.height = height;
    }

    public void setStateDescription(String stateDescription) {
        this.stateDescription = stateDescription;
    }

    @Override
    public String toString() {
        return "State Description: " + stateDescription + "\n"
                + "Height: " + height+ "\n"
                + "Width: " + width;
    }
}
