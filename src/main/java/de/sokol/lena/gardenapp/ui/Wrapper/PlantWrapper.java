package de.sokol.lena.gardenapp.ui.Wrapper;

import java.util.ArrayList;
import java.util.List;

import de.sokol.lena.gardenapp.model.Plant;
import de.sokol.lena.gardenapp.model.PlantTimeValue;
import de.sokol.lena.gardenapp.model.TimeValue;
import de.sokol.lena.gardenapp.ui.Attribute;

/**
 * Created by Lena on 24.02.2015.
 */
public class PlantWrapper extends AbstractVectorobjectWrapper {

    private static String PLANTNAME = "Plant name";
    private static String STATE_DESCRIPTION = "State Description";
    private static String HEIGHT = "Height";
    private static String WIDTH = "Width";
    private static String NO_VALUE = "No Value";
    private static String PLANTTYPE = "Plant type";

    private Plant plant;
    private PlantTimeValue newPlantTimeValue;

    public PlantWrapper(Plant plant) {
        super(plant);
        getList().add(new Attribute(Attribute.STRING_TYPE, PLANTNAME, plant.getPlantname(), false));
        getList().add(new Attribute(Attribute.PLANTTYPE_TYPE, PLANTTYPE,plant.getPlanttype(),false));
        this.plant = plant;
    }

    @Override
    public void changeAttribute(Attribute attribute) {
        String attributeName = attribute.getAttributeName();
        if (attributeName.equals(PLANTNAME)) {
            plant.setPlantname((String) attribute.getValue());
        } else if (attributeName.equals(STATE_DESCRIPTION)) {
            getNewPlantTimeValue().setStateDescription((String) attribute.getValue());
        } else if (attributeName.equals(HEIGHT)) {
            getNewPlantTimeValue().setHeight(Float.valueOf((String) attribute.getValue()));
        } else if (attributeName.equals(WIDTH)){
            getNewPlantTimeValue().setWidth(Float.valueOf((String)attribute.getValue()));
        }else if (attributeName.equals(PLANTTYPE)){
            plant.setPlanttype((String) attribute.getValue());
        }
        super.changeAttribute(attribute);
    }

    private PlantTimeValue getNewPlantTimeValue() {
        if (newPlantTimeValue == null) {
            newPlantTimeValue = new PlantTimeValue();
        }
        return newPlantTimeValue;
    }

    @Override
    public boolean hasTimeLine() {
        return true;
    }

    @Override
    public Attribute getLastTimeValueAsString() {
        if (getLastTimeValue() != null) {
            return new Attribute(Attribute.STRING_TYPE, "Tracking", "\n" + getLastTimeValue().toString(), true);
        } else {
            return new Attribute(Attribute.STRING_TYPE, "Tracking", NO_VALUE, true);
        }
    }

    @Override
    public TimeValue getLastTimeValue() {
        return plant.getTimeLine().getLastValue();
    }

    @Override
    public boolean saveTimeValue() {
        boolean result = false;
        if (newPlantTimeValue != null) {
            plant.getTimeLine().addTimeValue(newPlantTimeValue);
            result = true;
            newPlantTimeValue = null;
        }
        return result;
    }

    @Override
    public Iterable<Attribute> getTimeValueAttributes() {
        List<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute(Attribute.STRING_TYPE, STATE_DESCRIPTION, null, false));
        attributes.add(new Attribute(Attribute.FLOAT_TYPE, HEIGHT, null, false));
        attributes.add(new Attribute(Attribute.FLOAT_TYPE, WIDTH,null,false));
        return attributes;
    }
}
