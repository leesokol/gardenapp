package de.sokol.lena.gardenapp.ui.Wrapper;

import de.sokol.lena.gardenapp.model.GardenObjectPolygon;
import de.sokol.lena.gardenapp.ui.Attribute;

/**
 * Created by Lena on 11.04.2015.
 */
public class GardenObjectPolygonWrapper extends AbstractVectorobjectWrapper {
    private final static String GARDENOBJECTNAME = "Name";

    private GardenObjectPolygon polygon;

    public GardenObjectPolygonWrapper(GardenObjectPolygon polygon) {
        super(polygon);
        getList().add(new Attribute(Attribute.STRING_TYPE,GARDENOBJECTNAME,polygon.getGardenObjectName(),false));
        this.polygon = polygon;
    }

    @Override
    public void changeAttribute(Attribute attribute) {
        String attributeName = attribute.getAttributeName();
        if (attributeName.equals(GARDENOBJECTNAME)){
            polygon.setGardenObjectName((String)attribute.getValue());
        }
        super.changeAttribute(attribute);
    }
}
