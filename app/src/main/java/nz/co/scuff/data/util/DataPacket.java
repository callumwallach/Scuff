package nz.co.scuff.data.util;

import com.google.gson.annotations.Expose;

import java.util.HashMap;
import java.util.Map;

import nz.co.scuff.data.family.snapshot.DriverSnapshot;
import nz.co.scuff.data.family.snapshot.PassengerSnapshot;
import nz.co.scuff.data.journey.snapshot.JourneySnapshot;
import nz.co.scuff.data.school.snapshot.RouteSnapshot;
import nz.co.scuff.data.school.snapshot.SchoolSnapshot;

/**
 * Created by Callum on 11/05/2015.
 */
public class DataPacket {

    @Expose
    Map<Long, SchoolSnapshot> schoolSnapshots;
    @Expose
    Map<Long, RouteSnapshot> routeSnapshots;
    @Expose
    Map<String, JourneySnapshot> journeySnapshots;
    @Expose
    Map<Long, PassengerSnapshot> passengerSnapshots;
    @Expose
    Map<Long, DriverSnapshot> driverSnapshots;

    public DataPacket() {
        this.schoolSnapshots = new HashMap<>();
        this.routeSnapshots = new HashMap<>();
        this.journeySnapshots = new HashMap<>();
        this.passengerSnapshots = new HashMap<>();
        this.driverSnapshots = new HashMap<>();
    }

    public Map<Long, SchoolSnapshot> getSchoolSnapshots() {
        return schoolSnapshots;
    }

    public void setSchoolSnapshots(Map<Long, SchoolSnapshot> schoolSnapshots) {
        this.schoolSnapshots = schoolSnapshots;
    }

    public Map<Long, RouteSnapshot> getRouteSnapshots() {
        return routeSnapshots;
    }

    public void setRouteSnapshots(Map<Long, RouteSnapshot> routeSnapshots) {
        this.routeSnapshots = routeSnapshots;
    }

    public Map<String, JourneySnapshot> getJourneySnapshots() {
        return journeySnapshots;
    }

    public void setJourneySnapshots(Map<String, JourneySnapshot> journeySnapshots) {
        this.journeySnapshots = journeySnapshots;
    }

    public Map<Long, PassengerSnapshot> getPassengerSnapshots() {
        return passengerSnapshots;
    }

    public void setPassengerSnapshots(Map<Long, PassengerSnapshot> passengerSnapshots) {
        this.passengerSnapshots = passengerSnapshots;
    }

    public Map<Long, DriverSnapshot> getDriverSnapshots() {
        return driverSnapshots;
    }

    public void setDriverSnapshots(Map<Long, DriverSnapshot> driverSnapshots) {
        this.driverSnapshots = driverSnapshots;
    }
}
