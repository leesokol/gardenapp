package de.sokol.lena.gardenapp.ui.Wrapper;

import de.sokol.lena.gardenapp.model.GardenObjectCircle;
import de.sokol.lena.gardenapp.ui.Attribute;

/**
 * Created by Lena on 06.04.2015.
 */
public class GardenObjectCircleWrapper extends AbstractVectorobjectWrapper {

    private static String GARDENOBJECTNAME = "Garden Object Name";
    private static String WIDTH = "Width";

    private GardenObjectCircle gardenObjectCircle;


    public GardenObjectCircleWrapper(GardenObjectCircle gardenObjectCircle) {
        super(gardenObjectCircle);
        getList().add(new Attribute(Attribute.STRING_TYPE, GARDENOBJECTNAME, gardenObjectCircle.getGardenObjectName(), false));
        getList().add(new Attribute(Attribute.FLOAT_TYPE, WIDTH, gardenObjectCircle.getWidth(), false));
        this.gardenObjectCircle = gardenObjectCircle;
    }

    @Override
    public void changeAttribute(Attribute attribute) {
        String attributeName = attribute.getAttributeName();
        if (attributeName.equals(GARDENOBJECTNAME)) {
            gardenObjectCircle.setGardenObjectName((String) attribute.getValue());
        } else if (attributeName.equals(WIDTH)) {
            gardenObjectCircle.setWidth((Float) attribute.getValue());
        }
        super.changeAttribute(attribute);
    }
}
