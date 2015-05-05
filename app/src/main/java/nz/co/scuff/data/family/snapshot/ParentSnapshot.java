package nz.co.scuff.data.family.snapshot;

import com.google.gson.annotations.Expose;

import java.util.SortedSet;
import java.util.TreeSet;

import nz.co.scuff.data.school.snapshot.RouteSnapshot;
import nz.co.scuff.data.school.snapshot.SchoolSnapshot;

/**
 * Created by Callum on 4/05/2015.
 */
public class ParentSnapshot extends PersonSnapshot {

    @Expose
    private String email;
    @Expose
    private String phone;

    @Expose
    private SortedSet<SchoolSnapshot> schools;
    @Expose
    private SortedSet<ChildSnapshot> children;
    @Expose
    private SortedSet<RouteSnapshot> routes;

    public ParentSnapshot() {
        schools = new TreeSet<>();
        children = new TreeSet<>();
        routes = new TreeSet<>();
    }

    public long getParentId() {
        return super.getPersonId();
    }

    public void setParentId(long parentId) {
        super.setPersonId(parentId);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public SortedSet<SchoolSnapshot> getSchools() {
        return schools;
    }

    public void setSchools(SortedSet<SchoolSnapshot> schools) {
        this.schools = schools;
    }

    public SortedSet<ChildSnapshot> getChildren() {
        return children;
    }

    public void setChildren(SortedSet<ChildSnapshot> children) {
        this.children = children;
    }

    public SortedSet<RouteSnapshot> getRoutes() {
        return routes;
    }

    public void setRoutes(SortedSet<RouteSnapshot> route) {
        this.routes = route;
    }

    @Override
    public String toString() {
        return "ParentSnapshot{" +
                "email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", schools=" + schools +
                ", children=" + children +
                ", route=" + routes +
                "} " + super.toString();
    }
}
