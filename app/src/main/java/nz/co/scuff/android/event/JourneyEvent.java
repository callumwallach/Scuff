package nz.co.scuff.android.event;

import java.util.List;

import nz.co.scuff.data.journey.Journey;

/**
 * Created by Callum on 30/04/2015.
 */
public class JourneyEvent {

    private List<Journey> journeys;

    public JourneyEvent(List<Journey> journeys) {
        this.journeys = journeys;
    }

    public List<Journey> getJourneys() {
        return journeys;
    }

    @Override
    public String toString() {
        String toReturn = "JourneyEvent{" +
                "journeys=[";
        for (Journey journey : journeys) {
            toReturn += journey;
            toReturn += " ";
        }
        toReturn += "]}";
        return toReturn;
    }
}
