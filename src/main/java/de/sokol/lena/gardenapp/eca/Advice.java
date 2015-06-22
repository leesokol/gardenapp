package de.sokol.lena.gardenapp.eca;

/**
 * Represents an Advice that is displayed to the user.
 *
 * Created by Lena on 10.03.2015.
 */
public class Advice {

    //The message of the Advice
    private String message;
    //The Object that caused the Advice
    private Object source;
    //The EventType that caused the Advice
    private int eventType;

    /**
     * @param message Message displayed as advice
     * @param source != null
     * @param eventType IEVENT eventType
     */
    public Advice(String message,Object source,int eventType) {
        this.message = message;
        this.source = source;
        this.eventType = eventType;
    }

    public String getMessage() {
        return message;
    }

    public Object getSource() {
        return source;
    }

    @Override
    public boolean equals(Object o) {
        if (o != null || o instanceof Advice) {
            Advice ad = (Advice) o;
            return ad.message.equals(this.message)&&ad.source.equals(this.source);
        }
        return false;
    }

    public int getEventType() {
        return eventType;
    }

    @Override
    public String toString() {
        return getMessage();
    }

    @Override
    public int hashCode() {
        return message.hashCode();
    }
}
