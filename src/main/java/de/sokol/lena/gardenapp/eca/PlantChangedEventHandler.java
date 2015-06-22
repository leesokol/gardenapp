package de.sokol.lena.gardenapp.eca;

import android.util.Pair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.sokol.lena.gardenapp.model.Plant;
import de.sokol.lena.gardenapp.ui.MainActivity;

/**
 * Handles PlantChangeEvents.
 *
 * Created by Lena on 10.03.2015.
 */
public class PlantChangedEventHandler{

    private MainActivity context;
    private ResourceFacade fassade;

    public PlantChangedEventHandler(MainActivity context, ResourceFacade fassade) {
        this.context = context;
        this.fassade = fassade;
    }

    public void onEvent(PlantChangedEvent event) {
        Plant plant = (Plant) event.getSource();
        clearAdvices(event.getType(), plant);
        Set<Advice> adviceSet = new HashSet<>();
        for (Pair<String, String> pair : fassade.getPlantNeighborhoods()) {
            if (pair.first.equals(plant.getPlanttype())) {
                adviceSet.add(new Advice("Suggested Neighbor for " + plant.getPlanttype() + ": " + pair.second, plant, event.getType()));
            } else if (pair.second.equals(plant.getPlanttype())) {
                adviceSet.add(new Advice("Suggested Neighbor for " + plant.getPlanttype() + ": " + pair.first, plant, event.getType()));
            }
        }
        context.getAdvices().addAll(adviceSet);
    }

    /**
     * Deletes all advices that concern a plant and a specific Event Type
     * @param eventype
     * @param plant
     */
    private void clearAdvices(int eventype, Plant plant) {
        List<Advice> advices = new ArrayList<>();
        for (Advice advice : context.getAdvices()) {
            if (advice.getSource().equals(plant) && advice.getEventType() == eventype) {
                advices.add(advice);
            }
        }
        context.getAdvices().removeAll(advices);
    }
}
