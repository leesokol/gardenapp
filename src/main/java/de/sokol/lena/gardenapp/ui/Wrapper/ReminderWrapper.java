package de.sokol.lena.gardenapp.ui.Wrapper;

import java.util.Date;

import de.sokol.lena.gardenapp.model.Reminder;
import de.sokol.lena.gardenapp.model.VectorObject;
import de.sokol.lena.gardenapp.ui.Attribute;

/**
 * Created by lena on 23.04.15.
 */
public class ReminderWrapper extends AbstractVectorobjectWrapper {

    private static final String DESCRIPTION = "Description";
    private static final String DATE = "Date";

    private Reminder reminder;

    public ReminderWrapper(Reminder reminder) {
        super(reminder);
        getList().add(new Attribute(Attribute.STRING_TYPE,DESCRIPTION,reminder.getDescription(),false));
        getList().add(new Attribute(Attribute.DATE,DATE,reminder.getDate(),false));
        this.reminder = reminder;
    }

    @Override
    public void changeAttribute(Attribute attribute) {
        String attributeName = attribute.getAttributeName();
        if (attributeName.equals(DESCRIPTION)){
            reminder.setDescription((String)attribute.getValue());
        }else if (attributeName.equals(DATE)){
            reminder.setDate((Date)attribute.getValue());
        }
        super.changeAttribute(attribute);
    }
}
