package nz.co.scuff.data.family.snapshot;

import com.google.gson.annotations.Expose;

import nz.co.scuff.data.journey.snapshot.JourneySnapshot;
import nz.co.scuff.data.school.snapshot.RouteSnapshot;
import nz.co.scuff.data.school.snapshot.SchoolSnapshot;

import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by Callum on 4/05/2015.
 */
public class PassengerSnapshot extends PersonSnapshot {

    @Expose
    SortedSet<SchoolSnapshot> schools;
    @Expose
    SortedSet<DriverSnapshot> parents;
    @Expose
    private SortedSet<JourneySnapshot> journeys;
    @Expose
    private SortedSet<RouteSnapshot> registeredRoutes;

    public PassengerSnapshot() {
        schools = new TreeSet<>();
        parents = new TreeSet<>();
        journeys = new TreeSet<>();
        registeredRoutes = new TreeSet<>();
    }

    public SortedSet<SchoolSnapshot> getSchools() {
        return schools;
    }

    public void setSchools(SortedSet<SchoolSnapshot> schools) {
        this.schools = schools;
    }

    public SortedSet<DriverSnapshot> getParents() {
        return parents;
    }

    public void setParents(SortedSet<DriverSnapshot> parents) {
        this.parents = parents;
    }

    public SortedSet<JourneySnapshot> getJourneys() {
        return journeys;
    }

    public void setJourneys(SortedSet<JourneySnapshot> journeys) {
        this.journeys = journeys;
    }

    public SortedSet<RouteSnapshot> getRegisteredRoutes() {
        return registeredRoutes;
    }

    public void setRegisteredRoutes(SortedSet<RouteSnapshot> registeredRoutes) {
        this.registeredRoutes = registeredRoutes;
    }

    @Override
    public String toString() {
        return "PassengerSnapshot{" +
                "schools=" + schools +
                ", parents=" + parents +
                ", journeys=" + journeys +
                ", registeredRoutes=" + registeredRoutes +
                "} " + super.toString();
    }
}
