package de.sokol.lena.gardenapp.model.Layer;

/**
 * A GardenLayer contains thematic information about the garden.
 */
public interface GardenLayer {

    /**
     * Gives the Name of the Layer
     * @return
     */
    public String getName();

    /**
     * If the layer is visible
     * @return
     */
    public boolean isVisible();

    /**
     * Set the visibility of the layer
     * @param visible
     */
    public void setVisible(boolean visible);
}
