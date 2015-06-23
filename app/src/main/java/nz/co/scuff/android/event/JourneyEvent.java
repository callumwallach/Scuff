package nz.co.scuff.android.event;

import java.util.Collection;

import nz.co.scuff.data.journey.Journey;

/**
 * Created by Callum on 30/04/2015.
 */
public class JourneyEvent {

    private Collection<Journey> journeys;

    public JourneyEvent(Collection<Journey> journeys) {
        this.journeys = journeys;
    }

    public Collection<Journey> getJourneys() {
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
