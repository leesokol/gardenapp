package de.sokol.lena.gardenapp.model;

import java.util.List;

/**
 * Represents a Sensor
 *
 * Created by lena on 30.03.15.
 */
public abstract class Sensor<T extends SensorValue> extends Point {

    private String sensoridentifier;
    private TimeLine<T> sensorvalues;
    private TimeLine<SensorPosition> sensorpositions;

    public Sensor(int vectorObjectID){
        super(vectorObjectID);
    }

    public Sensor(){
        super();
        sensorpositions = new TimeLine<>();
        sensorvalues = new TimeLine<>();
    }
    
    public String getSensoridentifier() {
        return sensoridentifier;
    }

    public abstract List<T> getAllRelevantSensorValues();

    protected TimeLine<SensorPosition> getSensorpositions() {
        return sensorpositions;
    }

    protected TimeLine<T> getSensorvalues() {
        return sensorvalues;
    }

    @Override
    public void setPosition(Geoposition position) {
        super.setPosition(position);
        sensorpositions.addTimeValue(new SensorPosition(position));
    }

    public class SensorPosition extends TimeValue{

        private Geoposition position;

        public SensorPosition(Geoposition position){
            super();
            this.position = position;
        }

        public Geoposition getPosition() {
            return position;
        }

        public void setPosition(Geoposition position) {
            this.position = position;
        }

        @Override
        public String toString() {
            return getTimeStamp().toString()+ ": "+ "Position: " + position.toString();
        }
    }
}