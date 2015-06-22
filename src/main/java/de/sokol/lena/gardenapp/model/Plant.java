package de.sokol.lena.gardenapp.model;

import de.greenrobot.event.EventBus;
import de.sokol.lena.gardenapp.eca.PlantChangedEvent;

/**
 * Representing a Plant.
 *
 * Created by Lena on 24.02.2015.
 */
public class Plant extends de.sokol.lena.gardenapp.model.Point{

    private String plantname;
    private TimeLine<PlantTimeValue> timeLine;
    private String planttype;

    public Plant(int vectorObjectID){
        super(vectorObjectID);
        timeLine = new TimeLine<>();
    }

    public Plant(){
        super();
        timeLine = new TimeLine<>();
    }

    public Plant(Geoposition position) {
        super(position);
        timeLine = new TimeLine<>();
    }

    public TimeLine<PlantTimeValue> getTimeLine() {
        return timeLine;
    }

    public void setPlantname(String plantname) {
        this.plantname = plantname;
        EventBus.getDefault().post(new PlantChangedEvent(this));
    }

    public String getPlantname() {
        return plantname;
    }

    public String getPlanttype() {
        return planttype;
    }

    public void setPlanttype(String planttype) {
        this.planttype = planttype;
        EventBus.getDefault().post(new PlantChangedEvent(this));
    }

    @Override
    public String toString() {
        String result = "Plant: ";
        if (plantname == null){
            return result + getVectorObjectID();
        }
        return result + plantname;
    }
}
