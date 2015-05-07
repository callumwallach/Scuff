package nz.co.scuff.data.journey.snapshot;

import com.google.gson.annotations.Expose;

import nz.co.scuff.data.family.snapshot.DriverSnapshot;
import nz.co.scuff.data.family.snapshot.PassengerSnapshot;
import nz.co.scuff.data.school.snapshot.RouteSnapshot;
import nz.co.scuff.data.school.snapshot.SchoolSnapshot;
import nz.co.scuff.data.util.TrackingState;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by Callum on 20/04/2015.
 */
public class JourneySnapshot implements Comparable, Serializable {

    @Expose
    private String journeyId;
    @Expose
    private String appId;
    @Expose
    private String source;
    @Expose
    private float totalDistance;
    @Expose
    private long totalDuration;
    @Expose
    private Timestamp created;
    @Expose
    private Timestamp completed;
    @Expose
    private TrackingState state;

    @Expose
    private SchoolSnapshot school;
    @Expose
    private DriverSnapshot driver;
    @Expose
    private RouteSnapshot route;

    @Expose
    private SortedSet<WaypointSnapshot> waypoints;
    @Expose
    private SortedSet<PassengerSnapshot> passengers;

    public JourneySnapshot() {
        waypoints = new TreeSet<>();
        passengers = new TreeSet<>();
    }

    public String getJourneyId() {
        return journeyId;
    }

    public void setJourneyId(String journeyId) {
        this.journeyId = journeyId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public float getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(float totalDistance) {
        this.totalDistance = totalDistance;
    }

    public long getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(long totalDuration) {
        this.totalDuration = totalDuration;
    }

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

    public Timestamp getCompleted() {
        return completed;
    }

    public void setCompleted(Timestamp completed) {
        this.completed = completed;
    }

    public TrackingState getState() {
        return state;
    }

    public void setState(TrackingState state) {
        this.state = state;
    }

    public SchoolSnapshot getSchool() {
        return school;
    }

    public void setSchool(SchoolSnapshot school) {
        this.school = school;
    }

    public DriverSnapshot getDriver() {
        return driver;
    }

    public void setDriver(DriverSnapshot driver) {
        this.driver = driver;
    }

    public RouteSnapshot getRoute() {
        return route;
    }

    public void setRoute(RouteSnapshot route) {
        this.route = route;
    }

    public SortedSet<WaypointSnapshot> getWaypoints() {
        return waypoints;
    }

    public void setWaypoints(SortedSet<WaypointSnapshot> waypoints) {
        this.waypoints = waypoints;
    }

    public SortedSet<PassengerSnapshot> getPassengers() {
        return passengers;
    }

    public void setPassengers(SortedSet<PassengerSnapshot> passengers) {
        this.passengers = passengers;
    }

    @Override
    public int compareTo(Object another) {
        JourneySnapshot other = (JourneySnapshot)another;
        return this.created.compareTo(other.created);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JourneySnapshot that = (JourneySnapshot) o;

        if (journeyId != null ? !journeyId.equals(that.journeyId) : that.journeyId != null) return false;
        return !(created != null ? !created.equals(that.created) : that.created != null);

    }

    @Override
    public int hashCode() {
        int result = journeyId != null ? journeyId.hashCode() : 0;
        result = 31 * result + (created != null ? created.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "JourneySnapshot{" +
                "journeyId='" + journeyId + '\'' +
                ", appId='" + appId + '\'' +
                ", source='" + source + '\'' +
                ", totalDistance=" + totalDistance +
                ", totalDuration=" + totalDuration +
                ", created=" + created +
                ", completed=" + completed +
                ", state=" + state +
                ", school=" + school +
                ", driver=" + driver +
                ", route=" + route +
                ", waypoints=" + waypoints +
                ", passengers=" + passengers +
                '}';
    }

}

