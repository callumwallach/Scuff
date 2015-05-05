package nz.co.scuff.data.school;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;

import nz.co.scuff.data.family.Child;
import nz.co.scuff.data.school.snapshot.SchoolSnapshot;

/**
 * Created by Callum on 17/03/2015.
 */
@Table(name="Schools")
public class School extends Model implements Comparable, Serializable {

    @Column(name="SchoolId")
    private long schoolId;
    @Column(name="Name")
    private String name;
    @Column(name="Latitude")
    private double latitude;
    @Column(name="Longitude")
    private double longitude;
    @Column(name="Altitude")
    private double altitude;

    // one to many
    private SortedSet<Route> routes;

    // many to many
    private List<ChildSchool> childSchools;
    private List<ParentSchool> parentSchools;

/*    // one to one
    @Expose
    @Column(name="Schedule", onDelete = Column.ForeignKeyAction.CASCADE)
    private Schedule schedule;*/

    public School() {
        super();
    }

    public School(String name, double latitude, double longitude, double altitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;

        this.routes = new TreeSet<>();
        this.childSchools = new ArrayList<>();
        this.parentSchools = new ArrayList<>();

        //this.schedule = new Schedule();
    }

    public School(SchoolSnapshot snapshot) {
        this(snapshot.getName(), snapshot.getLatitude(), snapshot.getLongitude(), snapshot.getAltitude());
        this.schoolId = snapshot.getSchoolId();
    }

    public long getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(long schoolId) {
        this.schoolId = schoolId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    // active android relationship assist
    public SortedSet<Route> getRoutes() {
        return new TreeSet<>(getMany(Route.class, "SchoolFK"));
    }
    public void setRoutes(SortedSet<Route> routes) {
        this.routes = routes;
    }

    public void addRoute(Route route) {
        if (this.routes == null) {
            this.routes = new TreeSet<>();
        }
        this.routes.add(route);
    }

    public List<ChildSchool> getChildSchools() {
        return childSchools;
    }

    public void setChildSchools(List<ChildSchool> childSchools) {
        this.childSchools = childSchools;
    }

    public void addChildSchool(ChildSchool childSchool) {
        if (this.childSchools == null) {
            this.childSchools = new ArrayList<>();
        }
        this.childSchools.add(childSchool);
    }

    public List<ParentSchool> getParentSchools() {
        return parentSchools;
    }

    public void setParentSchools(List<ParentSchool> parentSchools) {
        this.parentSchools = parentSchools;
    }

    public void addParentSchool(ParentSchool parentSchool) {
        if (this.parentSchools == null) {
            this.parentSchools = new ArrayList<>();
        }
        this.parentSchools.add(parentSchool);
    }

    // TODO
    public List<Child> getChildrenForRoute(Route route) {
        return new ArrayList<>();
    }

/*    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }*/

    public static School findBySchoolId(long schoolId) {
        return new Select()
                .from(School.class)
                .where("SchoolId = ?", schoolId)
                .executeSingle();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        School school = (School) o;

        return schoolId == school.schoolId;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (int) (schoolId ^ (schoolId >>> 32));
        return result;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("School{");
        sb.append("schoolId=").append(schoolId);
        sb.append(", name='").append(name).append('\'');
        sb.append(", latitude=").append(latitude);
        sb.append(", longitude=").append(longitude);
        sb.append(", altitude=").append(altitude);
        //sb.append(", schedule=").append(schedule);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public int compareTo(Object another) {
        School other = (School)another;
        return this.name.compareTo(other.name);
    }
}