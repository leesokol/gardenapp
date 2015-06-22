package de.sokol.lena.gardenapp.model;

/**
 * Boundaries of decribed by minimal and maximal Latitudes and Longitudes
 * Created by Lena on 18.02.2015.
 */
public class BoundaryBox {

    public final double minLatitude;
    public final double maxLatitude;
    public final double minLongitude;
    public final double maxLongitude;

    public BoundaryBox(Geoposition lowerCorner, Geoposition upperCorner){
        minLatitude = lowerCorner.latitude;
        maxLatitude = upperCorner.latitude;
        minLongitude = lowerCorner.longitude;
        maxLongitude = upperCorner.longitude;
    }

    public BoundaryBox(double minLatitude,double minLongitude, double maxLatitude,double maxLongitude){
        this.minLatitude = minLatitude;
        this.maxLatitude = maxLatitude;
        this.minLongitude = minLongitude;
        this.maxLongitude = maxLongitude;
    }

    /**
     * Returns the center of the Boundary Box
     * @return
     */
    public Geoposition getCenter(){
        double latitudeOffset = (this.maxLatitude - this.minLatitude) / 2;
        double longitudeOffset = (this.maxLongitude - this.minLongitude) / 2;
        return new Geoposition(this.minLatitude + latitudeOffset, this.minLongitude + longitudeOffset);
    }

    @Override
    public String toString() {
        return "minLat:"+minLatitude+" maxLat:"+maxLatitude+" minLong:"+minLongitude+" maxLong:"+maxLongitude;
    }
}
