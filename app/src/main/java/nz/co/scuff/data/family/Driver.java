package nz.co.scuff.data.family;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.List;
import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;

import nz.co.scuff.data.family.snapshot.DriverSnapshot;
import nz.co.scuff.data.journey.Journey;
import nz.co.scuff.data.relationship.DriverPassenger;
import nz.co.scuff.data.relationship.DriverRoute;
import nz.co.scuff.data.relationship.DriverSchool;
import nz.co.scuff.data.school.Route;
import nz.co.scuff.data.school.School;

/**
 * Created by Callum on 17/03/2015.
 */
@Table(name="Drivers")
public class Driver extends Person {

    @Column(name="Email")
    private String email;
    @Column(name="Phone")
    private String phone;

    // many to many
    private List<DriverSchool> driverSchools;
    private List<DriverPassenger> driverPassengers;
    private List<DriverRoute> driverRoutes;

    // one to many
    /*@Column(name="Journeys")*/
    private SortedSet<Journey> journeys;

    public Driver() { }

    public Driver(String firstName, String lastName, Gender gender) {
        this(firstName, lastName, gender, null);
    }

    public Driver(String firstName, String lastName, Gender gender, String picture) {
        super(firstName, lastName, gender, picture);
        driverSchools = new ArrayList<>();
        driverPassengers = new ArrayList<>();
        driverRoutes = new ArrayList<>();
        journeys = new TreeSet<>();
    }

    public Driver(DriverSnapshot snapshot) {
        this(snapshot.getFirstName(), snapshot.getLastName(), snapshot.getGender(), snapshot.getPicture());
        this.setPersonId(snapshot.getPersonId());
        this.middleName = snapshot.getMiddleName();
        this.email = snapshot.getEmail();
        this.phone = snapshot.getPhone();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<DriverSchool> getDriverSchools() {
        if (this.driverSchools == null) {
            this.driverSchools = new ArrayList<>();
        }
        return driverSchools;
    }

    public void setDriverSchools(List<DriverSchool> driverSchools) {
        this.driverSchools = driverSchools;
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

    public List<DriverRoute> getDriverRoutes() {
        if (this.driverRoutes == null) {
            this.driverRoutes = new ArrayList<>();
        }
        return driverRoutes;
    }

    public void setDriverRoutes(List<DriverRoute> driverRoutes) {
        this.driverRoutes = driverRoutes;
    }

    public SortedSet<Journey> getJourneys() {
        if (this.journeys == null) {
            this.journeys = new TreeSet<>();
        }
        return journeys;
    }

    public void setJourneys(SortedSet<Journey> journeys) {
        this.journeys = journeys;
    }

    public DriverSnapshot toSnapshot() {
        DriverSnapshot snapshot = new DriverSnapshot();
        snapshot.setPersonId(personId);
        snapshot.setFirstName(firstName);
        snapshot.setMiddleName(middleName);
        snapshot.setLastName(lastName);
        snapshot.setGender(gender);
        snapshot.setPicture(picture);
        snapshot.setEmail(email);
        snapshot.setPhone(phone);
        return snapshot;
    }

    public SortedSet<School> getSchools() {
        List<School> schools = new Select()
                .from(School.class)
                .innerJoin(DriverSchool.class).on("Schools.Id = DriverSchools.SchoolFK")
                .where("DriverSchools.DriverFK = ?", getId())
                .execute();
        return new TreeSet<>(schools);
    }

    public SortedSet<Passenger> getChildren() {
        List<Passenger> passengers = new Select()
                .from(Passenger.class)
                .innerJoin(DriverPassenger.class).on("Passengers.Id = DriverPassengers.PassengerFK")
                .where("DriverPassengers.DriverFK = ?", getId())
                .execute();
        return new TreeSet<>(passengers);
    }

    // TODO complete. currently just returns first route from driver
    public Route getScheduledRoute() {
        List<Route> routes = new Select()
                .from(Route.class)
                .innerJoin(DriverRoute.class).on("Routes.Id = DriverRoutes.RouteFK")
                .where("DriverRoutes.DriverFK = ?", getId())
                .execute();
        return routes.iterator().hasNext() ? routes.iterator().next() : null;
    }

    public static Driver findByPersonId(long pk) {
        return new Select()
                .from(Driver.class)
                .where("PersonId = ?", pk)
                .executeSingle();
    }

    public static Driver findDriverByEmail(String email) {
        return new Select()
                .from(Driver.class)
                .where("Email = ?", email)
                .executeSingle();
    }

    @Override
    public String toString() {
        return "Driver{" +
                "phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                "} " + super.toString();
    }

}