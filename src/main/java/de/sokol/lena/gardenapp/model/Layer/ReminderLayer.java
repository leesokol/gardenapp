package de.sokol.lena.gardenapp.model.Layer;

import java.util.ArrayList;
import java.util.List;

import de.sokol.lena.gardenapp.model.Layer.VectorLayer;
import de.sokol.lena.gardenapp.model.Reminder;

/**
 * This Layer contains Reminders.
 *
 * Created by Lena on 24.04.2015.
 */
public class ReminderLayer extends VectorLayer {

    public ReminderLayer(String name) {
        super(name);
    }

    @Override
    public List<Class> getAllowedVectorObjects() {
        List<Class> list = new ArrayList<>();
        list.add(Reminder.class);
        return list;
    }

    //todo add reminder für den Alarmmanager
}
