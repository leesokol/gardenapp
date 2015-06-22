package de.sokol.lena.gardenapp.ui.Wrapper;

import de.sokol.lena.gardenapp.model.Ground;
import de.sokol.lena.gardenapp.ui.Attribute;

/**
 * Created by Lena on 11.04.2015.
 */
public class GroundWrapper extends AbstractVectorobjectWrapper{

    private static final String GROUNDTYPE = "Ground Type";

    private Ground ground;

    public GroundWrapper(Ground ground) {
        super(ground);
        getList().add(new Attribute(Attribute.GROUNDTYPE,GROUNDTYPE,ground.getGroundType(),false));
        this.ground = ground;
    }

    @Override
    public void changeAttribute(Attribute attribute) {
        String attributeName = attribute.getAttributeName();
        if(attributeName.equals(GROUNDTYPE)){
            ground.setGroundType((Ground.GroundType)attribute.getValue());
        }
        super.changeAttribute(attribute);
    }
}
