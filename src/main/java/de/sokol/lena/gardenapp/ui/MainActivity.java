package de.sokol.lena.gardenapp.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.layer.overlay.Circle;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.greenrobot.event.EventBus;
import de.sokol.lena.gardenapp.R;
import de.sokol.lena.gardenapp.eca.Advice;
import de.sokol.lena.gardenapp.eca.PlantChangedEventHandler;
import de.sokol.lena.gardenapp.eca.ResourceFacade;
import de.sokol.lena.gardenapp.model.ActionPoint;
import de.sokol.lena.gardenapp.model.BoundaryBox;
import de.sokol.lena.gardenapp.model.Building;
import de.sokol.lena.gardenapp.model.DummySensor;
import de.sokol.lena.gardenapp.model.Garden;
import de.sokol.lena.gardenapp.model.GardenFacade;
import de.sokol.lena.gardenapp.model.GardenListeners;
import de.sokol.lena.gardenapp.model.GardenObjectCircle;
import de.sokol.lena.gardenapp.model.GardenObjectPolygon;
import de.sokol.lena.gardenapp.model.Geoposition;
import de.sokol.lena.gardenapp.model.Ground;
import de.sokol.lena.gardenapp.model.Landmark;
import de.sokol.lena.gardenapp.model.LandmarkPolygon;
import de.sokol.lena.gardenapp.model.Layer.SatelliteLayer;
import de.sokol.lena.gardenapp.model.Layer.GardenLayer;
import de.sokol.lena.gardenapp.model.Layer.PlantLayer;
import de.sokol.lena.gardenapp.model.Layer.VectorLayer;
import de.sokol.lena.gardenapp.model.Line;
import de.sokol.lena.gardenapp.model.Patch;
import de.sokol.lena.gardenapp.model.Plant;
import de.sokol.lena.gardenapp.model.Point;
import de.sokol.lena.gardenapp.model.Polygon;
import de.sokol.lena.gardenapp.model.RFIDLandmark;
import de.sokol.lena.gardenapp.model.Rail;
import de.sokol.lena.gardenapp.model.Reminder;
import de.sokol.lena.gardenapp.model.Sensor;
import de.sokol.lena.gardenapp.model.TopologicalEdge;
import de.sokol.lena.gardenapp.model.TopologicalNode;
import de.sokol.lena.gardenapp.model.VectorObject;

public class MainActivity extends Activity implements MapViewFragment.OnFragmentInteractionListener,
        LayerFragment.OnLayerFragmentInteractionListener, ObjectListFragment.OnObjectListFragmentInteractionListener {

    private static final int SELECT_PHOTO = 100;
    private Garden garden;
    private GardenFacade gardenFacade;
    private String currentLayer;
    private Set<Advice> advices;
    private String sateliteLayerName;

    //MAYBE DO: zeitliche Zustände grafisch anzeigen(Liniendiagramme)
    //MAYBE DO: Rasterkarten mit Interpolation der Sensorwerte
    //MAYBE DO: Farbe je nach Eigenschaften etc.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_drawer);
        advices = new HashSet<>();
        setUpEventHandler();
        this.garden = new Garden();//new BoundaryBox(53.45525838232548, 9.961044139737893, 53.45564807836337, 9.961622155780603));//getDummyGarden();
        this.gardenFacade = new GardenFacade(garden, this);
        MapViewFragment mapViewFragment = MapViewFragment.newInstance(garden);
        LayerFragment layerFragment = new LayerFragment();
        layerFragment.setGarden(garden);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.mapview_fragment_container, mapViewFragment);
        transaction.add(R.id.left_drawer, layerFragment);
        transaction.commit();
    }

    /**
     * Import into a new Garden from the export.xml
     * @param newGarden
     */
    private void startImporting(Garden newGarden) {
        new AsyncTask<Garden, Void, Garden>() {
            @Override
            protected Garden doInBackground(Garden... params) {
                XMLImporter im = new XMLImporter();
                File f=  new File(Environment.getExternalStorageDirectory(), "export.xml");
                Garden g = im.readImputFile(f.getPath(),params[0]);
                return g;
            }

            @Override
            protected void onPostExecute(Garden garden) {
                MainActivity.this.garden = garden;
                gardenFacade = new GardenFacade(garden,MainActivity.this);
                MapViewFragment f = (MapViewFragment) getFragmentManager().findFragmentById(R.id.mapview_fragment_container);
                f.setGarden(garden);
                Fragment fragment = getFragmentManager().findFragmentById(R.id.left_drawer);
                if (fragment instanceof ObjectListFragment) {
                    ObjectListFragment objectListFragment = ObjectListFragment.newInstance(currentLayer);
                    objectListFragment.setGarden(gardenFacade);
                    getFragmentManager().beginTransaction().replace(R.id.left_drawer, objectListFragment).commit();
                } else {
                    LayerFragment layerFragment = new LayerFragment();
                    layerFragment.setGarden(garden);
                    getFragmentManager().beginTransaction().replace(R.id.left_drawer, layerFragment).commit();
                }
            }
        }.execute(newGarden);
    }

    public Set<Advice> getAdvices() {
        return advices;
    }

    public void onCreateLayerButtonClick(View view) {
        if (garden.getBoundary() != null) {
            createAddLayerDialog().show();
        } else {
            Toast.makeText(this, "No BoundaryBox created!", Toast.LENGTH_SHORT).show();
        }
    }

    public void onSwitchModeButtonClick(View view){
        if (currentLayer != null) {
            Fragment fragment = getFragmentManager().findFragmentById(R.id.left_drawer);
            if (fragment instanceof LayerFragment) {
                ObjectListFragment objectListFragment = ObjectListFragment.newInstance(currentLayer);
                objectListFragment.setGarden(this.gardenFacade);
                getFragmentManager().beginTransaction().replace(R.id.left_drawer, objectListFragment).commit();
            } else {
                LayerFragment layerFragment = new LayerFragment();
                layerFragment.setGarden(garden);
                getFragmentManager().beginTransaction().replace(R.id.left_drawer, layerFragment).commit();
            }
        }
    }

    private Dialog createAddLayerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.choose_layer_type).setItems(R.array.layer_types, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                TypedArray array = getResources().obtainTypedArray(R.array.layer_types);
                String layerType = array.getString(which);
                onCreateLayerDialog(layerType).show();
            }
        });
        return builder.create();
    }

    private Dialog onCreateLayerDialog(final String layerType) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View layout = inflater.inflate(R.layout.new_layer_dialog, null);
        builder.setView(layout).setPositiveButton(R.string.layername_check, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText editText = (EditText) layout.findViewById(R.id.layername);
                String text = editText.getText().toString();
                if (garden.containsLayer(text)) {
                    Toast.makeText(MainActivity.this, R.string.layer_text_notgiven, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!layerType.equals("SatelliteLayer")) {
                    gardenFacade.addLayer(text, layerType);
                } else {
                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    sateliteLayerName = text;
                    startActivityForResult(photoPickerIntent, SELECT_PHOTO);
                }

            }
        });
        return builder.create();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case SELECT_PHOTO:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    InputStream imageStream = null;
                    try {
                        imageStream = getContentResolver().openInputStream(selectedImage);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    Bitmap yourSelectedImage = BitmapFactory.decodeStream(imageStream);
                    gardenFacade.addSateliteLayer(sateliteLayerName, yourSelectedImage);
                }
        }
    }

    private void setUpEventHandler() {
        EventBus.getDefault().register(new PlantChangedEventHandler(this, new ResourceFacade(this)));
    }

    private Garden getDummyGarden() {

        Garden garden = new Garden(new BoundaryBox(52.516810, 13.400716, 52.518025, 13.403994));
        String layername = "TestLayer";
        VectorLayer layer = new VectorLayer(layername);
        garden.addLayer(layer);
        layer.addVectorObject(new Point(new Geoposition(52.517, 13.402)));
        Line line = new Line(new Geoposition(52.5175, 13.402));
        line.addPosition(new Geoposition(52.5175, 13.4025));
        line.addPosition(new Geoposition(52.51755, 13.40255));
        layer.addVectorObject(line);
        Polygon polygon = new Polygon();
        polygon.addPosition(new Geoposition(52.5177, 13.4028));
        polygon.addPosition(new Geoposition(52.51779, 13.4019));
        polygon.addPosition(new Geoposition(52.51778, 13.4019));
        layer.addVectorObject(polygon);
        BitmapDrawable draw = (BitmapDrawable) getResources().getDrawable(R.drawable.ic_launcher);
        SatelliteLayer rasterLayer = new SatelliteLayer("Rasterlayer", 140, 140, draw.getBitmap());
        garden.addLayer(rasterLayer);
        String layername2 = "Plantlayer";
        PlantLayer plantLayer = new PlantLayer(layername2);
        garden.addLayer(plantLayer);
        plantLayer.addVectorObject(new Plant(new Geoposition(52.51779, 13.4022)));
        //VectorObjects
        return garden;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Log.d("MENU", "onPrepareOptionsMenu");
        VectorLayer layer = gardenFacade.getVectorLayer(currentLayer);

        menu.findItem(R.id.addPoint).setVisible(false);
        menu.findItem(R.id.addLine).setVisible(false);
        menu.findItem(R.id.addPolygon).setVisible(false);
        menu.findItem(R.id.addPlant).setVisible(false);
        menu.findItem(R.id.addTopologicalNode).setVisible(false);
        menu.findItem(R.id.addTopologicalEdge).setVisible(false);
        menu.findItem(R.id.patch).setVisible(false);
        menu.findItem(R.id.actionpoint).setVisible(false);
        menu.findItem(R.id.building).setVisible(false);
        menu.findItem(R.id.gardenObjectCircle).setVisible(false);
        menu.findItem(R.id.gardenObjectPolygon).setVisible(false);
        menu.findItem(R.id.ground).setVisible(false);
        menu.findItem(R.id.Landmark).setVisible(false);
        menu.findItem(R.id.LandmarkPolygon).setVisible(false);
        menu.findItem(R.id.Rail).setVisible(false);
        menu.findItem(R.id.Sensor).setVisible(false);
        menu.findItem(R.id.Reminder).setVisible(false);

        if (layer != null) {
            if (layer.getAllowedVectorObjects().contains(de.sokol.lena.gardenapp.model.Point.class)) {
                menu.findItem(R.id.addPoint).setVisible(true);
            }
            if (layer.getAllowedVectorObjects().contains(Line.class)) {
                menu.findItem(R.id.addLine).setVisible(true);
            }
            if (layer.getAllowedVectorObjects().contains(Polygon.class)) {
                menu.findItem(R.id.addPolygon).setVisible(true);
            }
            if (layer.getAllowedVectorObjects().contains(Plant.class)) {
                menu.findItem(R.id.addPlant).setVisible(true);
            }
            if (layer.getAllowedVectorObjects().contains(TopologicalNode.class)) {
                menu.findItem(R.id.addTopologicalNode).setVisible(true);
            }
            if (layer.getAllowedVectorObjects().contains(TopologicalEdge.class)) {
                menu.findItem(R.id.addTopologicalEdge).setVisible(true);
            }
            if (layer.getAllowedVectorObjects().contains(Patch.class)) {
                menu.findItem(R.id.patch).setVisible(true);
            }
            if (layer.getAllowedVectorObjects().contains(ActionPoint.class)) {
                menu.findItem(R.id.actionpoint).setVisible(true);
            }
            if (layer.getAllowedVectorObjects().contains(Building.class)) {
                menu.findItem(R.id.building).setVisible(true);
            }
            if (layer.getAllowedVectorObjects().contains(GardenObjectCircle.class)) {
                menu.findItem(R.id.gardenObjectCircle).setVisible(true);
            }
            if (layer.getAllowedVectorObjects().contains(GardenObjectPolygon.class)) {
                menu.findItem(R.id.gardenObjectPolygon).setVisible(true);
            }
            if (layer.getAllowedVectorObjects().contains(Ground.class)) {
                menu.findItem(R.id.ground).setVisible(true);
            }
            if (layer.getAllowedVectorObjects().contains(Landmark.class)) {
                menu.findItem(R.id.Landmark).setVisible(true);
            }
            if (layer.getAllowedVectorObjects().contains(LandmarkPolygon.class)) {
                menu.findItem(R.id.LandmarkPolygon).setVisible(true);
            }
            if (layer.getAllowedVectorObjects().contains(Rail.class)) {
                menu.findItem(R.id.Rail).setVisible(true);
            }
            if (layer.getAllowedVectorObjects().contains(Sensor.class)) {
                menu.findItem(R.id.Sensor).setVisible(true);
            }
            if (layer.getAllowedVectorObjects().contains(Reminder.class)) {
                menu.findItem(R.id.Reminder).setVisible(true);
            }
        }
        super.onPrepareOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        MapViewFragment f = (MapViewFragment) getFragmentManager().findFragmentById(R.id.mapview_fragment_container);
        switch (id) {
            case R.id.addPoint:
                if (currentLayer != null) {
                    f.switchToAddPointMode(currentLayer, new Point());
                }
                return true;
            case R.id.addLine:
                if (currentLayer != null) {
                    f.switchToAddLineMode(currentLayer, new Line());
                }
                return true;
            case R.id.addPolygon:
                if (currentLayer != null) {
                    f.switchToAddPolgonMode(currentLayer, new Polygon());
                }
                return true;
            case R.id.addPlant:
                if (currentLayer != null) {
                    f.switchToAddPointMode(currentLayer, new Plant());
                }
                return true;
            case R.id.addTopologicalNode:
                if (currentLayer != null) {
                    f.switchToAddPointMode(currentLayer, new TopologicalNode());
                }
                return true;
            case R.id.addTopologicalEdge:
                if (currentLayer != null) {
                    showAddTopologicalEdgeDialog();
                }
                return true;
            case R.id.patch:
                if (currentLayer != null) {
                    f.switchToAddPolgonMode(currentLayer, new Patch());
                }
                return true;
            case R.id.actionpoint:
                if (currentLayer != null) {
                    f.switchToAddPointMode(currentLayer, new ActionPoint());
                }
                return true;
            case R.id.building:
                if (currentLayer != null) {
                    f.switchToAddPolgonMode(currentLayer, new Building());
                }
                return true;
            case R.id.gardenObjectCircle:
                if (currentLayer != null) {
                    f.switchToAddPointMode(currentLayer, new GardenObjectCircle());
                }
                return true;
            case R.id.gardenObjectPolygon:
                if (currentLayer != null) {
                    f.switchToAddPolgonMode(currentLayer, new GardenObjectPolygon());
                }
                return true;
            case R.id.ground:
                if (currentLayer != null) {
                    f.switchToAddPolgonMode(currentLayer, new Ground());
                }
                return true;
            case R.id.Landmark:
                if (currentLayer != null) {
                    View menuItemView = findViewById(R.id.Landmark);
                    showPopUpLandmark(menuItemView, f);
                }
                return true;
            case R.id.LandmarkPolygon:
                if (currentLayer != null) {
                    //TODO:show menu with different LandmarkTypes
                }
                return true;
            case R.id.Rail:
                if (currentLayer != null) {
                    f.switchToAddLineMode(currentLayer, new Rail());
                }
                return true;
            case R.id.Sensor:
                if (currentLayer != null) {
                    View menuItemView = findViewById(R.id.Sensor);
                    showPopUpSensor(menuItemView, f);
                }
                return true;
            case R.id.Reminder:
                if (currentLayer != null) {
                    f.switchToAddPointMode(currentLayer, new Reminder());
                }
                return true;
            case R.id.remove_objectdetails:
                ObjectDetailFragment objectDetailFragment = (ObjectDetailFragment) getFragmentManager().findFragmentById(R.id.object_detail_container);
                getFragmentManager().beginTransaction().remove(objectDetailFragment).commit();
                return true;
            case R.id.exportToXML:
                XMLExporter xmlExporter = XMLExporter.newInstance();
                String xml = xmlExporter.exportGardenToXML(garden);
                xmlExporter.writeToFile(xml);
                return true;
            case R.id.importFromXML:
                //MAYBE DO: Importauswahl von Dateien
                Garden newGarden = new Garden();
                f.disableGardenListener();
                Fragment fragment = getFragmentManager().findFragmentById(R.id.left_drawer);
                if (fragment instanceof ObjectListFragment) {
                    ObjectListFragment objectListFragment = (ObjectListFragment) fragment;
                    GardenListeners.getInstance().removeGardenListener(objectListFragment);
                } else {
                    LayerFragment layerFragment = (LayerFragment) fragment;
                    GardenListeners.getInstance().removeGardenListener(layerFragment);
                }
                startImporting(newGarden);
                return true;
            case R.id.advices:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Advices");
                ArrayList<Advice> list = new ArrayList<>(getAdvices());
                final ArrayAdapter<Advice> adviceArrayAdapter = new ArrayAdapter<Advice>(this, android.R.layout.simple_list_item_1, list);
                builder.setAdapter(adviceArrayAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Advice advice = adviceArrayAdapter.getItem(which);
                        if (advice.getSource() instanceof VectorObject) {
                            onSelectedObject((VectorObject) advice.getSource());
                        }
                        //Maybe Do:Delete Advices when a Source is deleted, Trigger changes for all other plants maybe
                        //Maybe Do:Delete Advice action
                    }
                });
                builder.create().show();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showPopUpLandmark(View view, final MapViewFragment f) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.inflate(R.menu.landmarks);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.LandmarkRFID:
                        f.switchToAddPointMode(currentLayer, new RFIDLandmark());
                        return true;
                }
                return false;
            }
        });
        popupMenu.show();
    }

    private void showPopUpSensor(View view, final MapViewFragment f) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.inflate(R.menu.sensors);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.DummySensor:
                        f.switchToAddPointMode(currentLayer, new DummySensor());
                        return true;
                }
                return false;
            }
        });
        popupMenu.show();
    }

    private void showAddTopologicalEdgeDialog() {
        final TopologicalEdge edge = new TopologicalEdge();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.add_topological_node, null);
        View node1 = view.findViewById(R.id.node1);
        View node2 = view.findViewById(R.id.node2);
        TextView nodetext = (TextView) node1.findViewById(R.id.attribute_text);
        nodetext.setText("Node 1:");
        nodetext = (TextView) node2.findViewById(R.id.attribute_text);
        nodetext.setText("Node 2:");

        final Spinner spinner1 = (Spinner) node1.findViewById(R.id.attribut_spinner);
        final Spinner spinner2 = (Spinner) node2.findViewById(R.id.attribut_spinner);

        final VectorLayer layer = gardenFacade.getVectorLayer(currentLayer);
        ArrayList<TopologicalNode> nodes = new ArrayList<>();
        for (VectorObject object : layer.getVectorObjects()) {
            if (object instanceof TopologicalNode) {
                nodes.add((TopologicalNode) object);
            }
        }
        if (nodes.size() < 2) {
            Toast.makeText(this, "No enough Nodes for Edge!", Toast.LENGTH_SHORT).show();
            return;
        }
        ArrayAdapter<TopologicalNode> adapter = new ArrayAdapter<TopologicalNode>(this, android.R.layout.simple_spinner_item, nodes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter);
        spinner2.setAdapter(adapter);
        builder.setView(view);
        builder.setPositiveButton(R.string.check, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                TopologicalNode node1 = (TopologicalNode) spinner1.getSelectedItem();
                TopologicalNode node2 = (TopologicalNode) spinner2.getSelectedItem();
                edge.setNode1(node1);
                edge.setNode2(node2);
                layer.addVectorObject(edge);
            }
        });
        builder.create().show();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        //No-Op
    }

    @Override
    public void onAddPoint(LatLong position, String layerName, de.sokol.lena.gardenapp.model.Point point) {
        //Add the Point to the Garden
        VectorLayer layer = (VectorLayer) gardenFacade.getVectorLayer(layerName);
        if (!layer.containsVectorObject(point)) {
            point.setPosition(new Geoposition(position.latitude, position.longitude));
            layer.addVectorObject(point);
        } else {
            point.setPosition(new Geoposition(position.latitude, position.longitude));
        }
    }

    @Override
    public void onAddLine(List<LatLong> positions, String layerName, Line line) {
        VectorLayer layer = (VectorLayer) gardenFacade.getVectorLayer(layerName);

        if (!layer.containsVectorObject(line)) {
            for (LatLong pos : positions) {
                line.addPosition(Util.LatLongToGeopostion(pos));
            }
            layer.addVectorObject(line);
        } else {
            ArrayList<Geoposition> poslist = new ArrayList<>();
            for (LatLong pos : positions) {
                poslist.add(Util.LatLongToGeopostion(pos));
            }
            line.replacePositions(poslist);
        }
    }

    @Override
    public void onAddPolygon(List<LatLong> positions, String layerName, Polygon polygon) {
        VectorLayer layer = (VectorLayer) gardenFacade.getVectorLayer(layerName);
        if (!layer.containsVectorObject(polygon)) {
            for (LatLong pos : positions) {
                polygon.addPosition(Util.LatLongToGeopostion(pos));
            }
            layer.addVectorObject(polygon);
        } else {
            ArrayList<Geoposition> poslist = new ArrayList<>();
            for (LatLong pos : positions) {
                poslist.add(Util.LatLongToGeopostion(pos));
            }
            polygon.replacePositions(poslist);
        }
    }

    @Override
    public void onAddGardenBoundary(Circle upleft, Circle downright) {
        BoundaryBox boundaryBox = new BoundaryBox(downright.getPosition().latitude,
                upleft.getPosition().longitude,
                upleft.getPosition().latitude,
                downright.getPosition().longitude);
        garden.setBoundary(boundaryBox);
        invalidateOptionsMenu();
    }

    @Override
    public void onSelectedLayer(GardenLayer layer) {
        if (layer != null) {
            this.getActionBar().setTitle(layer.getName());
            this.currentLayer = layer.getName();
        } else {
            this.currentLayer = null;
            this.getActionBar().setTitle("No layer");
        }
        invalidateOptionsMenu();
    }

    @Override
    public void onDeleteLayer(GardenLayer layer) {
        boolean result = this.garden.deleteLayer(layer);
        if (!result) {
            Log.e("DELETE LAYER", "Layer: " + layer + "could not be deleted");
        }
    }

    @Override
    public void onMoveLayerUp(GardenLayer layer) {
        this.garden.moveLayerUp(layer);
    }

    @Override
    public void onMoveLayerDown(GardenLayer layer) {
        this.garden.moveLayerDown(layer);
    }

    @Override
    public void onSelectedObject(VectorObject object) {
        MapViewFragment f = (MapViewFragment) getFragmentManager().findFragmentById(R.id.mapview_fragment_container);
        f.selectObject(object);
        ObjectDetailFragment objectDetailFragment = ObjectDetailFragment.newInstance();
        objectDetailFragment.setObject(object);
        VectorLayer layer = this.gardenFacade.getVectorLayer(currentLayer);
        objectDetailFragment.setLayer(layer);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.object_detail_container, objectDetailFragment);
        transaction.commit();
    }

    @Override
    public void onDeletedObject(VectorObject object) {
        VectorLayer layer = this.gardenFacade.getVectorLayer(currentLayer);
        boolean result = layer.deleteVectorObject(object);
        if (!result) {
            Toast.makeText(this, "Object: " + object.toString() + "could not be deleted", Toast.LENGTH_LONG).show();
            Log.e("DELETE OBJECT", "Object: " + object.toString() + "could not be deleted");
        }
    }

    @Override
    public void onObjectMoveUp(VectorObject object) {
        VectorLayer layer = this.gardenFacade.getVectorLayer(currentLayer);
        layer.moveObjectUp(object);
    }

    @Override
    public void onObjectMoveDown(VectorObject object) {
        VectorLayer layer = this.gardenFacade.getVectorLayer(currentLayer);
        layer.moveObjectDown(object);
    }

    @Override
    public void onChangeObject(VectorObject object) {
        MapViewFragment f = (MapViewFragment) getFragmentManager().findFragmentById(R.id.mapview_fragment_container);
        if (object instanceof de.sokol.lena.gardenapp.model.Point) {
            f.switchToAddPointMode(currentLayer, (de.sokol.lena.gardenapp.model.Point) object);
        } else if (object instanceof Line) {
            f.switchToAddLineMode(currentLayer, (Line) object);
        } else if (object instanceof Polygon) {
            f.switchToAddPolgonMode(currentLayer, (Polygon) object);
        }
    }
}
