package nz.co.scuff.android.util;

import nz.co.scuff.data.journey.Waypoint;

/**
 * Created by Callum on 30/04/2015.
 */
public class WaypointEvent {

    private Waypoint waypoint;

    public WaypointEvent(Waypoint waypoint) {
        this.waypoint = waypoint;
    }

    public Waypoint getWaypoint() {
        return waypoint;
    }

    @Override
    public String toString() {
        return "WaypointEvent{" +
                "waypoint=" + waypoint +
                '}';
    }
}
