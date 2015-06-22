package de.sokol.lena.gardenapp.eca;

import de.sokol.lena.gardenapp.model.Plant;

/**
 * This Event Type is used for changes to a Plant.
 *
 * Created by Lena on 10.03.2015.
 */
public class PlantChangedEvent implements IEvent {

    private Plant source;

    public PlantChangedEvent(Plant source) {
        this.source = source;
    }

    @Override
    public String getEventMessage() {
        return "PlantEventChanged:"+ source.toString();
    }

    @Override
    public Object getSource() {
        return source;
    }

    public int getType() {
        return IEvent.PLANT_CHANGED;
    }
}
