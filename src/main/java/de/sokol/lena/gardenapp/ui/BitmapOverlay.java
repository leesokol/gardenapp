package de.sokol.lena.gardenapp.ui;

import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;

import org.mapsforge.core.graphics.Bitmap;
import org.mapsforge.core.graphics.Canvas;
import org.mapsforge.core.model.BoundingBox;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.core.model.Point;
import org.mapsforge.core.model.Rectangle;
import org.mapsforge.core.util.MercatorProjection;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.layer.Layer;

/**
 * An Mapsforge Overlay for displaying a bitmap over the map. The bitmap is cut and scaled according to two bounding coordinates
 *
 * Created by Lena on 01.03.2015.
 */
public class BitmapOverlay extends Layer {

    private android.graphics.Bitmap bitmap;
    private LatLong upLeftCorner;
    private LatLong downRightCorner;
    private Bitmap mapforgeBitmap;
    private Resources res;

    /**
     * This Layer displays a bitmap scaled to the boundaries defined by the to geographical positions.
     *
     * @param bitmap          the original bitmap
     * @param upLeftCorner
     * @param downRightCorner
     */
    public BitmapOverlay(android.graphics.Bitmap bitmap, LatLong upLeftCorner, LatLong downRightCorner,Resources res) {
        this.bitmap = bitmap;
        this.upLeftCorner = upLeftCorner;
        this.downRightCorner = downRightCorner;
        this.res =res;
    }

    @Override
    public void draw(BoundingBox boundingBox, byte zoomLevel, Canvas canvas, Point topLeftPoint) {
        if (this.bitmap == null || this.downRightCorner == null || this.upLeftCorner == null) {
            return;
        }
        /*MAYBE DO: Save Resources by only storing a Resource id, Filename of the bitmap so it is only
        * created if the bitmap is visible.
        */

        long mapSize = MercatorProjection.getMapSize(zoomLevel, this.displayModel.getTileSize());
        //Translating the position of the Object to CanvasPixel
        int pixelXupleft = (int) (MercatorProjection.longitudeToPixelX(this.upLeftCorner.longitude, mapSize) - topLeftPoint.x);
        int pixelYupleft = (int) (MercatorProjection.latitudeToPixelY(this.upLeftCorner.latitude, mapSize) - topLeftPoint.y);
        int pixelXdownright = (int) (MercatorProjection.longitudeToPixelX(this.downRightCorner.longitude, mapSize) - topLeftPoint.x);
        int pixelYdownright = (int) (MercatorProjection.latitudeToPixelY(this.downRightCorner.latitude, mapSize) - topLeftPoint.y);

        Rectangle bitmapRectangle = new Rectangle(pixelXupleft, pixelYupleft, pixelXdownright, pixelYdownright);
        Rectangle canvasRectangle = new Rectangle(0, 0, canvas.getWidth(), canvas.getHeight());
        //Checking if the bitmapRectangle is visible in the canvas region
        if (!canvasRectangle.intersects(bitmapRectangle)) {
            return;
        }
        //width and height of the final whole bitmap
        int width = pixelXdownright - pixelXupleft + 1;
        int height = pixelYdownright - pixelYupleft + 1;

        //part of the whole final bitmap that is visible
        int newpixelXupleft = pixelXupleft;
        int originalBitmapXupleft = 0;
        int newpixelYupleft = pixelYupleft;
        int originalBitmapYupleft = 0;
        int newpixelXdownright = pixelXdownright;
        int originalBitmapXdownright = bitmap.getWidth() - 1;
        int newpixelYdownright = pixelYdownright;
        int originalBitmapYdownright = bitmap.getHeight() - 1;
        if (pixelXupleft < 0) {
            int cutOffLeft = 0 - pixelXupleft;
            originalBitmapXupleft = (int) ((double) cutOffLeft / (double) width * (double) bitmap.getWidth());
            newpixelXupleft = (int) ((double) originalBitmapXupleft / (double) bitmap.getWidth() * (double) width) + pixelXupleft;
        }
        if (pixelYupleft < 0) {
            int cutOffTop = 0 - pixelYupleft;
            originalBitmapYupleft = (int) ((double) cutOffTop / (double) height * (double) bitmap.getHeight());
            newpixelYupleft = (int) ((double) originalBitmapYupleft / (double) bitmap.getHeight() * (double) height) + pixelYupleft;
        }
        if (pixelXdownright > canvas.getWidth()) {
            int cutOffRight = pixelXdownright - canvas.getWidth();
            int cutOffOriginalBitmap = (int) ((double) cutOffRight / (double) width * (double) bitmap.getWidth());
            originalBitmapXdownright = bitmap.getWidth() - 1 - cutOffOriginalBitmap;
            cutOffRight = (int) ((double) cutOffOriginalBitmap / (double) bitmap.getWidth() * (double) width);
            newpixelXdownright = pixelXdownright - cutOffRight;
        }
        if (pixelYdownright > canvas.getHeight()) {
            int cutOffDown = pixelYdownright - canvas.getHeight();
            int cutOffOriginalBitmap = (int)((double) cutOffDown /(double)height * (double)bitmap.getHeight());
            originalBitmapYdownright = bitmap.getHeight() - 1 - cutOffOriginalBitmap;
            cutOffDown = (int) ((double) cutOffOriginalBitmap/(double)bitmap.getHeight()* (double)height);
            newpixelYdownright = pixelYdownright - cutOffDown;
        }

        //Create new Bitmap for the calculated coordinates
        int newBitmapwidth = originalBitmapXdownright-originalBitmapXupleft+1;
        int newBitmapheight = originalBitmapYdownright-originalBitmapYupleft+1;
        android.graphics.Bitmap bitmapcut = android.graphics.Bitmap.createBitmap(bitmap,originalBitmapXupleft,originalBitmapYupleft,newBitmapwidth,newBitmapheight);

        //Create the new Width and Height for the new scaled bitmap
        int newWidth = newpixelXdownright-newpixelXupleft+1;
        int newHeight = newpixelYdownright-newpixelYupleft+1;

        //NEED RESOURCES
        Bitmap mapforgeBitmap = AndroidGraphicFactory.convertToBitmap(new BitmapDrawable(res,bitmapcut));
        mapforgeBitmap.scaleTo(newWidth,newHeight);
        canvas.drawBitmap(mapforgeBitmap, newpixelXupleft, newpixelYupleft);

    }

    @Override
    public synchronized void onDestroy() {
        if (this.mapforgeBitmap != null) {
            this.mapforgeBitmap.decrementRefCount();
        }
    }
}
