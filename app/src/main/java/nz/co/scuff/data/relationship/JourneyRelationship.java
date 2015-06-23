package nz.co.scuff.data.relationship;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import nz.co.scuff.data.base.Coordinator;
import nz.co.scuff.data.journey.Journey;

/**
 * Created by Callum on 3/05/2015.
 */
@Table(name="JourneyRelationships")
public class JourneyRelationship extends Model {

    @Column(name = "CoordinatorFK", onDelete= Column.ForeignKeyAction.CASCADE)
    private Coordinator coordinator;
    @Column(name = "JourneyFK", onDelete= Column.ForeignKeyAction.CASCADE)
    private Journey journey;

    public JourneyRelationship() {
        super();
    }

    public JourneyRelationship(Coordinator coordinator, Journey journey) {
        this.coordinator = coordinator;
        this.journey = journey;
    }

    public Coordinator getCoordinator() {
        return coordinator;
    }

    public void setCoordinator(Coordinator coordinator) {
        this.coordinator = coordinator;
    }

    public Journey getJourney() {
        return journey;
    }

    public void setJourney(Journey journey) {
        this.journey = journey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JourneyRelationship that = (JourneyRelationship) o;

        if (!coordinator.equals(that.coordinator)) return false;
        return journey.equals(that.journey);

    }

    @Override
    public int hashCode() {
        int result = 31 * coordinator.hashCode();
        result = 31 * result + journey.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "JourneyRelationship{" +
                "coordinator=" + coordinator.getCoordinatorId() +
                ", journey=" + journey.getJourneyId() +
                "} " + super.toString();
    }
/*
    protected JourneyRelationship(Parcel in) {
        coordinator = (Coordinator) in.readValue(Coordinator.class.getClassLoader());
        journey = (Journey) in.readValue(Journey.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(coordinator);
        dest.writeValue(journey);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<JourneyRelationship> CREATOR = new Parcelable.Creator<JourneyRelationship>() {
        @Override
        public JourneyRelationship createFromParcel(Parcel in) {
            return new JourneyRelationship(in);
        }

        @Override
        public JourneyRelationship[] newArray(int size) {
            return new JourneyRelationship[size];
        }
    };*/
}