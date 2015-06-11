package nz.co.scuff.data.relationship;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import nz.co.scuff.data.base.Coordinator;
import nz.co.scuff.data.place.Place;

/**
 * Created by Callum on 3/05/2015.
 */
@Table(name="PlaceRelationships")
public class PlaceRelationship extends Model implements Parcelable {

    @Column(name = "CoordinatorFK", onDelete= Column.ForeignKeyAction.CASCADE)
    private Coordinator coordinator;
    @Column(name = "PlaceFK", onDelete= Column.ForeignKeyAction.CASCADE)
    private Place place;

    public PlaceRelationship() {
        super();
    }

    public PlaceRelationship(Coordinator coordinator, Place place) {
        this.coordinator = coordinator;
        this.place = place;
    }

    public Coordinator getCoordinator() {
        return coordinator;
    }

    public void setCoordinator(Coordinator coordinator) {
        this.coordinator = coordinator;
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }
    protected PlaceRelationship(Parcel in) {
        coordinator = (Coordinator) in.readValue(Coordinator.class.getClassLoader());
        place = (Place) in.readValue(Place.class.getClassLoader());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        PlaceRelationship that = (PlaceRelationship) o;

        if (!coordinator.equals(that.coordinator)) return false;
        return place.equals(that.place);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + coordinator.hashCode();
        result = 31 * result + place.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "PlaceRelationship{" +
                "coordinator=" + coordinator.getCoordinatorId() +
                ", place=" + place.getPlaceId() +
                "} " + super.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(coordinator);
        dest.writeValue(place);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<PlaceRelationship> CREATOR = new Parcelable.Creator<PlaceRelationship>() {
        @Override
        public PlaceRelationship createFromParcel(Parcel in) {
            return new PlaceRelationship(in);
        }

        @Override
        public PlaceRelationship[] newArray(int size) {
            return new PlaceRelationship[size];
        }
    };
}