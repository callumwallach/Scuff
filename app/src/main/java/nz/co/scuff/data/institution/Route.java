package nz.co.scuff.data.institution;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import nz.co.scuff.data.base.Coordinator;
import nz.co.scuff.data.base.ModifiableEntity;
import nz.co.scuff.data.base.Snapshotable;
import nz.co.scuff.data.institution.snapshot.RouteSnapshot;
import nz.co.scuff.data.place.Place;

/**
 * Created by Callum on 17/03/2015.
 */
@Table(name="Routes")
public class Route extends ModifiableEntity implements Snapshotable, Comparable {

    @Column(name="RouteId")
    private long routeId;
    @Column(name="Name")
    private String name;
    @Column(name="RouteMap")
    private String routeMap;

    @Column(name="OriginFK")
    private Place origin;
    @Column(name="DestinationFK")
    private Place destination;
    @Column(name="OwnerFK")
    private Coordinator owner;

    public Route() {
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

    public Coordinator getOwner() {
        return owner;
    }

    public void setOwner(Coordinator owner) {
        this.owner = owner;
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

    public void refresh(RouteSnapshot snapshot) {
        this.routeId = snapshot.getRouteId();
        this.name = snapshot.getName();
        this.routeMap = snapshot.getRouteMap();
        this.active = snapshot.isActive();
        this.lastModified = snapshot.getLastModified();
    }

    public RouteSnapshot toSnapshot() {
        RouteSnapshot snapshot = new RouteSnapshot();
        snapshot.setRouteId(routeId);
        snapshot.setName(name);
        snapshot.setRouteMap(routeMap);
        snapshot.setOriginId(origin.getPlaceId());
        snapshot.setDestinationId(destination.getPlaceId());
        snapshot.setOwnerId(owner.getCoordinatorId());
        snapshot.setActive(active);
        snapshot.setLastModified(lastModified);
        return snapshot;
    }

    public static Route findById(long routeId) {
        return new Select()
                .from(Route.class)
                .where("RouteId = ?", routeId)
                .executeSingle();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Route route = (Route) o;

        return routeId == route.routeId;
    }

    @Override
    public int hashCode() {
        return (int) (routeId ^ (routeId >>> 32));
    }

    @Override
    public String toString() {
        return "Route{" +
                "routeId=" + routeId +
                ", name='" + name + '\'' +
                ", routeMap='" + routeMap + '\'' +
                ", origin=" + (origin == null ? origin : origin.getPlaceId()) +
                ", destination=" + (destination == null ? destination : destination.getPlaceId()) +
                ", owner=" + (owner == null ? owner : owner.getCoordinatorId()) +
                "} " + super.toString();
    }

    @Override
    public int compareTo(Object another) {
        Route other = (Route)another;
        return other.name == null ? 1 : this.name.compareTo(other.name);

    }

    protected Route(Parcel in) {
        super(in);
        routeId = in.readLong();
        name = in.readString();
        routeMap = in.readString();
        origin = (Place) in.readValue(Place.class.getClassLoader());
        destination = (Place) in.readValue(Place.class.getClassLoader());
        owner = (Coordinator) in.readValue(Coordinator.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(routeId);
        dest.writeString(name);
        dest.writeString(routeMap);
        dest.writeValue(origin);
        dest.writeValue(destination);
        dest.writeValue(owner);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Route> CREATOR = new Parcelable.Creator<Route>() {
        @Override
        public Route createFromParcel(Parcel in) {
            return new Route(in);
        }

        @Override
        public Route[] newArray(int size) {
            return new Route[size];
        }
    };
}