package nz.co.scuff.data.base.snapshot;

import com.google.gson.annotations.Expose;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Callum on 3/06/2015.
 */
public abstract class CoordinatorSnapshot extends ModifiableSnapshot {

    @Expose
    private long coordinatorId;
    @Expose
    private Timestamp lastRefresh;

    @Expose
    private Set<Long> placeIds;
    @Expose
    private Set<Long> routeIds;
    @Expose
    private Set<Long> friendIds;
    @Expose
    private Set<Long> pastJourneyIds;
    @Expose
    private Set<Long> currentJourneyIds;

    public CoordinatorSnapshot() {
        super();
        placeIds = new HashSet<>();
        routeIds = new HashSet<>();
        friendIds = new HashSet<>();
        pastJourneyIds = new HashSet<>();
        currentJourneyIds = new HashSet<>();
    }

    public long getCoordinatorId() {
        return coordinatorId;
    }

    public void setCoordinatorId(long coordinatorId) {
        this.coordinatorId = coordinatorId;
    }

    public Timestamp getLastRefresh() {
        return lastRefresh;
    }

    public void setLastRefresh(Timestamp lastRefresh) {
        this.lastRefresh = lastRefresh;
    }

    public Set<Long> getPlaceIds() {
        return placeIds;
    }

    public void setPlaceIds(Set<Long> placeIds) {
        this.placeIds = placeIds;
    }

    public Set<Long> getFriendIds() {
        return friendIds;
    }

    public void setFriendIds(Set<Long> friendIds) {
        this.friendIds = friendIds;
    }

    public Set<Long> getPastJourneyIds() {
        return pastJourneyIds;
    }

    public void setPastJourneyIds(Set<Long> pastJourneyIds) {
        this.pastJourneyIds = pastJourneyIds;
    }

    public Set<Long> getCurrentJourneyIds() {
        return currentJourneyIds;
    }

    public void setCurrentJourneyIds(Set<Long> currentJourneyIds) {
        this.currentJourneyIds = currentJourneyIds;
    }

    public Set<Long> getRouteIds() {
        return routeIds;
    }

    public void setRouteIds(Set<Long> routeIds) {
        this.routeIds = routeIds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CoordinatorSnapshot that = (CoordinatorSnapshot) o;

        return coordinatorId == that.coordinatorId;

    }

    @Override
    public int hashCode() {
        return (int) (coordinatorId ^ (coordinatorId >>> 32));
    }

    @Override
    public String toString() {
        return "CoordinatorSnapshot{" +
                "coordinatorId=" + coordinatorId +
                ", lastRefresh=" + lastRefresh +
                ", placeIds=" + placeIds +
                ", routeIds=" + routeIds +
                ", friendIds=" + friendIds +
                ", pastJourneyIds=" + pastJourneyIds +
                ", currentJourneyIds=" + currentJourneyIds +
                "} " + super.toString();
    }
}
