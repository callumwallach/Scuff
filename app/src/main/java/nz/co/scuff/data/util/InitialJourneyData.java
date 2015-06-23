package nz.co.scuff.data.util;

import com.google.gson.annotations.Expose;

import nz.co.scuff.data.journey.snapshot.JourneySnapshot;
import nz.co.scuff.data.journey.snapshot.WaypointSnapshot;

/**
 * Created by Callum on 15/06/2015.
 */
public class InitialJourneyData {

    @Expose
    public JourneySnapshot journeySnapshot;
    @Expose
    public WaypointSnapshot waypointSnapshot;

    public InitialJourneyData(JourneySnapshot journeySnapshot, WaypointSnapshot waypointSnapshot) {
        this.journeySnapshot = journeySnapshot;
        this.waypointSnapshot = waypointSnapshot;
    }

    @Override
    public String toString() {
        return "InitialJourneyData{" +
                "journeySnapshot=" + journeySnapshot +
                ", waypointSnapshot=" + waypointSnapshot +
                '}';
    }
}
