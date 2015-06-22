package de.sokol.lena.gardenapp.model;

/**
 * Interface for Sensors that represents that the sensor is remote.
 *
 * Created by Lena on 11.04.2015.
 */
public interface hasRemoteSensor {

    /**
     * Makes an asynchron call to the sensor to retrieve sensor values.
     */
    public void makeAsyncSensorCall();
}
