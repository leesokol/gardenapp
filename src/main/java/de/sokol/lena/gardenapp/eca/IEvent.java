package de.sokol.lena.gardenapp.eca;

/**
 * Interface for Event Types. Types of Events are described via an Identifier.
 *
 * Created by Lena on 10.03.2015.
 */
public interface IEvent {

    public static final int PLANT_CHANGED = 1;

    /**
     * A message may contain further informatione about the Event
     * @return
     */
    public String getEventMessage();

    /**
     * @return the source of the Event
     */
    public Object getSource();

    public int getType();
}
