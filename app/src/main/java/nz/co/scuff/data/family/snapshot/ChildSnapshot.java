package nz.co.scuff.data.family.snapshot;

import com.google.gson.annotations.Expose;

import nz.co.scuff.data.school.snapshot.SchoolSnapshot;

import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by Callum on 4/05/2015.
 */
public class ChildSnapshot extends PersonSnapshot {

    @Expose
    SortedSet<SchoolSnapshot> schools;
    @Expose
    SortedSet<ParentSnapshot> parents;

    public ChildSnapshot() {
        schools = new TreeSet<>();
        parents = new TreeSet<>();
    }

    public long getChildId() {
        return super.getPersonId();
    }

    public void setChildId(long childId) {
        super.setPersonId(childId);
    }

    public SortedSet<SchoolSnapshot> getSchools() {
        return schools;
    }

    public void setSchools(SortedSet<SchoolSnapshot> schools) {
        this.schools = schools;
    }

    public SortedSet<ParentSnapshot> getParents() {
        return parents;
    }

    public void setParents(SortedSet<ParentSnapshot> parents) {
        this.parents = parents;
    }

    @Override
    public String toString() {
        return "ChildSnapshot{" +
                "schools=" + schools +
                ", parents=" + parents +
                "} " + super.toString();
    }
}
