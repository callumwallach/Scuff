package nz.co.scuff.data.journey;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Callum on 20/04/2015.
 */
@XmlRootElement
@Entity
public class Journey implements Serializable {

    //private static final long serialVersionUID = 2L;

    public enum TrackingState {
        ACTIVE, PAUSED, COMPLETED
    }

    @Id
    private String id;
    @NotNull
    private String appId;
    @NotNull
    private String schoolId;
    @NotNull
    private String driverId;
    @NotNull
    private String routeId;
    @NotNull
    private String source;
    @NotNull
    @Enumerated(EnumType.STRING)
    private TrackingState state;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name="journeyId", referencedColumnName="id")
    private Set<Waypoint> waypoints;

    public Journey() {
        waypoints = new HashSet<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public TrackingState getState() {
        return state;
    }

    public void setState(TrackingState state) {
        this.state = state;
    }

    public void addWaypoint(Waypoint waypoint) {
        waypoints.add(waypoint);
    }

    public Set<Waypoint> getWaypoints() {
        return waypoints;
    }

    public void setWaypoints(Set<Waypoint> waypoints) {
        this.waypoints = waypoints;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Journey journey = (Journey) o;

        if (!id.equals(journey.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
