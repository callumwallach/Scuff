package nz.co.scuff.data.journey;

import android.location.Location;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.google.gson.annotations.Expose;

import org.joda.time.DateTimeUtils;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

/**
 * Created by Callum on 20/04/2015.
 */
@Table(name="Waypoint")
public class Waypoint extends Model implements Serializable, Comparable {

    @Expose
    @Column(name="WaypointId")
    private String waypointId;
    @Expose
    @Column(name="Latitude")
    private double latitude;
    @Expose
    @Column(name="Longitude")
    private double longitude;
    @Expose
    @Column(name="Speed")
    private float speed;
    @Expose
    @Column(name="Bearing")
    private float bearing;
    @Expose
    @Column(name="Distance")
    private float distance;
    @Expose
    @Column(name="Duration")
    private long duration;
    @Expose
    @Column(name="Provider")
    private String provider;
    @Expose
    @Column(name="Accuracy")
    private float accuracy;
    @Expose
    @Column(name="Altitude")
    private double altitude;
    @Expose
    @Column(name="State")
    private int state;
    @Expose
    @Column(name="Created")
    private Timestamp created;

    // for use with ActiveAndroid. Not serialised
    @Column(name="JourneyFK")
    private Journey journey;

    public Journey getJourney() {
        return journey;
    }

    public void setJourney(Journey journey) {
        this.journey = journey;
    }

    public Waypoint() {
    }

    public Waypoint(String waypointId, float distance, long duration, int state, Location location) {
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

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

    public static Waypoint getLastWaypoint(Journey journey) {
         List<Waypoint> waypoints = new Select()
                .from(Waypoint.class)
                .where("JourneyFK = ?", journey.getId())
                .orderBy("Created DESC")
                .execute();
        return waypoints.iterator().hasNext() ? waypoints.iterator().next() : null;
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

        return !(waypointId != null ? !waypointId.equals(waypoint.waypointId) : waypoint.waypointId != null);

    }

    @Override
    public int hashCode() {
        return waypointId != null ? waypointId.hashCode() : 0;
    }
}
