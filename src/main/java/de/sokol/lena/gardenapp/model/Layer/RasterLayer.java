package de.sokol.lena.gardenapp.model.Layer;


import android.graphics.Bitmap;

/**
 * A RasterLayer saves informations in form of a raster containing values.
 * The Data in a RasterLayer can be returned as a Bitmap for display and saving purpose.
 * Created by Lena on 15.02.2015.
 */
public abstract class RasterLayer implements GardenLayer {

    private String name;
    //x and y values defining the resolution of the raster
    private int x_limit;
    private int y_limit;

    /**
     * Creates a RasterLayer with x_limit values on the x-Axis and y_limit values on the y axis.
     *
     * @param name
     * @param x_limit
     * @param y_limit
     */
    public RasterLayer(String name, int x_limit, int y_limit) {
        this.name = name;
        this.x_limit = x_limit;
        this.y_limit = y_limit;
    }

    @Override
    public String getName() {
        return name;
    }

    public int getX_limit() {
        return x_limit;
    }

    public int getY_limit() {
        return y_limit;
    }

    /**
     * Builds the raster according to the x and y limits.
     */
    protected abstract void buildRaster();

    /**
     * A Bitmap of the raster layer.
     *
     * @return
     */
    public abstract Bitmap toBitmap();

    @Override
    public String toString() {
        return getName();
    }
}
