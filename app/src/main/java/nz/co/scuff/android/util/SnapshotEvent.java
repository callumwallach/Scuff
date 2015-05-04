package nz.co.scuff.android.util;

import java.util.List;

import nz.co.scuff.data.journey.JourneySnapshot;

/**
 * Created by Callum on 30/04/2015.
 */
public class SnapshotEvent {

    private List<JourneySnapshot> snapshots;

    public SnapshotEvent(List<JourneySnapshot> snapshots) {
        this.snapshots = snapshots;
    }

    public List<JourneySnapshot> getSnapshots() {
        return snapshots;
    }

    @Override
    public String toString() {
        String toReturn = "SnapshotEvent{" +
                "snapshots=[";
        for (JourneySnapshot js : snapshots) {
            toReturn += js;
            toReturn += " ";
        }
        toReturn += "]}";
        return toReturn;
    }
}
