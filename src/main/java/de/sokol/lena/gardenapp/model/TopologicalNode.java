package de.sokol.lena.gardenapp.model;

/**
 * Represents a node in a topological map.
 *
 * Created by Lena on 27.02.2015.
 */
public class TopologicalNode extends de.sokol.lena.gardenapp.model.Point {

    public TopologicalNode(){
        super();
    }

    public TopologicalNode(int vectorObjectID){
        super(vectorObjectID);
    }

    //TODO:add a landmark to identify node or some other sort of identifcation
    public Landmark landmark;

    public Landmark getLandmark() {
        return landmark;
    }

    public void setLandmark(Landmark landmark) {
        this.landmark = landmark;
    }

    @Override
    public String toString() {
        return "Topological Node: " + getVectorObjectID();
    }
}
