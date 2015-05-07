package nz.co.scuff.android.util;

import java.util.List;

import nz.co.scuff.data.journey.Bus;

/**
 * Created by Callum on 30/04/2015.
 */
public class BusEvent {

    private List<Bus> buses;

    public BusEvent(List<Bus> buses) {
        this.buses = buses;
    }

    public List<Bus> getBuses() {
        return buses;
    }

    @Override
    public String toString() {
        String toReturn = "BusEvent{" +
                "buses=[";
        for (Bus bus : buses) {
            toReturn += bus;
            toReturn += " ";
        }
        toReturn += "]}";
        return toReturn;
    }
}
