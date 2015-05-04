package nz.co.scuff.data.journey;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import nz.co.scuff.data.util.TrackingState;

/**
 * Created by Callum on 20/04/2015.
 */
@Table(name="JourneySnapshots")
public class JourneySnapshot extends Model implements Comparable, Serializable {

    @Expose
    @Column(name="JourneyId")
    private String journeyId;
    @Expose
    @Column(name="SchoolId")
    private String schoolId;
    @Expose
    @Column(name="DriverId")
    private String driverId;
    @Expose
    @Column(name="RouteId")
    private String routeId;
    @Expose
    @Column(name="Started")
    private Timestamp started;
    @Expose
    @Column(name="Expiry")
    private Timestamp expiry;

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
    @Column(name="Accuracy")
    private float accuracy;
    @Expose
    @Column(name="Altitude")
    private double altitude;
    @Expose
    @Column(name="State")
    private TrackingState state;
    @Expose
    @Column(name="Timestamp")
    private Timestamp timestamp;

    public JourneySnapshot() {}

    public String getJourneyId() {
        return journeyId;
    }

    public void setJourneyId(String journeyId) {
        this.journeyId = journeyId;
    }

    public String getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public Timestamp getStarted() {
        return started;
    }

    public void setStarted(Timestamp started) {
        this.started = started;
    }

    public Timestamp getExpiry() {
        return expiry;
    }

    public void setExpiry(Timestamp expiry) {
        this.expiry = expiry;
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

    public TrackingState getState() {
        return state;
    }

    public void setState(TrackingState state) {
        this.state = state;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public static List<JourneySnapshot> findByRouteAndSchool(String routeId, String schoolId) {
        return new Select()
                .from(JourneySnapshot.class)
                .where("RouteId = ?", routeId)
                .where("SchoolId = ?", schoolId)
                .execute();
    }

    public static JourneySnapshot findByJourneyId(String journeyId) {
        List<JourneySnapshot> journeys = new Select()
                .from(JourneySnapshot.class)
                .where("JourneyId = ?", journeyId)
                .execute();
        return journeys.iterator().hasNext() ? journeys.iterator().next() : null;
    }

    @Override
    public int compareTo(Object another) {
        JourneySnapshot other = (JourneySnapshot)another;
        return this.timestamp.compareTo(other.timestamp);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JourneySnapshot that = (JourneySnapshot) o;

        if (journeyId != null ? !journeyId.equals(that.journeyId) : that.journeyId != null) return false;
        return !(timestamp != null ? !timestamp.equals(that.timestamp) : that.timestamp != null);

    }

    @Override
    public int hashCode() {
        int result = journeyId != null ? journeyId.hashCode() : 0;
        result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Snapshot{" +
                "journeyId='" + journeyId + '\'' +
                ", schoolId='" + schoolId + '\'' +
                ", driverId='" + driverId + '\'' +
                ", routeId='" + routeId + '\'' +
                ", started=" + started +
                ", expiry=" + expiry +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", speed=" + speed +
                ", bearing=" + bearing +
                ", accuracy=" + accuracy +
                ", altitude=" + altitude +
                ", state=" + state +
                ", timestamp=" + timestamp +
                '}';
    }
}

