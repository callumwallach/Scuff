package nz.co.scuff.data.journey;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.sql.Timestamp;

import nz.co.scuff.data.base.ModifiableEntity;
import nz.co.scuff.data.base.Snapshotable;
import nz.co.scuff.data.family.Child;
import nz.co.scuff.data.journey.snapshot.TicketSnapshot;
import nz.co.scuff.data.util.TicketState;

/**
 * Created by Callum on 10/05/2015.
 */
@Table(name="Tickets")
public class Ticket extends ModifiableEntity implements Snapshotable, Comparable {

    @Column(name="TicketId")
    private long ticketId;
    @Column(name="IssueDate")
    private Timestamp issueDate;
    @Column(name="State")
    private TicketState state;

    @Column(name="Stamp")
    private Stamp stamp;

    @Column(name="JourneyFK", onDelete = Column.ForeignKeyAction.CASCADE)
    private Journey journey;

    @Column(name="ChildFK", onDelete = Column.ForeignKeyAction.CASCADE)
    private Child child;

    public Ticket() {}

    public Ticket(TicketSnapshot snapshot) {
        super();
        this.ticketId = snapshot.getTicketId();
        this.issueDate = snapshot.getIssueDate();
        this.state = snapshot.getState();
    }

    public long getTicketId() {
        return ticketId;
    }

    public void setTicketId(long ticketId) {
        this.ticketId = ticketId;
    }

    public Timestamp getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Timestamp issueDate) {
        this.issueDate = issueDate;
    }

    public TicketState getState() {
        return state;
    }

    public void setState(TicketState state) {
        this.state = state;
    }

    public Journey getJourney() {
        return journey;
    }

    public void setJourney(Journey journey) {
        this.journey = journey;
    }

    public Child getChild() {
        return child;
    }

    public void setChild(Child child) {
        this.child = child;
    }

    public Stamp getStamp() {
        return stamp;
    }

    public void setStamp(Stamp stamp) {
        this.stamp = stamp;
    }

    public static Ticket findById(long ticketId) {
        return new Select()
                .from(Ticket.class)
                .where("TicketId = ?", ticketId)
                .executeSingle();
    }

    public void refresh(TicketSnapshot snapshot) {
        this.ticketId = snapshot.getTicketId();
        this.issueDate = snapshot.getIssueDate();
        this.state = snapshot.getState();
        this.active = snapshot.isActive();
        this.lastModified = snapshot.getLastModified();
    }

    public TicketSnapshot toSnapshot() {
        TicketSnapshot snapshot = new TicketSnapshot();
        snapshot.setTicketId(this.ticketId);
        snapshot.setIssueDate(this.issueDate);
        snapshot.setState(this.state);
        snapshot.setStampId(this.stamp.getStampId());
        snapshot.setJourneyId(this.journey.getJourneyId());
        snapshot.setChildId(this.child.getChildId());
        return snapshot;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Ticket ticket = (Ticket) o;

        return ticketId == ticket.ticketId;

    }

    @Override
    public int hashCode() {
        int result = 31 * (int) (ticketId ^ (ticketId >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "ticketId='" + ticketId + '\'' +
                ", issueDate=" + issueDate +
                ", state=" + state +
                ", stamp=" + stamp +
                ", journey=" + (journey == null ? journey : journey.getJourneyId()) +
                ", child=" + (child == null ? child : child.getChildId()) +
                '}';
    }

    @Override
    public int compareTo(Object another) {
        Ticket other = (Ticket) another;
        if (this.equals(other)) return 0;
        return this.issueDate.compareTo(other.issueDate);
    }

    protected Ticket(Parcel in) {
        super(in);
        ticketId = in.readLong();
        issueDate = (Timestamp) in.readValue(Timestamp.class.getClassLoader());
        state = (TicketState) in.readValue(TicketState.class.getClassLoader());
        stamp = (Stamp) in.readValue(Stamp.class.getClassLoader());
        journey = (Journey) in.readValue(Journey.class.getClassLoader());
        child = (Child) in.readValue(Child.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(ticketId);
        dest.writeValue(issueDate);
        dest.writeValue(state);
        dest.writeValue(stamp);
        dest.writeValue(journey);
        dest.writeValue(child);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Ticket> CREATOR = new Parcelable.Creator<Ticket>() {
        @Override
        public Ticket createFromParcel(Parcel in) {
            return new Ticket(in);
        }

        @Override
        public Ticket[] newArray(int size) {
            return new Ticket[size];
        }
    };
}
