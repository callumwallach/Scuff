package nz.co.scuff.data.journey;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.sql.Timestamp;

import nz.co.scuff.data.journey.snapshot.StampSnapshot;

/**
 * Created by Callum on 14/05/2015.
 */
@Table(name="Stamps")
public class Stamp extends Model implements Parcelable {

    @Column(name="StampId")
    private long stampId;
    @Column(name="Latitude")
    private double latitude;
    @Column(name="Longitude")
    private double longitude;
    @Column(name="StampDate")
    private Timestamp stampDate;

    public Stamp() {}

    public Stamp(StampSnapshot snapshot) {
        this.stampId = snapshot.getStampId();
        this.latitude = snapshot.getLatitude();
        this.longitude = snapshot.getLongitude();
        this.stampDate = snapshot.getStampDate();
    }

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

    public static Stamp findById(long stampId) {
        return new Select()
                .from(Stamp.class)
                .where("StampId = ?", stampId)
                .executeSingle();
    }

    public StampSnapshot toSnapshot() {
        StampSnapshot snapshot = new StampSnapshot();
        snapshot.setStampId(this.stampId);
        snapshot.setLatitude(this.latitude);
        snapshot.setLongitude(this.longitude);
        snapshot.setStampDate(this.stampDate);
        return snapshot;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Stamp stamp = (Stamp) o;

        return stampId == stamp.stampId;

    }

    @Override
    public int hashCode() {
        int result = 31 * (int) (stampId ^ (stampId >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "Stamp{" +
                "stampId=" + stampId +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", stampDate=" + stampDate +
                "} " + super.toString();
    }

    protected Stamp(Parcel in) {
        stampId = in.readLong();
        latitude = in.readDouble();
        longitude = in.readDouble();
        stampDate = (Timestamp) in.readValue(Timestamp.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(stampId);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeValue(stampDate);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Stamp> CREATOR = new Parcelable.Creator<Stamp>() {
        @Override
        public Stamp createFromParcel(Parcel in) {
            return new Stamp(in);
        }

        @Override
        public Stamp[] newArray(int size) {
            return new Stamp[size];
        }
    };

}
