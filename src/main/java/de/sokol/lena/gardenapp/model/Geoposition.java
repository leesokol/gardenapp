package de.sokol.lena.gardenapp.model;

/**
 * A Point in the geographical context.
 * Created by Lena on 18.02.2015.
 */
public class Geoposition {

    public final double latitude;
    public final double longitude;

    public Geoposition(double latitude, double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
