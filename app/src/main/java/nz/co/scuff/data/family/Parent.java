package nz.co.scuff.data.family;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.google.gson.annotations.Expose;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;

import nz.co.scuff.data.school.ParentRoute;
import nz.co.scuff.data.school.ParentSchool;
import nz.co.scuff.data.school.Route;
import nz.co.scuff.data.school.School;

/**
 * Created by Callum on 17/03/2015.
 */
@Table(name="Parents")
public class Parent extends Person {

    @Expose
    @Column(name="ParentId")
    private long parentId;

    @Expose
    @Column(name="Email")
    private String email;
    @Expose
    @Column(name="Phone")
    private String phone;

    // many to many
    @Expose
    private List<ParentSchool> parentSchools;
    @Expose
    private List<ChildParent> childParents;
    @Expose
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

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
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

    // TODO
    public SortedSet<School> getSchools() {
        return new TreeSet<>();
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

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer(super.toString());
        sb.append("Parent{");
        sb.append("parentId=").append(parentId);
        sb.append(", email='").append(email).append('\'');
        sb.append(", phone='").append(phone).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Parent driver = (Parent) o;

        return parentId == driver.parentId;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (int) (parentId ^ (parentId >>> 32));
        return result;
    }

}