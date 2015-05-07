package nz.co.scuff.android.data;

import android.location.Location;
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.Seconds;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import nz.co.scuff.android.util.Constants;
import nz.co.scuff.android.util.ScuffApplication;
import nz.co.scuff.android.util.ServerInterfaceGenerator;
import nz.co.scuff.data.family.Driver;
import nz.co.scuff.data.family.Passenger;
import nz.co.scuff.data.journey.Bus;
import nz.co.scuff.data.journey.snapshot.BusSnapshot;
import nz.co.scuff.data.relationship.DriverPassenger;
import nz.co.scuff.data.family.snapshot.PassengerSnapshot;
import nz.co.scuff.data.family.snapshot.DriverSnapshot;
import nz.co.scuff.data.journey.snapshot.JourneySnapshot;
import nz.co.scuff.data.relationship.PassengerRoute;
import nz.co.scuff.data.relationship.PassengerSchool;
import nz.co.scuff.data.relationship.DriverRoute;
import nz.co.scuff.data.relationship.DriverSchool;
import nz.co.scuff.data.school.Route;
import nz.co.scuff.data.school.School;
import nz.co.scuff.data.school.snapshot.RouteSnapshot;
import nz.co.scuff.data.school.snapshot.SchoolSnapshot;
import nz.co.scuff.data.util.TrackingState;
import nz.co.scuff.data.journey.Journey;
import nz.co.scuff.data.journey.Waypoint;
import nz.co.scuff.server.ScuffServerInterface;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Callum on 17/04/2015.
 */
public final class ScuffDatasource {

    private static final String TAG = "JourneyDatasource";
    private static final boolean D = true;

    private static float calculateDistance(Location here, Waypoint there) {
        Location location = new Location("Last Location");
        location.setLatitude(there.getLatitude());
        location.setLongitude(there.getLongitude());
        return here.distanceTo(location);
    }

    private static float calculateDistance(Waypoint here, Waypoint there) {
        Location hereLocation = new Location("Here Location");
        hereLocation.setLatitude(here.getLatitude());
        hereLocation.setLongitude(here.getLongitude());
        Location thereLocation = new Location("There Location");
        thereLocation.setLatitude(there.getLatitude());
        thereLocation.setLongitude(there.getLongitude());
        return hereLocation.distanceTo(thereLocation);
    }

    private static int calculateTimeSince(Waypoint waypoint) {
        DateTime then = new DateTime(waypoint.getCreated().getTime());
        Seconds seconds = Seconds.secondsBetween(then, DateTime.now());
        return seconds.getSeconds();
    }

    // TODO deal with this another way
    public static void cleanUpIncompleteJourney(Journey journey) {
        if (D) Log.d(TAG, "cleaning up journey="+journey);
        journey.delete();
    }

    public static int startJourney(Location location) {
        Journey journey = ((ScuffApplication) ScuffApplication.getContext()).getJourney();
        if (D) Log.d(TAG, "startJourney journey="+journey+" location="+location);

        // save journey first
        journey.save();
        Waypoint waypoint = new Waypoint(journey.getJourneyId()+":"+DateTimeUtils.currentTimeMillis(),
                0, 0, TrackingState.RECORDING, location);
        waypoint.setJourney(journey);
        waypoint.save();

        journey.getWaypoints().add(waypoint);
        journey.save();

        // create snapshot for transport
        JourneySnapshot journeySnapshot = journey.toSnapshot();
        journeySnapshot.setSchool(journey.getSchool().toSnapshot());
        journeySnapshot.setRoute(journey.getRoute().toSnapshot());
        journeySnapshot.setDriver(journey.getDriver().toSnapshot());
        // add first waypoint
        journeySnapshot.getWaypoints().add(waypoint.toSnapshot());

        if (D) Log.d(TAG, "posting journey snapshot="+journey);
        ScuffServerInterface client = ServerInterfaceGenerator.createService(ScuffServerInterface.class, Constants.SERVER_URL);
        client.postJourney(journeySnapshot, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                if (D) Log.d(TAG, "POST journey SUCCESS code=" + response.getStatus());
            }

            @Override
            public void failure(RetrofitError error) {
                if (D) Log.d(TAG, "POST journey FAILED code=" + error.getLocalizedMessage());
            }
        });
        return 1;
    }

    public static int pauseJourney(Location location) {
        Journey journey = ((ScuffApplication) ScuffApplication.getContext()).getJourney();
        if (D) Log.d(TAG, "pauseJourney journey="+journey+" location="+location);

        // update journey with new waypoint
        Waypoint lastWaypoint = journey.getCurrentWaypoint();
        Waypoint newWaypoint = new Waypoint(journey.getJourneyId()+":"+DateTimeUtils.currentTimeMillis(),
                calculateDistance(location, lastWaypoint), calculateTimeSince(lastWaypoint),
                TrackingState.PAUSED, location);

        newWaypoint.setJourney(journey);
        newWaypoint.save();

        journey.getWaypoints().add(newWaypoint);
        journey.setTotalDistance(journey.getTotalDistance() + newWaypoint.getDistance());
        journey.setTotalDuration(journey.getTotalDuration() + newWaypoint.getDuration());
        journey.setState(TrackingState.PAUSED);
        journey.save();

        // create journey snapshot for transport
        JourneySnapshot journeySnapshot = journey.toSnapshot();
        journeySnapshot.setSchool(journey.getSchool().toSnapshot());
        journeySnapshot.setRoute(journey.getRoute().toSnapshot());
        journeySnapshot.setDriver(journey.getDriver().toSnapshot());
        journeySnapshot.setTotalDistance(journey.getTotalDistance());
        journeySnapshot.setTotalDuration(journey.getTotalDuration());
        journeySnapshot.setState(journey.getState());
        // add waypoint
        journeySnapshot.getWaypoints().add(newWaypoint.toSnapshot());

        ScuffServerInterface client = ServerInterfaceGenerator.createService(ScuffServerInterface.class, Constants.SERVER_URL);
        client.postJourney(journeySnapshot, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                if (D) Log.d(TAG, "POST journey SUCCESS code=" + response.getStatus());
            }

            @Override
            public void failure(RetrofitError error) {
                if (D) Log.d(TAG, "POST journey FAILED code=" + error.getLocalizedMessage());
            }
        });
        return 1;
    }

    public static int continueJourney(Location location) {
        Journey journey = ((ScuffApplication) ScuffApplication.getContext()).getJourney();
        if (D) Log.d(TAG, "continueJourney journey="+journey+" location="+location);

        // update journey with new waypoint
        Waypoint newWaypoint = new Waypoint(journey.getJourneyId()+":"+DateTimeUtils.currentTimeMillis(),
                0, 0, TrackingState.RECORDING, location);

        newWaypoint.setJourney(journey);
        newWaypoint.save();

        journey.getWaypoints().add(newWaypoint);
        journey.setTotalDistance(journey.getTotalDistance() + newWaypoint.getDistance());
        journey.setTotalDuration(journey.getTotalDuration() + newWaypoint.getDuration());
        journey.setState(TrackingState.RECORDING);
        journey.save();

        // create journey snapshot for transport
        JourneySnapshot journeySnapshot = journey.toSnapshot();
        journeySnapshot.setSchool(journey.getSchool().toSnapshot());
        journeySnapshot.setRoute(journey.getRoute().toSnapshot());
        journeySnapshot.setDriver(journey.getDriver().toSnapshot());
        journeySnapshot.setTotalDistance(journey.getTotalDistance());
        journeySnapshot.setTotalDuration(journey.getTotalDuration());
        journeySnapshot.setState(journey.getState());
        // add waypoint
        journeySnapshot.getWaypoints().add(newWaypoint.toSnapshot());

        ScuffServerInterface client = ServerInterfaceGenerator.createService(ScuffServerInterface.class, Constants.SERVER_URL);
        client.postJourney(journeySnapshot, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                if (D) Log.d(TAG, "POST journey SUCCESS code=" + response.getStatus());
            }

            @Override
            public void failure(RetrofitError error) {
                if (D) Log.d(TAG, "POST journey FAILED code=" + error.getLocalizedMessage());
            }
        });
        return 1;
    }

    public static int stopJourney(Location location) {
        Journey journey = ((ScuffApplication) ScuffApplication.getContext()).getJourney();
        if (D) Log.d(TAG, "stopJourney journey="+journey+" location="+location);

        // update journey with new waypoint
        Waypoint lastWaypoint = journey.getCurrentWaypoint();
        Waypoint newWaypoint = new Waypoint(journey.getJourneyId()+":"+DateTimeUtils.currentTimeMillis(),
                calculateDistance(location, lastWaypoint), calculateTimeSince(lastWaypoint),
                TrackingState.COMPLETED, location);

        newWaypoint.setJourney(journey);
        newWaypoint.save();

        journey.getWaypoints().add(newWaypoint);
        journey.setCompleted(new Timestamp(DateTimeUtils.currentTimeMillis()));
        journey.setTotalDistance(journey.getTotalDistance() + newWaypoint.getDistance());
        journey.setTotalDuration(journey.getTotalDuration() + newWaypoint.getDuration());
        journey.setState(TrackingState.COMPLETED);
        journey.save();

        // clear cache
        ((ScuffApplication) ScuffApplication.getContext()).setJourney(null);

        // create journey snapshot for transport
        JourneySnapshot journeySnapshot = journey.toSnapshot();
        journeySnapshot.setSchool(journey.getSchool().toSnapshot());
        journeySnapshot.setRoute(journey.getRoute().toSnapshot());
        journeySnapshot.setDriver(journey.getDriver().toSnapshot());
        journeySnapshot.setTotalDistance(journey.getTotalDistance());
        journeySnapshot.setTotalDuration(journey.getTotalDuration());
        journeySnapshot.setState(journey.getState());
        journeySnapshot.setCompleted(journey.getCompleted());
        // add waypoint
        journeySnapshot.getWaypoints().add(newWaypoint.toSnapshot());

        ScuffServerInterface client = ServerInterfaceGenerator.createService(ScuffServerInterface.class, Constants.SERVER_URL);
        client.postJourney(journeySnapshot, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                if (D) Log.d(TAG, "POST journey SUCCESS code=" + response.getStatus());
            }

            @Override
            public void failure(RetrofitError error) {
                if (D) Log.d(TAG, "POST journey FAILED code=" + error.getLocalizedMessage());
            }
        });
        return 1;
    }

    public static int recordJourney(Location location) {
        Journey journey = ((ScuffApplication) ScuffApplication.getContext()).getJourney();
        if (D) Log.d(TAG, "recordJourney journey="+journey+" location="+location);

        // update journey with new waypoint
        Waypoint lastWaypoint = journey.getCurrentWaypoint();
        Waypoint newWaypoint = new Waypoint(journey.getJourneyId()+":"+DateTimeUtils.currentTimeMillis(),
                calculateDistance(location, lastWaypoint), calculateTimeSince(lastWaypoint),
                TrackingState.RECORDING, location);

        newWaypoint.setJourney(journey);
        newWaypoint.save();

        journey.getWaypoints().add(newWaypoint);
        journey.setTotalDistance(journey.getTotalDistance() + newWaypoint.getDistance());
        journey.setTotalDuration(journey.getTotalDuration() + newWaypoint.getDuration());
        journey.save();

        // create journey snapshot for transport
        JourneySnapshot journeySnapshot = journey.toSnapshot();
        journeySnapshot.setSchool(journey.getSchool().toSnapshot());
        journeySnapshot.setRoute(journey.getRoute().toSnapshot());
        journeySnapshot.setDriver(journey.getDriver().toSnapshot());
        journeySnapshot.setTotalDistance(journey.getTotalDistance());
        journeySnapshot.setTotalDuration(journey.getTotalDuration());
        journeySnapshot.setState(journey.getState());
        // add waypoint
        journeySnapshot.getWaypoints().add(newWaypoint.toSnapshot());

        ScuffServerInterface client = ServerInterfaceGenerator.createService(ScuffServerInterface.class, Constants.SERVER_URL);
        client.postJourney(journeySnapshot, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                if (D) Log.d(TAG, "POST journey SUCCESS code=" + response.getStatus());
            }

            @Override
            public void failure(RetrofitError error) {
                if (D) Log.d(TAG, "POST journey FAILED code=" + error.getLocalizedMessage());
            }
        });
        return 1;
    }
/*
    // may be null (esp if tracking a bus and then bus completes journey
    public static Journey getActiveJourney(String journeyId, boolean prune) {
        if (D) Log.d(TAG, "get active snapshot for journey=" + journeyId);

        ScuffServerInterface client = ServerInterfaceGenerator.createService(ScuffServerInterface.class, Constants.SERVER_URL);
        JourneySnapshot js = client.getActiveJourneySnapshot(journeyId, prune);
        // already have journey
        Journey journey = Journey.findByJourneyId(journeyId);
        journey.setTotalDistance(js.getTotalDistance());
        journey.setTotalDuration(js.getTotalDuration());
        journey.setCompleted(js.getCompleted());
        journey.setState(js.getState());
        // update waypoints
        WaypointSnapshot ws = js.getWaypoints().first();
        Waypoint waypoint = new Waypoint(ws);
        waypoint.save();
        journey.getWaypoints().add(waypoint);
        journey.save();
        return journey;
    }*/

    public static List<Bus> getActiveBuses(long routeId, long schoolId) {
        if (D) Log.d(TAG, "get active buses for route=" + routeId + " school=" + schoolId);

        // fresh journey search
        ScuffServerInterface client = ServerInterfaceGenerator.createService(ScuffServerInterface.class, Constants.SERVER_URL);
        List<BusSnapshot> busSnapshots = client.getActiveBusSnapshots(routeId, schoolId);

        List<Bus> buses = new ArrayList<>();
        for (BusSnapshot bs : busSnapshots) {
            Bus bus = Bus.findByJourneyId(bs.getJourneyId());
            if (bus != null) {
                // update
                bus.setLatitude(bs.getLatitude());
                bus.setLongitude(bs.getLongitude());
                bus.setSpeed(bs.getSpeed());
                bus.setBearing(bs.getBearing());
                bus.setAccuracy(bs.getAccuracy());
                bus.setAltitude(bs.getAltitude());
                bus.setCreated(bs.getCreated());
            } else {
                bus = new Bus(bs);
            }
            bus.save();
            buses.add(bus);
        }
        return buses;

    }

/*    public static List<Journey> getActiveJourneys(String routeId, String schoolId, boolean prune) {
        if (D) Log.d(TAG, "get active snapshots for route=" + routeId + " school=" + schoolId);

        // fresh journey search
        ScuffServerInterface client = ServerInterfaceGenerator.createService(ScuffServerInterface.class, Constants.SERVER_URL);
        List<JourneySnapshot> journeySnapshots = client.getActiveJourneySnapshots(routeId, schoolId, prune);
        List<Journey> journeys = new ArrayList<>();
        for (JourneySnapshot js : journeySnapshots) {
            Journey journey = new Journey(js);
            journey.save();
            // already have route
            Route route = Route.findByRouteId(js.getRoute().getRouteId());
            journey.setRoute(route);
            // check for driver
            Driver driver = Driver.findByPersonId(js.getDriver().getPersonId());
            if (driver == null) {
                driver = new Driver(js.getDriver());
                driver.save();
            }
            journey.setDriver(driver);
            // already have school
            School school = School.findBySchoolId(js.getSchool().getSchoolId());
            journey.setSchool(school);
            // waypoints
            WaypointSnapshot ws = js.getWaypoints().first();
            Waypoint waypoint = new Waypoint(ws);
            waypoint.save();
            journey.getWaypoints().add(waypoint);
            journey.save();
            journeys.add(journey);
        }
        return journeys;

    }*/

    public static Driver getDriverByEmail(String email) {
        if (D) Log.d(TAG, "getDriver by email="+email);

        // check locally first
        Driver driver = Driver.findDriverByEmail(email);
        if (driver != null) {
            return driver;
        }

        // not cached so load from server (first time)
        ScuffServerInterface client = ServerInterfaceGenerator.createService(ScuffServerInterface.class, Constants.SERVER_URL);
        DriverSnapshot driverSnapshot = client.getDriverSnapshotByEmail(email);
        driver = new Driver(driverSnapshot);
        driver.save();

        // drivers schools
        for (SchoolSnapshot schoolSnapshot : driverSnapshot.getSchoolsDrivenFor()) {
            School school = School.findBySchoolId(schoolSnapshot.getSchoolId());
            if (school == null) {
                school = new School(schoolSnapshot);
                school.save();
            }
            DriverSchool driverSchool = new DriverSchool(driver, school);
            driverSchool.save();
            school.getDriverSchools().add(driverSchool);
            driver.getDriverSchools().add(driverSchool);
            school.save();
        }

        // drivers children
        for (PassengerSnapshot passengerSnapshot : driverSnapshot.getChildren()) {
            Passenger passenger = Passenger.findByPersonId(passengerSnapshot.getPersonId());
            if (passenger == null) {
                passenger = new Passenger(passengerSnapshot);
                passenger.save();
            }
            DriverPassenger driverPassenger = new DriverPassenger(driver, passenger);
            driverPassenger.save();
            passenger.getDriverPassengers().add(driverPassenger);
            driver.getDriverPassengers().add(driverPassenger);

            // childrens schools
            for (SchoolSnapshot schoolSnapshot : passengerSnapshot.getSchools()) {
                // check db first
                School school = School.findBySchoolId(schoolSnapshot.getSchoolId());
                if (school == null) {
                    school = new School(schoolSnapshot);
                    school.save();
                }
                PassengerSchool passengerSchool = new PassengerSchool(passenger, school);
                passenger.getPassengerSchools().add(passengerSchool);
                school.getPassengerSchools().add(passengerSchool);
                school.save();
            }
            // childrens parents
            for (DriverSnapshot otherDriverSnapshot : passengerSnapshot.getParents()) {
                // if this isn't me
                if (otherDriverSnapshot.getPersonId() != driver.getPersonId()) {
                    Driver otherParent = Driver.findByPersonId(otherDriverSnapshot.getPersonId());
                    if (otherParent == null) {
                        otherParent = new Driver(otherDriverSnapshot);
                        otherParent.save();
                    }
                    DriverPassenger passengerOtherParent = new DriverPassenger(otherParent, passenger);
                    passengerOtherParent.save();
                    passenger.getDriverPassengers().add(passengerOtherParent);
                    otherParent.getDriverPassengers().add(passengerOtherParent);
                    otherParent.save();
                }
            }
            // childrens routes
            for (RouteSnapshot routeSnapshot : passengerSnapshot.getRegisteredRoutes()) {
                Route route = Route.findByRouteId(routeSnapshot.getRouteId());
                if (route == null) {
                    route = new Route(routeSnapshot);
                    route.save();
                }
                PassengerRoute passengerRoute = new PassengerRoute(passenger, route);
                passenger.getPassengerRoutes().add(passengerRoute);
                passengerRoute.save();
            }
            // childrens journeys
            for (JourneySnapshot journeySnapshot : passengerSnapshot.getJourneys()) {
                Journey journey = new Journey(journeySnapshot);
                journey.save();
                School jSchool = School.findBySchoolId(journeySnapshot.getSchool().getSchoolId());
                if (jSchool == null) {
                    jSchool = new School(journeySnapshot.getSchool());
                    jSchool.save();
                }
                journey.setSchool(jSchool);
                jSchool.getJourneys().add(journey);
                jSchool.save();
                Driver jDriver = Driver.findByPersonId(journeySnapshot.getDriver().getPersonId());
                if (jDriver == null) {
                    jDriver = new Driver(journeySnapshot.getDriver());
                    jDriver.save();
                }
                journey.setDriver(jDriver);
                jDriver.getJourneys().add(journey);
                jDriver.save();
                Route jRoute = Route.findByRouteId(journeySnapshot.getRoute().getRouteId());
                if (jRoute == null) {
                    jRoute = new Route(journeySnapshot.getRoute());
                    jRoute.save();
                }
                journey.setRoute(jRoute);
                journey.save();
            }
            passenger.save();
        }
        // drivers journeys
        for (JourneySnapshot journeySnapshot : driverSnapshot.getJourneys()) {
            Journey dJourney = Journey.findByJourneyId(journeySnapshot.getJourneyId());
            if (dJourney == null) {
                dJourney = new Journey(journeySnapshot);
                dJourney.save();
            }
            School jSchool = School.findBySchoolId(journeySnapshot.getSchool().getSchoolId());
            if (jSchool == null) {
                jSchool = new School(journeySnapshot.getSchool());
                jSchool.save();
            }
            dJourney.setSchool(jSchool);
            jSchool.getJourneys().add(dJourney);
            jSchool.save();
            // driver is me (and im in the db)
            dJourney.setDriver(driver);
            driver.getJourneys().add(dJourney);
            dJourney.save();
            Route jRoute = Route.findByRouteId(journeySnapshot.getRoute().getRouteId());
            if (jRoute == null) {
                jRoute = new Route(journeySnapshot.getRoute());
                jRoute.save();
            }
            dJourney.setRoute(jRoute);
            dJourney.save();
        }
        // drivers routes
        for (RouteSnapshot routeSnapshot : driverSnapshot.getRegisteredRoutes()) {
            Route dRoute = Route.findByRouteId(routeSnapshot.getRouteId());
            if (dRoute == null) {
                dRoute = new Route(routeSnapshot);
                dRoute.save();
            }
            School rSchool = School.findBySchoolId(routeSnapshot.getSchool().getSchoolId());
            dRoute.setSchool(rSchool);
            dRoute.save();
            rSchool.getRoutes().add(dRoute);
            rSchool.save();
            DriverRoute driverRoute = new DriverRoute(driver, dRoute);
            driverRoute.save();
            driver.getDriverRoutes().add(driverRoute);
        }
        driver.save();

        return driver;
    }

}
