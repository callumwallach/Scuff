package nz.co.scuff.data.school;

import android.os.Parcel;
import android.os.Parcelable;

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
public class Route extends Model implements Comparable, Serializable, Parcelable {

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
        snapshot.setSchoolId(school.getSchoolId());
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
        if (!super.equals(o)) return false;

        Route route = (Route) o;

        return routeId == route.routeId;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (int) (routeId ^ (routeId >>> 32));
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

    protected Route(Parcel in) {
        routeId = in.readLong();
        name = in.readString();
        routeMap = in.readString();
        school = (School) in.readValue(School.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(routeId);
        dest.writeString(name);
        dest.writeString(routeMap);
        dest.writeValue(school);
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