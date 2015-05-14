package nz.co.scuff.data.journey;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import nz.co.scuff.data.family.Driver;
import nz.co.scuff.data.journey.snapshot.JourneySnapshot;
import nz.co.scuff.data.school.Route;
import nz.co.scuff.data.school.School;
import nz.co.scuff.data.util.TrackingState;

/**
 * Created by Callum on 20/04/2015.
 */
@Table(name="Journeys")
public class Journey extends Model implements Comparable, Serializable {

    //private static final long serialVersionUID = 2L;

    @Column(name="JourneyId")
    private String journeyId;
    @Column(name="AppId")
    private String appId;

    @Column(name="SchoolFK", onDelete = Column.ForeignKeyAction.CASCADE)
    private School school;
    @Column(name="RouteFK")
    private Route route;
    @Column(name="DriverFK")
    private Driver driver;

    @Column(name="Source")
    private String source;

    // calculated fields
    @Column(name="TotalDistance")
    private float totalDistance;
    @Column(name="TotalDuration")
    private long totalDuration;

    @Column(name="Created")
    private Timestamp created;
    @Column(name="Completed")
    private Timestamp completed;
    @Column(name="State")
    private TrackingState state;

    // relationships
    private SortedSet<Waypoint> waypoints;
    private SortedSet<Ticket> tickets;

    public Journey() {
        waypoints = new TreeSet<>();
        tickets = new TreeSet<>();
    }

    public Journey(JourneySnapshot snapshot) {
        this.journeyId = snapshot.getJourneyId();
        this.appId = snapshot.getAppId();
        this.source = snapshot.getSource();
        // school, route and driver set manually
        this.totalDistance = snapshot.getTotalDistance();
        this.totalDuration = snapshot.getTotalDuration();
        this.created = snapshot.getCreated();
        this.completed = snapshot.getCompleted();
        this.state = snapshot.getState();

        waypoints = new TreeSet<>();
        tickets = new TreeSet<>();
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

    public School getSchool() {
        return school;
    }

    public void setSchool(School school) {
        this.school = school;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
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

    public SortedSet<Waypoint> getWaypoints() {
        if (waypoints == null) {
            waypoints = new TreeSet<>();
        }
        return waypoints;
    }

    public void setWaypoints(SortedSet<Waypoint> waypoints) {
        this.waypoints = waypoints;
    }

    public SortedSet<Ticket> getTickets() {
        if (this.tickets == null) {
            this.tickets = new TreeSet<>();
        }
        return tickets;
    }

    public void setTickets(SortedSet<Ticket> tickets) {
        this.tickets = tickets;
    }

    public static List<Journey> findByRouteAndSchool(String routeId, String schoolId) {
        return new Select()
                .from(Journey.class)
                .where("RouteId = ?", routeId)
                .where("SchoolId = ?", schoolId)
                .execute();
    }

    public Waypoint getCurrentWaypoint() {
        if (waypoints.isEmpty()) {
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

    public JourneySnapshot toSnapshot() {
        JourneySnapshot snapshot = new JourneySnapshot();
        snapshot.setJourneyId(journeyId);
        snapshot.setAppId(appId);
        snapshot.setSource(source);
        snapshot.setTotalDistance(totalDistance);
        snapshot.setTotalDuration(totalDuration);
        snapshot.setCreated(created);
        snapshot.setCompleted(completed);
        snapshot.setState(state);
        // entities
        snapshot.setSchoolId(school.getSchoolId());
        snapshot.setDriverId(driver.getPersonId());
        snapshot.setRouteId(route.getRouteId());
        return snapshot;
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
                ", source='" + source + '\'' +
                ", totalDistance=" + totalDistance +
                ", totalDuration=" + totalDuration +
                ", created=" + created +
                ", completed=" + completed +
                ", state=" + state +
                '}';
    }
}

