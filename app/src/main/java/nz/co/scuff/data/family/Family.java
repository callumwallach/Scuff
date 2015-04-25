package nz.co.scuff.data.family;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashSet;

import nz.co.scuff.data.school.School;

/**
 * Created by Callum on 17/03/2015.
 */
public class Family implements Parcelable {

    public Family() {}

    public Family(String name) {
        this.id = 1;
        this.name = name;
        this.passengers = new HashSet<>();
        this.drivers = new HashSet<>();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void addDriver(Driver driver) {
        this.drivers.add(driver);
    }

    public boolean removeDriver(Driver driver) {
        return this.drivers.remove(driver);
    }

    public HashSet<Driver> getDrivers() {
        return drivers;
    }

    public void setDrivers(HashSet<Driver> drivers) {
        this.drivers = drivers;
    }

    public boolean addPassenger(Passenger passenger) {
        return this.passengers.add(passenger);
    }

    public boolean removePassenger(Passenger passenger) {
        return this.passengers.remove(passenger);
    }

    public HashSet<Passenger> getPassengers() {
        return passengers;
    }

    public void setPassengers(HashSet<Passenger> passengers) {
        this.passengers = passengers;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private long id;
    private String name;
    private HashSet<Driver> drivers;
    private HashSet<Passenger> passengers;

    public HashSet<Driver> getDriversForSchool(School school) {
        HashSet<Driver> driversForSchool = new HashSet<>();
        for (Driver d : drivers) {
            if (d.getSchoolRoutes().keySet().contains(school)) {
                driversForSchool.add(d);
            }
        }
        return driversForSchool;
    }

    public HashSet<Passenger> getPassengersForSchool(School school) {
        HashSet<Passenger> passengersForSchool = new HashSet<>();
        for (Passenger p : passengers) {
            if (p.getSchool().equals(school)) {
                passengersForSchool.add(p);
            }
        }
        return passengersForSchool;
    }

    // generated
    private HashSet<School> driversSchools;

    public HashSet<School> getDriversSchools() {
        if (this.driversSchools == null) {
            this.driversSchools = new HashSet<>();
            for (Driver d : this.drivers) {
                this.driversSchools.addAll(d.getSchoolRoutes().keySet());
            }
        }
        return this.driversSchools;
    }

    // generated
    private HashSet<School> passengersSchools;

    public HashSet<School> getPassengersSchools() {
        if (this.passengersSchools == null) {
            this.passengersSchools = new HashSet<>();
            for (Passenger p : this.passengers) {
                this.passengersSchools.add(p.getSchool());
            }
        }
        return this.passengersSchools;
    }

    public HashSet<School> getAllSchools() {
        HashSet<School> allSchools = new HashSet<>(getDriversSchools());
        allSchools.addAll(getPassengersSchools());
        return allSchools;
    }

    protected Family(Parcel in) {
        id = in.readLong();
        name = in.readString();
        drivers = (HashSet) in.readValue(HashSet.class.getClassLoader());
        passengers = (HashSet) in.readValue(HashSet.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeValue(drivers);
        dest.writeValue(passengers);
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