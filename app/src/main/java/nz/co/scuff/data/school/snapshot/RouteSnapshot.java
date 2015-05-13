package nz.co.scuff.data.school.snapshot;

import com.google.gson.annotations.Expose;

/**
 * Created by Callum on 17/03/2015.
 */
public class RouteSnapshot implements Comparable {

    @Expose
    private long routeId;
    @Expose
    private String name;
    @Expose
    private String routeMap;
    @Expose
    private long schoolId;

    public RouteSnapshot() {}

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

    public long getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(long schoolId) {
        this.schoolId = schoolId;
    }

    @Override
    public int compareTo(Object another) {
        RouteSnapshot other = (RouteSnapshot)another;
        return this.name.compareTo(other.name);
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
        final StringBuffer sb = new StringBuffer("RouteSnapshot{");
        sb.append("routeId=").append(routeId);
        sb.append(", name='").append(name).append('\'');
        sb.append(", routeMap='").append(routeMap).append('\'');
        sb.append(", schoolId=").append(schoolId);
        sb.append('}');
        return sb.toString();
    }
}