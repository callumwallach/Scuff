package nz.co.scuff.data.journey;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.google.gson.annotations.Expose;

import org.joda.time.DateTimeUtils;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import nz.co.scuff.data.journey.snapshot.WaypointSnapshot;
import nz.co.scuff.data.util.TrackingState;

/**
 * Created by Callum on 20/04/2015.
 */
@Table(name="Waypoints")
public class Waypoint extends Model implements Comparable, Parcelable {

    @Column(name="WaypointId")
    private String waypointId;
    @Column(name="Latitude")
    private double latitude;
    @Column(name="Longitude")
    private double longitude;
    @Column(name="Speed")
    private float speed;
    @Column(name="Bearing")
    private float bearing;
    @Column(name="Distance")
    private float distance;
    @Column(name="Duration")
    private long duration;
    @Column(name="Provider")
    private String provider;
    @Column(name="Accuracy")
    private float accuracy;
    @Column(name="Altitude")
    private double altitude;
    @Column(name="State")
    private TrackingState state;
    @Column(name="Created")
    private Timestamp created;

    // for use with ActiveAndroid. Not serialised
    @Column(name="JourneyFK", onDelete = Column.ForeignKeyAction.CASCADE)
    private Journey journey;

    public Waypoint() {
    }

    public Waypoint(String waypointId, float distance, long duration, TrackingState state, Location location) {
        this.waypointId = waypointId;
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
        this.speed = location.getSpeed();
        this.bearing = location.getBearing();
        this.distance = distance;
        this.duration = duration;
        this.provider = location.getProvider();
        this.accuracy = location.getAccuracy();
        this.altitude = location.getAltitude();
        this.created = new Timestamp(DateTimeUtils.currentTimeMillis());
        this.state = state;
    }

    public Waypoint(WaypointSnapshot snapshot) {
        this.waypointId = snapshot.getWaypointId();
        this.latitude = snapshot.getLatitude();
        this.longitude = snapshot.getLongitude();
        this.speed = snapshot.getSpeed();
        this.bearing = snapshot.getBearing();
        this.distance = snapshot.getDistance();
        this.duration = snapshot.getDuration();
        this.provider = snapshot.getProvider();
        this.accuracy = snapshot.getAccuracy();
        this.altitude = snapshot.getAltitude();
        this.state = snapshot.getState();
        this.created = snapshot.getCreated();
    }

    public String getWaypointId() {
        return waypointId;
    }

    public void setWaypointId(String id) {
        this.waypointId = id;
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

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
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

    public TrackingState getState() {
        return state;
    }

    public void setState(TrackingState state) {
        this.state = state;
    }

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

    public Journey getJourney() {
        return journey;
    }

    public void setJourney(Journey journey) {
        this.journey = journey;
    }

    public static Waypoint findById(String waypointId) {
        return new Select()
                .from(Waypoint.class)
                .where("WaypointId = ?", waypointId)
                .executeSingle();
    }

    public WaypointSnapshot toSnapshot() {
        WaypointSnapshot snapshot = new WaypointSnapshot();
        snapshot.setWaypointId(waypointId);
        snapshot.setLatitude(latitude);
        snapshot.setLongitude(longitude);
        snapshot.setSpeed(speed);
        snapshot.setBearing(bearing);
        snapshot.setDistance(distance);
        snapshot.setDuration(duration);
        snapshot.setProvider(provider);
        snapshot.setAccuracy(accuracy);
        snapshot.setAltitude(altitude);
        snapshot.setState(state);
        snapshot.setCreated(created);
        return snapshot;
    }

    @Override
    public int compareTo(Object another) {
        Waypoint other = (Waypoint)another;
        return this.created.compareTo(other.created);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Waypoint waypoint = (Waypoint) o;

        return waypointId.equals(waypoint.waypointId);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (waypointId != null ? waypointId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Waypoint{" +
                "waypointId='" + waypointId + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", speed=" + speed +
                ", bearing=" + bearing +
                ", distance=" + distance +
                ", duration=" + duration +
                ", provider='" + provider + '\'' +
                ", accuracy=" + accuracy +
                ", altitude=" + altitude +
                ", state=" + state +
                ", created=" + created +
                '}';
    }

    protected Waypoint(Parcel in) {
        waypointId = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        speed = in.readFloat();
        bearing = in.readFloat();
        distance = in.readFloat();
        duration = in.readLong();
        provider = in.readString();
        accuracy = in.readFloat();
        altitude = in.readDouble();
        state = (TrackingState) in.readValue(TrackingState.class.getClassLoader());
        created = (Timestamp) in.readValue(Timestamp.class.getClassLoader());
        journey = (Journey) in.readValue(Journey.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(waypointId);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeFloat(speed);
        dest.writeFloat(bearing);
        dest.writeFloat(distance);
        dest.writeLong(duration);
        dest.writeString(provider);
        dest.writeFloat(accuracy);
        dest.writeDouble(altitude);
        dest.writeValue(state);
        dest.writeValue(created);
        dest.writeValue(journey);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Waypoint> CREATOR = new Parcelable.Creator<Waypoint>() {
        @Override
        public Waypoint createFromParcel(Parcel in) {
            return new Waypoint(in);
        }

        @Override
        public Waypoint[] newArray(int size) {
            return new Waypoint[size];
        }
    };
}
