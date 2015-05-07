package nz.co.scuff.data.family.snapshot;

import com.google.gson.annotations.Expose;

import java.util.SortedSet;
import java.util.TreeSet;

import nz.co.scuff.data.journey.snapshot.JourneySnapshot;
import nz.co.scuff.data.school.snapshot.RouteSnapshot;
import nz.co.scuff.data.school.snapshot.SchoolSnapshot;

/**
 * Created by Callum on 4/05/2015.
 */
public class DriverSnapshot extends PersonSnapshot {

    @Expose
    private String email;
    @Expose
    private String phone;

    @Expose
    private SortedSet<SchoolSnapshot> schoolsDrivenFor;
    @Expose
    private SortedSet<PassengerSnapshot> children;
    @Expose
    private SortedSet<RouteSnapshot> registeredRoutes;
    @Expose
    private SortedSet<JourneySnapshot> journeys;

    public DriverSnapshot() {
        schoolsDrivenFor = new TreeSet<>();
        children = new TreeSet<>();
        registeredRoutes = new TreeSet<>();
        journeys = new TreeSet<>();
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

    public SortedSet<SchoolSnapshot> getSchoolsDrivenFor() {
        return schoolsDrivenFor;
    }

    public void setSchoolsDrivenFor(SortedSet<SchoolSnapshot> schoolsDrivenFor) {
        this.schoolsDrivenFor = schoolsDrivenFor;
    }

    public SortedSet<PassengerSnapshot> getChildren() {
        return children;
    }

    public void setChildren(SortedSet<PassengerSnapshot> children) {
        this.children = children;
    }

    public SortedSet<RouteSnapshot> getRegisteredRoutes() {
        return registeredRoutes;
    }

    public void setRegisteredRoutes(SortedSet<RouteSnapshot> registeredRoutes) {
        this.registeredRoutes = registeredRoutes;
    }

    public SortedSet<JourneySnapshot> getJourneys() {
        return journeys;
    }

    public void setJourneys(SortedSet<JourneySnapshot> journeys) {
        this.journeys = journeys;
    }

    @Override
    public String toString() {
        return "DriverSnapshot{" +
                "email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", schoolsDrivenFor=" + schoolsDrivenFor +
                ", children=" + children +
                ", registeredRoutes=" + registeredRoutes +
                ", journeys=" + journeys +
                "} " + super.toString();
    }
}
