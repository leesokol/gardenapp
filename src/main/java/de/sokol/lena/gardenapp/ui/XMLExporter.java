package de.sokol.lena.gardenapp.ui;

import android.os.Environment;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import de.sokol.lena.gardenapp.model.ActionPoint;
import de.sokol.lena.gardenapp.model.Building;
import de.sokol.lena.gardenapp.model.Garden;
import de.sokol.lena.gardenapp.model.Layer.GardenConstructsLayer;
import de.sokol.lena.gardenapp.model.Layer.GardenLayer;
import de.sokol.lena.gardenapp.model.GardenObjectCircle;
import de.sokol.lena.gardenapp.model.GardenObjectPolygon;
import de.sokol.lena.gardenapp.model.Geoposition;
import de.sokol.lena.gardenapp.model.Ground;
import de.sokol.lena.gardenapp.model.Layer.GroundLayer;
import de.sokol.lena.gardenapp.model.Layer.ReminderLayer;
import de.sokol.lena.gardenapp.model.Layer.RobotLayer;
import de.sokol.lena.gardenapp.model.Layer.SensorLayer;
import de.sokol.lena.gardenapp.model.Line;
import de.sokol.lena.gardenapp.model.Patch;
import de.sokol.lena.gardenapp.model.Plant;
import de.sokol.lena.gardenapp.model.Layer.PlantLayer;
import de.sokol.lena.gardenapp.model.PlantTimeValue;
import de.sokol.lena.gardenapp.model.Polygon;
import de.sokol.lena.gardenapp.model.RFIDLandmark;
import de.sokol.lena.gardenapp.model.Rail;
import de.sokol.lena.gardenapp.model.Layer.RasterLayer;
import de.sokol.lena.gardenapp.model.Reminder;
import de.sokol.lena.gardenapp.model.TopologicalEdge;
import de.sokol.lena.gardenapp.model.Layer.TopologicalMap;
import de.sokol.lena.gardenapp.model.TopologicalNode;
import de.sokol.lena.gardenapp.model.Layer.VectorLayer;
import de.sokol.lena.gardenapp.model.VectorObject;

/**
 * Exports the garden as an xml file fitting the gml schema
 *
 * Created by Lena on 23.03.2015.
 */
public class XMLExporter {

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

    public static XMLExporter newInstance() {
        return new XMLExporter();
    }

    public void writeToFile(String xmlData) {
        File file = new File(Environment.getExternalStorageDirectory(), "export.xml");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(xmlData.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String exportGardenToXML(Garden garden) {
        String result = null;
        try {
            XmlSerializer xmlSerializer = Xml.newSerializer();
            StringWriter writer = new StringWriter();
            xmlSerializer.setOutput(writer);
            xmlSerializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
            xmlSerializer.startDocument("UTF-8", null);
            xmlSerializer.setPrefix(gardenmodelPrefix, gardenmodelNamespace);
            xmlSerializer.setPrefix(xsiPrefix, xsiNamespace);
            xmlSerializer.setPrefix(gmlPrefix, gmlNamespace);
            xmlSerializer.setPrefix(xlinkPrefix, xlinkNamespace);
            //Build Garden Object
            if (garden != null && garden.getBoundary() != null) {
                serializeGardenObject(garden, xmlSerializer);
            }
            xmlSerializer.endDocument();
            xmlSerializer.flush();
            result = writer.toString();
            Log.d("XML", result);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private void serializeGardenObject(Garden garden, XmlSerializer xmlSerializer) throws IOException {
        xmlSerializer.startTag(gardenmodelNamespace, gardenModel_garden);
        xmlSerializer.attribute(xsiNamespace, schemaLocationName, schemaLocationValue);
        xmlSerializer.attribute(gmlNamespace, "id", "Garden1");
        //Bounding
        xmlSerializer.startTag(gmlNamespace, boundedBy);
        xmlSerializer.startTag(gmlNamespace, envelope);
        xmlSerializer.startTag(gmlNamespace, lowerCorner);
        xmlSerializer.text("" + garden.getBoundary().minLatitude + " " + garden.getBoundary().minLongitude);
        xmlSerializer.endTag(gmlNamespace, lowerCorner);
        xmlSerializer.startTag(gmlNamespace, upperCorner);
        xmlSerializer.text("" + garden.getBoundary().maxLatitude + " " + garden.getBoundary().maxLongitude);
        xmlSerializer.endTag(gmlNamespace, upperCorner);
        xmlSerializer.endTag(gmlNamespace, envelope);
        xmlSerializer.endTag(gmlNamespace, boundedBy);
        //Layers
        int i = 0;
        for (GardenLayer layer : garden.getLayers()) {
            xmlSerializer.startTag(gardenmodelNamespace, layerMember);
            serializeLayerObject(layer, xmlSerializer, i);
            xmlSerializer.endTag(gardenmodelNamespace, layerMember);
            i++;
        }
        xmlSerializer.endTag(gardenmodelNamespace, gardenModel_garden);
    }

    private void serializeLayerObject(GardenLayer layer, XmlSerializer xmlSerializer, int i) throws IOException {
        if (layer instanceof VectorLayer) {
            String layerType = vectorlayer;
            if(layer instanceof GardenConstructsLayer){
                layerType= gardenConstructsLayer;
            }else
            if(layer instanceof GroundLayer){
                layerType = groundLayer;
            }else
            if(layer instanceof ReminderLayer){
                layerType= reminderLayer;
            }else
            if(layer instanceof RobotLayer){
                layerType= robotLayer;
            }else
            if (layer instanceof SensorLayer){
                layerType=sensorLayer;
            }
            if (layer instanceof PlantLayer) {
                layerType = plantLayer;
            } else if (layer instanceof TopologicalMap) {
                layerType = topologicalMap;
            }
            serializeStartOfVectorLayer(layerType, i, xmlSerializer, layer);
            //Add all the VectorObjects
            for (VectorObject object : ((VectorLayer) layer).getVectorObjects()) {
                xmlSerializer.startTag(gardenmodelNamespace, geoobjectMember);
                serializeVectorObject(xmlSerializer, object);
                xmlSerializer.endTag(gardenmodelNamespace, geoobjectMember);
            }
            xmlSerializer.endTag(gardenmodelNamespace, layerType);

        } else if (layer instanceof RasterLayer) {
            xmlSerializer.startTag(gardenmodelNamespace, rasterLayer);
            xmlSerializer.attribute(gmlNamespace, "id", "RL" + i);
            xmlSerializer.startTag(gardenmodelNamespace, layerName);
            xmlSerializer.text(layer.getName());
            xmlSerializer.endTag(gardenmodelNamespace, layerName);
            xmlSerializer.startTag(gardenmodelNamespace, rasterXsize);
            xmlSerializer.text("" + ((RasterLayer) layer).getX_limit());
            xmlSerializer.endTag(gardenmodelNamespace, rasterXsize);
            xmlSerializer.startTag(gardenmodelNamespace, rasterYsize);
            xmlSerializer.text("" + ((RasterLayer) layer).getY_limit());
            xmlSerializer.endTag(gardenmodelNamespace, rasterYsize);
            //TODO:Add Reference to a Bitmap
            xmlSerializer.endTag(gardenmodelNamespace, rasterLayer);
        }
    }

    private void serializeStartOfVectorLayer(String layerType, int i, XmlSerializer xmlSerializer, GardenLayer layer) throws IOException {
        xmlSerializer.startTag(gardenmodelNamespace, layerType);
        xmlSerializer.attribute(gmlNamespace, "id", "VL" + i);
        xmlSerializer.startTag(gardenmodelNamespace, layerName);
        xmlSerializer.text(layer.getName());
        xmlSerializer.endTag(gardenmodelNamespace, layerName);
    }

    private void serializeVectorObject(XmlSerializer xmlSerializer, VectorObject object) throws IOException {
        String vectorObjectType = null;
        if (object instanceof de.sokol.lena.gardenapp.model.Point) {
            if (object instanceof RFIDLandmark) {
                vectorObjectType = rfidLandmark;
                xmlSerializer.startTag(gardenmodelNamespace, vectorObjectType);
                xmlSerializer.attribute(gmlNamespace, "id", "" + object.getVectorObjectID());
                serializePointCenter(xmlSerializer, object);
                RFIDLandmark rfidLandmark = (RFIDLandmark) object;
                if (rfidLandmark.getRfidIdentifier() != null) {
                    xmlSerializer.startTag(gardenmodelNamespace, rfidIdentifier);
                    xmlSerializer.text("" + rfidLandmark.getRfidIdentifier().toString());
                    xmlSerializer.endTag(gardenmodelNamespace, rfidIdentifier);
                }
            } else if (object instanceof Reminder) {
                vectorObjectType = reminder;
                xmlSerializer.startTag(gardenmodelNamespace, vectorObjectType);
                xmlSerializer.attribute(gmlNamespace, "id", "" + object.getVectorObjectID());
                serializePointCenter(xmlSerializer, object);
                Reminder reminder = (Reminder) object;
                if (reminder.getDescription() != null) {
                    xmlSerializer.startTag(gardenmodelNamespace, description);
                    xmlSerializer.text(reminder.getDescription());
                    xmlSerializer.endTag(gardenmodelNamespace, description);
                }
                if (reminder.getDate() != null) {
                    xmlSerializer.startTag(gardenmodelNamespace, date);
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                    String ISOdate = df.format(reminder.getDate());
                    xmlSerializer.text(ISOdate);
                    xmlSerializer.endTag(gardenmodelNamespace, date);
                }
            } else if (object instanceof GardenObjectCircle) {
                vectorObjectType = gardenObjectCircle;
                xmlSerializer.startTag(gardenmodelNamespace, vectorObjectType);
                xmlSerializer.attribute(gmlNamespace, "id", "" + object.getVectorObjectID());
                serializePointCenter(xmlSerializer, object);
                GardenObjectCircle circle = (GardenObjectCircle) object;
                if (circle.getGardenObjectName() != null) {
                    xmlSerializer.startTag(gardenmodelNamespace, gardenObjectName);
                    xmlSerializer.text(circle.getGardenObjectName());
                    xmlSerializer.endTag(gardenmodelNamespace, gardenObjectName);
                }
                if (circle.getWidth() != null) {
                    xmlSerializer.startTag(gardenmodelNamespace, width);
                    xmlSerializer.text("" + circle.getWidth());
                    xmlSerializer.endTag(gardenmodelNamespace, width);
                }
            } else if (object instanceof ActionPoint) {
                vectorObjectType = actionpoint;
                xmlSerializer.startTag(gardenmodelNamespace, vectorObjectType);
                xmlSerializer.attribute(gmlNamespace, "id", "" + object.getVectorObjectID());
                ActionPoint actionPoint = (ActionPoint) object;
                serializePointCenter(xmlSerializer, object);
                if (actionPoint.getAction() != null) {
                    xmlSerializer.startTag(gardenmodelNamespace, action);
                    xmlSerializer.text(actionPoint.getAction().toString());
                    xmlSerializer.endTag(gardenmodelNamespace, action);
                }
            } else if (object instanceof Plant) {
                vectorObjectType = plant;
                xmlSerializer.startTag(gardenmodelNamespace, vectorObjectType);
                xmlSerializer.attribute(gmlNamespace, "id", "" + object.getVectorObjectID());
                Plant plant = (Plant) object;
                serializePointCenter(xmlSerializer, object);
                if (plant.getPlantname() != null) {
                    xmlSerializer.startTag(gardenmodelNamespace, plantName);
                    xmlSerializer.text(plant.getPlantname());
                    xmlSerializer.endTag(gardenmodelNamespace, plantName);
                }
                if (plant.getPlanttype() != null) {
                    xmlSerializer.startTag(gardenmodelNamespace, plantType);
                    xmlSerializer.text(plant.getPlanttype());
                    xmlSerializer.endTag(gardenmodelNamespace, plantType);
                }
                if (!plant.getTimeLine().getTimeValues().isEmpty()) {
                    xmlSerializer.startTag(gardenmodelNamespace, plantTimeLine);
                    for (PlantTimeValue pTimeValue : plant.getTimeLine().getTimeValues()) {
                        xmlSerializer.startTag(gardenmodelNamespace, plantTimeValue);
                        xmlSerializer.attribute(gmlNamespace, "id", "Time" + pTimeValue.getTimeStamp().hashCode());
                        xmlSerializer.startTag(gmlNamespace, timeStamp);
                        xmlSerializer.startTag(gmlNamespace, timeInstant);
                        xmlSerializer.attribute(gmlNamespace, "id", "TimeInstant" + object.getVectorObjectID());
                        xmlSerializer.startTag(gmlNamespace, timePosition);
                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                        String ISOdate = df.format(pTimeValue.getTimeStamp());
                        xmlSerializer.text(ISOdate);
                        xmlSerializer.endTag(gmlNamespace, timePosition);
                        xmlSerializer.endTag(gmlNamespace, timeInstant);
                        xmlSerializer.endTag(gmlNamespace, timeStamp);
                        xmlSerializer.startTag(gardenmodelNamespace, plantTimeValueStateDescription);
                        xmlSerializer.text(pTimeValue.getStateDescription());
                        xmlSerializer.endTag(gardenmodelNamespace, plantTimeValueStateDescription);
                        xmlSerializer.startTag(gardenmodelNamespace, plantTimeValueHeight);
                        xmlSerializer.text("" + pTimeValue.getHeight());
                        xmlSerializer.endTag(gardenmodelNamespace, plantTimeValueHeight);
                        xmlSerializer.startTag(gardenmodelNamespace, width);
                        xmlSerializer.text("" + pTimeValue.getWidth());
                        xmlSerializer.endTag(gardenmodelNamespace, width);
                        xmlSerializer.endTag(gardenmodelNamespace, plantTimeValue);
                    }
                    xmlSerializer.endTag(gardenmodelNamespace, plantTimeLine);
                }

            } else if (object instanceof TopologicalNode) {
                vectorObjectType = topologicalNode;
                xmlSerializer.startTag(gardenmodelNamespace, vectorObjectType);
                xmlSerializer.attribute(gmlNamespace, "id", "" + object.getVectorObjectID());
                serializePointCenter(xmlSerializer, object);
            } else {
                vectorObjectType = point;
                xmlSerializer.startTag(gardenmodelNamespace, vectorObjectType);
                xmlSerializer.attribute(gmlNamespace, "id", "" + object.getVectorObjectID());
                serializePointCenter(xmlSerializer, object);
            }

        } else if (object instanceof Line) {
            if (object instanceof Rail) {
                vectorObjectType = rail;
                xmlSerializer.startTag(gardenmodelNamespace, vectorObjectType);
                xmlSerializer.attribute(gmlNamespace, "id", "" + object.getVectorObjectID());
                serializeLine(xmlSerializer, object);
                Rail rail = (Rail) object;
                if (rail.getMaterial() != null) {
                    xmlSerializer.startTag(gardenmodelNamespace, material);
                    xmlSerializer.text(rail.getMaterial());
                    xmlSerializer.endTag(gardenmodelNamespace, material);
                }
            } else if (object instanceof TopologicalEdge) {
                vectorObjectType = topologicalEdge;
                xmlSerializer.startTag(gardenmodelNamespace, vectorObjectType);
                xmlSerializer.attribute(gmlNamespace, "id", "" + object.getVectorObjectID());
                serializeLine(xmlSerializer, object);
                xmlSerializer.startTag(gardenmodelNamespace, "node1");
                xmlSerializer.attribute(xlinkNamespace, xlinghref, "#" + ((TopologicalEdge) object).getNode1().getVectorObjectID());
                xmlSerializer.endTag(gardenmodelNamespace, "node1");
                xmlSerializer.startTag(gardenmodelNamespace, "node2");
                xmlSerializer.attribute(xlinkNamespace, xlinghref, "#" + ((TopologicalEdge) object).getNode2().getVectorObjectID());
                xmlSerializer.endTag(gardenmodelNamespace, "node2");
            } else {
                vectorObjectType = line;
                xmlSerializer.startTag(gardenmodelNamespace, vectorObjectType);
                xmlSerializer.attribute(gmlNamespace, "id", "" + object.getVectorObjectID());
                serializeLine(xmlSerializer, object);
            }

        } else if (object instanceof Polygon) {
            if (object instanceof Patch) {
                vectorObjectType = patch;
                xmlSerializer.startTag(gardenmodelNamespace, vectorObjectType);
                xmlSerializer.attribute(gmlNamespace, "id", "" + object.getVectorObjectID());
                serializePolygonSurface(xmlSerializer, object);
            } else if (object instanceof Ground) {
                vectorObjectType = ground;
                xmlSerializer.startTag(gardenmodelNamespace, vectorObjectType);
                xmlSerializer.attribute(gmlNamespace, "id", "" + object.getVectorObjectID());
                serializePolygonSurface(xmlSerializer, object);
                Ground ground = (Ground) object;
                if (ground.getGroundType() != null) {
                    xmlSerializer.startTag(gardenmodelNamespace, groundType);
                    xmlSerializer.text(ground.getGroundType().toString());
                    xmlSerializer.endTag(gardenmodelNamespace, groundType);
                }
            } else if (object instanceof GardenObjectPolygon) {
                vectorObjectType = gardenObjectPolygon;
                xmlSerializer.startTag(gardenmodelNamespace, vectorObjectType);
                xmlSerializer.attribute(gmlNamespace, "id", "" + object.getVectorObjectID());
                serializePolygonSurface(xmlSerializer, object);
                GardenObjectPolygon polygon = (GardenObjectPolygon) object;
                if (polygon.getGardenObjectName() != null) {
                    xmlSerializer.startTag(gardenmodelNamespace, gardenObjectName);
                    xmlSerializer.text(polygon.getGardenObjectName());
                    xmlSerializer.endTag(gardenmodelNamespace, gardenObjectName);
                }
            } else if (object instanceof Building) {
                vectorObjectType = building;
                xmlSerializer.startTag(gardenmodelNamespace, vectorObjectType);
                xmlSerializer.attribute(gmlNamespace, "id", "" + object.getVectorObjectID());
                serializePolygonSurface(xmlSerializer, object);
                Building build = (Building) object;
                if (build.getBuildingName() != null) {
                    xmlSerializer.startTag(gardenmodelNamespace, buildingName);
                    xmlSerializer.text(build.getBuildingName());
                    xmlSerializer.endTag(gardenmodelNamespace, buildingName);
                }
            } else {
                vectorObjectType = polygon;
                xmlSerializer.startTag(gardenmodelNamespace, vectorObjectType);
                xmlSerializer.attribute(gmlNamespace, "id", "" + object.getVectorObjectID());
                serializePolygonSurface(xmlSerializer, object);
            }
        }
        xmlSerializer.endTag(gardenmodelNamespace, vectorObjectType);
    }

    private void serializePointCenter(XmlSerializer xmlSerializer, VectorObject object) throws IOException {
        xmlSerializer.startTag(gardenmodelNamespace, pointCenter);
        xmlSerializer.startTag(gmlNamespace, point);
        xmlSerializer.attribute(gmlNamespace, "id", "Point" + object.getVectorObjectID());
        xmlSerializer.startTag(gmlNamespace, pos);
        xmlSerializer.text("" + object.getGeoposition().latitude + " " + object.getGeoposition().longitude);
        xmlSerializer.endTag(gmlNamespace, pos);
        xmlSerializer.endTag(gmlNamespace, point);
        xmlSerializer.endTag(gardenmodelNamespace, pointCenter);
    }

    private void serializeLine(XmlSerializer xmlSerializer, VectorObject object) throws IOException {
        xmlSerializer.startTag(gardenmodelNamespace, lineLine);
        xmlSerializer.startTag(gmlNamespace, lineString);
        xmlSerializer.attribute(gmlNamespace, "id", "Line" + object.getVectorObjectID());
        xmlSerializer.startTag(gmlNamespace, posList);
        Geoposition[] positions = object.getGeopositions();
        StringBuilder stringBuilder = new StringBuilder(positions[0].latitude + " " + positions[0].longitude);
        for (int i = 1; i < positions.length; i++) {
            stringBuilder.append(" ").append(positions[i].latitude).append(" ").append(positions[i].longitude);
        }
        xmlSerializer.text(stringBuilder.toString());
        xmlSerializer.endTag(gmlNamespace, posList);
        xmlSerializer.endTag(gmlNamespace, lineString);
        xmlSerializer.endTag(gardenmodelNamespace, lineLine);
    }

    private void serializePolygonSurface(XmlSerializer xmlSerializer, VectorObject object) throws IOException {
        xmlSerializer.startTag(gardenmodelNamespace, polygonSurface);
        xmlSerializer.startTag(gmlNamespace, polygon);
        xmlSerializer.attribute(gmlNamespace, "id", "Polygon" + object.getVectorObjectID());
        xmlSerializer.startTag(gmlNamespace, exterior);
        xmlSerializer.startTag(gmlNamespace, linearRing);
        xmlSerializer.startTag(gmlNamespace, posList);
        Geoposition[] positions = object.getGeopositions();
        StringBuilder stringBuilder = new StringBuilder(positions[0].latitude + " " + positions[0].longitude);
        for (int i = 1; i < positions.length; i++) {
            stringBuilder.append(" ").append(positions[i].latitude).append(" ").append(positions[i].longitude);
        }
        xmlSerializer.text(stringBuilder.toString());
        xmlSerializer.endTag(gmlNamespace, posList);
        xmlSerializer.endTag(gmlNamespace, linearRing);
        xmlSerializer.endTag(gmlNamespace, exterior);
        xmlSerializer.endTag(gmlNamespace, polygon);
        xmlSerializer.endTag(gardenmodelNamespace, polygonSurface);
    }
}
