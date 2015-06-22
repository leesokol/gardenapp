package de.sokol.lena.gardenapp.model;

/**
 * Representing a landmark that differs from a normal landmark in that it has a surface
 *
 * Created by lena on 30.03.15.
 */
public abstract class LandmarkPolygon extends Polygon {
    public LandmarkPolygon(int vectorObjectID){
        super(vectorObjectID);
    }
}
