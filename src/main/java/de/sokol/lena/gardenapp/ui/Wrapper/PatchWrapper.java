package de.sokol.lena.gardenapp.ui.Wrapper;

import de.sokol.lena.gardenapp.model.Patch;
import de.sokol.lena.gardenapp.ui.Attribute;

/**
 * Created by Lena on 06.04.2015.
 */
public class PatchWrapper extends AbstractVectorobjectWrapper {

    private static String PLANTREPRENSENTIVES = "Plant representatives";

    private Patch patch;

    public PatchWrapper(Patch patch) {
        super(patch);
        //MAybe do: jedes Plantobject einzeln als attribut darstellen
        getList().add(new Attribute(Attribute.PLANTSET,PLANTREPRENSENTIVES,patch.getPlantrepresentives().toString(),false));
        this.patch = patch;
    }
}
