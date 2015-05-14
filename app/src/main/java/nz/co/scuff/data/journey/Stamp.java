package nz.co.scuff.data.journey;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;

import java.io.Serializable;
import java.sql.Timestamp;

import nz.co.scuff.data.journey.snapshot.StampSnapshot;

/**
 * Created by Callum on 14/05/2015.
 */
public class Stamp extends Model implements Serializable {

    @Column(name="Latitude")
    private double latitude;
    @Column(name="Longitude")
    private long longitude;
    @Column(name="StampDate")
    private Timestamp stampDate;

    public Stamp() {}

    public Stamp(StampSnapshot snapshot) {
        this.latitude = snapshot.getLatitude();
        this.longitude = snapshot.getLongitude();
        this.stampDate = snapshot.getStampDate();
    }

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

    public StampSnapshot toSnapshot() {
        StampSnapshot snapshot = new StampSnapshot();
        snapshot.setLatitude(this.latitude);
        snapshot.setLongitude(this.longitude);
        snapshot.setStampDate(this.stampDate);
        return snapshot;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Stamp stamp = (Stamp) o;

        if (Double.compare(stamp.latitude, latitude) != 0) return false;
        if (longitude != stamp.longitude) return false;
        return !(stampDate != null ? !stampDate.equals(stamp.stampDate) : stamp.stampDate != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        long temp;
        temp = Double.doubleToLongBits(latitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (int) (longitude ^ (longitude >>> 32));
        result = 31 * result + (stampDate != null ? stampDate.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Stamp{");
        sb.append("latitude=").append(latitude);
        sb.append(", longitude=").append(longitude);
        sb.append(", stampDate=").append(stampDate);
        sb.append('}');
        return sb.toString();
    }
}
