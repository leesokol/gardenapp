package de.sokol.lena.gardenapp.model;

/**
 * Representing a rail for robots
 *
 * Created by lena on 30.03.15.
 */
public class Rail extends Line {

    private String material;

    public Rail(int vectorObjectID){
        super(vectorObjectID);
    }

    public Rail() {
        super();
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getMaterial() {
        return material;
    }

    @Override
    public String toString() {
        String result = "Rail: ";

        return result + getVectorObjectID();
    }
}
