package nz.co.scuff.data.util;

import com.google.gson.annotations.Expose;

/**
 * Created by Callum on 15/06/2015.
 */
public class InitialJourneyKeyPair {

    @Expose
    public long journeyId;
    @Expose
    public long waypointId;

    public InitialJourneyKeyPair(long journeyId, long waypointId) {
        this.journeyId = journeyId;
        this.waypointId = waypointId;
    }

    @Override
    public String toString() {
        return "InitialJourneyKeyPair{" +
                "journeyId=" + journeyId +
                ", waypointId=" + waypointId +
                '}';
    }
}
