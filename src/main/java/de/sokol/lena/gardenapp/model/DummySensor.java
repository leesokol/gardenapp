package de.sokol.lena.gardenapp.model;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.SortedSet;

/**
 * An example Sensor.
 *
 * Created by lena on 01.05.15.
 */
public class DummySensor extends Sensor<DummySensorValue> implements hasRemoteSensor{

    public DummySensor(int vectorObjectID){
        super(vectorObjectID);
    }

    public DummySensor() {
        super();
    }

    @Override
    public List<DummySensorValue> getAllRelevantSensorValues() {
        SensorPosition position =  getSensorpositions().getLastValue();
        Date d =  position.getTimeStamp();
        SortedSet<DummySensorValue> set = getSensorvalues().getTimeValues().tailSet(new DummySensorValue(d));
        return new ArrayList<>(set);
    }

    @Override
    public void makeAsyncSensorCall() {
        new AsyncTask<Void, Void, DummySensorValue>() {
            @Override
            protected DummySensorValue doInBackground(Void... params) {
                DummySensorValue result = new DummySensorValue();
                int random = (int)(Math.random()*(double)Integer.MAX_VALUE);
                result.setValue(random);
                return result;
            }

            @Override
            protected void onPostExecute(DummySensorValue dummySensorValue) {
                super.onPostExecute(dummySensorValue);
                newSensorValuesReady(dummySensorValue);
            }
        }.execute();
    }

    public void newSensorValuesReady(DummySensorValue value) {
        getSensorvalues().addTimeValue(value);
        GardenListeners.getInstance().informListenerChangedObejct();
    }

    @Override
    public String toString() {
        String result = "Sensor: ";
        return result + getVectorObjectID();
    }
}


