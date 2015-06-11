package nz.co.scuff.data.institution.snapshot;

import com.google.gson.annotations.Expose;

import nz.co.scuff.data.base.snapshot.ModifiableSnapshot;

/**
 * Created by Callum on 17/03/2015.
 */
public class RouteSnapshot extends ModifiableSnapshot {

    @Expose
    private long routeId;
    @Expose
    private String name;
    @Expose
    private String routeMap;
    @Expose
    private long originId;
    @Expose
    private long destinationId;
    @Expose
    private long ownerId;

    public RouteSnapshot() {
        super();
    }

    public long getRouteId() {
        return routeId;
    }

    public void setRouteId(long routeId) {
        this.routeId = routeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRouteMap() {
        return routeMap;
    }

    public void setRouteMap(String routeMap) {
        this.routeMap = routeMap;
    }

    public long getOriginId() {
        return originId;
    }

    public void setOriginId(long originId) {
        this.originId = originId;
    }

    public long getDestinationId() {
        return destinationId;
    }

    public void setDestinationId(long destinationId) {
        this.destinationId = destinationId;
    }

    public long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RouteSnapshot that = (RouteSnapshot) o;

        return routeId == that.routeId;

    }

    @Override
    public int hashCode() {
        return (int) (routeId ^ (routeId >>> 32));
    }

    @Override
    public String toString() {
        return "RouteSnapshot{" +
                "routeId=" + routeId +
                ", name='" + name + '\'' +
                ", routeMap='" + routeMap + '\'' +
                ", originId=" + originId +
                ", destinationId=" + destinationId +
                ", ownerId=" + ownerId +
                "} " + super.toString();
    }
}