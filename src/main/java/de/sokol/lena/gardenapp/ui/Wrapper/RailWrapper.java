package de.sokol.lena.gardenapp.ui.Wrapper;

import de.sokol.lena.gardenapp.model.Rail;
import de.sokol.lena.gardenapp.ui.Attribute;

/**
 * Created by Lena on 11.04.2015.
 */
public class RailWrapper extends AbstractVectorobjectWrapper {
    private static final String MATERIAL = "Material";

    private Rail rail;

    public RailWrapper(Rail rail) {
        super(rail);
        getList().add(new Attribute(Attribute.STRING_TYPE,MATERIAL,rail.getMaterial(),false));
        this.rail = rail;
    }

    @Override
    public void changeAttribute(Attribute attribute) {
        String attributeName = attribute.getAttributeName();
        if(attributeName.equals(MATERIAL)){
            rail.setMaterial((String)attribute.getValue());
        }

        super.changeAttribute(attribute);
    }
}
