package nz.co.scuff.data.journey.snapshot;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

import java.sql.Timestamp;

/**
 * Created by Callum on 10/05/2015.
 */
public class StampSnapshot implements Parcelable {

    @Expose
    private double latitude;
    @Expose
    private long longitude;
    @Expose
    private Timestamp stampDate;

    public StampSnapshot() {}

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public long getLongitude() {
        return longitude;
    }

    public void setLongitude(long longitude) {
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

        if (Double.compare(that.latitude, latitude) != 0) return false;
        if (longitude != that.longitude) return false;
        return stampDate.equals(that.stampDate);

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(latitude);
        result = (int) (temp ^ (temp >>> 32));
        result = 31 * result + (int) (longitude ^ (longitude >>> 32));
        result = 31 * result + stampDate.hashCode();
        return result;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("StampSnapshot{");
        sb.append("latitude=").append(latitude);
        sb.append(", longitude=").append(longitude);
        sb.append(", stampDate=").append(stampDate);
        sb.append('}');
        return sb.toString();
    }

    protected StampSnapshot(Parcel in) {
        latitude = in.readDouble();
        longitude = in.readLong();
        stampDate = (Timestamp) in.readValue(Timestamp.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(latitude);
        dest.writeLong(longitude);
        dest.writeValue(stampDate);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<StampSnapshot> CREATOR = new Parcelable.Creator<StampSnapshot>() {
        @Override
        public StampSnapshot createFromParcel(Parcel in) {
            return new StampSnapshot(in);
        }

        @Override
        public StampSnapshot[] newArray(int size) {
            return new StampSnapshot[size];
        }
    };

}
