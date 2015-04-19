package nz.co.scuff.data.school;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashSet;

import nz.co.scuff.data.family.Passenger;
import nz.co.scuff.data.family.Driver;

/**
 * Created by Callum on 17/03/2015.
 */
public class Bus implements Parcelable {

    private String name;
    private Driver driver;
    private HashSet<Passenger> passengers;

    public Bus(String name) {
        this.name = name;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
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


    protected Bus(Parcel in) {
        name = in.readString();
        driver = (Driver) in.readValue(Driver.class.getClassLoader());
        passengers = (HashSet) in.readValue(HashSet.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeValue(driver);
        dest.writeValue(passengers);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Bus> CREATOR = new Parcelable.Creator<Bus>() {
        @Override
        public Bus createFromParcel(Parcel in) {
            return new Bus(in);
        }

        @Override
        public Bus[] newArray(int size) {
            return new Bus[size];
        }
    };
}