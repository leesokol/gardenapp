package de.sokol.lena.gardenapp.ui.Wrapper;

import de.sokol.lena.gardenapp.model.Landmark;
import de.sokol.lena.gardenapp.model.TopologicalNode;
import de.sokol.lena.gardenapp.ui.Attribute;

/**
 * Created by Lena on 27.02.2015.
 */
public class TopologicalNodeWrapper extends AbstractVectorobjectWrapper {

    private static String LANDMARK = "Landmark";

    private TopologicalNode node;

    public TopologicalNodeWrapper(TopologicalNode node) {
        super(node);
        getList().add(new Attribute(Attribute.LANDMARK, LANDMARK, node.getLandmark(), false));
    }

    @Override
    public void changeAttribute(Attribute attribute) {
        String attributeName = attribute.getAttributeName();
        if (attributeName.equals(LANDMARK)) {
            node.setLandmark((Landmark) attribute.getValue());
        }
        super.changeAttribute(attribute);
    }
}
