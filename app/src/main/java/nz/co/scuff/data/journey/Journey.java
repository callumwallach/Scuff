package nz.co.scuff.data.journey;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import nz.co.scuff.data.util.TrackingState;

/**
 * Created by Callum on 20/04/2015.
 */
@Table(name="Journeys")
public class Journey extends Model implements Comparable, Serializable {

    //private static final long serialVersionUID = 2L;

    @Expose
    @Column(name="JourneyId")
    private String journeyId;
    @Expose
    @Column(name="AppId")
    private String appId;
    @Expose
    @Column(name="SchoolId")
    private String schoolId;
    @Expose
    @Column(name="DriverId")
    private String driverId;
    @Expose
    @Column(name="RouteId")
    private String routeId;
    @Expose
    @Column(name="Source")
    private String source;

    // calculated fields
    @Expose
    @Column(name="TotalDistance")
    private float totalDistance;
    @Expose
    @Column(name="TotalDuration")
    private long totalDuration;

    @Expose
    @Column(name="Created")
    private Timestamp created;
    @Expose
    @Column(name="Completed")
    private Timestamp completed;
    @Expose
    @Column(name="State")
    private TrackingState state;

    // relationships
    @Expose
    @Column(name="Waypoints")
    private SortedSet<Waypoint> waypoints;

    // active android relationship requirement
    public List<Waypoint> waypoints() {
        return getMany(Waypoint.class, "JourneyFK");
    }

    public Journey() {
        waypoints = new TreeSet<>();
    }

    public Journey(String journeyId, String appId, String schoolId, String driverId,
                   String routeId, String source, float totalDistance, long totalDuration,
                   Timestamp created, Timestamp completed, TrackingState state) {
        this.journeyId = journeyId;
        this.appId = appId;
        this.schoolId = schoolId;
        this.driverId = driverId;
        this.routeId = routeId;
        this.source = source;
        this.totalDistance = totalDistance;
        this.totalDuration = totalDuration;
        this.created = created;
        this.completed = completed;
        this.state = state;
        waypoints = new TreeSet<>();
    }

    public String getJourneyId() {
        return journeyId;
    }

    public void setJourneyId(String id) {
        this.journeyId = id;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
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

    public void addWaypoint(Waypoint waypoint) {
        waypoints.add(waypoint);
    }

    public SortedSet<Waypoint> getWaypoints() {
        return waypoints;
    }

    public void setWaypoints(SortedSet<Waypoint> waypoints) {
        this.waypoints = waypoints;
    }

    public Waypoint getCurrentWaypoint() {
        if (waypoints.size() == 0) {
            List<Waypoint> foundWaypoints = new Select()
                    .from(Waypoint.class)
                    .where("JourneyFK = ?", getId())
                    .orderBy("Created DESC")
                    .execute();
            waypoints.addAll(foundWaypoints);
        }
        return waypoints.first();
    }

    public static Journey findByJourneyId(String journeyId) {
        List<Journey> journeys = new Select()
                .from(Journey.class)
                .where("JourneyId = ?", journeyId)
                .execute();
        return journeys.iterator().hasNext() ? journeys.iterator().next() : null;
    }

    public static List<Journey> findIncomplete() {
        List<Journey> journeys = new Select()
                .from(Journey.class)
                .where("State != ?", TrackingState.COMPLETED)
                .execute();
        return journeys;
    }

    @Override
    public int compareTo(Object another) {
        Journey other = (Journey)another;
        return this.created.compareTo(other.created);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Journey journey = (Journey) o;

        return !(journeyId != null ? !journeyId.equals(journey.journeyId) : journey.journeyId != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (journeyId != null ? journeyId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Journey{" +
                "journeyId='" + journeyId + '\'' +
                ", appId='" + appId + '\'' +
                ", schoolId='" + schoolId + '\'' +
                ", driverId='" + driverId + '\'' +
                ", routeId='" + routeId + '\'' +
                ", source='" + source + '\'' +
                ", totalDistance=" + totalDistance +
                ", totalDuration=" + totalDuration +
                ", created=" + created +
                ", completed=" + completed +
                ", state=" + state +
                '}';
    }
}

