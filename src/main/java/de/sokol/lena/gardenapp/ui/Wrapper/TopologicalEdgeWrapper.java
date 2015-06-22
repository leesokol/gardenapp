package de.sokol.lena.gardenapp.ui.Wrapper;

import de.sokol.lena.gardenapp.model.TopologicalEdge;
import de.sokol.lena.gardenapp.model.TopologicalNode;
import de.sokol.lena.gardenapp.ui.Attribute;

/**
 * Created by Lena on 27.02.2015.
 */
public class TopologicalEdgeWrapper extends AbstractVectorobjectWrapper {

    private final String NODE1 = "Node 1";
    private final String NODE2 = "Node 2";

    private TopologicalEdge edge;

    public TopologicalEdgeWrapper(TopologicalEdge edge) {
        super(edge);
        this.edge = edge;
        getList().add(new Attribute(Attribute.NODE_TYPE,NODE1,edge.getNode1(),false));
        getList().add(new Attribute(Attribute.NODE_TYPE,NODE2,edge.getNode2(),false));
    }

    @Override
    public void changeAttribute(Attribute attribute) {
       if (attribute.getAttributeName().equals(NODE1)){
            edge.setNode1((TopologicalNode)attribute.getValue());
        }else if(attribute.getAttributeName().equals(NODE2)){
            edge.setNode2((TopologicalNode)attribute.getValue());
        }
        super.changeAttribute(attribute);
    }
}
