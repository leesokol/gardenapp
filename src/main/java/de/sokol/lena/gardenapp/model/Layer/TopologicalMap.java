package de.sokol.lena.gardenapp.model.Layer;

import java.util.ArrayList;
import java.util.List;

import de.sokol.lena.gardenapp.model.Layer.VectorLayer;
import de.sokol.lena.gardenapp.model.TopologicalEdge;
import de.sokol.lena.gardenapp.model.TopologicalNode;
import de.sokol.lena.gardenapp.model.VectorObject;

/**
 * This Layer represents a topological map.
 *
 * Created by Lena on 27.02.2015.
 */
public class TopologicalMap extends VectorLayer {

    public TopologicalMap(String name) {
        super(name);
    }

    @Override
    public List<Class> getAllowedVectorObjects() {
        List<Class> list = new ArrayList<>();
        list.add(TopologicalNode.class);
        list.add(TopologicalEdge.class);
        return list;
    }

    @Override
    public boolean deleteVectorObject(VectorObject object) {
        if(object instanceof TopologicalNode){
            for(VectorObject obj: getVectorObjects()){
                if (obj instanceof TopologicalEdge){
                   TopologicalEdge edge = (TopologicalEdge)obj;
                   if (object.equals(edge.getNode1())||object.equals(edge.getNode2())){
                       return false;
                   }
                }
            }
            return super.deleteVectorObject(object);
        }
        return super.deleteVectorObject(object);
    }
}
