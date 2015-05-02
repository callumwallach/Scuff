package nz.co.scuff.android.util;

import java.util.List;

import nz.co.scuff.data.journey.Snapshot;

/**
 * Created by Callum on 30/04/2015.
 */
public class SnapshotEvent {

    private List<Snapshot> snapshots;

    public SnapshotEvent(List<Snapshot> snapshots) {
        this.snapshots = snapshots;
    }

    public List<Snapshot> getSnapshots() {
        return snapshots;
    }

    @Override
    public String toString() {
        String toReturn = "SnapshotEvent{" +
                "snapshots=[";
        for (Snapshot js : snapshots) {
            toReturn += js;
            toReturn += " ";
        }
        toReturn += "]}";
        return toReturn;
    }
}
