package nz.co.scuff.data.journey.snapshot;

import com.google.gson.annotations.Expose;

import java.sql.Timestamp;

import nz.co.scuff.data.base.snapshot.Snapshot;

/**
 * Created by Callum on 10/05/2015.
 */
public class StampSnapshot implements Snapshot {

    @Expose
    private long stampId;
    @Expose
    private double latitude;
    @Expose
    private double longitude;
    @Expose
    private Timestamp stampDate;

    public StampSnapshot() {}

    public long getStampId() {
        return stampId;
    }

    public void setStampId(long stampId) {
        this.stampId = stampId;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Timestamp getStampDate() {
        return stampDate;
    }

    public void setStampDate(Timestamp stampDate) {
        this.stampDate = stampDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StampSnapshot that = (StampSnapshot) o;

        return stampId == that.stampId;

    }

    @Override
    public int hashCode() {
        return (int) (stampId ^ (stampId >>> 32));
    }

    @Override
    public String toString() {
        return "StampSnapshot{" +
                "stampId=" + stampId +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", stampDate=" + stampDate +
                '}';
    }
}
