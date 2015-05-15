package nz.co.scuff.android.event;

import android.location.Location;

/**
 * Created by Callum on 30/04/2015.
 */
public class LocationEvent {

    private Location location;

    public LocationEvent(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return "LocationEvent{" +
                "location=" + location +
                '}';
    }
}
