package de.sokol.lena.gardenapp.ui.Wrapper;

import de.sokol.lena.gardenapp.model.RFIDLandmark;
import de.sokol.lena.gardenapp.ui.Attribute;

/**
 * Created by Lena on 11.04.2015.
 */
public class RFIDLandmarkWrapper extends AbstractVectorobjectWrapper {

    private static final String RFIDID= "RFID Identifier";

    private RFIDLandmark landmark;

    public RFIDLandmarkWrapper(RFIDLandmark landmark) {
        super(landmark);
        getList().add(new Attribute(Attribute.INT_TYPE,RFIDID,landmark.getRfidIdentifier(),false));
        this.landmark = landmark;
    }

    @Override
    public void changeAttribute(Attribute attribute) {
        String attributeName= attribute.getAttributeName();
        if(attributeName.equals(RFIDID)){
            landmark.setRfidIdentifier((Integer)attribute.getValue());
        }
        super.changeAttribute(attribute);
    }
}
