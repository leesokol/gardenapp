package de.sokol.lena.gardenapp.ui;

import android.graphics.drawable.Drawable;
import android.util.Log;

import org.mapsforge.core.graphics.Bitmap;
import org.mapsforge.core.graphics.Color;
import org.mapsforge.core.graphics.Paint;
import org.mapsforge.core.graphics.Style;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.view.MapView;
import org.mapsforge.map.layer.Layer;
import org.mapsforge.map.layer.overlay.Circle;
import org.mapsforge.map.layer.overlay.Marker;
import org.mapsforge.map.layer.overlay.Polyline;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.sokol.lena.gardenapp.R;
import de.sokol.lena.gardenapp.model.ActionPoint;
import de.sokol.lena.gardenapp.model.BoundaryBox;
import de.sokol.lena.gardenapp.model.Building;
import de.sokol.lena.gardenapp.model.Garden;
import de.sokol.lena.gardenapp.model.GardenObjectCircle;
import de.sokol.lena.gardenapp.model.GardenObjectPolygon;
import de.sokol.lena.gardenapp.model.Geoposition;
import de.sokol.lena.gardenapp.model.Ground;
import de.sokol.lena.gardenapp.model.Layer.GardenLayer;
import de.sokol.lena.gardenapp.model.Layer.RasterLayer;
import de.sokol.lena.gardenapp.model.Layer.VectorLayer;
import de.sokol.lena.gardenapp.model.Line;
import de.sokol.lena.gardenapp.model.Patch;
import de.sokol.lena.gardenapp.model.Plant;
import de.sokol.lena.gardenapp.model.PlantTimeValue;
import de.sokol.lena.gardenapp.model.Point;
import de.sokol.lena.gardenapp.model.Polygon;
import de.sokol.lena.gardenapp.model.RFIDLandmark;
import de.sokol.lena.gardenapp.model.Rail;
import de.sokol.lena.gardenapp.model.Reminder;
import de.sokol.lena.gardenapp.model.TopologicalEdge;
import de.sokol.lena.gardenapp.model.TopologicalNode;
import de.sokol.lena.gardenapp.model.VectorObject;

/**
 * Translates a garden object in Mapsforge specific objects.
 * Created by Lena on 16.02.2015.
 */
public class MapsForgeView {
    // How to map the GardenLayers to Layers in Mapsforge:
    // <VectorObjectsID, Mapsforge-LayerObject>

    Garden garden;
    MapView mapView;
    Map<Integer, Layer> layers;

    public MapsForgeView(Garden garden, MapView mapView) {
        this.garden = garden;
        this.mapView = mapView;
        this.layers = new HashMap<>();
    }

    public void setGarden(Garden garden){
        this.garden = garden;
    }

    /**
     * Draws the Garden for the first time. Setting up the Layers. Setting Boundaries of the Garden.
     */
    public void setUpGarden() {
        //Drawing bounds of the Garden
        if (garden.getBoundary() != null) {
            Layer bounds = createFromBounds(this.garden.getBoundary());
            mapView.getLayerManager().getLayers().add(bounds);
        }
        layers.clear();
        //Drawing the other Layers
        for (GardenLayer layer : garden.getLayers()) {
            if (layer.isVisible()) {
                if (layer instanceof VectorLayer) {
                    VectorLayer layer1 = (VectorLayer) layer;
                    List<Layer> overlays = new ArrayList<>();
                    for (VectorObject object : layer1.getVectorObjects()) {
                        if (object instanceof Point) {
                            Layer l = createPoint((Point) object);
                            overlays.add(l);
                            layers.put(object.getVectorObjectID(), l);
                        } else if (object instanceof Line) {
                            Layer l = createLine((Line) object);
                            overlays.add(l);
                            layers.put(object.getVectorObjectID(), l);
                        } else if (object instanceof Polygon) {
                            Layer l = createPolygon((Polygon) object);
                            overlays.add(l);
                            layers.put(object.getVectorObjectID(), l);
                        }
                    }
                    mapView.getLayerManager().getLayers().addAll(overlays);
                } else {
                    RasterLayer rasterLayer = (RasterLayer) layer;
                    LatLong upLeft = new LatLong(garden.getBoundary().maxLatitude, garden.getBoundary().minLongitude);
                    LatLong downRight = new LatLong(garden.getBoundary().minLatitude, garden.getBoundary().maxLongitude);
                    BitmapOverlay overlay = new BitmapOverlay(rasterLayer.toBitmap(), upLeft, downRight,mapView.getResources());
                    mapView.getLayerManager().getLayers().add(overlay);
                }
            }
        }
    }

    /**
     * Make a Mapsforge Layer for a point
     * @param point
     * @return
     */
    private Layer createPoint(Point point) {
        AndroidGraphicFactory graphicFactory = AndroidGraphicFactory.INSTANCE;
        Paint paint = graphicFactory.createPaint();
        paint.setColor(getPaintforVectorObject(point));
        Log.d("CIRCLE", "Draw in black");
        float width = getCircleWidth(point);
        if (point instanceof Reminder){
            Reminder r = (Reminder) point;
            if(r.getDate()!= null && r.getDate().before(new Date())){
                Drawable d = mapView.getResources().getDrawable(R.drawable.reminder);
                Bitmap bitmap = AndroidGraphicFactory.convertToBitmap(d);
                bitmap.incrementRefCount();
                return  new Marker(Util.GeopositionToLatLong(r.getGeoposition()),bitmap,0,0);
            }
        }
        return new Circle(Util.GeopositionToLatLong(point.getGeoposition()), width, paint, paint);
    }

    /**
     * Returns a Mapsforge Layer for the Line
     * @param line
     * @return
     */
    private Layer createLine(Line line) {
        AndroidGraphicFactory graphicFactory = AndroidGraphicFactory.INSTANCE;
        Paint paint = graphicFactory.createPaint();
        paint.setColor(getPaintforVectorObject(line));
        paint.setStyle(Style.STROKE);
        paint.setStrokeWidth(3);
        Polyline p = new Polyline(paint, graphicFactory);
        for (Geoposition point : line.getGeopositions()) {
            p.getLatLongs().add(Util.GeopositionToLatLong(point));
        }
        return p;
    }

    /**
     * Returns a Mapsforge Layer for a polygon
     * @param polygon
     * @return
     */
    private Layer createPolygon(Polygon polygon) {
        AndroidGraphicFactory graphicFactory = AndroidGraphicFactory.INSTANCE;
        Paint paint = graphicFactory.createPaint();
        paint.setColor(getPaintforVectorObject(polygon));
        org.mapsforge.map.layer.overlay.Polygon p = new org.mapsforge.map.layer.overlay.Polygon(paint, paint, graphicFactory);
        for (Geoposition point : polygon.getGeopositions()) {
            p.getLatLongs().add(Util.GeopositionToLatLong(point));
        }
        return p;
    }

    /**
     * Creating a Polygon-Layer for the Bounds of the Garden
     */
    private Layer createFromBounds(BoundaryBox box) {
        AndroidGraphicFactory graphicFactory = AndroidGraphicFactory.INSTANCE;
        Paint p1 = graphicFactory.createPaint();
        p1.setColor(0x5500FF00);

        org.mapsforge.map.layer.overlay.Polygon bounds = new org.mapsforge.map.layer.overlay.Polygon(p1, p1, graphicFactory);
        LatLong upLeft = new LatLong(box.maxLatitude, box.minLongitude);
        LatLong upRight = new LatLong(box.maxLatitude, box.maxLongitude);
        LatLong downRight = new LatLong(box.minLatitude, box.maxLongitude);
        LatLong downLeft = new LatLong(box.minLatitude, box.minLongitude);
        bounds.getLatLongs().add(upLeft);
        bounds.getLatLongs().add(upRight);
        bounds.getLatLongs().add(downRight);
        bounds.getLatLongs().add(downLeft);
        return bounds;
    }

    /**
     * This method redraws the complete Garden.
     */
    public void redrawCompleteGarden() {
        if(!mapView.getLayerManager().getLayers().isEmpty()){
        Layer tilelayer = mapView.getLayerManager().getLayers().get(0);
        mapView.getLayerManager().getLayers().clear();
        mapView.getLayerManager().getLayers().add(tilelayer);
        setUpGarden();}
    }

    /**
     * Highlights the vectorobject and centers the mapview on it.
     * @param object
     */
    public void highlightObject(VectorObject object) {
        redrawCompleteGarden();
        Layer layer = layers.get(object.getVectorObjectID());
        AndroidGraphicFactory graphicFactory = AndroidGraphicFactory.INSTANCE;
        Paint p = graphicFactory.createPaint();
        p.setColor(Color.RED);
        if (layer instanceof Circle) {
            Circle c = (Circle) layer;
            c.setPaintFill(p);
            c.setPaintStroke(p);
            Log.d("CIRCLE", "Draw in red");
        } else if (layer instanceof Polyline) {
            Polyline l = (Polyline) layer;
            p.setStyle(Style.STROKE);
            l.setPaintStroke(p);
        } else if (layer instanceof org.mapsforge.map.layer.overlay.Polygon) {
            org.mapsforge.map.layer.overlay.Polygon polygon = (org.mapsforge.map.layer.overlay.Polygon) layer;
            polygon.setPaintStroke(p);
            polygon.setPaintFill(p);
        }
        mapView.getModel().mapViewPosition.setCenter(Util.GeopositionToLatLong(object.getGeoposition()));
        layer.requestRedraw();
    }

    /**
     * Returns the right paint for the vector objects
     * @param object
     * @return
     */
    private int getPaintforVectorObject(VectorObject object){
        if (object instanceof ActionPoint) {
            return mapView.getResources().getColor(R.color.ActionPoint);
        }else if(object instanceof Building){
            return mapView.getResources().getColor(R.color.Building);
        }else if(object instanceof GardenObjectCircle){
            return mapView.getResources().getColor(R.color.GardenObjectCircle);
        } else if(object instanceof GardenObjectPolygon){
            return mapView.getResources().getColor(R.color.GardenObjectPolygon);
        } else if(object instanceof Ground){
            Ground g =(Ground)object;
            switch (g.getGroundType()){
                case GRASS: return mapView.getResources().getColor(R.color.Ground_GRASS);
                case WATER: return mapView.getResources().getColor(R.color.Ground_WATER);
                case PAVEMENT: return mapView.getResources().getColor(R.color.Ground_PAVEMENT);
                case SOIL: return mapView.getResources().getColor(R.color.Ground_SOIL);
                case SAND: return mapView.getResources().getColor(R.color.Ground_SAND);
                case ROBOT_ACCESSABLE: return  mapView.getResources().getColor(R.color.Ground_ROBOT_ACCESSABLE);
                case ROBOT_NOT_ACCESSABLE: return mapView.getResources().getColor(R.color.Ground_ROBOT_NOT_ACCESSABLE);
                default: return mapView.getResources().getColor(R.color.Ground);
            }
        } else if (object instanceof Patch){
            return mapView.getResources().getColor(R.color.Patch);
        }else if(object instanceof Plant){
            return mapView.getResources().getColor(R.color.Plant);
        }else  if (object instanceof Rail){
            return mapView.getResources().getColor(R.color.Rail);
        }else if(object instanceof RFIDLandmark){
            return mapView.getResources().getColor(R.color.RFIDLandmark);
        }else if(object instanceof TopologicalEdge){
            return mapView.getResources().getColor(R.color.TopologicalEdge);
        }else if(object instanceof TopologicalNode){
            return mapView.getResources().getColor(R.color.TopologicalNode);
        }else  if(object instanceof Reminder){
            return mapView.getResources().getColor(R.color.Reminder);
        }
         return mapView.getResources().getColor(R.color.Default);
    }

    /**
     * Returns the width of the circle
     * @param point
     * @return
     */
    private float getCircleWidth(Point point){
        float result= 0.5f;
        if(point instanceof Plant){
            Plant p = (Plant) point;
            if(p.getTimeLine().getLastValue()!= null){
                PlantTimeValue t =p.getTimeLine().getLastValue();
                return t.getWidth()== null?result:t.getWidth();
            }

        }else if(point instanceof GardenObjectCircle){
            GardenObjectCircle o = (GardenObjectCircle) point;
            return o.getWidth() == null?result:o.getWidth();
        }
        return result;
    }
}

