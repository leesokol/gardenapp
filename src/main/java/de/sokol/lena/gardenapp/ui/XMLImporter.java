package de.sokol.lena.gardenapp.ui;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.sokol.lena.gardenapp.model.ActionPoint;
import de.sokol.lena.gardenapp.model.BoundaryBox;
import de.sokol.lena.gardenapp.model.Building;
import de.sokol.lena.gardenapp.model.Garden;
import de.sokol.lena.gardenapp.model.GardenObjectCircle;
import de.sokol.lena.gardenapp.model.GardenObjectPolygon;
import de.sokol.lena.gardenapp.model.Geoposition;
import de.sokol.lena.gardenapp.model.Ground;
import de.sokol.lena.gardenapp.model.Layer.GardenConstructsLayer;
import de.sokol.lena.gardenapp.model.Layer.GardenLayer;
import de.sokol.lena.gardenapp.model.Layer.GroundLayer;
import de.sokol.lena.gardenapp.model.Layer.PlantLayer;
import de.sokol.lena.gardenapp.model.Layer.ReminderLayer;
import de.sokol.lena.gardenapp.model.Layer.RobotLayer;
import de.sokol.lena.gardenapp.model.Layer.SensorLayer;
import de.sokol.lena.gardenapp.model.Layer.TopologicalMap;
import de.sokol.lena.gardenapp.model.Patch;
import de.sokol.lena.gardenapp.model.Plant;
import de.sokol.lena.gardenapp.model.PlantTimeValue;
import de.sokol.lena.gardenapp.model.RFIDLandmark;
import de.sokol.lena.gardenapp.model.Rail;
import de.sokol.lena.gardenapp.model.Reminder;
import de.sokol.lena.gardenapp.model.TopologicalEdge;
import de.sokol.lena.gardenapp.model.TopologicalNode;
import de.sokol.lena.gardenapp.model.VectorObject;

/**
 * Imports a garden from a xml file the fits the xml schema
 *
 * Created by Lena on 03.05.2015.
 */
public class XMLImporter {

    private String gardenmodelPrefix = "gardenmodel";
    private String gardenmodelNamespace = "http://www.gardenmodel.de/gardenmodel";
    private String xsiPrefix = "xsi";
    private String xsiNamespace = "http://www.w3.org/2001/XMLSchema-instance";
    private String gmlPrefix = "gml";
    private String gmlNamespace = "http://www.opengis.net/gml/3.2";
    private String schemaLocationName = "schemaLocation";
    private String schemaLocationValue = "http://www.gardenmodel.de/gardenmodel Gardenmodel.xsd";
    private String gardenModel_garden = "Garden";
    private String boundedBy = "boundedBy";
    private String envelope = "Envelope";
    private String pos = "pos";
    private String lowerCorner = "lowerCorner";
    private String upperCorner = "upperCorner";
    private String layerMember = "layerMember";
    private String vectorlayer = "VectorLayer";
    private String layerName = "layername";
    private String geoobjectMember = "geoobjectMember";
    private String plantLayer = "PlantLayer";
    private String topologicalMap = "TopologicalMap";
    private String rasterLayer = "RasterLayer";
    private String rasterXsize = "x_size";
    private String rasterYsize = "y_size";
    private String rasterBitmap = "bitmap";
    private String point = "Point";
    private String pointCenter = "center";
    private String line = "Line";
    private String lineLine = "line";
    private String polygon = "Polygon";
    private String polygonSurface = "surface";
    private String plant = "Plant";
    private String plantName = "plantName";
    private String plantType = "plantType";
    private String plantTimeLine = "timeLine";
    private String topologicalNode = "TopologicalNode";
    private String topologicalEdge = "TopologicalEdge";
    private String lineString = "LineString";
    private String posList = "posList";
    private String exterior = "exterior";
    private String linearRing = "LinearRing";
    private String plantTimeValue = "PlantTimeValue";
    private String plantTimeValueStateDescription = "stateDescription";
    private String plantTimeValueHeight = "height";
    private String timeStamp = "validTime";
    private String timeInstant = "TimeInstant";
    private String timePosition = "timePosition";
    private String xlinkNamespace = "http://www.w3.org/1999/xlink";
    private String xlinkPrefix = "xlink";
    private String xlinghref = "href";
    private String actionpoint = "ActionPoint";
    private String action = "action";
    private String building = "building";
    private String buildingName = "buildingName";
    private String gardenObjectCircle = "GardenObjectCircle";
    private String gardenObjectName = "gardenObjectName";
    private String width = "width";
    private String gardenObjectPolygon = "GardenObjectPolygon";
    private String ground = "Ground";
    private String groundType = "groundType";
    private String patch = "Patch";
    private String rail = "Rail";
    private String material = "material";
    private String reminder = "Reminder";
    private String description = "description";
    private String date = "date";
    private String rfidLandmark = "RFIDLandmark";
    private String rfidIdentifier = "rfidIdentifier";
    private String gardenConstructsLayer = "GardenConstructsLayer";
    private String groundLayer = "GroundLayer";
    private String reminderLayer = "ReminderLayer";
    private String robotLayer = "RobotLayer";
    private String sensorLayer = "SensorLayer";

    private HashMap<Integer, VectorObject> vectorObjects = new HashMap<>();
    private boolean missingReference = false;

    public Garden readImputFile(String uri, Garden newGarden) {
        Garden result = newGarden;
        do {
            missingReference = false;
            InputStream stream = null;
            try {
                stream = new FileInputStream(uri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            result = parseGardenFromStream(stream, newGarden);
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        while (missingReference);
        return result;
    }

    public Garden parseGardenFromStream(InputStream stream, Garden newGarden) {
        Garden garden = newGarden;
        if (stream != null) {

            try {
                XmlPullParser parser = Xml.newPullParser();
                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
                parser.setInput(stream, null);
                parser.nextTag();
                readFeed(parser, garden);
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return garden;
    }

    private void readFeed(XmlPullParser parser, Garden garden) throws IOException, XmlPullParserException {
        Log.d("Import", "Namespace: " + parser.getNamespace());
        Log.d("Import", "Name: " + parser.getName());
        parser.require(XmlPullParser.START_TAG, null, gardenModel_garden);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            Log.d("Import", "Name: " + parser.getName());
            if (name.equals(boundedBy)) {
                BoundaryBox boundaryBox = readBoundedBy(parser);
                garden.setBoundary(boundaryBox);
            } else if (name.equals(layerMember)) {
                GardenLayer layer = readGardenLayer(parser);
                garden.addLayer(layer);
            } else {
                skip(parser);
            }

        }
    }

    private GardenLayer readGardenLayer(XmlPullParser parser) throws IOException, XmlPullParserException {
        GardenLayer result = null;
        parser.require(XmlPullParser.START_TAG, null, layerMember);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            Log.d("Import", "Name: " + parser.getName());
            if (name.equals(groundLayer)) {
                result = readGroundLayer(parser);
            } else if (name.equals(gardenConstructsLayer)) {
                result = readGardenConstructsLayer(parser);
            } else if (name.equals(reminderLayer)) {
                result = readReminderLayer(parser);
            } else if (name.equals(robotLayer)) {
                result = readRobotLayer(parser);
            } else if (name.equals(sensorLayer)) {
                result = readSensorLayer(parser);
            } else if (name.equals(plantLayer)) {
                result = readPlantLayer(parser);
            } else if (name.equals(topologicalMap)) {
                result = readTopologicalMap(parser);
            } else {
                skip(parser);
            }
        }
        parser.require(XmlPullParser.END_TAG, null, layerMember);
        return result;
    }

    private GardenLayer readTopologicalMap(XmlPullParser parser) throws IOException, XmlPullParserException {
        TopologicalMap result = null;
        parser.require(XmlPullParser.START_TAG, null, topologicalMap);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();
            if (name.equals(layerName)) {
                String layername = readLayerName(parser);
                result = new TopologicalMap(layername);
            } else if (name.equals(geoobjectMember)) {
                VectorObject object = readGeoobjectMember(parser);
                result.addVectorObject(object);
                vectorObjects.put(object.getVectorObjectID(), object);
            } else {
                skip(parser);
            }
        }

        parser.require(XmlPullParser.END_TAG, null, topologicalMap);
        return result;
    }

    private GardenLayer readPlantLayer(XmlPullParser parser) throws IOException, XmlPullParserException {
        PlantLayer result = null;
        parser.require(XmlPullParser.START_TAG, null, plantLayer);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();
            if (name.equals(layerName)) {
                String layername = readLayerName(parser);
                result = new PlantLayer(layername);
            } else if (name.equals(geoobjectMember)) {
                VectorObject object = readGeoobjectMember(parser);
                result.addVectorObject(object);
                vectorObjects.put(object.getVectorObjectID(), object);
            } else {
                skip(parser);
            }
        }

        parser.require(XmlPullParser.END_TAG, null, plantLayer);
        return result;
    }

    private GardenLayer readSensorLayer(XmlPullParser parser) throws IOException, XmlPullParserException {
        SensorLayer result = null;
        parser.require(XmlPullParser.START_TAG, null, sensorLayer);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();
            if (name.equals(layerName)) {
                String layername = readLayerName(parser);
                result = new SensorLayer(layername);
            } else if (name.equals(geoobjectMember)) {
                VectorObject object = readGeoobjectMember(parser);
                result.addVectorObject(object);
                vectorObjects.put(object.getVectorObjectID(), object);
            } else {
                skip(parser);
            }
        }

        parser.require(XmlPullParser.END_TAG, null, sensorLayer);
        return result;
    }

    private GardenLayer readRobotLayer(XmlPullParser parser) throws IOException, XmlPullParserException {
        RobotLayer result = null;
        parser.require(XmlPullParser.START_TAG, null, robotLayer);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();
            if (name.equals(layerName)) {
                String layername = readLayerName(parser);
                result = new RobotLayer(layername);
            } else if (name.equals(geoobjectMember)) {
                VectorObject object = readGeoobjectMember(parser);
                result.addVectorObject(object);
                vectorObjects.put(object.getVectorObjectID(), object);
            } else {
                skip(parser);
            }
        }

        parser.require(XmlPullParser.END_TAG, null, robotLayer);
        return result;
    }

    private GardenLayer readReminderLayer(XmlPullParser parser) throws IOException, XmlPullParserException {
        ReminderLayer result = null;
        parser.require(XmlPullParser.START_TAG, null, reminderLayer);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();
            if (name.equals(layerName)) {
                String layername = readLayerName(parser);
                result = new ReminderLayer(layername);
            } else if (name.equals(geoobjectMember)) {
                VectorObject object = readGeoobjectMember(parser);
                result.addVectorObject(object);
                vectorObjects.put(object.getVectorObjectID(), object);
            } else {
                skip(parser);
            }
        }

        parser.require(XmlPullParser.END_TAG, null, reminderLayer);
        return result;
    }

    private GardenLayer readGardenConstructsLayer(XmlPullParser parser) throws IOException, XmlPullParserException {
        GardenConstructsLayer result = null;
        parser.require(XmlPullParser.START_TAG, null, gardenConstructsLayer);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();
            if (name.equals(layerName)) {
                String layername = readLayerName(parser);
                result = new GardenConstructsLayer(layername);
            } else if (name.equals(geoobjectMember)) {
                VectorObject object = readGeoobjectMember(parser);
                result.addVectorObject(object);
                vectorObjects.put(object.getVectorObjectID(), object);
            } else {
                skip(parser);
            }
        }

        parser.require(XmlPullParser.END_TAG, null, gardenConstructsLayer);
        return result;
    }

    private GardenLayer readGroundLayer(XmlPullParser parser) throws IOException, XmlPullParserException {
        GroundLayer result = null;
        parser.require(XmlPullParser.START_TAG, null, groundLayer);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();
            if (name.equals(layerName)) {
                String layername = readLayerName(parser);
                result = new GroundLayer(layername);
            } else if (name.equals(geoobjectMember)) {
                VectorObject object = readGeoobjectMember(parser);
                result.addVectorObject(object);
                vectorObjects.put(object.getVectorObjectID(), object);
            } else {
                skip(parser);
            }
        }

        parser.require(XmlPullParser.END_TAG, null, groundLayer);
        return result;
    }

    private VectorObject readGeoobjectMember(XmlPullParser parser) throws IOException, XmlPullParserException {
        VectorObject result = null;
        parser.require(XmlPullParser.START_TAG, null, geoobjectMember);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();
            if (name.equals(rfidLandmark)) {
                result = readRFIDLandmark(parser);
            } else if (name.equals(reminder)) {
                result = readReminder(parser);
            } else if (name.equals(gardenObjectCircle)) {
                result = readGardenObjectCircle(parser);
            } else if (name.equals(actionpoint)) {
                result = readActionPoint(parser);
            } else if (name.equals(plant)) {
                result = readPlant(parser);
            } else if (name.equals(topologicalNode)) {
                result = readTopologicalNode(parser);
            } else if (name.equals(rail)) {
                result = readRail(parser);
            } else if (name.equals(topologicalEdge)) {
                result = readTopologicalEdge(parser);
            } else if (name.equals(patch)) {
                result = readPatch(parser);
            } else if (name.equals(ground)) {
                result = readGround(parser);
            } else if (name.equals(gardenObjectPolygon)) {
                result = readGardenObjectPolygon(parser);
            } else if (name.equals(building)) {
                result = readBuilding(parser);
            } else {
                skip(parser);
            }
        }
        parser.require(XmlPullParser.END_TAG, null, geoobjectMember);
        return result;
    }

    private VectorObject readBuilding(XmlPullParser parser) throws IOException, XmlPullParserException {
        Building result = null;
        parser.require(XmlPullParser.START_TAG, null, building);
        String geoid = parser.getAttributeValue(null, "id");
        result = new Building(Integer.valueOf(geoid));
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals(polygonSurface)) {
                List<Geoposition> positions = readPolygonSuface(parser);
                result.replacePositions(positions);
            }
            else if (name.equals(buildingName)) {
                String string = readText(parser);
                result.setBuildingName(string);
            } else {
                skip(parser);
            }
        }
        parser.require(XmlPullParser.END_TAG, null, building);
        return result;
    }

    private VectorObject readGardenObjectPolygon(XmlPullParser parser) throws IOException, XmlPullParserException {
        GardenObjectPolygon result = null;
        parser.require(XmlPullParser.START_TAG, null, gardenObjectPolygon);
        String geoid = parser.getAttributeValue(null, "id");
        result = new GardenObjectPolygon(Integer.valueOf(geoid));
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals(polygonSurface)) {
                List<Geoposition> positions = readPolygonSuface(parser);
                result.replacePositions(positions);
            } else if (name.equals(gardenObjectName)) {
                String string = readText(parser);
                result.setGardenObjectName(string);
            } else {
                skip(parser);
            }
        }
        parser.require(XmlPullParser.END_TAG, null, gardenObjectPolygon);
        return result;
    }

    private VectorObject readPatch(XmlPullParser parser) throws IOException, XmlPullParserException {
        Patch result = null;
        parser.require(XmlPullParser.START_TAG, null, patch);
        String geoid = parser.getAttributeValue(null, "id");
        result = new Patch(Integer.valueOf(geoid));
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals(polygonSurface)) {
                List<Geoposition> positions = readPolygonSuface(parser);
                result.replacePositions(positions);
            } else {
                skip(parser);
            }
        }
        parser.require(XmlPullParser.END_TAG, null, patch);
        return result;
    }

    private VectorObject readTopologicalEdge(XmlPullParser parser) throws IOException, XmlPullParserException {
        TopologicalEdge result = null;
        parser.require(XmlPullParser.START_TAG, null, topologicalEdge);
        String geoid = parser.getAttributeValue(null, "id");
        result = new TopologicalEdge(Integer.valueOf(geoid));
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("node1")) {
                String string = parser.getAttributeValue(null, xlinghref).replace("#", "");
                TopologicalNode object = (TopologicalNode) vectorObjects.get(Integer.valueOf(string));
                if (object != null) {
                    result.setNode1(object);
                } else {
                    missingReference = true;
                }
                parser.nextTag();
            } else if (name.equals("node2")) {
                String string = parser.getAttributeValue(null, xlinghref).replace("#", "");
                TopologicalNode object = (TopologicalNode) vectorObjects.get(Integer.valueOf(string));
                if (object != null) {
                    result.setNode2(object);
                } else {
                    missingReference = true;
                }
                parser.nextTag();
            } else {
                skip(parser);
            }
        }
        parser.require(XmlPullParser.END_TAG, null, topologicalEdge);
        return result;
    }

    private VectorObject readRail(XmlPullParser parser) throws IOException, XmlPullParserException {
        Rail result = null;
        parser.require(XmlPullParser.START_TAG, null, rail);
        String geoid = parser.getAttributeValue(null, "id");
        result = new Rail(Integer.valueOf(geoid));
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals(lineLine)) {
                List<Geoposition> positions = readLineline(parser);
                result.replacePositions(positions);
            } else if (name.equals(material)) {
                String string = readText(parser);
                result.setMaterial(string);
            } else {
                skip(parser);
            }
        }
        parser.require(XmlPullParser.END_TAG, null, rail);
        return result;
    }

    private List<Geoposition> readLineline(XmlPullParser parser) throws IOException, XmlPullParserException {
        List<Geoposition> result = new ArrayList<>();
        parser.require(XmlPullParser.START_TAG, null, lineLine);
        while (parser.nextTag() == XmlPullParser.START_TAG && !parser.getName().equals(posList)) {
        }
        result = readCoordinateList(parser);
        while (parser.nextTag() == XmlPullParser.END_TAG && !parser.getName().equals(lineLine)) {
        }
        parser.require(XmlPullParser.END_TAG, null, lineLine);
        return result;

    }

    private VectorObject readTopologicalNode(XmlPullParser parser) throws IOException, XmlPullParserException {
        TopologicalNode result = null;
        parser.require(XmlPullParser.START_TAG, null, topologicalNode);
        String geoid = parser.getAttributeValue(null, "id");
        result = new TopologicalNode(Integer.valueOf(geoid));
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals(pointCenter)) {
                Geoposition position = readPointCenter(parser);
                result.setPosition(position);
            } else {
                skip(parser);
            }
        }
        parser.require(XmlPullParser.END_TAG, null, topologicalNode);
        return result;
    }

    private VectorObject readPlant(XmlPullParser parser) throws IOException, XmlPullParserException {
        Plant result = null;
        parser.require(XmlPullParser.START_TAG, null, plant);
        String geoid = parser.getAttributeValue(null, "id");
        result = new Plant(Integer.valueOf(geoid));
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            Log.d("Import", "Name: " + parser.getName());
            if (name.equals(pointCenter)) {
                Geoposition position = readPointCenter(parser);
                result.setPosition(position);
            } else if (name.equals(plantName)) {
                String string = readText(parser);
                result.setPlantname(string);
            } else if (name.equals(plantType)) {
                String string = readText(parser);
                result.setPlanttype(string);
            } else if (name.equals(plantTimeLine)) {
                List<PlantTimeValue> timeValues = readPlantTimeLine(parser);
                for (PlantTimeValue timeValue : timeValues) {
                    result.getTimeLine().addTimeValue(timeValue);
                }
            } else {
                skip(parser);
            }
        }
        parser.require(XmlPullParser.END_TAG, null, plant);
        return result;
    }

    private List<PlantTimeValue> readPlantTimeLine(XmlPullParser parser) throws IOException, XmlPullParserException {
        List<PlantTimeValue> result = new ArrayList<>();
        parser.require(XmlPullParser.START_TAG, null, plantTimeLine);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();
            if (name.equals(plantTimeValue)) {
                PlantTimeValue tm = readPlantTimeValue(parser);
                result.add(tm);
            } else {
                skip(parser);
            }
        }
        parser.require(XmlPullParser.END_TAG, null, plantTimeLine);
        return result;
    }

    private PlantTimeValue readPlantTimeValue(XmlPullParser parser) throws IOException, XmlPullParserException {
        PlantTimeValue result = new PlantTimeValue();
        parser.require(XmlPullParser.START_TAG, null, plantTimeValue);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();
            if (name.equals(timeStamp)) {
                //TODO:
                Date date = readTimeStamp(parser);
                result.getTimeStamp().setTime(date.getTime());
            } else if (name.equals(plantTimeValueStateDescription)) {
                String string = readText(parser);
                result.setStateDescription(string);
            } else if (name.equals(plantTimeValueHeight)) {
                String string = readText(parser);
                result.setHeight(Float.valueOf(string));
            } else if (name.equals(width)) {
                String string = readText(parser);
                result.setWidth(Float.valueOf(string));
            } else {
                skip(parser);
            }
        }
        parser.require(XmlPullParser.END_TAG, null, plantTimeValue);
        return result;
    }

    private Date readTimeStamp(XmlPullParser parser) throws IOException, XmlPullParserException {
        Date result = null;
        parser.require(XmlPullParser.START_TAG, null, timeStamp);
        while (parser.nextTag() == XmlPullParser.START_TAG && !parser.getName().equals(timePosition)) {
        }
        result = readDate(parser);
        while (parser.nextTag() == XmlPullParser.END_TAG && !parser.getName().equals(timeStamp)) {
        }
        parser.require(XmlPullParser.END_TAG, null, timeStamp);

        return result;
    }

    private VectorObject readActionPoint(XmlPullParser parser) throws IOException, XmlPullParserException {
        ActionPoint result = null;
        parser.require(XmlPullParser.START_TAG, null, actionpoint);
        String geoid = parser.getAttributeValue(null, "id");
        result = new ActionPoint(Integer.valueOf(geoid));
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals(pointCenter)) {
                Geoposition position = readPointCenter(parser);
                result.setPosition(position);
            } else if (name.equals(action)) {
                String string = readText(parser);
                result.setAction(ActionPoint.Action.valueOf(string));
            } else {
                skip(parser);
            }
        }
        parser.require(XmlPullParser.END_TAG, null, actionpoint);
        return result;
    }

    private VectorObject readGardenObjectCircle(XmlPullParser parser) throws IOException, XmlPullParserException {
        GardenObjectCircle result = null;
        parser.require(XmlPullParser.START_TAG, null, gardenObjectCircle);
        String geoid = parser.getAttributeValue(null, "id");
        result = new GardenObjectCircle(Integer.valueOf(geoid));
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals(pointCenter)) {
                Geoposition position = readPointCenter(parser);
                result.setPosition(position);
            } else if (name.equals(gardenObjectName)) {
                String string = readText(parser);
                result.setGardenObjectName(string);
            } else if (name.equals(width)) {
                String string = readText(parser);
                result.setWidth(Float.valueOf(string));
            } else {
                skip(parser);
            }
        }
        parser.require(XmlPullParser.END_TAG, null, gardenObjectCircle);
        return result;
    }

    private VectorObject readReminder(XmlPullParser parser) throws IOException, XmlPullParserException {
        Reminder result = null;
        parser.require(XmlPullParser.START_TAG, null, reminder);
        String geoid = parser.getAttributeValue(null, "id");
        result = new Reminder(Integer.valueOf(geoid));
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();

            if (name.equals(pointCenter)) {
                Geoposition position = readPointCenter(parser);
                result.setPosition(position);
            } else if (name.equals(description)) {
                String string = readText(parser);
                result.setDescription(string);
            } else if (name.equals(date)) {
                Date date1 = readDate(parser);
                result.setDate(date1);
            } else {
                skip(parser);
            }
        }
        parser.require(XmlPullParser.END_TAG, null, reminder);
        return result;
    }

    private Date readDate(XmlPullParser parser) throws IOException, XmlPullParserException {
        Date result = new Date();
        if (parser.next() == XmlPullParser.TEXT) {
            String string = parser.getText();
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            try {
                result = df.parse(string);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            parser.nextTag();
        }
        return result;
    }

    private VectorObject readRFIDLandmark(XmlPullParser parser) throws IOException, XmlPullParserException {
        RFIDLandmark result = null;
        parser.require(XmlPullParser.START_TAG, null, rfidLandmark);
        String geoid = parser.getAttributeValue(null, "id");
        result = new RFIDLandmark(Integer.valueOf(geoid));
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals(pointCenter)) {
                Geoposition position = readPointCenter(parser);
                result.setPosition(position);
            } else if (name.equals(rfidIdentifier)) {
                String string = readText(parser);
                result.setRfidIdentifier(Integer.valueOf(string));
            } else {
                skip(parser);
            }
        }

        parser.require(XmlPullParser.END_TAG, null, rfidLandmark);
        return result;
    }

    private Geoposition readPointCenter(XmlPullParser parser) throws IOException, XmlPullParserException {
        Geoposition result = null;
        parser.require(XmlPullParser.START_TAG, null, pointCenter);
        while (parser.nextTag() == XmlPullParser.START_TAG && !parser.getName().equals(pos)) {
        }
        result = readCoordinates(parser);
        while (parser.nextTag() == XmlPullParser.END_TAG && !parser.getName().equals(pointCenter)) {
        }
        parser.require(XmlPullParser.END_TAG, null, pointCenter);
        return result;
    }

    private VectorObject readGround(XmlPullParser parser) throws IOException, XmlPullParserException {
        Ground result = null;
        parser.require(XmlPullParser.START_TAG, null, ground);
        String geoid = parser.getAttributeValue(null, "id");
        result = new Ground(Integer.valueOf(geoid));
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals(polygonSurface)) {
                List<Geoposition> positions = readPolygonSuface(parser);
                result.replacePositions(positions);
            } else if (name.equals(groundType)) {
                String string = readText(parser);
                result.setGroundType(Ground.GroundType.valueOf(string));
            } else {
                skip(parser);
            }
        }
        parser.require(XmlPullParser.END_TAG, null, ground);
        return result;
    }

    private List<Geoposition> readPolygonSuface(XmlPullParser parser) throws IOException, XmlPullParserException {
        List<Geoposition> result = new ArrayList<>();
        parser.require(XmlPullParser.START_TAG, null, polygonSurface);
        while (parser.nextTag() == XmlPullParser.START_TAG && !parser.getName().equals(posList)) {
        }
        result = readCoordinateList(parser);
        while (parser.nextTag() == XmlPullParser.END_TAG && !parser.getName().equals(polygonSurface)) {
        }
        parser.require(XmlPullParser.END_TAG, null, polygonSurface);
        return result;
    }

    private List<Geoposition> readCoordinateList(XmlPullParser parser) throws IOException, XmlPullParserException {
        List<Geoposition> result = new ArrayList<>();
        parser.require(XmlPullParser.START_TAG, null, posList);
        if (parser.next() == XmlPullParser.TEXT) {
            String s = parser.getText();
            String[] positions = s.split("\\s");
            for (int i = 0; i < positions.length; i = i + 2) {
                Double lat = Double.valueOf(positions[i]);
                Double lon = Double.valueOf(positions[i + 1]);
                result.add(new Geoposition(lat, lon));
            }
            parser.nextTag();
        }
        parser.require(XmlPullParser.END_TAG, null, posList);
        return result;
    }

    private String readLayerName(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = null;
        parser.require(XmlPullParser.START_TAG, null, layerName);
        result = readText(parser);
        parser.require(XmlPullParser.END_TAG, null, layerName);
        return result;
    }

    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private BoundaryBox readBoundedBy(XmlPullParser parser) throws IOException, XmlPullParserException {
        BoundaryBox result = null;
        parser.require(XmlPullParser.START_TAG, null, boundedBy);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals(envelope)) {
                Geoposition low = null;
                Geoposition up = null;
                if (parser.nextTag() == XmlPullParser.START_TAG && parser.getName().equals(lowerCorner)) {
                    low = readCoordinates(parser);
                }
                if (parser.nextTag() == XmlPullParser.START_TAG && parser.getName().equals(upperCorner)) {
                    up = readCoordinates(parser);
                }
                if (low != null && up != null) {
                    result = new BoundaryBox(low, up);
                }
                parser.nextTag();
                parser.require(XmlPullParser.END_TAG, null, envelope);
            } else {
                skip(parser);
            }
        }
        parser.require(XmlPullParser.END_TAG, null, boundedBy);
        return result;
    }

    private Geoposition readCoordinates(XmlPullParser parser) throws IOException, XmlPullParserException {
        Geoposition result = null;
        if (parser.next() == XmlPullParser.TEXT) {
            String text = parser.getText();
            String[] coordinates = text.split("\\s");
            double lat = Double.valueOf(coordinates[0]);
            double lon = Double.valueOf(coordinates[1]);
            result = new Geoposition(lat, lon);
            parser.nextTag();
        }
        return result;
    }


    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException(parser.getName());
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}
