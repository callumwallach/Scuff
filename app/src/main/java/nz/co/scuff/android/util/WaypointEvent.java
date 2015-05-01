package nz.co.scuff.android.util;

import java.util.List;

import nz.co.scuff.data.journey.Waypoint;

/**
 * Created by Callum on 30/04/2015.
 */
public class WaypointEvent {

    private List<Waypoint> waypoints;

    public WaypointEvent(List<Waypoint> waypoints) {
        this.waypoints = waypoints;
    }

    public List<Waypoint> getWaypoints() {
        return waypoints;
    }

    @Override
    public String toString() {
        String toReturn = "WaypointEvent{" +
                "waypoints=[";
        for (Waypoint w : waypoints) {
            toReturn += w;
        }
        toReturn += "]}";
        return toReturn;
    }
}
