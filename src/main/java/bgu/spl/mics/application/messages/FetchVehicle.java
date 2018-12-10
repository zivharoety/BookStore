package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

public class FetchVehicle implements Event<DeliveryVehicle> {
    private int distance;

    public FetchVehicle (int distance){
        this.distance = distance;
    }

    public int getDistance() {
        return distance;
    }
}
