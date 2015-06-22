package de.sokol.lena.gardenapp.ui;

/**
 * Represents an attribute of a Vector Object to simplify the process of displaying a fitting UI Element for changing said attribute
 * <p/>
 * Created by Lena on 24.02.2015.
 */
public class Attribute {

    //TYPES OF ATTRIBUTES
    public static final int STRING_TYPE = 1;
    public static final int INT_TYPE = 2;
    public static final int FLOAT_TYPE = 3;
    public static final int NODE_TYPE = 4;
    public static final int PLANTTYPE_TYPE = 5;
    public static final int PLANTSET = 6;//TODO: IMplementieren
    public static final int ACTIONTYPE = 7;//TODO: IMplementieren
    public static final int GROUNDTYPE = 8;
    public static final int DATE = 9;
    public static final int LANDMARK = 10;//TODO:implementieren

    private int attributType;
    private String attributeName;
    private Object value;
    //Final Attribute
    private boolean isFinal;

    public Attribute(int attributType, String attributeName, Object value, boolean isFinal) {
        this.attributType = attributType;
        this.attributeName = attributeName;
        this.value = value;
        this.isFinal = isFinal;
    }

    public int getAttributType() {
        return attributType;
    }

    public Object getValue() {
        return value;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public boolean isFinal() {
        return isFinal;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return getAttributeName() + ": " + getValue();
    }
}
