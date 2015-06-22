package de.sokol.lena.gardenapp.model;

import java.util.Date;

/**
 * Time Values are Values that hold a timestamp and are sortable by that timestamp
 *
 * Created by Lena on 25.02.2015.
 */
public abstract class TimeValue implements Comparable<TimeValue> {

    protected Date timeStamp;

    public TimeValue() {
        this.timeStamp = new Date(System.currentTimeMillis());
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    @Override
    public int compareTo(TimeValue another) {
        return this.getTimeStamp().compareTo(another.getTimeStamp());
    }
}
