package de.sokol.lena.gardenapp.ui;

import org.mapsforge.core.model.LatLong;

import de.sokol.lena.gardenapp.model.Geoposition;

/**
 * Some util functions
 *
 * Created by Lena on 18.02.2015.
 */
public class Util {

    public static LatLong GeopositionToLatLong(Geoposition geoposition){
        return new LatLong(geoposition.latitude,geoposition.longitude);
    }

    public static Geoposition LatLongToGeopostion(LatLong position){
        return  new Geoposition(position.latitude,position.longitude);
    }
}
