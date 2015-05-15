package nz.co.scuff.data.journey;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.io.Serializable;
import java.sql.Timestamp;

import nz.co.scuff.data.family.Passenger;
import nz.co.scuff.data.journey.snapshot.TicketSnapshot;

/**
 * Created by Callum on 10/05/2015.
 */
@Table(name="Tickets")
public class Ticket extends Model implements Comparable, Serializable {

    @Column(name="TicketId")
    private long ticketId;
    @Column(name="IssueDate")
    private Timestamp issueDate;

    @Column(name="Stamp")
    private Stamp stamp;

    @Column(name="JourneyFK", onDelete = Column.ForeignKeyAction.CASCADE)
    private Journey journey;

    @Column(name="PassengerFK", onDelete = Column.ForeignKeyAction.CASCADE)
    private Passenger passenger;

    public Ticket() {}

    public Ticket(TicketSnapshot snapshot) {
        this.ticketId = snapshot.getTicketId();
        this.issueDate = snapshot.getIssueDate();
        this.stamp = (snapshot.getStamp() == null ? null : new Stamp(snapshot.getStamp()));
    }

    public long getTicketId() {
        return ticketId;
    }

    public void setTicketId(long ticketId) {
        this.ticketId = ticketId;
    }

    public Journey getJourney() {
        return journey;
    }

    public void setJourney(Journey journey) {
        this.journey = journey;
    }

    public Passenger getPassenger() {
        return passenger;
    }

    public void setPassenger(Passenger passenger) {
        this.passenger = passenger;
    }

    public Timestamp getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Timestamp issueDate) {
        this.issueDate = issueDate;
    }

    public Stamp getStamp() {
        return stamp;
    }

    public void setStamp(Stamp stamp) {
        this.stamp = stamp;
    }

    public TicketSnapshot toSnapshot() {
        TicketSnapshot snapshot = new TicketSnapshot();
        snapshot.setTicketId(this.ticketId);
        snapshot.setIssueDate(this.issueDate);
        snapshot.setStamp(this.stamp == null? null : this.stamp.toSnapshot());
        snapshot.setJourneyId(this.journey.getJourneyId());
        snapshot.setPassengerId(this.passenger.getPersonId());
        return snapshot;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Ticket ticket = (Ticket) o;

        return ticketId == ticket.ticketId;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (int) (ticketId ^ (ticketId >>> 32));
        return result;
    }

    @Override
    public int compareTo(Object another) {
        Ticket other = (Ticket) another;
        return this.issueDate.compareTo(other.issueDate);
    }
}
