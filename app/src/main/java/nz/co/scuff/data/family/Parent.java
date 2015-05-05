package nz.co.scuff.data.family;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;

import nz.co.scuff.data.family.snapshot.ParentSnapshot;
import nz.co.scuff.data.school.ParentRoute;
import nz.co.scuff.data.school.ParentSchool;
import nz.co.scuff.data.school.Route;
import nz.co.scuff.data.school.School;

/**
 * Created by Callum on 17/03/2015.
 */
@Table(name="Parents")
public class Parent extends Person {

    @Column(name="Email")
    private String email;
    @Column(name="Phone")
    private String phone;

    // many to many
    private List<ParentSchool> parentSchools;
    private List<ChildParent> childParents;
    private List<ParentRoute> parentRoutes;

    // generated
    private Map<School, Set<Route>> schoolRoutes;

    public Parent() { }

    public Parent(String firstName, String lastName, Gender gender) {
        this(firstName, lastName, gender, null);
    }

    public Parent(String firstName, String lastName, Gender gender, String picture) {
        super(firstName, lastName, gender, picture);
        parentSchools = new ArrayList<>();
        parentRoutes = new ArrayList<>();
    }

    public Parent(ParentSnapshot snapshot) {
        this(snapshot.getFirstName(), snapshot.getLastName(), snapshot.getGender(), snapshot.getPicture());
        this.setParentId(snapshot.getParentId());
        this.middleName = snapshot.getMiddleName();
        this.email = snapshot.getEmail();
        this.phone = snapshot.getPhone();
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

    public List<ChildParent> getChildParents() {
        return childParents;
    }

    public void setChildParents(List<ChildParent> childParents) {
        this.childParents = childParents;
    }

    public void addChildParent(ChildParent childParent) {
        if (this.childParents == null) {
            this.childParents = new ArrayList<>();
        }
        this.childParents.add(childParent);
    }

    public List<ParentRoute> getParentRoutes() {
        return parentRoutes;
    }

    public void setParentRoutes(List<ParentRoute> parentRoutes) {
        this.parentRoutes = parentRoutes;
    }

    public void addParentRoute(ParentRoute parentRoute) {
        if (this.parentRoutes == null) {
            this.parentRoutes = new ArrayList<>();
        }
        this.parentRoutes.add(parentRoute);
    }

    public SortedSet<School> getSchools() {
        List<School> schools = new Select()
                .from(School.class)
                .innerJoin(ParentSchool.class).on("schools.id = parentschools.schoolfk")
                .where("parentschools.parentfk = ?", getId())
                .execute();
        return new TreeSet<>(schools);
    }

    // TODO
    public SortedSet<School> getSchoolsIDriveFor() {
        return new TreeSet<>();
    }

    // TODO
    public SortedSet<Child> getChildren() {
        return new TreeSet<>();
    }

    // TODO
    public SortedSet<School> getSchoolsMyChildrenGoTo() {
        return new TreeSet<>();
    }


    // TODO complete
    public SortedSet<Route> getRoutesForSchool(School school) {
        if (this.schoolRoutes == null) {
            this.schoolRoutes = new HashMap<>();
            // http://stackoverflow.com/questions/26207948/activeandroid-many-to-many-relationship
            new Select()
                    .from(Parent.class)
                    .innerJoin(ParentSchool.class).on("parent.id = studentcourses.id")
                    .where("driverschool.school = ?", school.getSchoolId())
                    .execute();
            /*if (this.schools != null) {
                for (School s : this.schools) {
                    this.schoolRoutes.put(s, s.getParentRoutes());
                }
            }*/
        }
        return new TreeSet<>(this.schoolRoutes.get(school));
    }

    // TODO complete
    public Route getScheduledRoute() {
        List<Route> routes = new Select()
                .from(Route.class)
                .innerJoin(ParentRoute.class).on("routes.id = parentroutes.routefk")
                .where("parentroutes.parentfk = ?", getId())
                .execute();
        return routes.iterator().hasNext() ? routes.iterator().next() : null;
    }

    public static Parent findParentByPK(long pk) {
        return new Select()
                .from(Parent.class)
                .where("PersonId = ?", pk)
                .executeSingle();
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer(super.toString());
        sb.append("Parent{");
        sb.append(", email='").append(email).append('\'');
        sb.append(", phone='").append(phone).append('\'');
        sb.append('}');
        return sb.toString();
    }

}