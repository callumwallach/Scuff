package nz.co.scuff.data.relationship;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import nz.co.scuff.data.base.Coordinator;
import nz.co.scuff.data.family.Child;

/**
 * Created by Callum on 3/05/2015.
 */
@Table(name="ParentalRelationships")
public class ParentalRelationship extends Model {

    @Column(name = "AdultFK", onDelete= Column.ForeignKeyAction.CASCADE)
    public Coordinator adult;
    @Column(name = "ChildFK", onDelete= Column.ForeignKeyAction.CASCADE)
    public Child child;

    public ParentalRelationship() {
        super();
    }

    public ParentalRelationship(Coordinator adult, Child child) {
        this.adult = adult;
        this.child = child;
    }

    public Child getChild() {
        return child;
    }

    public void setChild(Child child) {
        this.child = child;
    }

    public Coordinator getAdult() {
        return adult;
    }

    public void setAdult(Coordinator adult) {
        this.adult = adult;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ParentalRelationship that = (ParentalRelationship) o;

        if (!adult.equals(that.adult)) return false;
        return child.equals(that.child);

    }

    @Override
    public int hashCode() {
        int result = 31 * adult.hashCode();
        result = 31 * result + child.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ParentalRelationship{" +
                "adult=" + adult.getCoordinatorId() +
                ", child=" + child.getChildId() +
                "} " + super.toString();
    }
/*
    protected ParentalRelationship(Parcel in) {
        adult = (Coordinator) in.readValue(Coordinator.class.getClassLoader());
        child = (Child) in.readValue(Child.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(adult);
        dest.writeValue(child);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<ParentalRelationship> CREATOR = new Parcelable.Creator<ParentalRelationship>() {
        @Override
        public ParentalRelationship createFromParcel(Parcel in) {
            return new ParentalRelationship(in);
        }

        @Override
        public ParentalRelationship[] newArray(int size) {
            return new ParentalRelationship[size];
        }
    };*/
}