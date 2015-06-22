package de.sokol.lena.gardenapp.model;

import java.util.Date;

/**
 * Represents a reminder
 *
 * Created by lena on 22.04.15.
 */
public class Reminder extends Point {

    private String description;
    private Date date;

    public Reminder(int vectorObjectID){
        super(vectorObjectID);
    }

    public Reminder() {
        super();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        GardenListeners.getInstance().informListenerChangedObejct();
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
        GardenListeners.getInstance().informListenerChangedObejct();
    }

    @Override
    public String toString() {
        String result = "Reminder: " + getVectorObjectID();
        if (date != null && date.before(new Date())) {
            result = "DUE " + result;
        }
        return result;
    }
}
