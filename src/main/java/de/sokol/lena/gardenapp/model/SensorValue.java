package de.sokol.lena.gardenapp.model;

import java.util.Date;

/**
 * Represents the values of a specific sensor
 */
public abstract class SensorValue extends TimeValue{

    public SensorValue(Date timestamp){
        this.timeStamp = timestamp;
    }

}
