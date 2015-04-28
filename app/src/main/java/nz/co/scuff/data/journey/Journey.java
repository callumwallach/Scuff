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

import nz.co.scuff.android.util.TrackingState;

/**
 * Created by Callum on 20/04/2015.
 */
@Table(name="Journeys")
public class Journey extends Model implements Serializable, Comparable {

    //private static final long serialVersionUID = 2L;

    @Expose
    @Column(name="JourneyId")
    private String journeyId;
    @Expose
    @Column(name="AppId")
    private long appId;
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

    public Journey(String journeyId, long appId, String schoolId, String driverId,
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

    public long getAppId() {
        return appId;
    }

    public void setAppId(long appId) {
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

    public static Journey findByJourneyId(String journeyId) {
        List<Journey> journeys = new Select()
                .from(Journey.class)
                .where("JourneyId = ?", journeyId)
                .execute();
        return journeys.iterator().hasNext() ? journeys.iterator().next() : null;
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
        return journeyId != null ? journeyId.hashCode() : 0;
    }
}

/*
package nz.co.scuff.data.journey;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.Date;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

@Table(name="Journeys")
public class Journey extends Model implements Serializable, Comparable {

    //private static final long serialVersionUID = 2L;

    public enum TrackingState {
        RECORDING, PAUSED, COMPLETED
    }

    @Expose
    @Column(name="JourneyId")
    private String journeyId;
    @Expose
    @Column(name="AppId")
    private long appId;
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
    private Date created;
    @Expose
    @Column(name="Completed")
    private Date completed;
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

    public Journey(String journeyId, long appId, String schoolId, String driverId,
                   String routeId, String source, float totalDistance, long totalDuration,
                   Date created, Date completed, TrackingState state) {
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

    public long getAppId() {
        return appId;
    }

    public void setAppId(long appId) {
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

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getCompleted() {
        return completed;
    }

    public void setCompleted(Date completed) {
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

    public Waypoint getLastWaypoint() {
        return waypoints.first();
    }

    public SortedSet<Waypoint> getWaypoints() {
        return waypoints;
    }

    public void setWaypoints(SortedSet<Waypoint> waypoints) {
        this.waypoints = waypoints;
    }

    public static Journey findByJourneyId(String journeyId) {
        List<Journey> journeys = new Select()
                .from(Journey.class)
                .where("JourneyId = ?", journeyId)
                .execute();
        return journeys.iterator().hasNext() ? journeys.iterator().next() : null;
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
        return journeyId != null ? journeyId.hashCode() : 0;
    }
}

 */