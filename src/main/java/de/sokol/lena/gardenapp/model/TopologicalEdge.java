package de.sokol.lena.gardenapp.model;

/**
 * Represents a Edge in a topological map
 *
 * Created by Lena on 27.02.2015.
 */
public class TopologicalEdge extends Line {
    private TopologicalNode node1;
    private TopologicalNode node2;

    public TopologicalEdge(int vectorObjectID){
        super(vectorObjectID);
    }

    public TopologicalEdge() {
        super();
    }

    public void setNode1(TopologicalNode node1) {
        this.node1 = node1;
        updatePositions();
    }

    private void updatePositions() {
        geopositions.clear();
        if (node1 != null) {
            geopositions.add(node1.getGeoposition());
        }
        if (node2 != null) {
            geopositions.add(node2.getGeoposition());
        }
    }

    public void setNode2(TopologicalNode node2) {
        this.node2 = node2;
        updatePositions();
    }

    public TopologicalNode getNode1() {
        return node1;
    }

    public TopologicalNode getNode2() {
        return node2;
    }

    @Override
    public String toString() {
        return "Topological Edge: " + node1.toString() + "<-->" + node2.toString();
    }
}
