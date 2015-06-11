package nz.co.scuff.data.journey.snapshot;

import com.google.gson.annotations.Expose;

import java.sql.Timestamp;

import nz.co.scuff.data.base.snapshot.ModifiableSnapshot;

/**
 * Created by Callum on 10/05/2015.
 */
public class TicketSnapshot extends ModifiableSnapshot {

    @Expose
    private long ticketId;
    @Expose
    private Timestamp issueDate;

    @Expose
    private long stampId;
    @Expose
    private String journeyId;
    @Expose
    private long childId;

    public TicketSnapshot() {}

    public long getTicketId() {
        return ticketId;
    }

    public void setTicketId(long ticketId) {
        this.ticketId = ticketId;
    }

    public String getJourneyId() {
        return journeyId;
    }

    public void setJourneyId(String journeyId) {
        this.journeyId = journeyId;
    }

    public long getChildId() {
        return childId;
    }

    public void setChildId(long childId) {
        this.childId = childId;
    }

    public Timestamp getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Timestamp issueDate) {
        this.issueDate = issueDate;
    }

    public long getStampId() {
        return stampId;
    }

    public void setStampId(long stampId) {
        this.stampId = stampId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TicketSnapshot that = (TicketSnapshot) o;

        return ticketId == that.ticketId;

    }

    @Override
    public int hashCode() {
        return (int) (ticketId ^ (ticketId >>> 32));
    }

/*    @Override
    public int compareTo(Object another) {
        TicketSnapshot other = (TicketSnapshot) another;
        return this.issueDate.compareTo(other.issueDate);
    }*/

    @Override
    public String toString() {
        return "TicketSnapshot{" +
                "ticketId=" + ticketId +
                ", issueDate=" + issueDate +
                ", stampId=" + stampId +
                ", journeyId='" + journeyId + '\'' +
                ", childId=" + childId +
                "} " + super.toString();
    }

}
