package nz.co.scuff.android.event;

/**
 * Created by Callum on 30/04/2015.
 */
public class RecordEvent {

    private long journeyId;

    public RecordEvent(long journeyId) {
        this.journeyId = journeyId;
    }

    public long getJourneyId() {
        return journeyId;
    }

    @Override
    public String toString() {
        return "RecordEvent{" +
                "journeyId=" + journeyId +
                '}';
    }
}
