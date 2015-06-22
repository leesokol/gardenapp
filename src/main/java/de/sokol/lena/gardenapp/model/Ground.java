package de.sokol.lena.gardenapp.model;

/**
 * Created by lena on 30.03.15.
 * <p/>
 * Describing a ground surface
 */
public class Ground extends Polygon {

    //UntergrundTyp
    private GroundType groundType;


    public Ground(int vectorObjectID){
        super(vectorObjectID);
    }

    public Ground() {
        super();
        setGroundType(GroundType.NONE);
    }

    public GroundType getGroundType() {
        return groundType;
    }

    public void setGroundType(GroundType groundType) {
        if (groundType != this.groundType) {
            this.groundType = groundType;
            GardenListeners.getInstance().informListenerChangedObejct();
        }
    }

    //MAYBE DO Subclasses instead of enum
    public enum GroundType {
        NONE, GRASS, WATER, PAVEMENT, SOIL, SAND, ROBOT_ACCESSABLE, ROBOT_NOT_ACCESSABLE
    }

    @Override
    public String toString() {
        String result = "Ground: " + getGroundType();
        return  result;
    }
}
