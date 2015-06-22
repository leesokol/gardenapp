package de.sokol.lena.gardenapp.model.Layer;

import java.util.ArrayList;
import java.util.List;

import de.sokol.lena.gardenapp.model.Geoposition;
import de.sokol.lena.gardenapp.model.Patch;
import de.sokol.lena.gardenapp.model.Plant;
import de.sokol.lena.gardenapp.model.VectorObject;

/**
 * This Layer only contains Plant Type Objects: Plants, Patch.
 *
 * Created by Lena on 25.02.2015.
 */
public class PlantLayer extends VectorLayer {
    public PlantLayer(String name) {
        super(name);
    }

    @Override
    public List<Class> getAllowedVectorObjects() {
        List<Class> list = new ArrayList<>();
        list.add(Plant.class);
        list.add(Patch.class);
        return list;
    }

    @Override
    public boolean addVectorObject(VectorObject object) {
        boolean result = super.addVectorObject(object);
        recheckAllPatches();
        return result;
    }

    @Override
    public boolean deleteVectorObject(VectorObject object) {
        boolean result = super.deleteVectorObject(object);
        recheckAllPatches();
        return result;
    }

    private void recheckAllPatches() {
        List<Patch> patches = new ArrayList<>();
        List<Plant> plants = new ArrayList<>();
        for (VectorObject object : getVectorObjects()) {
            if (object instanceof Patch) {
                Patch patch = (Patch)object;
                patch.getPlantrepresentives().clear();
                patches.add(patch);
            } else if (object instanceof Plant) {
                plants.add((Plant) object);
            }
        }

        for (Patch patch : patches) {
            for (Plant plant : plants) {
                if (pointInsidePolygon(plant.getGeoposition(), patch.getGeopositions())) {
                    patch.addAPlantReprensentive(plant);
                }
            }
        }
    }

    private boolean pointInsidePolygon(Geoposition geoposition, Geoposition[] geopositions) {
        Geoposition[] newpositions = new Geoposition[geopositions.length + 1];
        newpositions[0] = geopositions[geopositions.length - 1];
        int t = -1;
        System.arraycopy(geopositions, 0, newpositions, 1, geopositions.length);
        for (int i = 0; i < newpositions.length - 1; i++) {
            t = t * kreuzProdukttest(geoposition, newpositions[i], newpositions[i + 1]);
        }
        if (t == 1) {
            return true;
        } else if (t == 0) {
            //auf einer Ecke oder Kante
            return false;
        }
        return false;
    }

    private int kreuzProdukttest(Geoposition point, Geoposition linesegment1, Geoposition linesegment2) {
        if (point.latitude == linesegment1.latitude && linesegment1.latitude == linesegment2.latitude) {
            if ((linesegment1.longitude <= point.longitude && point.longitude <= linesegment2.longitude) ||
                    (linesegment2.longitude <= point.longitude && point.longitude <= linesegment1.longitude)) {
                return 0;
            } else {
                return 1;
            }
        }
        if (linesegment1.latitude > linesegment2.latitude) {
            Geoposition segment1 = linesegment1;
            linesegment1 = linesegment2;
            linesegment2 = segment1;
        }
        if (point.latitude == linesegment1.latitude && point.longitude == linesegment1.longitude) {
            return 0;
        }
        if (point.latitude <= linesegment1.latitude || point.latitude > linesegment2.latitude) {
            return 1;
        }
        double delta = (linesegment1.longitude - point.longitude) * (linesegment2.latitude - point.latitude) - (linesegment1.latitude - point.latitude) * (linesegment2.longitude - point.longitude);
        if (delta > 0) {
            return -1;
        } else if (delta < 0) {
            return 1;
        } else {
            return 0;
        }
    }

    //TODO: React to changes to this layer by rechecking all Patches and Plants
}
