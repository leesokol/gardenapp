package de.sokol.lena.gardenapp.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A Polygon is a VectorObject that consists of Geopositions.
 * <p/>
 * Created by lena on 15.02.15.
 */
public class Polygon extends VectorObject {
    //MAYBE DO: Rename this class for clearification purpose
    
    List<Geoposition> geopositions = new ArrayList<>();

    public Polygon(int vectorObjectID){
        super(vectorObjectID);
    }

    public Polygon() {
        super();
    }

    public void addPosition(Geoposition position){
        geopositions.add(position);
    }

    @Override
    public Geoposition[] getGeopositions() {
        Geoposition[] result = new Geoposition[geopositions.size()];
        return geopositions.toArray(result);
    }

    @Override
    public Geoposition getGeoposition() {
        if (geopositions.isEmpty())
            return null;
        else
            return geopositions.get(0);
    }

    public void replacePositions(List<Geoposition> positions){
        if(!positions.isEmpty()){
            geopositions.clear();
            geopositions.addAll(positions);
            GardenListeners.getInstance().informListenerChangedObejct();
        }
    }
}
