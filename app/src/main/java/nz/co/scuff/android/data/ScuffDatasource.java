package nz.co.scuff.android.data;

import android.location.Location;
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.Seconds;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nz.co.scuff.android.util.Constants;
import nz.co.scuff.android.util.ScuffApplication;
import nz.co.scuff.android.util.ServerInterfaceGenerator;
import nz.co.scuff.data.family.Driver;
import nz.co.scuff.data.family.Passenger;
import nz.co.scuff.data.journey.Bus;
import nz.co.scuff.data.journey.Ticket;
import nz.co.scuff.data.journey.snapshot.BusSnapshot;
import nz.co.scuff.data.journey.snapshot.TicketSnapshot;
import nz.co.scuff.data.journey.snapshot.WaypointSnapshot;
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
import nz.co.scuff.data.util.DataPacket;
import nz.co.scuff.data.util.TrackingState;
import nz.co.scuff.data.journey.Journey;
import nz.co.scuff.data.journey.Waypoint;
import nz.co.scuff.server.ScuffServerInterface;
import nz.co.scuff.server.error.ResourceNotFoundException;
import nz.co.scuff.server.error.DefaultErrorHandler;
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

    public static int startJourney(String journeyId, Location location) {
        Journey journey = ((ScuffApplication) ScuffApplication.getContext()).getJourney();
        if (D) Log.d(TAG, "startJourney journey="+journeyId+" location="+location);

        // save journey first
        journey.save();
        Waypoint waypoint = new Waypoint(journey.getJourneyId()+":"+DateTimeUtils.currentTimeMillis(),
                0, 0, TrackingState.RECORDING, location);
        waypoint.setJourney(journey);
        waypoint.save();

        journey.getWaypoints().add(waypoint);
        journey.save();

        ((ScuffApplication) ScuffApplication.getContext()).setJourney(journey);

        // create snapshot for transport (full journey as created it server side)
        JourneySnapshot journeySnapshot = journey.toSnapshot();
        journeySnapshot.setSchoolId(journey.getSchool().getSchoolId());
        journeySnapshot.setRouteId(journey.getRoute().getRouteId());
        journeySnapshot.setDriverId(journey.getDriver().getPersonId());
        // add first waypoint
        journeySnapshot.getWaypoints().add(waypoint.toSnapshot());

        if (D) Log.d(TAG, "posting journey snapshot="+journey);
        ScuffServerInterface client = ServerInterfaceGenerator.createService(ScuffServerInterface.class, Constants.SERVER_URL);
        client.postJourney(journeySnapshot, new Callback<List<TicketSnapshot>>() {
            @Override
            public void success(List<TicketSnapshot> tickets, Response response) {
                if (D) Log.d(TAG, "Start journey SUCCESS code=" + response.getStatus());
                if (D) Log.d(TAG, "result=" + tickets);
            }

            @Override
            public void failure(RetrofitError error) {
                if (D) Log.d(TAG, "Start journey FAILED code=" + error.getLocalizedMessage());
            }
        });
        return 1;
    }

    public static int pauseJourney(final String journeyId, Location location) {
        //Journey journey = ((ScuffApplication) ScuffApplication.getContext()).getJourney();
        if (D) Log.d(TAG, "pauseJourney journey="+journeyId+" location="+location);
        Journey journey = Journey.findByJourneyId(journeyId);

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

        ((ScuffApplication) ScuffApplication.getContext()).setJourney(journey);

        // create journey snapshot for transport (only journeyId and changed data required)
        JourneySnapshot journeySnapshot = new JourneySnapshot();
        journeySnapshot.setJourneyId(journey.getJourneyId());
        journeySnapshot.setTotalDistance(journey.getTotalDistance());
        journeySnapshot.setTotalDuration(journey.getTotalDuration());
        journeySnapshot.setState(journey.getState());
        // add waypoint
        journeySnapshot.getWaypoints().add(newWaypoint.toSnapshot());

        ScuffServerInterface client = ServerInterfaceGenerator.createService(ScuffServerInterface.class, Constants.SERVER_URL);
        client.updateJourney(journeySnapshot.getJourneyId(), journeySnapshot, new Callback<List<TicketSnapshot>>() {
            @Override
            public void success(List<TicketSnapshot> tickets, Response response) {
                if (D) Log.d(TAG, "Pause journey SUCCESS code=" + response.getStatus());
                receiveTickets(journeyId, tickets);
            }

            @Override
            public void failure(RetrofitError error) {
                if (D) Log.d(TAG, "Pause journey FAILED code=" + error.getLocalizedMessage());
            }
        });
        return 1;
    }

    public static int continueJourney(final String journeyId, Location location) {
        //Journey journey = ((ScuffApplication) ScuffApplication.getContext()).getJourney();
        if (D) Log.d(TAG, "continueJourney journey="+journeyId+" location="+location);
        Journey journey = Journey.findByJourneyId(journeyId);

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

        ((ScuffApplication) ScuffApplication.getContext()).setJourney(journey);

        // create journey snapshot for transport (only journeyId and changed data required)
        JourneySnapshot journeySnapshot = new JourneySnapshot();
        journeySnapshot.setJourneyId(journey.getJourneyId());
        journeySnapshot.setTotalDistance(journey.getTotalDistance());
        journeySnapshot.setTotalDuration(journey.getTotalDuration());
        journeySnapshot.setState(journey.getState());
        // add waypoint
        journeySnapshot.getWaypoints().add(newWaypoint.toSnapshot());

        ScuffServerInterface client = ServerInterfaceGenerator.createService(ScuffServerInterface.class, Constants.SERVER_URL);
        client.updateJourney(journeySnapshot.getJourneyId(), journeySnapshot, new Callback<List<TicketSnapshot>>() {
            @Override
            public void success(List<TicketSnapshot> tickets, Response response) {
                if (D) Log.d(TAG, "Continue journey SUCCESS code=" + response.getStatus());
                receiveTickets(journeyId, tickets);
            }

            @Override
            public void failure(RetrofitError error) {
                if (D) Log.d(TAG, "Continue journey FAILED code=" + error.getLocalizedMessage());
            }
        });
        return 1;
    }

    public static int stopJourney(String journeyId, Location location) {
        //Journey journey = ((ScuffApplication) ScuffApplication.getContext()).getJourney();
        if (D) Log.d(TAG, "stopJourney journey="+journeyId+" location="+location);
        Journey journey = Journey.findByJourneyId(journeyId);

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

        // create journey snapshot for transport (only journeyId and changed data required)
        JourneySnapshot journeySnapshot = new JourneySnapshot();
        journeySnapshot.setJourneyId(journey.getJourneyId());
        journeySnapshot.setTotalDistance(journey.getTotalDistance());
        journeySnapshot.setTotalDuration(journey.getTotalDuration());
        journeySnapshot.setState(journey.getState());
        journeySnapshot.setCompleted(journey.getCompleted());
        // add waypoint
        journeySnapshot.getWaypoints().add(newWaypoint.toSnapshot());

        ScuffServerInterface client = ServerInterfaceGenerator.createService(ScuffServerInterface.class, Constants.SERVER_URL);
        client.updateJourney(journeySnapshot.getJourneyId(), journeySnapshot, new Callback<List<TicketSnapshot>>() {
            @Override
            public void success(List<TicketSnapshot> tickets, Response response) {
                if (D) Log.d(TAG, "Stop journey SUCCESS code=" + response.getStatus());
                if (D) Log.d(TAG, "result=" + tickets);
            }

            @Override
            public void failure(RetrofitError error) {
                if (D) Log.d(TAG, "Stop journey FAILED code=" + error.getLocalizedMessage());
            }
        });
        return 1;
    }

    public static int recordJourney(final String journeyId, Location location) {
        // TODO need to pass the journey to here rather than get from global
        //Journey journey = ((ScuffApplication) ScuffApplication.getContext()).getJourney();
        if (D) Log.d(TAG, "startJourney journey="+journeyId+" location="+location);
        Journey journey = Journey.findByJourneyId(journeyId);

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

        // create journey snapshot for transport (only journeyId and changed data required)
        JourneySnapshot journeySnapshot = new JourneySnapshot();
        journeySnapshot.setJourneyId(journey.getJourneyId());
        journeySnapshot.setTotalDistance(journey.getTotalDistance());
        journeySnapshot.setTotalDuration(journey.getTotalDuration());
        journeySnapshot.setState(journey.getState());
        // add waypoint
        journeySnapshot.getWaypoints().add(newWaypoint.toSnapshot());

        ScuffServerInterface client = ServerInterfaceGenerator.createService(ScuffServerInterface.class, Constants.SERVER_URL);
        client.updateJourney(journeySnapshot.getJourneyId(), journeySnapshot, new Callback<List<TicketSnapshot>>() {
            @Override
            public void success(List<TicketSnapshot> tickets, Response response) {
                if (D) Log.d(TAG, "Record journey SUCCESS code=" + response.getStatus());
                receiveTickets(journeyId, tickets);
            }

            @Override
            public void failure(RetrofitError error) {
                if (D) Log.d(TAG, "Record journey FAILED code=" + error.getLocalizedMessage());
            }
        });
        return 1;
    }

    private static void receiveTickets(String journeyId, List<TicketSnapshot> snapshots) {
        if (D) Log.d(TAG, "receiveTickets=" + snapshots);
        Journey journey = Journey.findByJourneyId(journeyId);
        for (TicketSnapshot ts : snapshots) {
            Ticket ticket = new Ticket(ts);
            ticket.save();
            Passenger passenger = Passenger.findByPersonId(ts.getPassengerId());
            ticket.setJourney(journey);
            ticket.setPassenger(passenger);
            ticket.save();
            journey.getTickets().add(ticket);
        }
        journey.save();
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

    public static List<Journey> getActiveJourneys(long routeId, long schoolId) {
        if (D) Log.d(TAG, "get active buses for route=" + routeId + " school=" + schoolId);

        // fresh journey search
        ScuffServerInterface client = ServerInterfaceGenerator.createService(ScuffServerInterface.class, Constants.SERVER_URL);
        DataPacket packet = client.getActiveJourneys(routeId, schoolId);
        for (Long dId : packet.getDriverSnapshots().keySet()) {
            DriverSnapshot ds = packet.getDriverSnapshots().get(dId);
            Driver driver = Driver.findByPersonId(ds.getPersonId());
            if (driver == null) {
                // create
                driver = new Driver(ds);
                driver.save();
            }
        }
        for (String wId : packet.getWaypointSnapshots().keySet()) {
            WaypointSnapshot ws = packet.getWaypointSnapshots().get(wId);
            Waypoint waypoint = new Waypoint(ws);
            waypoint.save();
        }
        List<Journey> journeys = new ArrayList<>();
        for (String jId : packet.getJourneySnapshots().keySet()) {
            JourneySnapshot js = packet.getJourneySnapshots().get(jId);
            Journey journey = Journey.findByJourneyId(js.getJourneyId());
            if (journey == null) {
                // create
                journey = new Journey(js);
                journey.save();
            }
            for (WaypointSnapshot ws : js.getWaypoints()) {
                Waypoint waypoint = Waypoint.findByWaypointId(ws.getWaypointId());
                journey.getWaypoints().add(waypoint);
            }
            journey.setDriver(Driver.findByPersonId(js.getDriverId()));
            journey.setRoute(Route.findByRouteId(js.getRouteId()));
            journey.setSchool(School.findBySchoolId(js.getSchoolId()));
            journey.save();
            journeys.add(journey);
        }
        return journeys;

        /*List<BusSnapshot> busSnapshots = client.getActiveJourneys(routeId, schoolId);

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
        return buses;*/

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

    public static List<Ticket> requestTickets(String journeyId, List<Long> passengerIds) throws ResourceNotFoundException {
        if (D) Log.d(TAG, "requestTickets for journey="+journeyId+" and ids="+passengerIds);

        // TODO ensure only one request per child per journey (done at server currently)
        ScuffServerInterface client = ServerInterfaceGenerator.createService(ScuffServerInterface.class, Constants.SERVER_URL, new DefaultErrorHandler());
        List<TicketSnapshot> ticketSnapshots = client.requestTickets(journeyId, passengerIds);
        Journey journey = Journey.findByJourneyId(journeyId);
        List<Ticket> tickets = new ArrayList<>();
        for (TicketSnapshot ts : ticketSnapshots) {
            Ticket ticket = new Ticket(ts);
            ticket.save();
            ticket.setJourney(journey);
            ticket.setPassenger(Passenger.findByPersonId(ts.getPassengerId()));
            ticket.save();
            if (D) Log.d(TAG, "saving ticket="+ticket);
            tickets.add(ticket);
        }
        journey.save();
        return tickets;
    }

/*    public static void requestTickets(String journeyId, List<Long> passengerIds) {
        if (D) Log.d(TAG, "requestTickets for journey="+journeyId+" and ids="+passengerIds);

        ScuffServerInterface client = ServerInterfaceGenerator.createService(ScuffServerInterface.class, Constants.SERVER_URL);
        client.requestTickets(journeyId, passengerIds, new Callback<List<TicketSnapshot>>() {
            @Override
            public void success(List<TicketSnapshot> tickets, Response response) {
                if (D) Log.d(TAG, "Request tickets SUCCESS code=" + response.getStatus());
                if (D) {
                    for (TicketSnapshot ticket : tickets) {
                        Log.d(TAG, "received ticket=" + ticket);
                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {
                if (D) Log.d(TAG, "Request tickets FAILED code=" + error.getLocalizedMessage());
            }
        });
    }*/

    public static void postRegistration(DataPacket packet) {
        if (D) Log.d(TAG, "postRegistration packet="+packet);

        ScuffServerInterface client = ServerInterfaceGenerator.createService(ScuffServerInterface.class, Constants.SERVER_URL);
        client.postRegistration(packet, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                if (D) Log.d(TAG, "POST registration SUCCESS code=" + response.getStatus());
            }

            @Override
            public void failure(RetrofitError error) {
                if (D) Log.d(TAG, "POST registration FAILED code=" + error.getLocalizedMessage());
            }
        });
    }

    public static List<School> getSchools(double latitude, double longitude) {
        if (D) Log.d(TAG, "getSchools lat="+latitude+" long="+longitude);

        ScuffServerInterface client = ServerInterfaceGenerator.createService(ScuffServerInterface.class, Constants.SERVER_URL);
        DataPacket packet = client.getSchools(latitude, longitude, Constants.DEFAULT_RADIUS);
        Map<Long, SchoolSnapshot> schoolMap = packet.getSchoolSnapshots();
        Map<Long, RouteSnapshot> routeMap = packet.getRouteSnapshots();

        List<School> schools = new ArrayList<>();
        for (Long sk : schoolMap.keySet()) {
            SchoolSnapshot ss = schoolMap.get(sk);
            School school = new School(ss);
            for (Long rId : ss.getRouteIds()) {
                RouteSnapshot rs = routeMap.get(rId);
                Route route = new Route(rs);
                school.getRoutes().add(route);
            }
            schools.add(school);
        }
        return schools;
    }

    public static Driver getDriver(String email) {
        if (D) Log.d(TAG, "getDriverId by email="+email);

        ScuffServerInterface client = ServerInterfaceGenerator.createService(ScuffServerInterface.class, Constants.SERVER_URL);

        // check locally first
        Driver currentDriver = Driver.findDriverByEmail(email);
        if (currentDriver != null) {
            return currentDriver;
        }

        // not cached so full load from server (first time)
        // retrieving
        // Driver - Routes
        //        - Schools
        //        - Children
        // Passenger - Routes
        //           - Schools
        //           - Parents
        DataPacket packet = client.getDriver(email);
        DriverSnapshot currentDriverSnapshot = new DriverSnapshot();

        Map<Long, SchoolSnapshot> schoolSnapshots = packet.getSchoolSnapshots();
        Map<Long, RouteSnapshot> routeSnapshots = packet.getRouteSnapshots();
        Map<Long, PassengerSnapshot> passengerSnapshots = packet.getPassengerSnapshots();
        Map<Long, DriverSnapshot> driverSnapshots = packet.getDriverSnapshots();

        Map<Long, School> cachedSchools = new HashMap<>();
        Map<Long, Route> cachedRoutes = new HashMap<>();
        Map<Long, Passenger> cachedPassengers = new HashMap<>();
        Map<Long, Driver> cachedDrivers = new HashMap<>();

        // save the entity data
        for (long driverId : driverSnapshots.keySet()) {
            DriverSnapshot ds = driverSnapshots.get(driverId);
            Driver d = Driver.findDriverByEmail(ds.getEmail());
            if (d == null) {
                d = new Driver(ds);
                d.save();
            }
            cachedDrivers.put(driverId, d);
            if (d.getEmail().equals(email)) {
                // this is me
                currentDriver = d;
                currentDriverSnapshot = ds;
            }
        }
        for (long schoolId : schoolSnapshots.keySet()) {
            SchoolSnapshot ss = schoolSnapshots.get(schoolId);
            School s = School.findBySchoolId(ss.getSchoolId());
            if (s == null) {
                s = new School(ss);
                s.save();
            }
            cachedSchools.put(schoolId, s);
        }
        for (long routeId : routeSnapshots.keySet()) {
            RouteSnapshot rs = routeSnapshots.get(routeId);
            Route r = Route.findByRouteId(rs.getRouteId());
            if (r == null) {
                r = new Route(rs);
                r.save();
            }
            cachedRoutes.put(routeId, r);
        }
        for (long passengerId : passengerSnapshots.keySet()) {
            PassengerSnapshot ps = passengerSnapshots.get(passengerId);
            Passenger p = Passenger.findByPersonId(ps.getPersonId());
            if (p == null) {
                p = new Passenger(ps);
                p.save();
            }
            cachedPassengers.put(passengerId, p);
        }

        assert(currentDriver != null);

        // drivers schools
        for (long schoolId : currentDriverSnapshot.getSchoolIdsDrivenFor()) {
            School school = cachedSchools.get(schoolId);
            assert(school != null);
            DriverSchool driverSchool = new DriverSchool(currentDriver, school);
            driverSchool.save();
            school.getDriverSchools().add(driverSchool);
            currentDriver.getDriverSchools().add(driverSchool);
            school.save();
        }

        // drivers routes
        for (long routeId : currentDriverSnapshot.getRegisteredRouteIds()) {
            Route route = cachedRoutes.get(routeId);
            assert(route != null);
            School school = cachedSchools.get(routeSnapshots.get(routeId).getSchoolId());
            assert(school != null);
            route.setSchool(school);
            route.save();
            school.getRoutes().add(route);
            school.save();
            DriverRoute driverRoute = new DriverRoute(currentDriver, route);
            driverRoute.save();
            currentDriver.getDriverRoutes().add(driverRoute);
        }

        // drivers journeys
        // ON DEMAND - each time accessed display cached entries and request new entries from server

        // drivers children
        for (long passengerId : currentDriverSnapshot.getChildrenIds()) {
            Passenger passenger = cachedPassengers.get(passengerId);
            assert(passenger != null);
            DriverPassenger driverPassenger = new DriverPassenger(currentDriver, passenger);
            driverPassenger.save();
            passenger.getDriverPassengers().add(driverPassenger);
            currentDriver.getDriverPassengers().add(driverPassenger);

            // childrens schools
            for (long schoolId : passengerSnapshots.get(passengerId).getSchoolIds()) {
                School school = cachedSchools.get(schoolId);
                assert(school != null);
                PassengerSchool passengerSchool = new PassengerSchool(passenger, school);
                passengerSchool.save();
                passenger.getPassengerSchools().add(passengerSchool);
                school.getPassengerSchools().add(passengerSchool);
                school.save();
            }
            // childrens parents
            for (long parentId : passengerSnapshots.get(passengerId).getParentIds()) {
                Driver parent = cachedDrivers.get(parentId);
                assert(parent != null);
                DriverPassenger passengerParent = new DriverPassenger(parent, passenger);
                passengerParent.save();
                passenger.getDriverPassengers().add(passengerParent);
                parent.getDriverPassengers().add(passengerParent);
                parent.save();
            }
            // childrens routes
            for (long routeId : passengerSnapshots.get(passengerId).getRegisteredRouteIds()) {
                Route route = cachedRoutes.get(routeId);
                assert(route != null);
                PassengerRoute passengerRoute = new PassengerRoute(passenger, route);
                passengerRoute.save();
                passenger.getPassengerRoutes().add(passengerRoute);
            }
            // childrens journeys/boarding info
            // ON DEMAND - each time accessed display cached entries and request new entries from server

            // update passenger
            passenger.save();
        }

        currentDriver.save();

        return currentDriver;

        /*// not cached so load from server (first time)
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

        return currentDriver;
        */
    }

}
