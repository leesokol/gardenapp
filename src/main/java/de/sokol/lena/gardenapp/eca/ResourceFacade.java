package de.sokol.lena.gardenapp.eca;

import android.content.Context;
import android.util.Pair;

import java.util.HashSet;
import java.util.Set;

import de.sokol.lena.gardenapp.R;

/**
 * Allows for an access to informations concerning the advices,
 *
 * Created by Lena on 11.03.2015.
 */
public class ResourceFacade {

    private Context context;

    public ResourceFacade(Context context) {
        this.context = context;
    }

    /**
     * Returns all compatible planttypes
     * @return
     */
    public Iterable<Pair<String,String>> getPlantNeighborhoods(){
        Set<Pair<String,String>> pairSet = new HashSet<>();
        String[] plant_neighborhoods = context.getResources().getStringArray(R.array.plant_neighborhoods);
        for (String plantn : plant_neighborhoods) {
            String[] pairstring = plantn.split("\\|");
            Pair<String, String> pair = new Pair<>(pairstring[0], pairstring[1]);
            pairSet.add(pair);
        }
        return pairSet;
    }

}
