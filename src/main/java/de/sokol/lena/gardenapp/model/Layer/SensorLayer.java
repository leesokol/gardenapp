package de.sokol.lena.gardenapp.model.Layer;

import java.util.ArrayList;
import java.util.List;

import de.sokol.lena.gardenapp.model.DummySensor;
import de.sokol.lena.gardenapp.model.Layer.VectorLayer;
import de.sokol.lena.gardenapp.model.Sensor;

/**
 * This Layer holds Sensors.
 *
 * Created by Lena on 06.04.2015.
 */
public class SensorLayer extends VectorLayer {
    public SensorLayer(String name) {
        super(name);
    }

    @Override
    public List<Class> getAllowedVectorObjects() {
        List<Class> list = new ArrayList<>();
        list.add(Sensor.class);
        list.add(DummySensor.class);
        return list;
    }
}
