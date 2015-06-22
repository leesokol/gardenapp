package de.sokol.lena.gardenapp.model;

import java.util.SortedSet;
import java.util.TreeSet;

/**
 * A TimeLine holds Time Values sorted by time stamp
 *
 * Created by Lena on 25.02.2015.
 */
public class TimeLine<T extends TimeValue> {

    private SortedSet<T> timeValues;

    public TimeLine() {
        this.timeValues = new TreeSet<>();
    }

    public T getLastValue() {
        if (!timeValues.isEmpty()) {
            return timeValues.last();
        } else {
            return null;
        }
    }

    public SortedSet<T> getTimeValues() {
        return timeValues;
    }

    public boolean addTimeValue(T value) {
        boolean result = false;
        result= timeValues.add(value);
        GardenListeners.getInstance().informListenerChangedObejct();
        return result;
    }
}
