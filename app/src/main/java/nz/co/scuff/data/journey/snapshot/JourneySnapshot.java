package nz.co.scuff.data.journey.snapshot;

import com.google.gson.annotations.Expose;

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
    private long schoolId;
    @Expose
    private long driverId;
    @Expose
    private long routeId;

    @Expose
    private SortedSet<WaypointSnapshot> waypoints;
    @Expose
    private SortedSet<TicketSnapshot> tickets;

    public JourneySnapshot() {
        waypoints = new TreeSet<>();
        tickets = new TreeSet<>();
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

    public long getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(long schoolId) {
        this.schoolId = schoolId;
    }

    public long getDriverId() {
        return driverId;
    }

    public void setDriverId(long driverId) {
        this.driverId = driverId;
    }

    public long getRouteId() {
        return routeId;
    }

    public void setRouteId(long routeId) {
        this.routeId = routeId;
    }

    public SortedSet<WaypointSnapshot> getWaypoints() {
        return waypoints;
    }

    public void setWaypoints(SortedSet<WaypointSnapshot> waypoints) {
        this.waypoints = waypoints;
    }

    public SortedSet<TicketSnapshot> getTickets() {
        return tickets;
    }

    public void setTickets(SortedSet<TicketSnapshot> tickets) {
        this.tickets = tickets;
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
                ", schoolId=" + schoolId +
                ", driverId=" + driverId +
                ", routeId=" + routeId +
                ", waypoints=" + waypoints +
                ", tickets=" + tickets +
                '}';
    }

}

