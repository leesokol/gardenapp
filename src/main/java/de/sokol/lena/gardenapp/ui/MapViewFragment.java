package de.sokol.lena.gardenapp.ui;

import android.app.Activity;
import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.mapsforge.core.graphics.Color;
import org.mapsforge.core.graphics.Paint;
import org.mapsforge.core.graphics.Style;
import org.mapsforge.core.model.BoundingBox;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.core.model.Point;
import org.mapsforge.map.android.AndroidPreferences;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.graphics.AndroidResourceBitmap;
import org.mapsforge.map.android.util.AndroidUtil;
import org.mapsforge.map.android.view.MapView;
import org.mapsforge.map.layer.Layer;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.layer.overlay.Circle;
import org.mapsforge.map.layer.overlay.Polygon;
import org.mapsforge.map.layer.overlay.Polyline;
import org.mapsforge.map.layer.renderer.TileRendererLayer;
import org.mapsforge.map.model.MapViewPosition;
import org.mapsforge.map.model.common.PreferencesFacade;
import org.mapsforge.map.reader.MapFile;
import org.mapsforge.map.rendertheme.InternalRenderTheme;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.sokol.lena.gardenapp.R;
import de.sokol.lena.gardenapp.model.BoundaryBox;
import de.sokol.lena.gardenapp.model.Garden;
import de.sokol.lena.gardenapp.model.GardenListeners;
import de.sokol.lena.gardenapp.model.Line;
import de.sokol.lena.gardenapp.model.VectorObject;

/**
 * Fragment that holds a Mapforge-Mapview.
 */
public class MapViewFragment extends Fragment implements GardenListeners.GardenListener {

    //HARBURG
    private final LatLong DEFAULT_START_POSITION = new LatLong(53.455578, 9.961006);
    private final byte DEFAULT_START_ZOOM_LEVEL = (byte) 16;
    private final byte ZOOM_MAX = (byte) 22;//MAYBE DO Erweitern durch weitere Zoomstufen(Currently Bug in Mapsforge)
    private final byte ZOOM_MIN = (byte) 10;

    //=Member variable Argument names=

    //====================================
    //=Member variables=
    private MapView mapView;
    private TileCache tileCache;
    private PreferencesFacade preferencesFacade;
    private MapsForgeView mapsForgeView;
    private ActionMode mActionMode;
    private Garden garden;

    //=Listener for the managing Activity=
    private OnFragmentInteractionListener mListener;

    public MapViewFragment() {
        // Required empty public constructor
    }

    /**
     * Factory method for a new MapViewFragment
     *
     * @return A new instance of fragment MapViewFragment.
     */
    public static MapViewFragment newInstance(Garden garden) {
        MapViewFragment fragment = new MapViewFragment();
        //Put the parameter in an Argument Bundle
        Bundle args = new Bundle();
        fragment.setArguments(args);
        fragment.setGarden(garden);
        return fragment;
    }

    public void disableGardenListener() {
        GardenListeners.getInstance().removeGardenListener(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (garden.getBoundary() == null) {
            inflater.inflate(R.menu.initialize_garden_menu, menu);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        if (garden.getBoundary() != null) {
            menu.removeItem(R.id.addBoundaryBox);
        }
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.addBoundaryBox) {
            AndroidGraphicFactory graphicFactory = AndroidGraphicFactory.INSTANCE;
            Paint p = graphicFactory.createPaint();
            p.setColor(0x9900FF00);
            final Circle p1 = new Circle(null, 1f, p, p);//Upperleft (maxLat,minLong)
            final Circle p2 = new Circle(null, 1f, p, p);//Downright (minLat,maxLong)
            Polygon polygon = new Polygon(p, p, graphicFactory) {
                @Override
                public boolean onTap(LatLong tapLatLong, Point layerXY, Point tapXY) {
                    if (p1.getPosition() == null) {
                        p1.setLatLong(tapLatLong);
                        requestRedraw();
                    } else {
                        double latp1 = p1.getPosition().latitude;
                        double longp1 = p1.getPosition().longitude;
                        double latnew = tapLatLong.latitude;
                        double longnew = tapLatLong.longitude;
                        double latp2 = p2.getPosition() != null ? p2.getPosition().latitude : latnew;
                        double longp2 = p2.getPosition() != null ? p2.getPosition().longitude : longnew;
                        double minLat = Math.min(latp1, Math.min(latnew, latp2));
                        double maxLat = Math.max(latp1, Math.max(latnew, latp2));
                        double minLong = Math.min(longp1, Math.min(longp2, longnew));
                        double maxLong = Math.max(longp1, Math.max(longp2, longnew));
                        LatLong upLeft = new LatLong(maxLat, minLong);
                        LatLong upRight = new LatLong(maxLat, maxLong);
                        LatLong downRight = new LatLong(minLat, maxLong);
                        LatLong downLeft = new LatLong(minLat, minLong);
                        p1.setLatLong(upLeft);
                        p2.setLatLong(downRight);
                        getLatLongs().clear();
                        getLatLongs().add(upLeft);
                        getLatLongs().add(upRight);
                        getLatLongs().add(downRight);
                        getLatLongs().add(downLeft);
                    }
                    requestRedraw();
                    return true;
                }
            };
            this.mapView.getLayerManager().getLayers().add(p1);
            this.mapView.getLayerManager().getLayers().add(p2);
            this.mapView.getLayerManager().getLayers().add(polygon);
            mActionMode = getActivity().startActionMode(new AddGardenBoundaryBoxActionModeCallback(p1, p2, polygon));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //Retrieving the values for the member variables with the Argument names
        }
        Activity activity = this.getActivity();
        AndroidGraphicFactory.createInstance(activity.getApplication());
        this.preferencesFacade = new AndroidPreferences(activity.getSharedPreferences(
                getPersistableId(), activity.MODE_PRIVATE));
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.mapview_fragment, container, false);

        //Initializing the MapView
        this.mapView = (MapView) view.findViewById(R.id.mapView);
        this.mapView.getModel().init(preferencesFacade);
        this.mapView.setClickable(true);
        this.mapView.getMapScaleBar().setVisible(true);
        this.mapView.setBuiltInZoomControls(true);
        //Zoom (ZoomControls)
        this.mapView.getMapZoomControls().setZoomLevelMin(ZOOM_MIN);
        this.mapView.getMapZoomControls().setZoomLevelMax(ZOOM_MAX);

        //Initializing the Position
        initializePosition(mapView.getModel().mapViewPosition);

        // create a tile cache of suitable size
        this.tileCache = AndroidUtil.createTileCache(this.getActivity(), "mapcache",
                mapView.getModel().displayModel.getTileSize(), 1.0f,
                this.mapView.getModel().frameBufferModel.getOverdrawFactor());

        // tile renderer layer using internal render theme
        MapFile mapdata = new MapFile(new File(Environment.getExternalStorageDirectory(), "germany.map"));
        TileRendererLayer tileRendererLayer = new TileRendererLayer(tileCache, mapdata,
                this.mapView.getModel().mapViewPosition, true, true, AndroidGraphicFactory.INSTANCE);
        tileRendererLayer.setXmlRenderTheme(InternalRenderTheme.OSMARENDER);

        // only once a layer is associated with a mapView the rendering starts
        this.mapView.getLayerManager().getLayers().add(tileRendererLayer);
        this.mapsForgeView = new MapsForgeView(this.garden, mapView);
        mapsForgeView.setUpGarden();

        return view;
    }

    private void initializePosition(MapViewPosition mvp) {
        if (garden.getBoundary() == null) {
            mvp.setCenter(DEFAULT_START_POSITION);
            mapView.getModel().mapViewPosition.setMapLimit(null);
        } else {
            mvp.setCenter(Util.GeopositionToLatLong(garden.getBoundary().getCenter()));
            BoundaryBox boundaryBox = garden.getBoundary();
            mapView.getModel().mapViewPosition.setMapLimit(new BoundingBox(boundaryBox.minLatitude,
                    boundaryBox.minLongitude, boundaryBox.maxLatitude, boundaryBox.maxLongitude));
        }
        mvp.setZoomLevel(DEFAULT_START_ZOOM_LEVEL);

        mvp.setZoomLevelMax(ZOOM_MAX);
    }

    public void selectObject(VectorObject object) {
        mapsForgeView.highlightObject(object);
    }

    public void switchToAddPointMode(String currentLayer, de.sokol.lena.gardenapp.model.Point point) {
        //Möglichkeiten auf ein Touch-Event zu reagieren:
        //GestureDetector und umrechenen von Pixel in Koordinaten wie in TouchEventHandler
        //Overriding the onTap etc von einem spezifische Layer (vielleicht einfach einen TouchDummyLayer)
        // wie in den Samples
        //Erweitere die Layer-Klasse um einen Möglichkeit eine Listener anzuschließen
        AndroidGraphicFactory graphicFactory = AndroidGraphicFactory.INSTANCE;
        Paint p = graphicFactory.createPaint();
        p.setColor(Color.BLACK);

        Layer layer = this.mapsForgeView.layers.get(point.getVectorObjectID());
        if (layer != null) {
            mapsForgeView.layers.remove(point.getVectorObjectID());
            this.mapView.getLayerManager().redrawLayers();
        }

        LatLong pos = null;
        if (point.getGeoposition() != null) {
            pos = Util.GeopositionToLatLong(point.getGeoposition());
        }

        Circle circle = new Circle(pos, 0.5f, p, p) {
            @Override
            public boolean onTap(LatLong tapLatLong, org.mapsforge.core.model.Point layerXY, org.mapsforge.core.model.Point tapXY) {
                setLatLong(tapLatLong);
                setRadius(0.5f);
                Log.d("TAP", "Tapped on " + tapLatLong.toString());
                requestRedraw();
                return true;
            }
        };
        this.mapView.getLayerManager().getLayers().add(circle);
        mActionMode = getActivity().startActionMode(new AddPointActionModeCallback(circle, currentLayer, point));

        //Add a new GestureDetector to the MapView
        /*GestureDetector.OnGestureListener listener =  new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                Log.d("POINT", "TAPPP" + e.getX() + e.getY());
                return super.onSingleTapConfirmed(e);
            }
        };
        this.mapView.setGestureDetector(new GestureDetector(this.getActivity(),listener));*/
    }

    public void switchToAddLineMode(String currentLayer, Line lineobject) {
        AndroidGraphicFactory graphicFactory = AndroidGraphicFactory.INSTANCE;
        Paint pointcolor = graphicFactory.createPaint();
        pointcolor.setColor(0xBB000000);
        final Circle p1 = new Circle(null, 0.5f, pointcolor, pointcolor);
        Paint p = graphicFactory.createPaint();
        p.setColor(0xBB000000);
        p.setStyle(Style.STROKE);

        List<LatLong> positions = new ArrayList<>();
        Layer layer = this.mapsForgeView.layers.get(lineobject.getVectorObjectID());
        if (layer != null) {
            mapsForgeView.layers.remove(lineobject.getVectorObjectID());
            this.mapView.getLayerManager().redrawLayers();
            Polyline l = (Polyline) layer;
            positions = l.getLatLongs();
        }

        Polyline line = new Polyline(p, graphicFactory) {
            @Override
            public boolean onTap(LatLong tapLatLong, org.mapsforge.core.model.Point layerXY, org.mapsforge.core.model.Point tapXY) {
                getLatLongs().add(tapLatLong);
                if (p1.getPosition() == null) {
                    p1.setLatLong(getLatLongs().get(0));
                }
                Log.d("TAP", "Tapped on " + tapLatLong.toString());
                requestRedraw();
                return true;
            }
        };
        line.getLatLongs().addAll(positions);
        this.mapView.getLayerManager().getLayers().add(p1);
        this.mapView.getLayerManager().getLayers().add(line);
        mActionMode = getActivity().startActionMode(new AddLineActionModeCallback(line, p1, currentLayer, lineobject));
    }

    public void switchToAddPolgonMode(String currentLayer, de.sokol.lena.gardenapp.model.Polygon poly) {
        AndroidGraphicFactory graphicFactory = AndroidGraphicFactory.INSTANCE;
        Paint pointcolor = graphicFactory.createPaint();
        pointcolor.setColor(0xAA000000);
        final Circle p1 = new Circle(null, 0.5f, pointcolor, pointcolor);
        final Circle p2 = new Circle(null, 0.5f, pointcolor, pointcolor);
        Paint p = graphicFactory.createPaint();
        p.setColor(0xAA000000);
        Paint ps = graphicFactory.createPaint();
        ps.setColor(Color.BLACK);
        ps.setStyle(Style.STROKE);

        List<LatLong> positions = new ArrayList<>();
        Layer layer = this.mapsForgeView.layers.get(poly.getVectorObjectID());
        if (layer != null) {
            mapsForgeView.layers.remove(poly.getVectorObjectID());
            this.mapView.getLayerManager().redrawLayers();
            Polygon l = (Polygon) layer;
            positions = l.getLatLongs();
        }

        Polygon polygon = new Polygon(p, ps, graphicFactory) {
            @Override
            public boolean onTap(LatLong tapLatLong, org.mapsforge.core.model.Point layerXY, org.mapsforge.core.model.Point tapXY) {
                getLatLongs().add(tapLatLong);
                if (p1.getPosition() == null) {
                    p1.setLatLong(getLatLongs().get(0));
                } else if (p2.getPosition() == null) {
                    p2.setLatLong(getLatLongs().get(1));
                }

                Log.d("TAP", "Tapped on " + tapLatLong.toString());
                requestRedraw();
                return true;
            }
        };
        polygon.getLatLongs().addAll(positions);
        this.mapView.getLayerManager().getLayers().add(p1);
        this.mapView.getLayerManager().getLayers().add(p2);
        this.mapView.getLayerManager().getLayers().add(polygon);

        mActionMode = getActivity().startActionMode(new AddPolygonActionModeCallback(polygon, p1, p2, currentLayer, poly));

    }


    @Override
    public void onPause() {
        super.onPause();
        mapView.getModel().save(this.preferencesFacade);
        this.preferencesFacade.save();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Destroying Layers
        for (Layer layer : mapView.getLayerManager().getLayers()) {
            mapView.getLayerManager().getLayers().remove(layer);
            layer.onDestroy();
        }
        //Destroy Cache
        this.tileCache.destroy();
        //Destroy MapView
        this.mapView.destroy();
        //Clearing Bitmaps
        AndroidResourceBitmap.clearResourceBitmaps();

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public String getPersistableId() {
        return this.getClass().getSimpleName();
    }

    public void setGarden(Garden garden) {
        this.garden = garden;
        GardenListeners.getInstance().addGardenListener(this);
        if (mapsForgeView != null) {
            mapsForgeView.setGarden(garden);
            mapsForgeView.redrawCompleteGarden();
        }
        if(mapView != null){
        if(garden.getBoundary()!= null){
            informAddedBoundary();
        }else{
            mapView.getModel().mapViewPosition.setMapLimit(null);
        }}
    }

    @Override
    public void informAddedObject() {
        //Maybe DO: Add only one new Layer
        mapsForgeView.redrawCompleteGarden();
    }

    @Override
    public void informAddedLayer() {
        mapsForgeView.redrawCompleteGarden();
    }

    @Override
    public void informChangedVisibility() {
       mapsForgeView.redrawCompleteGarden();
        Log.d("CHECKBOX", "redrawing");
    }

    @Override
    public void informDeletedLayer() {
        mapsForgeView.redrawCompleteGarden();
    }

    @Override
    public void informMovedLayer() {
        mapsForgeView.redrawCompleteGarden();
    }

    @Override
    public void informDeletedObject() {
        mapsForgeView.redrawCompleteGarden();
    }

    @Override
    public void informMovedObject() {
        mapsForgeView.redrawCompleteGarden();
    }

    @Override
    public void informChangedObject() {
        mapsForgeView.redrawCompleteGarden();
    }

    @Override
    public void informAddedBoundary() {
        BoundaryBox boundaryBox = garden.getBoundary();
        mapView.getModel().mapViewPosition.setMapLimit(new BoundingBox(boundaryBox.minLatitude,
                boundaryBox.minLongitude, boundaryBox.maxLatitude, boundaryBox.maxLongitude));
        mapsForgeView.redrawCompleteGarden();
    }

    //================================================
    //=Inner Interfaces and Classes

    /**
     * FragmentInteractionListener that needs to be implemented by the managing activity
     */
    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);

        public void onAddPoint(LatLong position, String layerName, de.sokol.lena.gardenapp.model.Point point);

        public void onAddLine(List<LatLong> positions, String layerName, Line line);

        public void onAddPolygon(List<LatLong> positions, String layerName, de.sokol.lena.gardenapp.model.Polygon polgon);

        public void onAddGardenBoundary(Circle upleft, Circle downright);
    }

    private class AddLineActionModeCallback implements ActionMode.Callback {

        public Polyline layer;
        public String currentLayer;
        public Line line;
        public Circle p1;

        public AddLineActionModeCallback(Polyline layer, Circle p1, String currentLayer, Line line) {
            this.p1 = p1;
            this.layer = layer;
            this.currentLayer = currentLayer;
            this.line = line;
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.add_multipoint_menu, menu);
            mode.setTitle(R.string.title_action_mode_addLine);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.decline:
                    Log.d("ACTIONMODE", "DECLINE");
                    layer.getLatLongs().clear();
                    p1.setLatLong(null);
                    mode.finish();
                    return true;
                case R.id.undo:
                    Log.d("ACTIONMODE", "UNDO");
                    if (!layer.getLatLongs().isEmpty()) {
                        layer.getLatLongs().remove(layer.getLatLongs().size() - 1);
                        if (layer.getLatLongs().isEmpty()) {
                            p1.setLatLong(null);
                        }
                        layer.requestRedraw();
                    }
                    return true;
                default:
                    Log.d("ACTIONMODE", "DEFAULT");
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            //Checking if Decline is called before
            if (!layer.getLatLongs().isEmpty()) {
                mListener.onAddLine(layer.getLatLongs(), currentLayer, line);
            }
            mapView.getLayerManager().getLayers().remove(layer);
            mapView.getLayerManager().getLayers().remove(p1);
            mActionMode = null;
        }
    }

    private class AddPointActionModeCallback implements ActionMode.Callback {
        private Circle layer;
        private String currentLayer;
        private de.sokol.lena.gardenapp.model.Point point;

        public AddPointActionModeCallback(Circle layer, String currentLayer, de.sokol.lena.gardenapp.model.Point point) {
            this.layer = layer;
            this.currentLayer = currentLayer;
            this.point = point;
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.add_point_menu, menu);
            mode.setTitle(R.string.title_action_mode_addPoint);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.decline:
                    Log.d("ACTIONMODE", "DECLINE");
                    layer.setLatLong(null);
                    mode.finish();
                    return true;
                default:
                    Log.d("ACTIONMODE", "DEFAULT");
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            //Checking if Decline is called before
            if (layer.getPosition() != null) {
                mListener.onAddPoint(layer.getPosition(), currentLayer, point);
            }
            mapView.getLayerManager().getLayers().remove(layer);
            mActionMode = null;
        }
    }

    //Maybe Do: Put Line and Polygon callbacks together.

    private class AddPolygonActionModeCallback implements ActionMode.Callback {
        private Polygon polygon;
        private String currentLayer;
        private de.sokol.lena.gardenapp.model.Polygon poly;
        private Circle p1;
        private Circle p2;

        public AddPolygonActionModeCallback(Polygon polygon, Circle p1, Circle p2, String currentLayer, de.sokol.lena.gardenapp.model.Polygon poly) {
            this.p1 = p1;
            this.p2 = p2;
            this.polygon = polygon;
            this.currentLayer = currentLayer;
            this.poly = poly;
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.add_multipoint_menu, menu);
            mode.setTitle(R.string.title_action_mode_addPolygon);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.decline:
                    Log.d("ACTIONMODE", "DECLINE");
                    polygon.getLatLongs().clear();
                    p1.setLatLong(null);
                    mode.finish();
                    return true;
                case R.id.undo:
                    Log.d("ACTIONMODE", "UNDO");
                    if (!polygon.getLatLongs().isEmpty()) {
                        polygon.getLatLongs().remove(polygon.getLatLongs().size() - 1);
                        if (polygon.getLatLongs().size() == 1) {
                            p2.setLatLong(null);
                        }
                        if (polygon.getLatLongs().isEmpty()) {
                            p1.setLatLong(null);
                        }
                        polygon.requestRedraw();
                    }
                    return true;
                default:
                    Log.d("ACTIONMODE", "DEFAULT");
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            //Checking if Decline is called before
            if (!polygon.getLatLongs().isEmpty()) {
                mListener.onAddPolygon(polygon.getLatLongs(), currentLayer, poly);
            }
            mapView.getLayerManager().getLayers().remove(polygon);
            mapView.getLayerManager().getLayers().remove(p1);
            mapView.getLayerManager().getLayers().remove(p2);
            mActionMode = null;
        }
    }

    private class AddGardenBoundaryBoxActionModeCallback implements ActionMode.Callback {

        private Circle p1;
        private Circle p2;
        private Polygon polygon;

        public AddGardenBoundaryBoxActionModeCallback(Circle p1, Circle p2, Polygon polygon) {
            this.p1 = p1;
            this.p2 = p2;
            this.polygon = polygon;
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.add_multipoint_menu, menu);
            mode.setTitle(R.string.addBoundaryBox);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.decline:
                    p1.setLatLong(null);
                    mode.finish();
                    return true;
                case R.id.undo:
                    if (p2.getPosition() == null) {
                        p1.setLatLong(null);
                    } else {
                        p2.setLatLong(null);
                    }
                    polygon.getLatLongs().clear();
                    polygon.requestRedraw();
                    return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            //Checking for Decline and sufficient data
            if (p1.getPosition() != null && p2.getPosition() != null) {
                mListener.onAddGardenBoundary(p1, p2);
            }
            mapView.getLayerManager().getLayers().remove(polygon);
            mapView.getLayerManager().getLayers().remove(p1);
            mapView.getLayerManager().getLayers().remove(p2);
            mActionMode = null;
        }
    }
}
