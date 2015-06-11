package nz.co.scuff.data.place.snapshot;

import com.google.gson.annotations.Expose;

import nz.co.scuff.data.base.snapshot.ModifiableSnapshot;

/**
 * Created by Callum on 3/06/2015.
 */
public class PlaceSnapshot extends ModifiableSnapshot {

    @Expose
    private long placeId;
    @Expose
    private String name;
    @Expose
    private double latitude;
    @Expose
    private double longitude;
    @Expose
    private double altitude;

    public PlaceSnapshot() {
        super();
    }

    public long getPlaceId() {
        return placeId;
    }

    public void setPlaceId(long placeId) {
        this.placeId = placeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    @Override
    public String toString() {
        return "PlaceSnapshot{" +
                "placeId=" + placeId +
                ", name='" + name + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", altitude=" + altitude +
                "} " + super.toString();
    }
}
