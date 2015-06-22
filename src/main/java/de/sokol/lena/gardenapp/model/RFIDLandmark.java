package de.sokol.lena.gardenapp.model;

/**
 * Represents a RFIDLandmark
 *
 * Created by Lena on 06.04.2015.
 */
public class RFIDLandmark extends Landmark{

    private Integer rfidIdentifier;

    public RFIDLandmark(int vectorObjectID){
        super(vectorObjectID);
    }

    public RFIDLandmark() {
        super();
    }

    public Integer getRfidIdentifier() {
        return rfidIdentifier;
    }

    public void setRfidIdentifier(Integer rfidIdentifier) {
        this.rfidIdentifier = rfidIdentifier;
    }

    @Override
    public String toString() {
        String result = "RFID Landmark: ";
        if(rfidIdentifier == null){
            return result + getVectorObjectID();
        }
        return result + rfidIdentifier;
    }
}
