package de.sokol.lena.gardenapp.ui;

import de.sokol.lena.gardenapp.model.ActionPoint;
import de.sokol.lena.gardenapp.model.Building;
import de.sokol.lena.gardenapp.model.DummySensor;
import de.sokol.lena.gardenapp.model.GardenObjectCircle;
import de.sokol.lena.gardenapp.model.GardenObjectPolygon;
import de.sokol.lena.gardenapp.model.Ground;
import de.sokol.lena.gardenapp.model.Line;
import de.sokol.lena.gardenapp.model.Patch;
import de.sokol.lena.gardenapp.model.Plant;
import de.sokol.lena.gardenapp.model.Polygon;
import de.sokol.lena.gardenapp.model.RFIDLandmark;
import de.sokol.lena.gardenapp.model.Rail;
import de.sokol.lena.gardenapp.model.Reminder;
import de.sokol.lena.gardenapp.model.TopologicalEdge;
import de.sokol.lena.gardenapp.model.TopologicalNode;
import de.sokol.lena.gardenapp.model.VectorObject;
import de.sokol.lena.gardenapp.ui.Wrapper.ActionPointWrapper;
import de.sokol.lena.gardenapp.ui.Wrapper.BuildingWrapper;
import de.sokol.lena.gardenapp.ui.Wrapper.DummySensorWrapper;
import de.sokol.lena.gardenapp.ui.Wrapper.GardenObjectCircleWrapper;
import de.sokol.lena.gardenapp.ui.Wrapper.GardenObjectPolygonWrapper;
import de.sokol.lena.gardenapp.ui.Wrapper.GroundWrapper;
import de.sokol.lena.gardenapp.ui.Wrapper.IVectorobjectWrapper;
import de.sokol.lena.gardenapp.ui.Wrapper.LineWrapper;
import de.sokol.lena.gardenapp.ui.Wrapper.PatchWrapper;
import de.sokol.lena.gardenapp.ui.Wrapper.PlantWrapper;
import de.sokol.lena.gardenapp.ui.Wrapper.PointWrapper;
import de.sokol.lena.gardenapp.ui.Wrapper.PolygonWrapper;
import de.sokol.lena.gardenapp.ui.Wrapper.RFIDLandmarkWrapper;
import de.sokol.lena.gardenapp.ui.Wrapper.RailWrapper;
import de.sokol.lena.gardenapp.ui.Wrapper.ReminderWrapper;
import de.sokol.lena.gardenapp.ui.Wrapper.TopologicalEdgeWrapper;
import de.sokol.lena.gardenapp.ui.Wrapper.TopologicalNodeWrapper;

/**
 * Return the right wrapper object for a vector object
 *
 * Created by Lena on 24.02.2015.
 */
public class WrapperFactory {

    public static IVectorobjectWrapper createWrapper(VectorObject object) {
        //Not very nice, subclasses has to be checked first otherwise the wrong Wrapper may be chosen.
        //An other solution would be the Visitor Pattern.
        if (object instanceof TopologicalEdge) {
            return new TopologicalEdgeWrapper((TopologicalEdge) object);
        }
        if (object instanceof TopologicalNode) {
            return new TopologicalNodeWrapper((TopologicalNode) object);
        }
        if (object instanceof Plant) {
            return new PlantWrapper((Plant) object);
        }
        if (object instanceof ActionPoint){
            return new ActionPointWrapper((ActionPoint)object);
        }
        if (object instanceof Building){
            return new BuildingWrapper((Building)object);
        }
        if (object instanceof GardenObjectCircle){
            return new GardenObjectCircleWrapper((GardenObjectCircle)object);
        }
        if (object instanceof GardenObjectPolygon){
            return new GardenObjectPolygonWrapper((GardenObjectPolygon)object);
        }
        if (object instanceof Ground){
            return new GroundWrapper((Ground)object);
        }
        if (object instanceof Patch){
            return new PatchWrapper((Patch)object);
        }
        if (object instanceof Rail){
            return new RailWrapper((Rail)object);
        }
        if(object instanceof RFIDLandmark){
            return new RFIDLandmarkWrapper((RFIDLandmark)object);
        }
        if(object instanceof Reminder){
            return new ReminderWrapper((Reminder)object);
        }
        if(object instanceof DummySensor){
            return  new DummySensorWrapper((DummySensor)object);
        }
        //================Vectorobjects===============
        if (object instanceof de.sokol.lena.gardenapp.model.Point) {
            return new PointWrapper((de.sokol.lena.gardenapp.model.Point) object);
        }
        if (object instanceof Polygon) {
            return new PolygonWrapper((Polygon) object);
        }
        if (object instanceof Line) {
            return new LineWrapper((Line) object);
        }
        return null;
    }
}
