package de.sokol.lena.gardenapp.model.Layer;

import java.util.ArrayList;
import java.util.List;

import de.sokol.lena.gardenapp.model.Ground;
import de.sokol.lena.gardenapp.model.Layer.VectorLayer;

/**
 * This type of Layer only contains Ground Type Objects
 *
 * Created by Lena on 06.04.2015.
 */
public class GroundLayer extends VectorLayer {

    public GroundLayer(String name) {
        super(name);
    }

    @Override
    public List<Class> getAllowedVectorObjects() {
        List<Class> list = new ArrayList<>();
        list.add(Ground.class);
        return list;
    }
}
