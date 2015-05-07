package nz.co.scuff.data.family;

import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.ArrayList;
import java.util.List;

import nz.co.scuff.data.family.snapshot.PassengerSnapshot;
import nz.co.scuff.data.relationship.DriverPassenger;
import nz.co.scuff.data.relationship.JourneyPassenger;
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
    private List<JourneyPassenger> journeyPassengers;

    public Passenger() { }

    public Passenger(String firstName, String lastName, Gender gender) {
        this(firstName, lastName, gender, null);
    }

    public Passenger(String firstName, String lastName, Gender gender, String picture) {
        super(firstName, lastName, gender, picture);
        passengerSchools = new ArrayList<>();
        driverPassengers = new ArrayList<>();
        passengerRoutes = new ArrayList<>();
        journeyPassengers = new ArrayList<>();
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

    public List<JourneyPassenger> getJourneyPassengers() {
        if (this.journeyPassengers == null) {
            this.journeyPassengers = new ArrayList<>();
        }
        return journeyPassengers;
    }

    public void setJourneyPassengers(List<JourneyPassenger> journeyPassengers) {
        this.journeyPassengers = journeyPassengers;
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


}