package de.sokol.lena.gardenapp.ui.Wrapper;

import java.util.List;

import de.sokol.lena.gardenapp.model.DummySensor;
import de.sokol.lena.gardenapp.model.DummySensorValue;
import de.sokol.lena.gardenapp.model.TimeValue;
import de.sokol.lena.gardenapp.ui.Attribute;

/**
 * Created by Lena on 02.05.2015.
 */
public class DummySensorWrapper extends AbstractVectorobjectWrapper {

    private static String NO_VALUE = "No Value";

    private DummySensor sensor;

    public DummySensorWrapper(DummySensor object) {
        super(object);
        this.sensor = object;
    }

    @Override
    public boolean hasTimeLine() {
        return true;
    }

    @Override
    public TimeValue getLastTimeValue() {
        List<DummySensorValue> list= sensor.getAllRelevantSensorValues();
        if(list.isEmpty()){
            return null;
        }
        return list.get(list.size()-1);
    }

    @Override
    public Attribute getLastTimeValueAsString() {
        if(getLastTimeValue() != null){
            return  new Attribute(Attribute.STRING_TYPE, "Tracking", "\n"+ getLastTimeValue().toString(), true);
        }
        return new Attribute(Attribute.STRING_TYPE, "Tracking", NO_VALUE, true);
    }
}
