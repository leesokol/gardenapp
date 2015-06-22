package de.sokol.lena.gardenapp.ui.Wrapper;

import de.sokol.lena.gardenapp.model.Building;
import de.sokol.lena.gardenapp.ui.Attribute;

/**
 * Created by Lena on 06.04.2015.
 */
public class BuildingWrapper extends AbstractVectorobjectWrapper {

    private static String BUILDING_NAME = "building Name";

    private Building building;
    public BuildingWrapper(Building building) {
        super(building);
        getList().add(new Attribute(Attribute.STRING_TYPE,BUILDING_NAME,building.getBuildingName(),false));
        this.building = building;
    }

    @Override
    public void changeAttribute(Attribute attribute) {
        String attributeName = attribute.getAttributeName();
        if (attributeName.equals(BUILDING_NAME)){
            building.setBuildingName((String)attribute.getValue());
        }
        super.changeAttribute(attribute);
    }
}
