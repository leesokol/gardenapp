package de.sokol.lena.gardenapp.model;

import java.util.Date;

/**
 * A Dummy Sensor Value.
 */
public class DummySensorValue extends SensorValue {

    private int value;

    public DummySensorValue(){
        super(new Date());
    }

    public DummySensorValue(Date timestamp) {
        super(timestamp);
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Value: " + value + "\n" + "Time: " + getTimeStamp().toString();
    }
}
