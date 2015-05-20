package nz.co.scuff.data.relationship;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.io.Serializable;

import nz.co.scuff.data.family.Passenger;
import nz.co.scuff.data.journey.Journey;

/**
 * Created by Callum on 3/05/2015.
 */
@Table(name="JourneyPassengers")
public class JourneyPassenger extends Model implements Serializable, Parcelable {

    @Column(name = "JourneyFK", onDelete= Column.ForeignKeyAction.CASCADE)
    private Journey journey;
    @Column(name = "PassengerFK", onDelete= Column.ForeignKeyAction.CASCADE)
    private Passenger passenger;

    public JourneyPassenger() {
        super();
    }

    public JourneyPassenger(Journey journey, Passenger passenger) {
        this.journey = journey;
        this.passenger = passenger;
    }

    public Journey getJourney() {
        return journey;
    }

    public void setJourney(Journey journey) {
        this.journey = journey;
    }

    public Passenger getPassenger() {
        return passenger;
    }

    public void setPassenger(Passenger passenger) {
        this.passenger = passenger;
    }

    protected JourneyPassenger(Parcel in) {
        journey = (Journey) in.readValue(Journey.class.getClassLoader());
        passenger = (Passenger) in.readValue(Passenger.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(journey);
        dest.writeValue(passenger);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<JourneyPassenger> CREATOR = new Parcelable.Creator<JourneyPassenger>() {
        @Override
        public JourneyPassenger createFromParcel(Parcel in) {
            return new JourneyPassenger(in);
        }

        @Override
        public JourneyPassenger[] newArray(int size) {
            return new JourneyPassenger[size];
        }
    };
}