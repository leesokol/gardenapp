package de.sokol.lena.gardenapp.model;

/**
 * A VectorObject is either a Point, Line or Polygon and contains at least one Geoposition
 * describing its position and Form.
 * Created by lena on 15.02.15.
 */
public abstract class VectorObject {

    //=Member variables=
    private final int vectorObjectID;

    //VectorIDGenerator
    private static int vectorIDNext = 0;

    public VectorObject() {
        //Create a unique identifier
        this.vectorObjectID = VectorObject.setUpID();
    }

    public VectorObject(int vectorObjectID){
        this.vectorObjectID = vectorObjectID;
        vectorIDNext = Math.max(vectorIDNext,vectorObjectID+1);
    }

    private synchronized static int setUpID() {
        return vectorIDNext++;
    }

    public int getVectorObjectID() {
        return vectorObjectID;
    }

    /**
     * Returns all Geopositions defining the VectorObject in order of drawing direction.
     *
     * @return the geopositions in drawing order
     */
    public abstract Geoposition[] getGeopositions();

    /**
     * Returns the first Geoposition of the VectorObject.
     *
     * @return null if the Object has no position yet
     */
    public abstract Geoposition getGeoposition();

    @Override
    public String toString() {
        return "VectorObject: " + getVectorObjectID();
    }
}