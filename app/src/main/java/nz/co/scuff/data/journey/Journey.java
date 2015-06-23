package nz.co.scuff.data.journey;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.sql.Timestamp;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import nz.co.scuff.data.base.Coordinator;
import nz.co.scuff.data.base.ModifiableEntity;
import nz.co.scuff.data.base.Snapshotable;
import nz.co.scuff.data.journey.snapshot.JourneySnapshot;
import nz.co.scuff.data.institution.Route;
import nz.co.scuff.data.place.Place;
import nz.co.scuff.data.util.Constants;
import nz.co.scuff.data.util.TrackingState;

/**
 * Created by Callum on 20/04/2015.
 */
@Table(name="Journeys")
public class Journey extends ModifiableEntity implements Snapshotable, Comparable {

    @Column(name="JourneyId")
    private long journeyId;
    @Column(name="AppId")
    private String appId;
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

    @Column(name="RouteFK")
    private Route route;

    @Column(name="OwnerFK")
    private Coordinator owner;
    @Column(name="AgentFK")
    private Coordinator agent;
    @Column(name="GuideFK")
    private Coordinator guide;

    @Column(name="Origin")
    private Place origin;
    @Column(name="Destination")
    private Place destination;

    // relationships
    private SortedSet<Waypoint> waypoints;
    private SortedSet<Ticket> tickets;

    public Journey() {
        super();
        this.journeyId = Constants.JOURNEY_PRE_CREATED_KEY;
        /*this.waypoints = new TreeSet<>();
        this.tickets = new TreeSet<>();*/
    }

    public Journey(JourneySnapshot snapshot) {
        this.journeyId = snapshot.getJourneyId();
        this.appId = snapshot.getAppId();
        this.source = snapshot.getSource();
        this.totalDistance = snapshot.getTotalDistance();
        this.totalDuration = snapshot.getTotalDuration();
        this.created = snapshot.getCreated();
        this.completed = snapshot.getCompleted();
        this.state = snapshot.getState();

        this.active = snapshot.isActive();
        this.lastModified = snapshot.getLastModified();

        /*waypoints = new TreeSet<>();
        tickets = new TreeSet<>();*/
    }

    public long getJourneyId() {
        return journeyId;
    }

    public void setJourneyId(long journeyId) {
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

    public Coordinator getOwner() {
        return owner;
    }

    public void setOwner(Coordinator owner) {
        this.owner = owner;
    }

    public Coordinator getAgent() {
        return agent;
    }

    public void setAgent(Coordinator agent) {
        this.agent = agent;
    }

    public Coordinator getGuide() {
        return guide;
    }

    public void setGuide(Coordinator guide) {
        this.guide = guide;
    }

    public Place getOrigin() {
        return origin;
    }

    public void setOrigin(Place origin) {
        this.origin = origin;
    }

    public Place getDestination() {
        return destination;
    }

    public void setDestination(Place destination) {
        this.destination = destination;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public SortedSet<Waypoint> getWaypoints() {
        if (this.waypoints == null) {
            this.waypoints = new TreeSet<>(getMany(Waypoint.class, "JourneyFK"));
        }
        return this.waypoints;
    }

    public void setWaypoints(SortedSet<Waypoint> waypoints) {
        this.waypoints = waypoints;
    }

    public SortedSet<Ticket> getTickets() {
        if (this.tickets == null) {
            this.tickets = new TreeSet<>(getMany(Ticket.class, "JourneyFK"));
        }
        return this.tickets;
    }

    public void setTickets(SortedSet<Ticket> tickets) {
        this.tickets = tickets;
    }

    public Waypoint getCurrentWaypoint() {
        return new Select()
                .from(Waypoint.class)
                .where("JourneyFK = ?", getId())
                .orderBy("Created DESC")
                .executeSingle();
    }

    public static Journey findById(long journeyId) {
        return new Select()
                .from(Journey.class)
                .where("JourneyId = ?", journeyId)
                .executeSingle();
    }

    public static List<Journey> findIncomplete() {
        return new Select()
                .from(Journey.class)
                .where("State != ?", TrackingState.COMPLETED)
                .execute();
    }

    public void refresh(JourneySnapshot snapshot) {
        this.journeyId = snapshot.getJourneyId();
        this.appId = snapshot.getAppId();
        this.source = snapshot.getSource();
        this.totalDistance = snapshot.getTotalDistance();
        this.totalDuration = snapshot.getTotalDuration();
        this.created = snapshot.getCreated();
        this.completed = snapshot.getCompleted();
        this.state = snapshot.getState();
        this.active = snapshot.isActive();
        this.lastModified = snapshot.getLastModified();
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
        snapshot.setOwnerId(owner.getCoordinatorId());
        snapshot.setAgentId(agent.getCoordinatorId());
        snapshot.setGuideId(guide.getCoordinatorId());
        snapshot.setOriginId(origin.getPlaceId());
        snapshot.setDestinationId(destination.getPlaceId());
        snapshot.setRouteId(route.getRouteId());
        //
        snapshot.setActive(active);
        snapshot.setLastModified(lastModified);
        return snapshot;
    }

    @Override
    public int compareTo(Object another) {
        Journey other = (Journey)another;
        if (this.equals(other)) return 0;
        return this.created.compareTo(other.created);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Journey journey = (Journey) o;

        return journeyId == journey.journeyId;

    }

    @Override
    public int hashCode() {
        int result = 31 * (int) (journeyId ^ (journeyId >>> 32));
        return result;
    }

    @Override
    public String toString() {
        String s = "Journey{" +
                "journeyId='" + journeyId + '\'' +
                ", appId='" + appId + '\'' +
                ", source='" + source + '\'' +
                ", totalDistance=" + totalDistance +
                ", totalDuration=" + totalDuration +
                ", created=" + created +
                ", completed=" + completed +
                ", state=" + state +
                ", route=" + (route == null ? route : route.getRouteId()) +
                ", owner=" + (owner == null ? owner : owner.getCoordinatorId()) +
                ", agent=" + (agent == null ? agent : agent.getCoordinatorId()) +
                ", guide=" + (guide == null ? guide : guide.getCoordinatorId()) +
                ", origin=" + (origin == null ? origin : origin.getPlaceId()) +
                ", destination=" + (destination == null ? destination : destination.getPlaceId()) +
                ", waypoints=" + waypoints;
        s += ", tickets=";
        if ((tickets == null) || tickets.isEmpty()) {
            s += "[empty]";
        } else {
            for (Ticket o : tickets) {
                s += " ";
                s += o.getTicketId();
            }
        }
        s += "} " + super.toString();
        return s;
    }

    protected Journey(Parcel in) {
        super(in);
        journeyId = in.readLong();
        appId = in.readString();
        source = in.readString();
        totalDistance = in.readFloat();
        totalDuration = in.readLong();
        created = (Timestamp) in.readValue(Timestamp.class.getClassLoader());
        completed = (Timestamp) in.readValue(Timestamp.class.getClassLoader());
        state = (TrackingState) in.readValue(TrackingState.class.getClassLoader());
        route = (Route) in.readValue(Route.class.getClassLoader());
        owner = (Coordinator) in.readValue(Coordinator.class.getClassLoader());
        agent = (Coordinator) in.readValue(Coordinator.class.getClassLoader());
        guide = (Coordinator) in.readValue(Coordinator.class.getClassLoader());
        origin = (Place) in.readValue(Place.class.getClassLoader());
        destination = (Place) in.readValue(Place.class.getClassLoader());
/*        waypoints = (SortedSet) in.readValue(SortedSet.class.getClassLoader());
        tickets = (SortedSet) in.readValue(SortedSet.class.getClassLoader());*/
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(journeyId);
        dest.writeString(appId);
        dest.writeString(source);
        dest.writeFloat(totalDistance);
        dest.writeLong(totalDuration);
        dest.writeValue(created);
        dest.writeValue(completed);
        dest.writeValue(state);
        dest.writeValue(route);
        dest.writeValue(owner);
        dest.writeValue(agent);
        dest.writeValue(guide);
        dest.writeValue(origin);
        dest.writeValue(destination);
/*        dest.writeValue(waypoints);
        dest.writeValue(tickets);*/
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Journey> CREATOR = new Parcelable.Creator<Journey>() {
        @Override
        public Journey createFromParcel(Parcel in) {
            return new Journey(in);
        }

        @Override
        public Journey[] newArray(int size) {
            return new Journey[size];
        }
    };

}

