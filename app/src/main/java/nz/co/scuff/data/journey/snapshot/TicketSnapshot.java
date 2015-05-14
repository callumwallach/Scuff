package nz.co.scuff.data.journey.snapshot;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

import java.sql.Timestamp;

/**
 * Created by Callum on 10/05/2015.
 */
public class TicketSnapshot implements Comparable, Parcelable {

    @Expose
    private String ticketId;
    @Expose
    private Timestamp issueDate;

    @Expose
    private StampSnapshot stamp;

    @Expose
    private String journeyId;
    @Expose
    private long passengerId;

    public TicketSnapshot() {}

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public String getJourneyId() {
        return journeyId;
    }

    public void setJourneyId(String journeyId) {
        this.journeyId = journeyId;
    }

    public long getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(long passengerId) {
        this.passengerId = passengerId;
    }

    public Timestamp getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Timestamp issueDate) {
        this.issueDate = issueDate;
    }

    public StampSnapshot getStamp() {
        return stamp;
    }

    public void setStamp(StampSnapshot stamp) {
        this.stamp = stamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TicketSnapshot that = (TicketSnapshot) o;

        return !(ticketId != null ? !ticketId.equals(that.ticketId) : that.ticketId != null);

    }

    @Override
    public int hashCode() {
        return ticketId != null ? ticketId.hashCode() : 0;
    }

    @Override
    public int compareTo(Object another) {
        TicketSnapshot other = (TicketSnapshot) another;
        return this.issueDate.compareTo(other.issueDate);
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("TicketSnapshot{");
        sb.append("ticketId='").append(ticketId).append('\'');
        sb.append(", issueDate=").append(issueDate);
        sb.append(", stamp=").append(stamp);
        sb.append(", journeyId='").append(journeyId).append('\'');
        sb.append(", passengerId=").append(passengerId);
        sb.append('}');
        return sb.toString();
    }

    protected TicketSnapshot(Parcel in) {
        ticketId = in.readString();
        issueDate = (Timestamp) in.readValue(Timestamp.class.getClassLoader());
        stamp = (StampSnapshot) in.readValue(StampSnapshot.class.getClassLoader());
        journeyId = in.readString();
        passengerId = in.readLong();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ticketId);
        dest.writeValue(issueDate);
        dest.writeValue(stamp);
        dest.writeString(journeyId);
        dest.writeLong(passengerId);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<TicketSnapshot> CREATOR = new Parcelable.Creator<TicketSnapshot>() {
        @Override
        public TicketSnapshot createFromParcel(Parcel in) {
            return new TicketSnapshot(in);
        }

        @Override
        public TicketSnapshot[] newArray(int size) {
            return new TicketSnapshot[size];
        }
    };
}
