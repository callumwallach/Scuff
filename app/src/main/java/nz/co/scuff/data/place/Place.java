package nz.co.scuff.data.place;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import nz.co.scuff.data.base.ModifiableEntity;
import nz.co.scuff.data.base.Snapshotable;
import nz.co.scuff.data.place.snapshot.PlaceSnapshot;

/**
 * Created by Callum on 3/06/2015.
 */
@Table(name="Places")
public class Place extends ModifiableEntity implements Snapshotable, Comparable {

    @Column(name="PlaceId")
    private long placeId;
    @Column(name="Name")
    private String name;
    @Column(name="Latitude")
    private double latitude;
    @Column(name="Longitude")
    private double longitude;
    @Column(name="Altitude")
    private double altitude;

    public Place() {
    }

    public Place(String name, double latitude, double longitude, double altitude) {
        super();
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
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

    public static Place findById(long coordinatorId) {
        return new Select()
                .from(Place.class)
                .where("PlaceId = ?", coordinatorId)
                .executeSingle();
    }

    public void refresh(PlaceSnapshot snapshot) {
        this.placeId = snapshot.getPlaceId();
        this.name = snapshot.getName();
        this.latitude = snapshot.getLatitude();
        this.longitude = snapshot.getLongitude();
        this.altitude = snapshot.getAltitude();
        this.active = snapshot.isActive();
        this.lastModified = snapshot.getLastModified();
    }

    public PlaceSnapshot toSnapshot() {
        PlaceSnapshot snapshot = new PlaceSnapshot();
        snapshot.setPlaceId(placeId);
        snapshot.setName(name);
        snapshot.setLatitude(latitude);
        snapshot.setLongitude(longitude);
        snapshot.setAltitude(altitude);
        snapshot.setActive(active);
        snapshot.setLastModified(lastModified);
        return snapshot;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Place place = (Place) o;

        return placeId == place.placeId;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (int) (placeId ^ (placeId >>> 32));
        return result;
    }

    @Override
    public int compareTo(Object another) {
        Place that = (Place)another;
        return this.name.compareTo(that.name);
    }

    @Override
    public String toString() {
        return "Place{" +
                "placeId=" + placeId +
                ", name=" + name +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", altitude=" + altitude +
                '}';
    }

    protected Place(Parcel in) {
        super(in);
        placeId = in.readLong();
        name = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        altitude = in.readDouble();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(placeId);
        dest.writeString(name);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeDouble(altitude);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Place> CREATOR = new Parcelable.Creator<Place>() {
        @Override
        public Place createFromParcel(Parcel in) {
            return new Place(in);
        }

        @Override
        public Place[] newArray(int size) {
            return new Place[size];
        }
    };

}
