package nz.co.scuff.data.family;

import com.activeandroid.annotation.Table;

import java.util.ArrayList;
import java.util.List;

import nz.co.scuff.data.family.snapshot.ChildSnapshot;
import nz.co.scuff.data.school.ChildSchool;

/**
 * Created by Callum on 17/03/2015.
 */
@Table(name="Children")
public class Child extends Person {

    // many to many
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

    public Child(ChildSnapshot snapshot) {
        this(snapshot.getFirstName(), snapshot.getLastName(), snapshot.getGender(), snapshot.getPicture());
        this.setChildId(snapshot.getChildId());
        this.middleName = snapshot.getMiddleName();
    }

    public long getChildId() {
        return super.getPersonId();
    }

    public void setChildId(long childId) {
        super.setPersonId(childId);
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
    public String toString() {
        final StringBuffer sb = new StringBuffer(super.toString());
        sb.append("Child{");
        sb.append('}');
        return sb.toString();
    }

}