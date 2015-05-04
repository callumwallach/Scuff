package nz.co.scuff.data.school;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Callum on 17/03/2015.
 */
@Table(name="Routes")
public class Route extends Model implements Comparable, Serializable {

    @Expose
    @Column(name="RouteId")
    private long routeId;
    @Expose
    @Column(name="Name")
    private String name;
    @Expose
    @Column(name="RouteMap")
    private String routeMap;

    private List<ParentRoute> parentRoutes;

    // for use with ActiveAndroid. Not serialised
    @Column(name="SchoolFK", onDelete = Column.ForeignKeyAction.CASCADE)
    private School school;

    public Route(String name) {
        this.name = name;
        this.routeMap = "Currently not available";
    }

    public Route(String name, String routeMap) {
        this.name = name;
        this.routeMap = routeMap;
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

    public void addParentRoute(ParentRoute parentRoute) {
        if (this.parentRoutes == null) {
            this.parentRoutes = new ArrayList<>();
        }
        this.parentRoutes.add(parentRoute);
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
        final StringBuffer sb = new StringBuffer("Route{");
        sb.append("routeId=").append(routeId);
        sb.append(", name='").append(name).append('\'');
        sb.append(", routeMap='").append(routeMap).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public int compareTo(Object another) {
        Route other = (Route)another;
        return this.name.compareTo(other.name);
    }
}