package de.sokol.lena.gardenapp.model;

import java.util.HashSet;

/**
 * Holding all Listeners of the Garden and allows for centrally informing all listeners.
 *
 * Created by lena on 28.02.15.
 */
public class GardenListeners {
    private static GardenListeners ourInstance = new GardenListeners();

    public static GardenListeners getInstance() {
        return ourInstance;
    }

    private HashSet<GardenListener> listeners;

    private GardenListeners() {
        this.listeners = new HashSet<>();
    }

    public void informListenerAddedLayer(){
        for (GardenListener listener: listeners){
            listener.informAddedLayer();
        }
    }

    public void informListenerChangedVisibility(){
        for (GardenListener listener: listeners){
            listener.informChangedVisibility();
        }
    }

    public void informListenerAddedObject(){
        for (GardenListener listener: listeners){
            listener.informAddedObject();
        }
    }

    public void informListenerDeletedLayer(){
        for (GardenListener listener: listeners){
            listener.informDeletedLayer();
        }
    }

    public void informListenerMovedLayer() {
        for (GardenListener listener : listeners) {
            listener.informMovedLayer();
        }
    }

    public void informListenerDeletedObject(){
        for (GardenListener listener: listeners){
            listener.informDeletedObject();
        }
    }

    public void informListenerMovedObject(){
        for (GardenListener listener: listeners){
            listener.informMovedObject();
        }
    }

    public void informListenerChangedObejct(){
        for (GardenListener listener: listeners){
            listener.informChangedObject();
        }
    }

    public void informListenerAddedBoundary(){
        for (GardenListener listener: listeners){
            listener.informAddedBoundary();
        }
    }

    public void addGardenListener(GardenListener listener) {
        listeners.add(listener);
    }

    public void removeGardenListener(GardenListener listener) {
        listeners.remove(listener);
    }

    public interface GardenListener {
        public void informAddedObject();

        public void informAddedLayer();

        public void informChangedVisibility();

        public void informDeletedLayer();

        public void informMovedLayer();

        public void informDeletedObject();

        public void informMovedObject();

        public void informChangedObject();

        public void informAddedBoundary();
    }

}
