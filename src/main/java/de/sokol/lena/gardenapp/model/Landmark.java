package de.sokol.lena.gardenapp.model;

/**
 * Representing a robotic landmark
 *
 * Created by lena on 30.03.15.
 */
public abstract class Landmark extends Point {

    public Landmark(){
        super();
    }

    public Landmark(int vectorObjectID){
        super(vectorObjectID);
    }
}
