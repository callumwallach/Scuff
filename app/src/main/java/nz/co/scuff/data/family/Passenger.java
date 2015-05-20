package nz.co.scuff.data.family;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import nz.co.scuff.data.family.snapshot.PassengerSnapshot;
import nz.co.scuff.data.journey.Ticket;
import nz.co.scuff.data.relationship.DriverPassenger;
import nz.co.scuff.data.relationship.PassengerRoute;
import nz.co.scuff.data.relationship.PassengerSchool;

/**
 * Created by Callum on 17/03/2015.
 */
@Table(name="Passengers")
public class Passenger extends Person {

    // many to many
    private List<PassengerSchool> passengerSchools;
    private List<DriverPassenger> driverPassengers;
    private List<PassengerRoute> passengerRoutes;

    private SortedSet<Ticket> tickets;

    public Passenger() { }

    public Passenger(String firstName, String lastName, Gender gender) {
        this(firstName, lastName, gender, null);
    }

    public Passenger(String firstName, String lastName, Gender gender, String picture) {
        super(firstName, lastName, gender, picture);
        passengerSchools = new ArrayList<>();
        driverPassengers = new ArrayList<>();
        passengerRoutes = new ArrayList<>();
        tickets = new TreeSet<>();
    }

    public Passenger(PassengerSnapshot snapshot) {
        this(snapshot.getFirstName(), snapshot.getLastName(), snapshot.getGender(), snapshot.getPicture());
        this.setPersonId(snapshot.getPersonId());
        this.middleName = snapshot.getMiddleName();
    }

    public List<PassengerSchool> getPassengerSchools() {
        if (this.passengerSchools == null) {
            this.passengerSchools = new ArrayList<>();
        }
        return passengerSchools;
    }

    public void setPassengerSchools(List<PassengerSchool> passengerSchools) {
        this.passengerSchools = passengerSchools;
    }

    public List<DriverPassenger> getDriverPassengers() {
        if (this.driverPassengers == null) {
            this.driverPassengers = new ArrayList<>();
        }
        return driverPassengers;
    }

    public void setDriverPassengers(List<DriverPassenger> driverPassengers) {
        this.driverPassengers = driverPassengers;
    }

    public List<PassengerRoute> getPassengerRoutes() {
        if (this.passengerRoutes == null) {
            this.passengerRoutes = new ArrayList<>();
        }
        return passengerRoutes;
    }

    public void setPassengerRoutes(List<PassengerRoute> passengerRoutes) {
        this.passengerRoutes = passengerRoutes;
    }

    public SortedSet<Ticket> getTickets() {
        if (this.tickets == null) {
            this.tickets = new TreeSet<>();
        }
        return tickets;
    }

    public void setTickets(SortedSet<Ticket> tickets) {
        this.tickets = tickets;
    }

    public static Passenger findByPersonId(long pk) {
        return new Select()
                .from(Passenger.class)
                .where("PersonId = ?", pk)
                .executeSingle();
    }

    public PassengerSnapshot toSnapshot() {
        PassengerSnapshot snapshot = new PassengerSnapshot();
        snapshot.setPersonId(personId);
        snapshot.setFirstName(firstName);
        snapshot.setMiddleName(middleName);
        snapshot.setLastName(lastName);
        snapshot.setGender(gender);
        snapshot.setPicture(picture);
        return snapshot;
    }

    @Override
    public String toString() {
        return "Passenger{} " + super.toString();
    }

    protected Passenger(Parcel in) {
        super(in);
        if (in.readByte() == 0x01) {
            passengerSchools = new ArrayList<PassengerSchool>();
            in.readList(passengerSchools, PassengerSchool.class.getClassLoader());
        } else {
            passengerSchools = null;
        }
        if (in.readByte() == 0x01) {
            driverPassengers = new ArrayList<DriverPassenger>();
            in.readList(driverPassengers, DriverPassenger.class.getClassLoader());
        } else {
            driverPassengers = null;
        }
        if (in.readByte() == 0x01) {
            passengerRoutes = new ArrayList<PassengerRoute>();
            in.readList(passengerRoutes, PassengerRoute.class.getClassLoader());
        } else {
            passengerRoutes = null;
        }
        tickets = (SortedSet) in.readValue(SortedSet.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        if (passengerSchools == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(passengerSchools);
        }
        if (driverPassengers == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(driverPassengers);
        }
        if (passengerRoutes == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(passengerRoutes);
        }
        dest.writeValue(tickets);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Passenger> CREATOR = new Parcelable.Creator<Passenger>() {
        @Override
        public Passenger createFromParcel(Parcel in) {
            return new Passenger(in);
        }

        @Override
        public Passenger[] newArray(int size) {
            return new Passenger[size];
        }
    };

}