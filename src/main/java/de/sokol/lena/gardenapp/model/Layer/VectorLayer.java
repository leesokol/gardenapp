package de.sokol.lena.gardenapp.model.Layer;

import java.util.ArrayList;
import java.util.List;

import de.sokol.lena.gardenapp.model.GardenListeners;
import de.sokol.lena.gardenapp.model.Line;
import de.sokol.lena.gardenapp.model.Polygon;
import de.sokol.lena.gardenapp.model.VectorObject;

/**
 * A Layer containing vector objects like points, lines and polygons.
 * Created by Lena on 15.02.2015.
 */
public class VectorLayer implements GardenLayer {

    private String name;
    private List<VectorObject> vectorObjects;
    private boolean visible;

    public VectorLayer(String name) {
        this.name = name;
        this.vectorObjects = new ArrayList<>();
        this.visible = true;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
        GardenListeners.getInstance().informListenerChangedVisibility();
    }

    /**
     * Returns the objects in this layer in their drawing order
     *
     * @return an iterable of all VectorObjects in the Layer
     */
    public Iterable<VectorObject> getVectorObjects() {
        return vectorObjects;
    }

    /**
     * Adding a VectorObject to the Layer.
     *
     * @param object the vectorObject
     * @return true if the VectorObject could successfully added to the Layer
     * false if the VectorObject could not be added to the Layer
     */
    public boolean addVectorObject(VectorObject object) {
        //MAYBE DO: Add the Object only if it is compatible to the Bounding Box of the Garden
        boolean result = false;
        if (getAllowedVectorObjects().contains(object.getClass())) {
            result = vectorObjects.add(object);
            GardenListeners.getInstance().informListenerAddedObject();
        }
        return result;
    }

    public boolean deleteVectorObject(VectorObject object) {
        boolean result = vectorObjects.remove(object);
        GardenListeners.getInstance().informListenerDeletedObject();
        return result;
    }

    public boolean moveObjectUp(VectorObject object) {
        boolean result = false;
        int index = vectorObjects.indexOf(object);
        if (index < vectorObjects.size() - 1) {
            vectorObjects.remove(index);
            vectorObjects.add(index + 1, object);
            result = true;
            GardenListeners.getInstance().informListenerMovedObject();
        }
        return result;
    }

    public boolean containsVectorObject(VectorObject object){
         return vectorObjects.contains(object);
    }

    public boolean moveObjectDown(VectorObject object) {
        boolean result = false;
        int index = vectorObjects.indexOf(object);
        if (0 < index) {
            vectorObjects.remove(index);
            vectorObjects.add(index - 1, object);
            result = true;
            GardenListeners.getInstance().informListenerMovedObject();
        }
        return result;
    }

    public List<Class> getAllowedVectorObjects() {
        List<Class> list = new ArrayList<>();
        list.add(de.sokol.lena.gardenapp.model.Point.class);
        list.add(Polygon.class);
        list.add(Line.class);
        return list;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof VectorLayer) {
            VectorLayer l = (VectorLayer) o;
            return l.getName().equals(this.getName());
        }

        return false;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return getName();
    }
}
