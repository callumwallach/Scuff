package nz.co.scuff.data.school;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;

import nz.co.scuff.data.journey.Journey;
import nz.co.scuff.data.relationship.DriverSchool;
import nz.co.scuff.data.relationship.PassengerSchool;
import nz.co.scuff.data.school.snapshot.SchoolSnapshot;

/**
 * Created by Callum on 17/03/2015.
 */
@Table(name="Schools")
public class School extends Model implements Comparable, Serializable {

    @Column(name="SchoolId")
    private long schoolId;
    @Column(name="Name")
    private String name;
    @Column(name="Latitude")
    private double latitude;
    @Column(name="Longitude")
    private double longitude;
    @Column(name="Altitude")
    private double altitude;

    // one to many
    /*@Column(name="Routes")*/
    private SortedSet<Route> routes;
    /*@Column(name="Journeys")*/
    private SortedSet<Journey> journeys;

    // many to many
    private List<PassengerSchool> passengerSchools;
    private List<DriverSchool> driverSchools;

    public School() {
        super();
    }

    public School(String name, double latitude, double longitude, double altitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;

        this.routes = new TreeSet<>();
        this.journeys = new TreeSet<>();
        this.passengerSchools = new ArrayList<>();
        this.driverSchools = new ArrayList<>();

    }

    public School(SchoolSnapshot snapshot) {
        this(snapshot.getName(), snapshot.getLatitude(), snapshot.getLongitude(), snapshot.getAltitude());
        this.schoolId = snapshot.getSchoolId();
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

    public SortedSet<Route> getRoutes() {
        List<Route> routes = getMany(Route.class, "SchoolFK");
        if (routes != null) {
            return new TreeSet<>(routes);
        }
        return new TreeSet<>();
    }

    public void setRoutes(SortedSet<Route> routes) {
        this.routes = routes;
    }

    public SortedSet<Journey> getJourneys() {
        List<Journey> journeys = getMany(Journey.class, "SchoolFK");
        if (journeys != null) {
            return new TreeSet<>(journeys);
        }
        return new TreeSet<>();
    }

    public void setJourneys(SortedSet<Journey> journeys) {
        this.journeys = journeys;
    }

    public List<PassengerSchool> getPassengerSchools() {
        if (passengerSchools == null) {
            passengerSchools = new ArrayList<>();
        }
        return passengerSchools;
    }

    public void setPassengerSchools(List<PassengerSchool> passengerSchools) {
        this.passengerSchools = passengerSchools;
    }

    public List<DriverSchool> getDriverSchools() {
        if (driverSchools == null) {
            driverSchools = new ArrayList<>();
        }
        return driverSchools;
    }

    public void setDriverSchools(List<DriverSchool> driverSchools) {
        this.driverSchools = driverSchools;
    }

    public static School findBySchoolId(long schoolId) {
        return new Select()
                .from(School.class)
                .where("SchoolId = ?", schoolId)
                .executeSingle();
    }

    public SchoolSnapshot toSnapshot() {
        SchoolSnapshot snapshot = new SchoolSnapshot();
        snapshot.setSchoolId(schoolId);
        snapshot.setName(name);
        snapshot.setLatitude(latitude);
        snapshot.setLongitude(longitude);
        snapshot.setAltitude(altitude);
        return snapshot;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        School school = (School) o;

        return schoolId == school.schoolId;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (int) (schoolId ^ (schoolId >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "School{" +
                "schoolId=" + schoolId +
                ", name='" + name + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", altitude=" + altitude +
                '}';
    }

    @Override
    public int compareTo(Object another) {
        School other = (School)another;
        return this.name.compareTo(other.name);
    }
}