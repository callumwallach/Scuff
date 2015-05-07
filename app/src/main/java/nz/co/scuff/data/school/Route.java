package nz.co.scuff.data.school;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.io.Serializable;

import nz.co.scuff.data.school.snapshot.RouteSnapshot;

/**
 * Created by Callum on 17/03/2015.
 */
@Table(name="Routes")
public class Route extends Model implements Comparable, Serializable {

    @Column(name="RouteId")
    private long routeId;
    @Column(name="Name")
    private String name;
    @Column(name="RouteMap")
    private String routeMap;

    // for use with ActiveAndroid. Not serialised
    @Column(name="SchoolFK", onDelete = Column.ForeignKeyAction.CASCADE)
    private School school;

    public Route() {}

    public Route(String name, String routeMap, School school) {
        this.name = name;
        this.routeMap = routeMap;
        this.school = school;
    }

    public Route(RouteSnapshot snapshot) {
        this.routeId = snapshot.getRouteId();
        this.name = snapshot.getName();
        this.routeMap = snapshot.getRouteMap();
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

    public School getSchool() {
        return school;
    }

    public void setSchool(School school) {
        this.school = school;
    }

    public RouteSnapshot toSnapshot() {
        RouteSnapshot snapshot = new RouteSnapshot();
        snapshot.setRouteId(routeId);
        snapshot.setName(name);
        snapshot.setRouteMap(routeMap);
        return snapshot;
    }

    public static Route findByRouteId(long routeId) {
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

        if (routeId != route.routeId) return false;
        if (name != null ? !name.equals(route.name) : route.name != null) return false;
        return !(routeMap != null ? !routeMap.equals(route.routeMap) : route.routeMap != null);

    }

    @Override
    public int hashCode() {
        int result = (int) (routeId ^ (routeId >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (routeMap != null ? routeMap.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Route{" +
                "routeId=" + routeId +
                ", name='" + name + '\'' +
                ", routeMap='" + routeMap + '\'' +
                '}';
    }

    @Override
    public int compareTo(Object another) {
        Route other = (Route)another;
        return this.name.compareTo(other.name);
    }
}