package nz.co.scuff.data.school;

import com.google.gson.annotations.Expose;

/**
 * Created by Callum on 4/05/2015.
 */
public class SchoolSnapshot implements Comparable {

    @Expose
    private long schoolId;
    @Expose
    private String name;
    @Expose
    private double latitude;
    @Expose
    private double longitude;
    @Expose
    private double altitude;

    public SchoolSnapshot() {
    }

    public long getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(long schoolId) {
        this.schoolId = schoolId;
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
    public int compareTo(Object another) {
        SchoolSnapshot other = (SchoolSnapshot)another;
        return this.name.compareTo(other.name);
    }

    @Override
    public String toString() {
        return "SchoolSnapshot{" +
                "schoolId=" + schoolId +
                ", name='" + name + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", altitude=" + altitude +
                '}';
    }
}
