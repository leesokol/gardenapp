package de.sokol.lena.gardenapp.ui.Wrapper;

import de.sokol.lena.gardenapp.model.ActionPoint;
import de.sokol.lena.gardenapp.ui.Attribute;

/**
 * Created by Lena on 06.04.2015.
 */
public class ActionPointWrapper extends AbstractVectorobjectWrapper {

    private static String ACTION = "Action";

    private ActionPoint actionPoint;

    public ActionPointWrapper(ActionPoint actionPoint) {
        super(actionPoint);
        getList().add(new Attribute(Attribute.ACTIONTYPE,ACTION,actionPoint.getAction(),false));
        this.actionPoint =actionPoint;
    }

    @Override
    public void changeAttribute(Attribute attribute) {
        String attributeName = attribute.getAttributeName();
        if (attributeName.equals(ACTION)){
            actionPoint.setAction((ActionPoint.Action)attribute.getValue());
        }
        super.changeAttribute(attribute);
    }
}
