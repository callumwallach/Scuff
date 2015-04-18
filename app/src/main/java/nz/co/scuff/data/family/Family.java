package nz.co.scuff.data.family;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashSet;

import nz.co.scuff.data.school.School;

/**
 * Created by Callum on 17/03/2015.
 */
public class Family implements Parcelable {

    public Family(String name) {
        this.id = 1;
        this.name = name;
        this.schools = new HashSet<>();
        this.children = new HashSet<>();
        this.parents = new HashSet<>();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean addSchool(School school) {
        return this.schools.add(school);
    }

    public boolean removeSchool(School school) {
        return this.schools.remove(school);
    }

    public HashSet<School> getSchools() {
        return schools;
    }

    public void setSchools(HashSet<School> schools) {
        this.schools = schools;
    }

    public void addParent(Parent parent) {
        this.parents.add(parent);
    }

    public boolean removeParent(Parent parent) {
        return this.parents.remove(parent);
    }

    public HashSet<Parent> getParents() {
        return parents;
    }

    public void setParents(HashSet<Parent> parents) {
        this.parents = parents;
    }

    public boolean addChild(Child child) {
        return this.children.add(child);
    }

    public boolean removeChild(Child child) {
        return this.children.remove(child);
    }

    public HashSet<Child> getChildren() {
        return children;
    }

    public void setChildren(HashSet<Child> children) {
        this.children = children;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private long id;
    private String name;
    private HashSet<School> schools;
    private HashSet<Parent> parents;
    private HashSet<Child> children;


    protected Family(Parcel in) {
        id = in.readLong();
        name = in.readString();
        schools = (HashSet) in.readValue(HashSet.class.getClassLoader());
        parents = (HashSet) in.readValue(HashSet.class.getClassLoader());
        children = (HashSet) in.readValue(HashSet.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeValue(schools);
        dest.writeValue(parents);
        dest.writeValue(children);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Family> CREATOR = new Parcelable.Creator<Family>() {
        @Override
        public Family createFromParcel(Parcel in) {
            return new Family(in);
        }

        @Override
        public Family[] newArray(int size) {
            return new Family[size];
        }
    };
}