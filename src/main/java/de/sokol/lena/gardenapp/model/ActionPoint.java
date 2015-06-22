package de.sokol.lena.gardenapp.model;

/**
 * Represents a point at which a roboter can perform a certain action. Example actions: Charge, water
 *
 * Created by lena on 30.03.15.
 */
public class ActionPoint extends Point {

    private Action action;

    public ActionPoint(){
        super();
    }

    public ActionPoint(int vectorObjectID){
        super(vectorObjectID);
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public Action getAction() {
        return action;
    }

    @Override
    public String toString() {
        String result = "Action Point: ";
        if (action == null){
            return result + getVectorObjectID();
        }
        return result + action;
    }

    public enum Action {
       CHARGE, WATER
   }
}
