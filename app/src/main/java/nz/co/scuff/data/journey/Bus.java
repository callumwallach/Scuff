package nz.co.scuff.data.journey;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import nz.co.scuff.data.journey.snapshot.BusSnapshot;

/**
 * Created by Callum on 7/05/2015.
 */
@Table(name="Buses")
public class Bus extends Model implements Comparable, Serializable, Parcelable {

    @Column(name="JourneyId")
    private String journeyId;
    @Column(name="DriverId")
    private long routeId;
    @Column(name="SchoolId")
    private long schoolId;

    @Column(name="DriverName")
    private String driverName;

    @Column(name="Latitude")
    private double latitude;
    @Column(name="Longitude")
    private double longitude;
    @Column(name="Speed")
    private float speed;
    @Column(name="Bearing")
    private float bearing;
    @Column(name="Accuracy")
    private float accuracy;
    @Column(name="Altitude")
    private double altitude;
    @Column(name="Created")
    private Timestamp created;

    public Bus() {
        super();
    }

    public Bus(BusSnapshot snapshot) {
        this.journeyId = snapshot.getJourneyId();
        this.routeId = snapshot.getRouteId();
        this.schoolId = snapshot.getSchoolId();
        this.driverName = snapshot.getDriverName();
        this.latitude = snapshot.getLatitude();
        this.longitude = snapshot.getLongitude();
        this.speed = snapshot.getSpeed();
        this.bearing = snapshot.getBearing();
        this.accuracy = snapshot.getAccuracy();
        this.altitude = snapshot.getAltitude();
        this.created = snapshot.getCreated();
    }

    public String getJourneyId() {
        return journeyId;
    }

    public void setJourneyId(String journeyId) {
        this.journeyId = journeyId;
    }

    public long getRouteId() {
        return routeId;
    }

    public void setRouteId(long routeId) {
        this.routeId = routeId;
    }

    public long getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(long schoolId) {
        this.schoolId = schoolId;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
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

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getBearing() {
        return bearing;
    }

    public void setBearing(float bearing) {
        this.bearing = bearing;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

    public static Bus findByJourneyId(String journeyId) {
        return new Select()
                .from(Bus.class)
                .where("JourneyId = ?", journeyId)
                .executeSingle();
    }

    public static List<Bus> findByRouteAndSchool(String routeId, String schoolId) {
        return new Select()
                .from(Bus.class)
                .where("RouteId = ?", routeId)
                .where("SchoolId = ?", schoolId)
                .execute();
    }

    @Override
    public int compareTo(Object another) {
        Bus other = (Bus)another;
        return this.created.compareTo(other.created);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Bus bus = (Bus) o;

        if (!journeyId.equals(bus.journeyId)) return false;
        return created.equals(bus.created);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + journeyId.hashCode();
        result = 31 * result + created.hashCode();
        return result;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Bus{");
        sb.append("journeyId='").append(journeyId).append('\'');
        sb.append(", routeId=").append(routeId);
        sb.append(", schoolId=").append(schoolId);
        sb.append(", driverName='").append(driverName).append('\'');
        sb.append(", latitude=").append(latitude);
        sb.append(", longitude=").append(longitude);
        sb.append(", speed=").append(speed);
        sb.append(", bearing=").append(bearing);
        sb.append(", accuracy=").append(accuracy);
        sb.append(", altitude=").append(altitude);
        sb.append(", created=").append(created);
        sb.append('}');
        return sb.toString();
    }

    protected Bus(Parcel in) {
        journeyId = in.readString();
        routeId = in.readLong();
        schoolId = in.readLong();
        driverName = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        speed = in.readFloat();
        bearing = in.readFloat();
        accuracy = in.readFloat();
        altitude = in.readDouble();
        created = (Timestamp) in.readValue(Timestamp.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(journeyId);
        dest.writeLong(routeId);
        dest.writeLong(schoolId);
        dest.writeString(driverName);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeFloat(speed);
        dest.writeFloat(bearing);
        dest.writeFloat(accuracy);
        dest.writeDouble(altitude);
        dest.writeValue(created);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Bus> CREATOR = new Parcelable.Creator<Bus>() {
        @Override
        public Bus createFromParcel(Parcel in) {
            return new Bus(in);
        }

        @Override
        public Bus[] newArray(int size) {
            return new Bus[size];
        }
    };
}

