package de.sokol.lena.gardenapp.model;

/**
 * A Point is defined by one Geoposition.
 * Created by lena on 15.02.15.
 */
public class Point extends VectorObject {
    //=Member Variable=
    private Geoposition position;

    public Point() {
        super();
    }

    public Point(int vectorObjectID){
        super(vectorObjectID);
    }

    public Point(Geoposition position) {
        //MAYBE DO Do not allow null
        super();
        this.position = position;
    }

    public void setPosition(Geoposition position) {
        this.position = position;
        GardenListeners.getInstance().informListenerChangedObejct();
    }

    @Override
    public Geoposition[] getGeopositions() {
        return new Geoposition[]{position};
    }

    @Override
    public Geoposition getGeoposition() {
        return position;
    }
}
