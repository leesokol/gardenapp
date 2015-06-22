package de.sokol.lena.gardenapp.ui.Wrapper;

import de.sokol.lena.gardenapp.model.TimeValue;
import de.sokol.lena.gardenapp.ui.Attribute;

/**
 * A Wrapper allows a easier access and an display of the vector objects attributes.
 *
 * Created by Lena on 23.02.2015.
 */
public interface IVectorobjectWrapper {

    /**
     *
     * @return the attributes of the vector objects
     */
    public Iterable<Attribute> getAttributes();

    /**
     *
     * @param attribute changes the attribute on the original vector object or on a new time value
     */
    public void changeAttribute(Attribute attribute);

    /**
     *
     * @return true if the vector object hat time values
     */
    public boolean hasTimeLine();

    /**
     *
     * @return the last time value or null
     */
    public TimeValue getLastTimeValue();

    /**
     *
     * @return saves the changed Time Value in the TimeLine of the vector object
     */
    public boolean saveTimeValue();

    public Attribute getLastTimeValueAsString();

    /**
     *
     * @return the attributes of the TimeValue
     */
    public Iterable<Attribute> getTimeValueAttributes();

    /**
     *
     * @return true if the vector object implements the hasRemoteSensor interface
     */
    public boolean hasRemoteSensor();

    /**
     * Call the Remote sensor if the Sensor is a remote sensor
     */
    public void callRemoteSensor();
}
