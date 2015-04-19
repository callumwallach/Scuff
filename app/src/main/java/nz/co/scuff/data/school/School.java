package nz.co.scuff.data.school;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashSet;

import nz.co.scuff.data.family.Passenger;
import nz.co.scuff.data.family.Driver;
import nz.co.scuff.data.schedule.Schedule;

/**
 * Created by Callum on 17/03/2015.
 */
public class School implements Parcelable {

    private String name;
    private double latitude;
    private double longitude;
    private double elevation;

    private HashSet<Route> routes;
    private Schedule schedule;

    private HashSet<Passenger> passengers;
    private HashSet<Driver> drivers;

    public School(String name, double latitude, double longitude, double elevation) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.elevation = elevation;

        this.routes = new HashSet<>();
        this.schedule = new Schedule();
        this.passengers = new HashSet<>();
        this.drivers = new HashSet<>();
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public boolean addChild(Passenger passenger) {
        return this.passengers.add(passenger);
    }

    public boolean removeChild(Passenger passenger) {
        return this.passengers.remove(passenger);
    }

    public HashSet<Passenger> getPassengers() {
        return passengers;
    }

    public void setPassengers(HashSet<Passenger> passengers) {
        this.passengers = passengers;
    }

    public boolean addDriver(Driver driver) {
        return this.drivers.add(driver);
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getElevation() {
        return elevation;
    }

    public void setElevation(double elevation) {
        this.elevation = elevation;
    }

    public boolean addRoute(Route route) {
        return this.routes.add(route);
    }

    public boolean removeRoute(Route route) {
        return this.routes.remove(route);
    }

    public HashSet<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(HashSet<Route> routes) {
        this.routes = routes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        School school = (School) o;

        return name.equals(school.name);

    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return name;
    }

    protected School(Parcel in) {
        name = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        elevation = in.readDouble();
        routes = (HashSet) in.readValue(HashSet.class.getClassLoader());
        schedule = (Schedule) in.readValue(Schedule.class.getClassLoader());
        passengers = (HashSet) in.readValue(HashSet.class.getClassLoader());
        drivers = (HashSet) in.readValue(HashSet.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeDouble(elevation);
        dest.writeValue(routes);
        dest.writeValue(schedule);
        dest.writeValue(passengers);
        dest.writeValue(drivers);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<School> CREATOR = new Parcelable.Creator<School>() {
        @Override
        public School createFromParcel(Parcel in) {
            return new School(in);
        }

        @Override
        public School[] newArray(int size) {
            return new School[size];
        }
    };
}