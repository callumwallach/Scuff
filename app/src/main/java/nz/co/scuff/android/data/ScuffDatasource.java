package nz.co.scuff.android.data;

import android.location.Location;
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.Seconds;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nz.co.scuff.android.util.Constants;
import nz.co.scuff.android.util.ScuffApplication;
import nz.co.scuff.android.util.ServerInterfaceGenerator;
import nz.co.scuff.data.base.Coordinator;
import nz.co.scuff.data.base.snapshot.CoordinatorSnapshot;
import nz.co.scuff.data.family.Child;
import nz.co.scuff.data.family.ChildData;
import nz.co.scuff.data.family.PersonalData;
import nz.co.scuff.data.institution.InstitutionData;
import nz.co.scuff.data.journey.Stamp;
import nz.co.scuff.data.journey.Ticket;
import nz.co.scuff.data.journey.snapshot.StampSnapshot;
import nz.co.scuff.data.journey.snapshot.TicketSnapshot;
import nz.co.scuff.data.journey.snapshot.WaypointSnapshot;
import nz.co.scuff.data.place.Place;
import nz.co.scuff.data.place.snapshot.PlaceSnapshot;
import nz.co.scuff.data.relationship.FriendRelationship;
import nz.co.scuff.data.relationship.GuidingRelationship;
import nz.co.scuff.data.relationship.JourneyRelationship;
import nz.co.scuff.data.relationship.ParentalRelationship;
import nz.co.scuff.data.family.snapshot.ChildSnapshot;
import nz.co.scuff.data.family.snapshot.AdultSnapshot;
import nz.co.scuff.data.journey.snapshot.JourneySnapshot;
import nz.co.scuff.data.relationship.PlaceRelationship;
import nz.co.scuff.data.institution.Route;
import nz.co.scuff.data.institution.snapshot.RouteSnapshot;
import nz.co.scuff.data.institution.snapshot.InstitutionSnapshot;
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

    private static final String CO = "CO";
    private static final String IN = "IN";
    private static final String AD = "AD";
    private static final String WA = "WA";
    private static final String JO = "JO";
    private static final String RO = "RO";
    private static final String PL = "PL";
    private static final String CH = "CH";
    private static final String TI = "TI";
    private static final String ST = "ST";

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
        long currentMillis = DateTimeUtils.currentTimeMillis();
        Waypoint waypoint = new Waypoint(journey.getJourneyId()+":"+currentMillis,
                0, 0, TrackingState.RECORDING, location);
        waypoint.setJourney(journey);
        waypoint.save();

        journey.getWaypoints().add(waypoint);
        journey.save();

        Set<Coordinator> coordinators = new HashSet<>();
        coordinators.add(journey.getOwner());
        coordinators.add(journey.getAgent());
        coordinators.add(journey.getGuide());

        for (Coordinator coordinator : coordinators) {
            JourneyRelationship relationship = new JourneyRelationship(coordinator, journey);
            relationship.save();
            coordinator.getCurrentJourneyRelationships().add(relationship);
            coordinator.save();
        }

        ((ScuffApplication) ScuffApplication.getContext()).setJourney(journey);

        // create waypoint snapshot
        WaypointSnapshot waypointSnapshot = waypoint.toSnapshot();

        // create snapshot for transport (full journey as created it server side)
        JourneySnapshot journeySnapshot = journey.toSnapshot();
        journeySnapshot.setOwnerId(journey.getOwner().getCoordinatorId());
        journeySnapshot.setAgentId(journey.getAgent().getCoordinatorId());
        journeySnapshot.setGuideId(journey.getGuide().getCoordinatorId());
        journeySnapshot.setOriginId(journey.getOrigin().getPlaceId());
        journeySnapshot.setDestinationId(journey.getDestination().getPlaceId());
        journeySnapshot.setRouteId(journey.getRoute().getRouteId());
        // add first waypoint
        journeySnapshot.getWaypointIds().add(waypointSnapshot.getWaypointId());

        DataPacket packet = new DataPacket();
        packet.getJourneySnapshots().put(journeySnapshot.getJourneyId(), journeySnapshot);
        packet.getWaypointSnapshots().put(waypointSnapshot.getWaypointId(), waypointSnapshot);

        if (D) Log.d(TAG, "posting journey snapshot="+journey);
        ScuffServerInterface client = ServerInterfaceGenerator.createService(ScuffServerInterface.class, Constants.SERVER_URL);
        client.postJourney(packet, new Callback<DataPacket>() {
            @Override
            public void success(DataPacket packet, Response response) {
                if (D) Log.d(TAG, "Start journey SUCCESS code=" + response.getStatus());
                if (D) Log.d(TAG, "Received response data packet=" + packet);
                //syncData(packet);
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
        Journey journey = Journey.findById(journeyId);

        // update journey with new waypoint
        Waypoint lastWaypoint = journey.getCurrentWaypoint();
        long currentMillis = DateTimeUtils.currentTimeMillis();
        Waypoint newWaypoint = new Waypoint(journey.getJourneyId()+":"+ currentMillis,
                calculateDistance(location, lastWaypoint), calculateTimeSince(lastWaypoint),
                TrackingState.PAUSED, location);

        newWaypoint.setJourney(journey);
        newWaypoint.save();

        journey.getWaypoints().add(newWaypoint);
        journey.setTotalDistance(journey.getTotalDistance() + newWaypoint.getDistance());
        journey.setTotalDuration(journey.getTotalDuration() + newWaypoint.getDuration());
        journey.setState(TrackingState.PAUSED);
        journey.setLastModified(new Timestamp(currentMillis));
        journey.save();

        ((ScuffApplication) ScuffApplication.getContext()).setJourney(journey);

        // create waypoint snapshot
        WaypointSnapshot waypointSnapshot = newWaypoint.toSnapshot();

        // create journey snapshot for transport (only journeyId and changed data required)
        JourneySnapshot journeySnapshot = new JourneySnapshot();
        journeySnapshot.setJourneyId(journey.getJourneyId());
        journeySnapshot.setTotalDistance(journey.getTotalDistance());
        journeySnapshot.setTotalDuration(journey.getTotalDuration());
        journeySnapshot.setState(journey.getState());
        journeySnapshot.setLastModified(journey.getLastModified());
        // add waypoint
        journeySnapshot.getWaypointIds().add(waypointSnapshot.getWaypointId());

        DataPacket packet = new DataPacket();
        packet.getJourneySnapshots().put(journeySnapshot.getJourneyId(), journeySnapshot);
        packet.getWaypointSnapshots().put(waypointSnapshot.getWaypointId(), waypointSnapshot);

        ScuffServerInterface client = ServerInterfaceGenerator.createService(ScuffServerInterface.class, Constants.SERVER_URL);
        client.updateJourney(journeySnapshot.getJourneyId(), packet, new Callback<DataPacket>() {
            @Override
            public void success(DataPacket packet, Response response) {
                if (D) Log.d(TAG, "Pause journey SUCCESS code=" + response.getStatus());
                syncData(packet);
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
        Journey journey = Journey.findById(journeyId);

        // update journey with new waypoint
        long currentMillis = DateTimeUtils.currentTimeMillis();
        Waypoint newWaypoint = new Waypoint(journey.getJourneyId()+":"+currentMillis,
                0, 0, TrackingState.RECORDING, location);

        newWaypoint.setJourney(journey);
        newWaypoint.save();

        journey.getWaypoints().add(newWaypoint);
        journey.setTotalDistance(journey.getTotalDistance() + newWaypoint.getDistance());
        journey.setTotalDuration(journey.getTotalDuration() + newWaypoint.getDuration());
        journey.setState(TrackingState.RECORDING);
        journey.setLastModified(new Timestamp(currentMillis));
        journey.save();

        ((ScuffApplication) ScuffApplication.getContext()).setJourney(journey);

        // create waypoint snapshot
        WaypointSnapshot waypointSnapshot = newWaypoint.toSnapshot();

        // create journey snapshot for transport (only journeyId and changed data required)
        JourneySnapshot journeySnapshot = new JourneySnapshot();
        journeySnapshot.setJourneyId(journey.getJourneyId());
        journeySnapshot.setTotalDistance(journey.getTotalDistance());
        journeySnapshot.setTotalDuration(journey.getTotalDuration());
        journeySnapshot.setState(journey.getState());
        journeySnapshot.setLastModified(journey.getLastModified());
        // add waypoint
        journeySnapshot.getWaypointIds().add(waypointSnapshot.getWaypointId());

        DataPacket packet = new DataPacket();
        packet.getJourneySnapshots().put(journeySnapshot.getJourneyId(), journeySnapshot);
        packet.getWaypointSnapshots().put(waypointSnapshot.getWaypointId(), waypointSnapshot);

        ScuffServerInterface client = ServerInterfaceGenerator.createService(ScuffServerInterface.class, Constants.SERVER_URL);
        client.updateJourney(journeySnapshot.getJourneyId(), packet, new Callback<DataPacket>() {
            @Override
            public void success(DataPacket packet, Response response) {
                if (D) Log.d(TAG, "Continue journey SUCCESS code=" + response.getStatus());
                syncData(packet);
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
        Journey journey = Journey.findById(journeyId);

        // update journey with new waypoint
        Waypoint lastWaypoint = journey.getCurrentWaypoint();
        long currentMillis = DateTimeUtils.currentTimeMillis();
        Waypoint newWaypoint = new Waypoint(journey.getJourneyId()+":"+currentMillis,
                calculateDistance(location, lastWaypoint), calculateTimeSince(lastWaypoint),
                TrackingState.COMPLETED, location);

        newWaypoint.setJourney(journey);
        newWaypoint.save();

        journey.getWaypoints().add(newWaypoint);
        journey.setCompleted(new Timestamp(DateTimeUtils.currentTimeMillis()));
        journey.setTotalDistance(journey.getTotalDistance() + newWaypoint.getDistance());
        journey.setTotalDuration(journey.getTotalDuration() + newWaypoint.getDuration());
        journey.setState(TrackingState.COMPLETED);
        journey.setLastModified(new Timestamp(currentMillis));
        journey.save();

/*      // Performed via query for completed journeys
        Set<Coordinator> coordinators = new HashSet<>();
        coordinators.add(journey.getOwner());
        coordinators.add(journey.getAgent());
        coordinators.add(journey.getGuide());

        for (Coordinator coordinator : coordinators) {
            JourneyRelationship fauxRelationship = new JourneyRelationship(coordinator, journey);
            Iterator itr = coordinator.getCurrentJourneyRelationships().iterator();
            while (itr.hasNext()) {
                JourneyRelationship relationship = (JourneyRelationship) itr.next();
                if (fauxRelationship.equals(relationship)) {
                    itr.remove();
                    coordinator.getPastJourneyRelationships().add(relationship);
                    coordinator.save();
                }
            }
        }*/

        // clear cache
        ((ScuffApplication) ScuffApplication.getContext()).setJourney(null);

        // create waypoint snapshot
        WaypointSnapshot waypointSnapshot = newWaypoint.toSnapshot();

        // create journey snapshot for transport (only journeyId and changed data required)
        JourneySnapshot journeySnapshot = new JourneySnapshot();
        journeySnapshot.setJourneyId(journey.getJourneyId());
        journeySnapshot.setTotalDistance(journey.getTotalDistance());
        journeySnapshot.setTotalDuration(journey.getTotalDuration());
        journeySnapshot.setState(journey.getState());
        journeySnapshot.setCompleted(journey.getCompleted());
        journeySnapshot.setLastModified(journey.getLastModified());
// add waypoint
        journeySnapshot.getWaypointIds().add(waypointSnapshot.getWaypointId());

        DataPacket packet = new DataPacket();
        packet.getJourneySnapshots().put(journeySnapshot.getJourneyId(), journeySnapshot);
        packet.getWaypointSnapshots().put(waypointSnapshot.getWaypointId(), waypointSnapshot);

        ScuffServerInterface client = ServerInterfaceGenerator.createService(ScuffServerInterface.class, Constants.SERVER_URL);
        client.updateJourney(journeySnapshot.getJourneyId(), packet, new Callback<DataPacket>() {
            @Override
            public void success(DataPacket packet, Response response) {
                if (D) Log.d(TAG, "Stop journey SUCCESS code=" + response.getStatus());
                //syncData(packet);
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
        Journey journey = Journey.findById(journeyId);

        // update journey with new waypoint
        Waypoint lastWaypoint = journey.getCurrentWaypoint();
        long currentMillis = DateTimeUtils.currentTimeMillis();
        Waypoint newWaypoint = new Waypoint(journey.getJourneyId()+":"+currentMillis,
                calculateDistance(location, lastWaypoint), calculateTimeSince(lastWaypoint),
                TrackingState.RECORDING, location);

        newWaypoint.setJourney(journey);
        newWaypoint.save();

        journey.getWaypoints().add(newWaypoint);
        journey.setTotalDistance(journey.getTotalDistance() + newWaypoint.getDistance());
        journey.setTotalDuration(journey.getTotalDuration() + newWaypoint.getDuration());
        journey.setLastModified(new Timestamp(currentMillis));
        journey.save();

        // create waypoint snapshot
        WaypointSnapshot waypointSnapshot = newWaypoint.toSnapshot();

        // create journey snapshot for transport (only journeyId and changed data required)
        JourneySnapshot journeySnapshot = new JourneySnapshot();
        journeySnapshot.setJourneyId(journey.getJourneyId());
        journeySnapshot.setTotalDistance(journey.getTotalDistance());
        journeySnapshot.setTotalDuration(journey.getTotalDuration());
        journeySnapshot.setState(journey.getState());
        journeySnapshot.setLastModified(journey.getLastModified());
        // add waypoint
        journeySnapshot.getWaypointIds().add(waypointSnapshot.getWaypointId());

        DataPacket packet = new DataPacket();
        packet.getJourneySnapshots().put(journeySnapshot.getJourneyId(), journeySnapshot);
        packet.getWaypointSnapshots().put(waypointSnapshot.getWaypointId(), waypointSnapshot);

        ScuffServerInterface client = ServerInterfaceGenerator.createService(ScuffServerInterface.class, Constants.SERVER_URL);
        client.updateJourney(journeySnapshot.getJourneyId(), packet, new Callback<DataPacket>() {
            @Override
            public void success(DataPacket packet, Response response) {
                if (D) Log.d(TAG, "Record journey SUCCESS code=" + response.getStatus());
                syncData(packet);
                //receiveTickets(journeyId, packet);
            }

            @Override
            public void failure(RetrofitError error) {
                if (D) Log.d(TAG, "Record journey FAILED code=" + error.getLocalizedMessage());
            }
        });
        return 1;
    }

/*    private static void receiveTickets(String journeyId, List<TicketSnapshot> snapshots) {
        if (D) Log.d(TAG, "receiveTickets=" + snapshots);
        Journey journey = Journey.findByJourneyId(journeyId);
        for (TicketSnapshot ts : snapshots) {
            Ticket ticket = new Ticket(ts);
            ticket.save();
            Child child = Child.findByPersonId(ts.getChildId());
            ticket.setJourney(journey);
            ticket.setChild(child);
            ticket.save();
            journey.getTickets().add(ticket);
        }
        journey.save();
    }*/

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
        WaypointSnapshot ws = js.getWaypointIds().first();
        Waypoint waypoint = new Waypoint(ws);
        waypoint.save();
        journey.getWaypointIds().add(waypoint);
        journey.save();
        return journey;
    }*/

    private static Map<String, Map> syncData(DataPacket packet) {
        if (D) Log.d(TAG, "syncing data packet=" + packet);
        
        Map<Long, Coordinator> cachedCoordinators = new HashMap<>();
        Map<Long, Coordinator> cachedInstitutions = new HashMap<>();
        Map<Long, Coordinator> cachedAdults = new HashMap<>();
        Map<String, Waypoint> cachedWaypoints = new HashMap<>();
        Map<String, Journey> cachedJourneys = new HashMap<>();
        Map<Long, Route> cachedRoutes = new HashMap<>();
        Map<Long, Place> cachedPlaces = new HashMap<>();
        Map<Long, Child> cachedChildren = new HashMap<>();
        Map<Long, Ticket> cachedTickets = new HashMap<>();
        Map<Long, Stamp> cachedStamps = new HashMap<>();

        Map<Long, PlaceSnapshot> placeSnapshots = packet.getPlaceSnapshots();
        Map<Long, InstitutionSnapshot> institutionSnapshots = packet.getInstitutionSnapshots();
        Map<Long, RouteSnapshot> routeSnapshots = packet.getRouteSnapshots();
        Map<String, JourneySnapshot> journeySnapshots = packet.getJourneySnapshots();
        Map<String, WaypointSnapshot> waypointSnapshots = packet.getWaypointSnapshots();
        Map<Long, ChildSnapshot> childSnapshots = packet.getChildSnapshots();
        Map<Long, AdultSnapshot> adultSnapshots = packet.getAdultSnapshots();
        Map<Long, TicketSnapshot> ticketSnapshots = packet.getTicketSnapshots();
        Map<Long, StampSnapshot> stampSnapshots = packet.getStampSnapshots();

        Set<Long> modifiedAdultIds = new HashSet<>();
        Set<Long> modifiedInstitutionIds = new HashSet<>();
        Set<Long> modifiedChildIds = new HashSet<>();
        Set<Long> modifiedRouteIds = new HashSet<>();
        Set<String> modifiedJourneyIds = new HashSet<>();
        Set<Long> modifiedTicketIds = new HashSet<>();

        // save/refresh entities
        // ADULTS
        for (Long dId : adultSnapshots.keySet()) {
            AdultSnapshot s = adultSnapshots.get(dId);
            if (D) Log.d(TAG, "snapshot=" + s);
            Coordinator coordinator = Coordinator.findById(s.getCoordinatorId());
            if (coordinator == null) {
                coordinator = new Coordinator();
                coordinator.refresh(s);
                coordinator.save();
                modifiedAdultIds.add(coordinator.getCoordinatorId());
            } else if (s.isDirty(coordinator.getLastModified())) {
                coordinator.refresh(s);
                coordinator.save();
                modifiedAdultIds.add(coordinator.getCoordinatorId());
            }
            // not null and not dirty -> don't save
            cachedAdults.put(coordinator.getCoordinatorId(), coordinator);
            cachedCoordinators.put(coordinator.getCoordinatorId(), coordinator);
        }
        // INSTITUTIONS
        for (Long iId : institutionSnapshots.keySet()) {
            InstitutionSnapshot s = institutionSnapshots.get(iId);
            if (D) Log.d(TAG, "snapshot=" + s);
            Coordinator institution = Coordinator.findById(s.getCoordinatorId());
            if (institution == null) {
                institution = new Coordinator();
                institution.refresh(s);
                institution.save();
                modifiedInstitutionIds.add(institution.getCoordinatorId());
            } else if (s.isDirty(institution.getLastModified())) {
                institution.refresh(s);
                institution.save();
                modifiedInstitutionIds.add(institution.getCoordinatorId());
            }
            cachedInstitutions.put(institution.getCoordinatorId(), institution);
            cachedCoordinators.put(institution.getCoordinatorId(), institution);
        }
        // CHILDREN
        for (Long cId : childSnapshots.keySet()) {
            ChildSnapshot s = childSnapshots.get(cId);
            if (D) Log.d(TAG, "snapshot=" + s);
            Child child = Child.findById(s.getChildId());
            if (child == null) {
                child = new Child();
                child.refresh(s);
                child.save();
                modifiedChildIds.add(child.getChildId());
            } else if (s.isDirty(child.getLastModified())) {
                child.refresh(s);
                child.save();
                modifiedChildIds.add(child.getChildId());
            }
            cachedChildren.put(child.getChildId(), child);
        }
        // ROUTES
        // only modified to be set active/inactive
        for (Long rId : routeSnapshots.keySet()) {
            RouteSnapshot s = routeSnapshots.get(rId);
            if (D) Log.d(TAG, "snapshot=" + s);
            Route route = Route.findById(s.getRouteId());
            if (route == null) {
                route = new Route();
                route.refresh(s);
                route.save();
                modifiedRouteIds.add(route.getRouteId());
            } else if (s.isDirty(route.getLastModified())) {
                // only change is to active flag
                route.refresh(s);
                route.save();
                modifiedRouteIds.add(route.getRouteId());
            }
            cachedRoutes.put(route.getRouteId(), route);
        }
        // JOURNEYS
        // modified with duration and distance updates + waypoints
        for (String jId : journeySnapshots.keySet()) {
            JourneySnapshot s = journeySnapshots.get(jId);
            if (D) Log.d(TAG, "snapshot=" + s);
            Journey journey = Journey.findById(s.getJourneyId());
            if (journey == null) {
                journey = new Journey();
                journey.refresh(s);
                journey.save();
                modifiedJourneyIds.add(journey.getJourneyId());
            } else if (s.isDirty(journey.getLastModified())) {
                journey.refresh(s);
                journey.save();
                modifiedJourneyIds.add(journey.getJourneyId());
            }
            cachedJourneys.put(journey.getJourneyId(), journey);
        }
        // WAYPOINTS
        // cannot be modified
        for (String wId : waypointSnapshots.keySet()) {
            WaypointSnapshot s = waypointSnapshots.get(wId);
            if (D) Log.d(TAG, "snapshot=" + s);
            Waypoint waypoint = Waypoint.findById(s.getWaypointId());
            if (waypoint == null) {
                waypoint = new Waypoint(s);
                waypoint.save();
            }
            cachedWaypoints.put(waypoint.getWaypointId(), waypoint);
        }
        // TICKETS
        // modified with stamp
        for (Long tId : ticketSnapshots.keySet()) {
            TicketSnapshot s = ticketSnapshots.get(tId);
            if (D) Log.d(TAG, "snapshot=" + s);
            Ticket ticket = Ticket.findById(s.getTicketId());
            if (ticket == null) {
                ticket = new Ticket(s);
                ticket.save();
                modifiedTicketIds.add(ticket.getTicketId());
            } else if (s.isDirty(ticket.getLastModified())) {
                ticket.refresh(s);
                ticket.save();
                modifiedTicketIds.add(ticket.getTicketId());
            }
            cachedTickets.put(ticket.getTicketId(), ticket);
        }
        // TERMINAL entities (no relationships)
        // PLACES
        // only modified to be set active/inactive
        for (Long pId : placeSnapshots.keySet()) {
            PlaceSnapshot s = placeSnapshots.get(pId);
            if (D) Log.d(TAG, "snapshot=" + s);
            Place place = Place.findById(s.getPlaceId());
            if (place == null) {
                place = new Place();
                place.refresh(s);
                place.save();
            } else if (s.isDirty(place.getLastModified())) {
                place.refresh(s);
                place.save();
            }
            cachedPlaces.put(place.getPlaceId(), place);
        }
        // STAMPS
        // cannot be modified
        for (Long pId : stampSnapshots.keySet()) {
            StampSnapshot s = stampSnapshots.get(pId);
            if (D) Log.d(TAG, "snapshot=" + s);
            Stamp stamp = Stamp.findById(s.getStampId());
            if (stamp == null) {
                stamp = new Stamp(s);
                stamp.save();
            }
            cachedStamps.put(stamp.getStampId(), stamp);
        }

        // build master cache
        Map<String, Map> organised = new HashMap<>();
        organised.put(AD, cachedAdults);
        organised.put(CH, cachedChildren);
        organised.put(CO, cachedCoordinators);
        organised.put(IN, cachedInstitutions);
        organised.put(JO, cachedJourneys);
        organised.put(PL, cachedPlaces);
        organised.put(RO, cachedRoutes);
        organised.put(ST, cachedStamps);
        organised.put(TI, cachedTickets);
        organised.put(WA, cachedWaypoints);

        // RELATIONSHIPS

        // ADULTS
        for (Long id : modifiedAdultIds) {
            doAdultRelationships(cachedAdults.get(id), adultSnapshots.get(id), organised);
        }
        // INSTITUTIONS
        for (Long id : modifiedInstitutionIds) {
            doInstitutionRelationships(cachedInstitutions.get(id), institutionSnapshots.get(id), organised);
        }
        // CHILDREN
        for (Long id : modifiedChildIds) {
            doChildrenRelationships(cachedChildren.get(id), childSnapshots.get(id), organised);
        }
        // ROUTES
        for (Long id : modifiedRouteIds) {
            doRouteRelationships(cachedRoutes.get(id), routeSnapshots.get(id), organised);
        }
        // JOURNEYS
        for (String id : modifiedJourneyIds) {
            doJourneyRelationships(cachedJourneys.get(id), journeySnapshots.get(id), organised);
        }
        // TICKETS
        for (Long id : modifiedTicketIds) {
            doTicketRelationships(cachedTickets.get(id), ticketSnapshots.get(id), organised);
        }

        if (D) {
            Log.d(TAG, "sync complete organised="+organised);
            for (String key : organised.keySet()) {
                Map value = organised.get(key);
                Log.d(TAG, "key="+key+" value="+value);
            }
        }
        return organised;

    }

    private static Stamp getStamp(long id, Map<String, Map> caches) {
        Stamp entity = (Stamp)caches.get(ST).get(id);
        if (entity == null) {
            entity = Stamp.findById(id);
        }
        return entity;
    }

    private static Ticket getTicket(long id, Map<String, Map> caches) {
        Ticket entity = (Ticket)caches.get(TI).get(id);
        if (entity == null) {
            entity = Ticket.findById(id);
        }
        return entity;
    }
    
    private static Journey getJourney(String id, Map<String, Map> caches) {
        Journey entity = (Journey)caches.get(JO).get(id);
        if (entity == null) {
            entity = Journey.findById(id);
        }
        return entity;
    }

    private static Child getChild(long id, Map<String, Map> caches) {
        Child entity = (Child)caches.get(CH).get(id);
        if (entity == null) {
            entity = Child.findById(id);
        }
        return entity;
    }

    private static Route getRoute(long id, Map<String, Map> caches) {
        Route entity = (Route)caches.get(RO).get(id);
        if (entity == null) {
            entity = Route.findById(id);
        }
        return entity;
    }

    private static Coordinator getCoordinator(long id, Map<String, Map> caches) {
        Coordinator entity = (Coordinator)caches.get(CO).get(id);
        if (entity == null) {
            entity = Coordinator.findById(id);
            if (entity == null) {
                entity = Coordinator.findById(id);
            }
        }
        return entity;
    }

    private static Coordinator getAdult(long id, Map<String, Map> caches) {
        Coordinator entity = (Coordinator)caches.get(AD).get(id);
        if (entity == null) {
            entity = Coordinator.findById(id);
        }
        return entity;
    }

    private static Coordinator getInstitution(long id, Map<String, Map> caches) {
        Coordinator entity = (Coordinator)caches.get(IN).get(id);
        if (entity == null) {
            entity = Coordinator.findById(id);
        }
        return entity;
    }

    private static Place getPlace(long id, Map<String, Map> caches) {
        Place entity = (Place)caches.get(PL).get(id);
        if (entity == null) {
            entity = Place.findById(id);
        }
        return entity;
    }

    private static Waypoint getWaypoint(String id, Map<String, Map> caches) {
        Waypoint entity = (Waypoint)caches.get(WA).get(id);
        if (entity == null) {
            entity = Waypoint.findById(id);
        }
        return entity;
    }

    private static void doTicketRelationships(Ticket existingData, TicketSnapshot incomingData,
                                              Map<String, Map> caches) {
        if (D) Log.d(TAG, "ticket relationships existing=" + existingData +" snapshot="+incomingData);

        // STAMPS
        long stampId = incomingData.getStampId();
        if (stampId != nz.co.scuff.data.util.Constants.LONG_VALUE_SET_TO_NULL) {
            existingData.setStamp(getStamp(stampId, caches));
        }
        // JOURNEY
        Journey journey = getJourney(incomingData.getJourneyId(), caches);
        existingData.setJourney(journey);
        if (journey.getTickets().add(existingData)) {
            journey.save();
        }
        // CHILD
        Child child = getChild(incomingData.getChildId(), caches);
        existingData.setChild(child);
        if (child.getTickets().add(existingData)) {
            child.save();
        }

        // save
        existingData.save();

    }

    private static void doJourneyRelationships(Journey existingData, JourneySnapshot incomingData,
                                               Map<String, Map> caches) {
        if (D) Log.d(TAG, "journey relationships existing=" + existingData +" snapshot="+incomingData);

        // WAYPOINTS
        Set<String> incomingWaypointIds = incomingData.getWaypointIds();
        if (!incomingWaypointIds.contains(nz.co.scuff.data.util.Constants.STRING_COLLECTION_NOT_RETRIEVED)) {
            // cannot remove waypoints from journey
            for (String id : incomingWaypointIds) {
                Waypoint cached = getWaypoint(id, caches);
                if (!existingData.getWaypoints().contains(cached)) {
                    existingData.getWaypoints().add(cached);
                    cached.setJourney(existingData);
                    cached.save();
                }
            }
        }
        // TICKETS
        Set<Long> incomingTicketIds = incomingData.getTicketIds();
        if (!incomingTicketIds.contains(nz.co.scuff.data.util.Constants.LONG_COLLECTION_NOT_RETRIEVED)) {
            Iterator itr = existingData.getTickets().iterator();
            while (itr.hasNext()) {
                Ticket ticket = (Ticket) itr.next();
                if (!incomingTicketIds.contains(ticket.getTicketId())) {
                    itr.remove();
                    ticket.delete();
                }
            }
            for (long id : incomingTicketIds) {
                Ticket cached = getTicket(id, caches);
                if (!existingData.getTickets().contains(cached)) {
                    existingData.getTickets().add(cached);
                    cached.setJourney(existingData);
                    cached.save();
                }
            }
        }

        // ROUTE
        existingData.setRoute(getRoute(incomingData.getRouteId(), caches));
        // OWNER
        existingData.setOwner(getCoordinator(incomingData.getOwnerId(), caches));
        // AGENT
        existingData.setAgent(getCoordinator(incomingData.getAgentId(), caches));
        // GUIDE
        existingData.setGuide(getAdult(incomingData.getGuideId(), caches));
        // ORIGIN
        existingData.setOrigin(getPlace(incomingData.getOriginId(), caches));
        // DESTINATION
        existingData.setDestination(getPlace(incomingData.getDestinationId(), caches));

        // save
        existingData.save();

    }

    private static void doRouteRelationships(Route existingData, RouteSnapshot incomingData,
                                             Map<String, Map> caches) {
        if (D) Log.d(TAG, "route relationships existing=" + existingData +" snapshot="+incomingData);

        // COORDINATOR
        Coordinator incomingCoordinator = getCoordinator(incomingData.getOwnerId(), caches);
        existingData.setOwner(incomingCoordinator);
        incomingCoordinator.getRoutes().add(existingData);
        incomingCoordinator.save();
        // ORIGIN
        existingData.setOrigin(getPlace(incomingData.getOriginId(), caches));
        // DESTINATION
        existingData.setDestination(getPlace(incomingData.getDestinationId(), caches));

        // save
        existingData.save();

    }

    private static void doChildrenRelationships(Child existingData, ChildSnapshot incomingData,
                                                Map<String, Map> caches) {
        if (D) Log.d(TAG, "children relationships existing=" + existingData +" snapshot="+incomingData);

        // INSTITUTION DATA
        ChildData data = incomingData.getChildData();
        //data.setChild(existingData);
        data.save();
        existingData.setChildData(data);

        // TICKETS
        Set<Long> incomingTicketIds = incomingData.getTicketIds();
        if (!incomingTicketIds.contains(nz.co.scuff.data.util.Constants.LONG_COLLECTION_NOT_RETRIEVED)) {
            Iterator itr = existingData.getTickets().iterator();
            while (itr.hasNext()) {
                Ticket ticket = (Ticket) itr.next();
                if (!incomingTicketIds.contains(ticket.getTicketId())) {
                    itr.remove();
                    ticket.delete();
                }
            }
            for (long id : incomingTicketIds) {
                Ticket cached = getTicket(id, caches);
                if (!existingData.getTickets().contains(cached)) {
                    existingData.getTickets().add(cached);
                    cached.setChild(existingData);
                    cached.save();
                }
            }
        }

        existingData.save();

    }

    private static void doAdultRelationships(Coordinator existingData, AdultSnapshot incomingData,
                                             Map<String, Map> caches) {
        if (D) Log.d(TAG, "adult relationships existing=" + existingData +" snapshot="+incomingData);

        // PERSONAL DATA
        PersonalData data = incomingData.getPersonalData();
        //data.setAdult(existingData);
        data.save();
        existingData.setPersonalData(data);

        // CHILDREN
        Set<Long> incomingChildIds = incomingData.getChildrenIds();
        if (!incomingChildIds.contains(nz.co.scuff.data.util.Constants.LONG_COLLECTION_NOT_RETRIEVED)) {

            Iterator itr = existingData.getChildrenRelationships().iterator();
            while (itr.hasNext()) {
                ParentalRelationship relationship = (ParentalRelationship) itr.next();
                Child cached = getChild(relationship.getChild().getChildId(), caches);
                if (!incomingChildIds.contains(cached.getChildId())) {
                    itr.remove();
                    cached.getParentRelationships().remove(relationship);
                    cached.save();
                    relationship.delete();
                }
            }
            for (long id : incomingChildIds) {
                Child cached = getChild(id, caches);
                ParentalRelationship relationship = new ParentalRelationship(existingData, cached);
                if (!existingData.getChildrenRelationships().contains(relationship)) {
                    relationship.save();
                    existingData.getChildrenRelationships().add(relationship);
                    cached.getParentRelationships().add(relationship);
                    cached.save();
                }
            }
        }
        // GUIDEES
        Set<Long> incomingGuideeIds = incomingData.getGuideeIds();
        if (!incomingGuideeIds.contains(nz.co.scuff.data.util.Constants.LONG_COLLECTION_NOT_RETRIEVED)) {

            Iterator itr = existingData.getGuideeRelationships().iterator();
            while (itr.hasNext()) {
                GuidingRelationship relationship = (GuidingRelationship) itr.next();
                Coordinator cached = getInstitution(relationship.getInstitution().getCoordinatorId(), caches);
                if (!incomingGuideeIds.contains(cached.getCoordinatorId())) {
                    itr.remove();
                    cached.getGuideRelationships().remove(relationship);
                    cached.save();
                    relationship.delete();
                }
            }
            for (long id : incomingGuideeIds) {
                Coordinator cached = getInstitution(id, caches);
                GuidingRelationship relationship = new GuidingRelationship(existingData, cached);
                if (!existingData.getGuideeRelationships().contains(relationship)) {
                    relationship.save();
                    existingData.getGuideeRelationships().add(relationship);
                    cached.getGuideRelationships().add(relationship);
                    cached.save();
                }
            }
        }

        doCoordinatorRelationships(existingData, incomingData, caches);

        existingData.save();

    }

    private static void doInstitutionRelationships(Coordinator existingData, InstitutionSnapshot incomingData,
                                                   Map<String, Map> caches) {
        if (D) Log.d(TAG, "institution relationships existing=" + existingData +" snapshot="+incomingData);

        // INSTITUTION DATA
        InstitutionData data = incomingData.getInstitutionData();
        //data.setInstitution(existingData);
        data.save();
        existingData.setInstitutionData(data);

        // GUIDES
        Set<Long> incomingGuideIds = incomingData.getGuideIds();
        if (!incomingGuideIds.contains(nz.co.scuff.data.util.Constants.LONG_COLLECTION_NOT_RETRIEVED)) {

            Iterator itr = existingData.getGuideRelationships().iterator();
            while (itr.hasNext()) {
                GuidingRelationship relationship = (GuidingRelationship) itr.next();
                Coordinator cached = getAdult(relationship.getAdult().getCoordinatorId(), caches);
                if (!incomingGuideIds.contains(cached.getCoordinatorId())) {
                    itr.remove();
                    cached.getGuideeRelationships().remove(relationship);
                    cached.save();
                    relationship.delete();
                }
            }
            for (long id : incomingGuideIds) {
                Coordinator cached = getAdult(id, caches);
                GuidingRelationship relationship = new GuidingRelationship(cached, existingData);
                if (!existingData.getGuideRelationships().contains(relationship)) {
                    relationship.save();
                    existingData.getGuideRelationships().add(relationship);
                    cached.getGuideeRelationships().add(relationship);
                    cached.save();
                }
            }
        }

        doCoordinatorRelationships(existingData, incomingData, caches);

        existingData.save();

    }

    private static void doCoordinatorRelationships(Coordinator existingData, CoordinatorSnapshot incomingData,
                                                   Map<String, Map> caches) {
        if (D) Log.d(TAG, "adult relationships existing=" + existingData +" snapshot="+incomingData);

        // ROUTES
        Set<Long> incomingRouteIds = incomingData.getRouteIds();
        if (!incomingRouteIds.contains(nz.co.scuff.data.util.Constants.LONG_COLLECTION_NOT_RETRIEVED)) {
            Iterator itr = existingData.getRoutes().iterator();
            while (itr.hasNext()) {
                Route route = (Route) itr.next();
                if (!incomingRouteIds.contains(route.getRouteId())) {
                    itr.remove();
                    route.delete();
                }
            }
            for (long id : incomingRouteIds) {
                Route cached = getRoute(id, caches);
                if (!existingData.getRoutes().contains(cached)) {
                    existingData.getRoutes().add(cached);
                    cached.setOwner(existingData);
                    cached.save();
                }
            }
        }
        // PLACES
        Set<Long> incomingPlaceIds = incomingData.getPlaceIds();
        if (!incomingPlaceIds.contains(nz.co.scuff.data.util.Constants.LONG_COLLECTION_NOT_RETRIEVED)) {

            Iterator itr = existingData.getPlaceRelationships().iterator();
            while (itr.hasNext()) {
                PlaceRelationship relationship = (PlaceRelationship) itr.next();
                Place cached = getPlace(relationship.getPlace().getPlaceId(), caches);
                if (!incomingPlaceIds.contains(cached.getPlaceId())) {
                    itr.remove();
                    relationship.delete();
                }
            }
            for (long id : incomingPlaceIds) {
                Place cached = getPlace(id, caches);
                PlaceRelationship relationship = new PlaceRelationship(existingData, cached);
                if (!existingData.getPlaceRelationships().contains(relationship)) {
                    relationship.save();
                    existingData.getPlaceRelationships().add(relationship);
                    //cached.save();
                }
            }
        }
        // FRIENDS
        Set<Long> incomingFriendIds = incomingData.getFriendIds();
        if (!incomingFriendIds.contains(nz.co.scuff.data.util.Constants.LONG_COLLECTION_NOT_RETRIEVED)) {

            Iterator itr = existingData.getFriendRelationships().iterator();
            while (itr.hasNext()) {
                FriendRelationship relationship = (FriendRelationship) itr.next();
                long friendId = relationship.getCoordinator1().getCoordinatorId() != existingData.getCoordinatorId() ?
                        relationship.getCoordinator1().getCoordinatorId() : relationship.getCoordinator2().getCoordinatorId();
                Coordinator cached = getCoordinator(friendId, caches);
                if (!incomingFriendIds.contains(cached.getCoordinatorId())) {
                    itr.remove();
                    cached.getFriendRelationships().remove(relationship);
                    cached.save();
                    relationship.delete();
                }
            }
            for (long id : incomingFriendIds) {
                Coordinator cached = getCoordinator(id, caches);
                FriendRelationship relationship = new FriendRelationship(existingData, cached);
                if (!existingData.getFriendRelationships().contains(relationship)) {
                    relationship.save();
                    existingData.getFriendRelationships().add(relationship);
                    //cached.save();
                }
            }
        }
        // PAST JOURNEYS
        Set<String> incomingJourneyIds = incomingData.getPastJourneyIds();
        if (!incomingJourneyIds.contains(nz.co.scuff.data.util.Constants.STRING_COLLECTION_NOT_RETRIEVED)) {

            for (String id : incomingJourneyIds) {
                Journey cached = getJourney(id, caches);
                JourneyRelationship relationship = new JourneyRelationship(existingData, cached);
                if (!existingData.getPastJourneyRelationships().contains(relationship)) {
                    relationship.save();
                    existingData.getPastJourneyRelationships().add(relationship);
                    //cached.save();
                }
            }
        }
        // PAST JOURNEYS
        incomingJourneyIds = incomingData.getCurrentJourneyIds();
        if (!incomingJourneyIds.contains(nz.co.scuff.data.util.Constants.STRING_COLLECTION_NOT_RETRIEVED)) {

            for (String id : incomingJourneyIds) {
                Journey cached = getJourney(id, caches);
                JourneyRelationship relationship = new JourneyRelationship(existingData, cached);
                if (!existingData.getCurrentJourneyRelationships().contains(relationship)) {
                    relationship.save();
                    existingData.getCurrentJourneyRelationships().add(relationship);
                    //cached.save();
                }
            }
        }

    }

    public static List<Journey> getActiveJourneys(long coordinatorId) {
        if (D) Log.d(TAG, "get active journeys for adult=" + coordinatorId);

        // fresh journey search
        ScuffServerInterface client = ServerInterfaceGenerator.createService(ScuffServerInterface.class, Constants.SERVER_URL);
        DataPacket packet = client.getActiveJourneys(coordinatorId);
        Map<String, Map> organised = syncData(packet);
        Map<String, Journey> journeys = organised.get(JO);
        return new ArrayList<>(journeys.values());
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
            // check for adult
            Coordinator adult = Coordinator.findByPersonId(js.getCoordinator1().getPersonId());
            if (adult == null) {
                adult = new Coordinator(js.getCoordinator1());
                adult.save();
            }
            journey.setCoordinator1(adult);
            // already have school
            Institution school = Institution.findByCoordinatorId(js.getInstitution().getInstitutionId());
            journey.setInstitution(school);
            // waypoints
            WaypointSnapshot ws = js.getWaypointIds().first();
            Waypoint waypoint = new Waypoint(ws);
            waypoint.save();
            journey.getWaypointIds().add(waypoint);
            journey.save();
            journeys.add(journey);
        }
        return journeys;

    }*/

    public static List<Ticket> requestTickets(String journeyId, List<Long> passengerIds) throws ResourceNotFoundException {
        if (D) Log.d(TAG, "requestTickets for journey="+journeyId+" and ids=" + passengerIds);

        // TODO ensure only one request per child per journey (done at server currently)
        ScuffServerInterface client = ServerInterfaceGenerator.createService(ScuffServerInterface.class, Constants.SERVER_URL, new DefaultErrorHandler());
        DataPacket packet = client.requestTickets(journeyId, passengerIds);
        Map<String, Map> organised = syncData(packet);
        return (List<Ticket>)organised.get(TI).values();

    }

    /*public static List<Ticket> requestTickets(String journeyId, List<Long> passengerIds) throws ResourceNotFoundException {
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
            ticket.setChild(Child.findByPersonId(ts.getChildId()));
            ticket.save();
            if (D) Log.d(TAG, "saving ticket="+ticket);
            tickets.add(ticket);
        }
        journey.save();
        return tickets;
    }*/

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

    /*public static List<Institution> getSchools(double latitude, double longitude) {
        if (D) Log.d(TAG, "getInstitutions lat="+latitude+" long="+longitude);

        ScuffServerInterface client = ServerInterfaceGenerator.createService(ScuffServerInterface.class, Constants.SERVER_URL);
        DataPacket packet = client.getSchools(latitude, longitude, Constants.DEFAULT_RADIUS);
        Map<Long, InstitutionSnapshot> schoolMap = packet.getSchoolSnapshots();
        Map<Long, RouteSnapshot> routeMap = packet.getRouteSnapshots();

        List<Institution> institutions = new ArrayList<>();
        for (Long sk : schoolMap.keySet()) {
            InstitutionSnapshot ss = schoolMap.get(sk);
            Institution institution = new Institution();
            institution.refresh(ss);
            for (Long rId : ss.getRouteIds()) {
                RouteSnapshot rs = routeMap.get(rId);
                Route route = new Route();
                route.refresh(rs);
                institution.getRoutes().add(route);
            }
            institutions.add(institution);
        }
        return institutions;
    }*/

    public static Coordinator getUser(String email) throws ResourceNotFoundException {
        if (D) Log.d(TAG, "getUser by email=" + email);

        // check locally first
        Coordinator currentCoordinator = Coordinator.findByEmail(email);
        // if found, request changes from server. if not found retrieve user
        long lastModified = currentCoordinator == null ? 0L : currentCoordinator.getLastModified().getTime();
        ScuffServerInterface client = ServerInterfaceGenerator.createService(ScuffServerInterface.class, Constants.SERVER_URL);
        DataPacket packet = client.getDriver(email, lastModified);
        if (D) Log.d(TAG, "received data packet=" + packet);
        syncData(packet);

        // retrieve refreshed adult from database
        return Coordinator.findByEmail(email);
    }

    /*public static Coordinator getUser(String email) throws ResourceNotFoundException {
        if (D) Log.d(TAG, "getDriverId by email=" + email);

        // check locally first
        Coordinator currentAdult = Coordinator.findDriverByEmail(email);
        // not cached so full load from server (first time)
        // retrieving
        // Coordinator - Routes
        //        - Schools
        //        - Children
        // Passenger - Routes
        //           - Schools
        //           - Parents
        ScuffServerInterface client = ServerInterfaceGenerator.createService(ScuffServerInterface.class, Constants.SERVER_URL);
        DataPacket packet = client.getUser(email, currentAdult == null ? 0L : currentAdult.getLastModified().getTime());

        Map<Long, InstitutionSnapshot> schoolSnapshots = packet.getSchoolSnapshots();
        Map<Long, RouteSnapshot> routeSnapshots = packet.getRouteSnapshots();
        Map<Long, ChildSnapshot> passengerSnapshots = packet.getPassengerSnapshots();
        Map<Long, AdultSnapshot> driverSnapshots = packet.getDriverSnapshots();

        Map<Long, Institution> cachedSchools = new HashMap<>();
        Map<Long, Route> cachedRoutes = new HashMap<>();
        Map<Long, Child> cachedPassengers = new HashMap<>();
        Map<Long, Coordinator> cachedDrivers = new HashMap<>();

        // current adult cache
        AdultSnapshot currentAdultSnapshot = new AdultSnapshot();
        Timestamp now = new Timestamp(DateTimeUtils.currentTimeMillis());

        // save the entity data
        for (long driverId : driverSnapshots.keySet()) {
            AdultSnapshot ds = driverSnapshots.get(driverId);
            Coordinator d = Coordinator.findDriverByEmail(ds.getEmail());
            if (d == null) {
                d = new Coordinator();
            }
            d.refresh(ds);
            d.setLastModified(now);
            d.save();
            cachedDrivers.put(driverId, d);
            if (d.getEmail().equals(email)) {
                // this is me
                currentAdult = d;
                currentAdultSnapshot = ds;
            }
        }
        for (long schoolId : schoolSnapshots.keySet()) {
            InstitutionSnapshot ss = schoolSnapshots.get(schoolId);
            Institution s = Institution.findByCoordinatorId(ss.getSchoolId());
            if (s == null) {
                s = new Institution();
            }
            s.refresh(ss);
            s.setLastModified(now);
            s.save();
            cachedSchools.put(schoolId, s);
        }
        for (long routeId : routeSnapshots.keySet()) {
            RouteSnapshot rs = routeSnapshots.get(routeId);
            Route r = Route.findByRouteId(rs.getRouteId());
            if (r == null) {
                r = new Route();
            }
            r.refresh(rs);
            r.setLastModified(now);
            r.save();
            cachedRoutes.put(routeId, r);
        }
        for (long passengerId : passengerSnapshots.keySet()) {
            ChildSnapshot ps = passengerSnapshots.get(passengerId);
            Child p = Child.findByPersonId(ps.getPersonId());
            if (p == null) {
                p = new Child();
            }
            p.refresh(ps);
            p.setLastModified(now);
            p.save();
            cachedPassengers.put(passengerId, p);
        }

        assert (currentAdult != null);

        // drivers schools
        for (long schoolId : currentAdultSnapshot.getSchoolIdsDrivenFor()) {
            Institution institution = cachedSchools.get(schoolId);
            assert (institution != null);
            FriendRelationship friendRelationship = new FriendRelationship(currentAdult, institution);
            friendRelationship.save();
            institution.getCoordinatorCoordinators().add(friendRelationship);
            currentAdult.getDriverSchools().add(friendRelationship);
            institution.save();
        }

        // drivers routes
        for (long routeId : currentAdultSnapshot.getRegisteredRouteIds()) {
            Route route = cachedRoutes.get(routeId);
            assert (route != null);
            Institution institution = cachedSchools.get(routeSnapshots.get(routeId).getSchoolId());
            assert (institution != null);
            route.setInstitution(institution);
            route.save();
            institution.getRoutes().add(route);
            institution.save();
            PlaceRelationship placesRelationship = new PlaceRelationship(currentAdult, route);
            placesRelationship.save();
            currentAdult.getCoordinatorRoutes().add(placesRelationship);
        }

        // drivers journeys
        // ON DEMAND - each time accessed display cached entries and request new entries from server

        // drivers children
        for (long passengerId : currentAdultSnapshot.getChildrenIds()) {
            Child child = cachedPassengers.get(passengerId);
            assert (child != null);
            ParentalRelationship parentalRelationship = new ParentalRelationship(currentAdult, child);
            parentalRelationship.save();
            child.getParents().add(parentalRelationship);
            currentAdult.getChildren().add(parentalRelationship);

            // childrens schools
            for (long schoolId : passengerSnapshots.get(passengerId).getSchoolIds()) {
                Institution institution = cachedSchools.get(schoolId);
                assert (institution != null);
                PassengerSchool passengerSchool = new PassengerSchool(child, institution);
                passengerSchool.save();
                child.getPassengerSchools().add(passengerSchool);
                institution.getPassengerSchools().add(passengerSchool);
                institution.save();
            }
            // childrens parents
            for (long parentId : passengerSnapshots.get(passengerId).getParentIds()) {
                Coordinator parent = cachedDrivers.get(parentId);
                assert (parent != null);
                ParentalRelationship passengerParent = new ParentalRelationship(parent, child);
                passengerParent.save();
                child.getParents().add(passengerParent);
                parent.getChildren().add(passengerParent);
                parent.save();
            }
            // childrens routes
            for (long routeId : passengerSnapshots.get(passengerId).getRegisteredRouteIds()) {
                Route route = cachedRoutes.get(routeId);
                assert (route != null);
                PassengerRoute passengerRoute = new PassengerRoute(child, route);
                passengerRoute.save();
                child.getPassengerRoutes().add(passengerRoute);
            }
            // childrens journeys/boarding info
            // ON DEMAND - each time accessed display cached entries and request new entries from server

            // update passenger
            child.save();
        }

        currentAdult.save();

        return currentAdult;
    }*/

        /*// not cached so load from server (first time)
        AdultSnapshot driverSnapshot = client.getDriverSnapshotByEmail(email);
        adult = new Coordinator(driverSnapshot);
        adult.save();

        // drivers schools
        for (InstitutionSnapshot schoolSnapshot : driverSnapshot.getSchoolsDrivenFor()) {
            Institution school = Institution.findByCoordinatorId(schoolSnapshot.getInstitutionId());
            if (school == null) {
                school = new Institution(schoolSnapshot);
                school.save();
            }
            FriendRelationship driverSchool = new FriendRelationship(adult, school);
            driverSchool.save();
            school.getCoordinatorCoordinators().add(driverSchool);
            adult.getCoordinatorCoordinators().add(driverSchool);
            school.save();
        }

        // drivers children
        for (PassengerSnapshot passengerSnapshot : driverSnapshot.getChildren()) {
            Passenger passenger = Passenger.findByPersonId(passengerSnapshot.getPersonId());
            if (passenger == null) {
                passenger = new Passenger(passengerSnapshot);
                passenger.save();
            }
            DriverPassenger driverPassenger = new DriverPassenger(adult, passenger);
            driverPassenger.save();
            passenger.getChildren().add(driverPassenger);
            adult.getChildren().add(driverPassenger);

            // childrens schools
            for (InstitutionSnapshot schoolSnapshot : passengerSnapshot.getInstitutions()) {
                // check db first
                Institution school = Institution.findByCoordinatorId(schoolSnapshot.getInstitutionId());
                if (school == null) {
                    school = new Institution(schoolSnapshot);
                    school.save();
                }
                PassengerSchool passengerSchool = new PassengerSchool(passenger, school);
                passenger.getPassengerSchools().add(passengerSchool);
                school.getPassengerSchools().add(passengerSchool);
                school.save();
            }
            // childrens parents
            for (AdultSnapshot otherDriverSnapshot : passengerSnapshot.getParents()) {
                // if this isn't me
                if (otherDriverSnapshot.getPersonId() != adult.getPersonId()) {
                    Coordinator otherParent = Coordinator.findByPersonId(otherDriverSnapshot.getPersonId());
                    if (otherParent == null) {
                        otherParent = new Coordinator(otherDriverSnapshot);
                        otherParent.save();
                    }
                    DriverPassenger passengerOtherParent = new DriverPassenger(otherParent, passenger);
                    passengerOtherParent.save();
                    passenger.getChildren().add(passengerOtherParent);
                    otherParent.getChildren().add(passengerOtherParent);
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
            for (JourneySnapshot journeySnapshot : passengerSnapshot.getChildren()) {
                Journey journey = new Journey(journeySnapshot);
                journey.save();
                Institution jSchool = Institution.findByCoordinatorId(journeySnapshot.getInstitution().getInstitutionId());
                if (jSchool == null) {
                    jSchool = new Institution(journeySnapshot.getInstitution());
                    jSchool.save();
                }
                journey.setInstitution(jSchool);
                jSchool.getChildren().add(journey);
                jSchool.save();
                Coordinator jDriver = Coordinator.findByPersonId(journeySnapshot.getCoordinator1().getPersonId());
                if (jDriver == null) {
                    jDriver = new Coordinator(journeySnapshot.getCoordinator1());
                    jDriver.save();
                }
                journey.setCoordinator1(jDriver);
                jDriver.getChildren().add(journey);
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
        for (JourneySnapshot journeySnapshot : driverSnapshot.getChildren()) {
            Journey dJourney = Journey.findByJourneyId(journeySnapshot.getJourneyId());
            if (dJourney == null) {
                dJourney = new Journey(journeySnapshot);
                dJourney.save();
            }
            Institution jSchool = Institution.findByCoordinatorId(journeySnapshot.getInstitution().getInstitutionId());
            if (jSchool == null) {
                jSchool = new Institution(journeySnapshot.getInstitution());
                jSchool.save();
            }
            dJourney.setInstitution(jSchool);
            jSchool.getChildren().add(dJourney);
            jSchool.save();
            // adult is me (and im in the db)
            dJourney.setCoordinator1(adult);
            adult.getChildren().add(dJourney);
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
            Institution rSchool = Institution.findByCoordinatorId(routeSnapshot.getInstitution().getInstitutionId());
            dRoute.setInstitution(rSchool);
            dRoute.save();
            rSchool.getRoutes().add(dRoute);
            rSchool.save();
            PlaceRelationship driverRoute = new PlaceRelationship(adult, dRoute);
            driverRoute.save();
            adult.getCoordinatorRoutes().add(driverRoute);
        }
        adult.save();

        return currentAdult;
        */

}
