package de.sokol.lena.gardenapp.ui.Wrapper;

import java.util.ArrayList;
import java.util.List;

import de.sokol.lena.gardenapp.model.TimeValue;
import de.sokol.lena.gardenapp.model.VectorObject;
import de.sokol.lena.gardenapp.model.hasRemoteSensor;
import de.sokol.lena.gardenapp.ui.Attribute;

/**
 *
 *
 * Created by Lena on 27.02.2015.
 */
public abstract class AbstractVectorobjectWrapper implements IVectorobjectWrapper {

    private static String ID = "ID";

    private List<Attribute> attributes;
    private hasRemoteSensor remote;

    public AbstractVectorobjectWrapper(VectorObject object) {
        attributes = new ArrayList<>();
        if (object instanceof hasRemoteSensor) {
            this.remote = (hasRemoteSensor) object;
        }
        this.attributes.add(new Attribute(Attribute.INT_TYPE, ID, object.getVectorObjectID(), true));
    }

    protected List<Attribute> getList() {
        return attributes;
    }

    @Override
    public Iterable<Attribute> getAttributes() {
        return getList();
    }

    @Override
    public void changeAttribute(Attribute attribute) {
        //GardenListeners.getInstance().informListenerChangedObejct();
    }

    @Override
    public boolean hasTimeLine() {
        return false;
    }

    @Override
    public TimeValue getLastTimeValue() {
        return null;
    }

    @Override
    public boolean saveTimeValue() {
        return false;
    }

    @Override
    public Attribute getLastTimeValueAsString() {
        return null;
    }

    @Override
    public Iterable<Attribute> getTimeValueAttributes() {
        return null;
    }

    @Override
    public boolean hasRemoteSensor() {
        return remote != null;
    }

    @Override
    public void callRemoteSensor() {
        if (hasRemoteSensor()) {
            remote.makeAsyncSensorCall();
        }
    }
}
