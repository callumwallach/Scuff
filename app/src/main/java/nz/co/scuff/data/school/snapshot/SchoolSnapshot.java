package nz.co.scuff.data.school.snapshot;

import com.google.gson.annotations.Expose;

import nz.co.scuff.data.family.snapshot.PassengerSnapshot;
import nz.co.scuff.data.family.snapshot.DriverSnapshot;
import nz.co.scuff.data.journey.snapshot.JourneySnapshot;

import java.util.SortedSet;
import java.util.TreeSet;

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

    @Expose
    private SortedSet<RouteSnapshot> routes;
    @Expose
    private SortedSet<PassengerSnapshot> children;
    @Expose
    private SortedSet<DriverSnapshot> parents;
    @Expose
    private SortedSet<JourneySnapshot> journeys;

    public SchoolSnapshot() {
        routes = new TreeSet<>();
        children = new TreeSet<>();
        parents = new TreeSet<>();
        journeys = new TreeSet<>();
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

    public SortedSet<RouteSnapshot> getRoutes() {
        return routes;
    }

    public void setRoutes(SortedSet<RouteSnapshot> routes) {
        this.routes = routes;
    }

    public SortedSet<PassengerSnapshot> getChildren() {
        return children;
    }

    public void setChildren(SortedSet<PassengerSnapshot> children) {
        this.children = children;
    }

    public SortedSet<DriverSnapshot> getParents() {
        return parents;
    }

    public void setParents(SortedSet<DriverSnapshot> parents) {
        this.parents = parents;
    }

    public SortedSet<JourneySnapshot> getJourneys() {
        return journeys;
    }

    public void setJourneys(SortedSet<JourneySnapshot> journeys) {
        this.journeys = journeys;
    }

    @Override
    public int compareTo(Object another) {
        SchoolSnapshot other = (SchoolSnapshot)another;
        return this.name.compareTo(other.name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SchoolSnapshot that = (SchoolSnapshot) o;

        return schoolId == that.schoolId;

    }

    @Override
    public int hashCode() {
        return (int) (schoolId ^ (schoolId >>> 32));
    }

    @Override
    public String toString() {
        return "SchoolSnapshot{" +
                "schoolId=" + schoolId +
                ", name='" + name + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", altitude=" + altitude +
                ", routes=" + routes +
                ", children=" + children +
                ", parents=" + parents +
                ", journeys=" + journeys +
                '}';
    }
}
