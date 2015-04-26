package nz.co.scuff.data.journey;

import android.location.Location;

import org.joda.time.DateTimeUtils;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by Callum on 20/04/2015.
 */
public class Waypoint implements Serializable {

    private String id;
    private double latitude;
    private double longitude;
    private float speed;
    private float bearing;
    private float totalDistance;
    private String provider;
    private float accuracy;
    private double altitude;
    private int state;
    private Timestamp timestamp;

    public Waypoint() {
    }

    public Waypoint(String id, float totalDistance, int state, Location location) {
        this.id = id;
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
        this.speed = location.getSpeed();
        this.bearing = location.getBearing();
        this.totalDistance = totalDistance;
        this.provider = location.getProvider();
        this.accuracy = location.getAccuracy();
        this.altitude = location.getAltitude();
        this.timestamp = new Timestamp(DateTimeUtils.currentTimeMillis());
        this.state = state;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public float getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(float totalDistance) {
        this.totalDistance = totalDistance;
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

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Waypoint waypoint = (Waypoint) o;

        return id.equals(waypoint.id);

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
