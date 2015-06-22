package de.sokol.lena.gardenapp.model.Layer;

import android.graphics.Bitmap;

import de.sokol.lena.gardenapp.model.GardenListeners;

/**
 * Represents a Sallite Picture.
 *
 * Created by Lena on 01.03.2015.
 */
public class SatelliteLayer extends RasterLayer {

    private boolean visible = true;
    //the satellite picture
    private android.graphics.Bitmap bitmap;

    /**
     * Creates a RasterLayer with x_limit values on the x-Axis and y_limit values on the y axis.
     *
     * @param name
     * @param x_limit
     * @param y_limit
     */
    public SatelliteLayer(String name, int x_limit, int y_limit, Bitmap bitmap) {
        super(name, x_limit, y_limit);
        this.bitmap = bitmap;
        buildRaster();
    }

    @Override
    protected void buildRaster() {
        bitmap = Bitmap.createScaledBitmap(bitmap, getX_limit(), getY_limit(), false);
    }

    @Override
    public Bitmap toBitmap() {
        return bitmap;
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
        GardenListeners.getInstance().informListenerChangedVisibility();
    }
}
