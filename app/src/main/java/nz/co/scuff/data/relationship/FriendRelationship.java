package nz.co.scuff.data.relationship;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import nz.co.scuff.data.base.Coordinator;

/**
 * Created by Callum on 3/05/2015.
 */
@Table(name="FriendRelationships")
public class FriendRelationship extends Model implements Parcelable {

    @Column(name = "Coordinator1FK", onDelete= Column.ForeignKeyAction.CASCADE)
    private Coordinator coordinator1;
    @Column(name = "Coordinator2FK", onDelete= Column.ForeignKeyAction.CASCADE)
    private Coordinator coordinator2;

    public FriendRelationship() {
    }

    public FriendRelationship(Coordinator coordinator1, Coordinator coordinator2) {
        this.coordinator1 = coordinator1;
        this.coordinator2 = coordinator2;
    }

    public Coordinator getCoordinator1() {
        return coordinator1;
    }

    public void setCoordinator1(Coordinator coordinator1) {
        this.coordinator1 = coordinator1;
    }

    public Coordinator getCoordinator2() {
        return coordinator2;
    }

    public void setCoordinator2(Coordinator coordinator2) {
        this.coordinator2 = coordinator2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        FriendRelationship that = (FriendRelationship) o;

        return ((coordinator1.equals(that.coordinator1) && coordinator2.equals(that.coordinator2)) ||
                (coordinator1.equals(that.coordinator2) && coordinator2.equals(that.coordinator1)));

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + coordinator1.hashCode();
        result = 31 * result + coordinator2.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "FriendRelationship{" +
                "coordinator1=" + coordinator1.getCoordinatorId() +
                ", coordinator2=" + coordinator2.getCoordinatorId() +
                "} " + super.toString();
    }

    protected FriendRelationship(Parcel in) {
        coordinator1 = (Coordinator) in.readValue(Coordinator.class.getClassLoader());
        coordinator2 = (Coordinator) in.readValue(Coordinator.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(coordinator1);
        dest.writeValue(coordinator2);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<FriendRelationship> CREATOR = new Parcelable.Creator<FriendRelationship>() {
        @Override
        public FriendRelationship createFromParcel(Parcel in) {
            return new FriendRelationship(in);
        }

        @Override
        public FriendRelationship[] newArray(int size) {
            return new FriendRelationship[size];
        }
    };

}