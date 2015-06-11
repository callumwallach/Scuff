package nz.co.scuff.data.journey.snapshot;

import com.google.gson.annotations.Expose;

import nz.co.scuff.data.base.snapshot.Snapshot;
import nz.co.scuff.data.util.TrackingState;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by Callum on 20/04/2015.
 */
public class WaypointSnapshot implements Snapshot, Comparable {

    @Expose
    private String waypointId;
    @Expose
    private double latitude;
    @Expose
    private double longitude;
    @Expose
    private float speed;
    @Expose
    private float bearing;
    @Expose
    private float distance;
    @Expose
    private long duration;
    @Expose
    private String provider;
    @Expose
    private float accuracy;
    @Expose
    private double altitude;
    @Expose
    private TrackingState state;
    @Expose
    private Timestamp created;

    public WaypointSnapshot() {}

    public String getWaypointId() {
        return waypointId;
    }

    public void setWaypointId(String waypointId) {
        this.waypointId = waypointId;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WaypointSnapshot that = (WaypointSnapshot) o;

        return waypointId.equals(that.waypointId);

    }

    @Override
    public int hashCode() {
        int result = waypointId != null ? waypointId.hashCode() : 0;
        result = 31 * result + (created != null ? created.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(Object another) {
        WaypointSnapshot other = (WaypointSnapshot)another;
        return this.created.compareTo(other.created);
    }

    @Override
    public String toString() {
        return "WaypointSnapshot{" +
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

}

