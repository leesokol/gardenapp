package de.sokol.lena.gardenapp.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A Line is a VectorObject consisting of a number of connected Geoopositions
 * Created by lena on 15.02.15.
 */
public class Line extends VectorObject {

    List<Geoposition> geopositions = new ArrayList<>();

    public Line() {
        super();
        this.geopositions = new ArrayList<>();
    }

    public Line(int vectorObjectID){
         super(vectorObjectID);
    }


    /**
     * Creates a Line with the following startposition
     * @param startPosition Startposition of the line
     */
    public Line(Geoposition startPosition) {
        this();
        geopositions.add(startPosition);
    }

    /**
     * Add the position to the end of the line
     * @param position the position adding to the line
     */
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
