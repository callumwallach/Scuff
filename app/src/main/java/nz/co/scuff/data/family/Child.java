package nz.co.scuff.data.family;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

import nz.co.scuff.data.school.ChildSchool;

/**
 * Created by Callum on 17/03/2015.
 */
@Table(name="Children")
public class Child extends Person {

    @Expose
    @Column(name="ChildId")
    private long childId;

    // many to many
    @Expose
    private List<ChildSchool> childSchools;
    private List<ChildParent> childParents;

    public Child() { }

    public Child(String firstName, String lastName, Gender gender) {
        this(firstName, lastName, gender, null);
    }

    public Child(String firstName, String lastName, Gender gender, String picture) {
        super(firstName, lastName, gender, picture);
        childSchools = new ArrayList<>();
    }

    public long getChildId() {
        return childId;
    }

    public void setChildId(long childId) {
        this.childId = childId;
    }

    public List<ChildSchool> getChildSchools() {
        return childSchools;
    }

    public void setChildSchools(List<ChildSchool> childSchools) {
        this.childSchools = childSchools;
    }

    public void addChildSchool(ChildSchool childSchools) {
        if (this.childSchools == null) {
            this.childSchools = new ArrayList<>();
        }
        this.childSchools.add(childSchools);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Child child = (Child) o;

        return childId == child.childId;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (int) (childId ^ (childId >>> 32));
        return result;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer(super.toString());
        sb.append("Child{");
        sb.append("childId=").append(childId);
        sb.append('}');
        return sb.toString();
    }

}