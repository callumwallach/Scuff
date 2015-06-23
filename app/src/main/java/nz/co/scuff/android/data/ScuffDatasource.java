package nz.co.scuff.android.data;

import android.location.Location;
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.Seconds;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.greenrobot.event.EventBus;
import nz.co.scuff.android.event.RecordEvent;
import nz.co.scuff.android.event.TicketEvent;
import nz.co.scuff.android.util.Constants;
import nz.co.scuff.android.util.ScuffApplication;
import nz.co.scuff.server.ServerInterfaceGenerator;
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
import nz.co.scuff.server.error.ResourceException;
import nz.co.scuff.server.error.DefaultErrorHandler;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Callum on 17/04/2015.
 */
public final class ScuffDatasource {

    private static final String TAG = "ScuffDatasource";
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
        DateTime now = DateTime.now();
        Seconds seconds = Seconds.secondsBetween(then, now);
        return seconds.getSeconds();
    }

    // TODO deal with this another way
    public static void cleanUpIncompleteJourney(Journey journey) {
        if (D) Log.d(TAG, "cleaning up journey="+journey);
        journey.delete();
    }

    public static void startJourney(Journey journey, Location location) {
        if (D) Log.d(TAG, "startJourney location="+location);

        WaypointSnapshot waypointSnapshot = new Waypoint(0, 0, TrackingState.RECORDING, location).toSnapshot();
        JourneySnapshot journeySnapshot = journey.toSnapshot();
        journeySnapshot.getWaypointIds().add(waypointSnapshot.getWaypointId());

        DataPacket outgoingData = new DataPacket();
        // both journey and waypoint ids should be -1 (precreated)
        outgoingData.getJourneySnapshots().put(journeySnapshot.getJourneyId(), journeySnapshot);
        outgoingData.getWaypointSnapshots().put(waypointSnapshot.getWaypointId(), waypointSnapshot);

        if (D) Log.d(TAG, "outgoing data="+outgoingData);
        ScuffServerInterface client = ServerInterfaceGenerator.createService(ScuffServerInterface.class, Constants.SERVER_URL);
        client.startJourney(outgoingData, new Callback<DataPacket>() {
            @Override
            public void success(DataPacket incomingData, Response response) {
                if (D) Log.d(TAG, "Start journey SUCCESS code=" + response.getStatus());
                syncData(incomingData);
                ScuffApplication application = ((ScuffApplication) ScuffApplication.getContext());
                application.setJourney(Journey.findById(incomingData.getItemOfInterest()));
                EventBus.getDefault().post(new RecordEvent(incomingData.getItemOfInterest()));
            }

            @Override
            public void failure(RetrofitError error) {
                if (D) Log.d(TAG, "Start journey FAILED code=" + error.getLocalizedMessage());
            }
        });
    }

    public static void pauseJourney(long journeyId, Location location) {
        if (D) Log.d(TAG, "pauseJourney journey="+journeyId+" location="+location);

        Journey journey = Journey.findById(journeyId);

        // update journey with new waypoint
        Waypoint lastWaypoint = journey.getCurrentWaypoint();
        Waypoint newWaypoint = new Waypoint(calculateDistance(location, lastWaypoint),
                calculateTimeSince(lastWaypoint), TrackingState.PAUSED, location);
        // save waypoint
        newWaypoint.setJourney(journey);
        newWaypoint.save();

        WaypointSnapshot waypointSnapshot = newWaypoint.toSnapshot();

        journey.getWaypoints().add(newWaypoint);
        journey.setTotalDistance(journey.getTotalDistance() + waypointSnapshot.getDistance());
        journey.setTotalDuration(journey.getTotalDuration() + waypointSnapshot.getDuration());
        journey.setState(TrackingState.PAUSED);
        journey.save();

        ((ScuffApplication) ScuffApplication.getContext()).setJourney(journey);

        JourneySnapshot journeySnapshot = journey.toSnapshot();
        journeySnapshot.getWaypointIds().add(waypointSnapshot.getWaypointId());

        DataPacket outgoingData = new DataPacket();
        outgoingData.getJourneySnapshots().put(journeySnapshot.getJourneyId(), journeySnapshot);
        outgoingData.getWaypointSnapshots().put(waypointSnapshot.getWaypointId(), waypointSnapshot);

        ScuffServerInterface client = ServerInterfaceGenerator.createService(ScuffServerInterface.class, Constants.SERVER_URL);
        client.updateJourney(journeySnapshot.getJourneyId(), outgoingData, new Callback<DataPacket>() {
            @Override
            public void success(DataPacket incomingData, Response response) {
                if (D) Log.d(TAG, "Pause journey SUCCESS code=" + response.getStatus());
                syncData(incomingData);
                //ScuffApplication application = ((ScuffApplication) ScuffApplication.getContext());
                //application.setJourney(Journey.findById(incomingData.getItemOfInterest()));
            }

            @Override
            public void failure(RetrofitError error) {
                if (D) Log.d(TAG, "Pause journey FAILED code=" + error.getLocalizedMessage());
            }
        });
    }

    public static void continueJourney(final long journeyId, Location location) {
        if (D) Log.d(TAG, "continueJourney journey="+journeyId+" location="+location);

        Journey journey = Journey.findById(journeyId);

        // update journey with new waypoint
        Waypoint newWaypoint = new Waypoint(0, 0, TrackingState.RECORDING, location);
        newWaypoint.setJourney(journey);
        newWaypoint.save();

        WaypointSnapshot waypointSnapshot = newWaypoint.toSnapshot();

        journey.getWaypoints().add(newWaypoint);
        journey.setTotalDistance(journey.getTotalDistance() + waypointSnapshot.getDistance());
        journey.setTotalDuration(journey.getTotalDuration() + waypointSnapshot.getDuration());
        journey.setState(TrackingState.RECORDING);
        journey.save();

        ((ScuffApplication) ScuffApplication.getContext()).setJourney(journey);

        JourneySnapshot journeySnapshot = journey.toSnapshot();
        journeySnapshot.getWaypointIds().add(waypointSnapshot.getWaypointId());

        DataPacket outgoingData = new DataPacket();
        outgoingData.getJourneySnapshots().put(journeySnapshot.getJourneyId(), journeySnapshot);
        outgoingData.getWaypointSnapshots().put(waypointSnapshot.getWaypointId(), waypointSnapshot);

        ScuffServerInterface client = ServerInterfaceGenerator.createService(ScuffServerInterface.class, Constants.SERVER_URL);
        client.updateJourney(journeySnapshot.getJourneyId(), outgoingData, new Callback<DataPacket>() {
            @Override
            public void success(DataPacket incomingData, Response response) {
                if (D) Log.d(TAG, "Continue journey SUCCESS code=" + response.getStatus());
                syncData(incomingData);
                //ScuffApplication application = ((ScuffApplication) ScuffApplication.getContext());
                //application.setJourney(Journey.findById(incomingData.getItemOfInterest()));
                EventBus.getDefault().post(new RecordEvent(journeyId));
            }

            @Override
            public void failure(RetrofitError error) {
                if (D) Log.d(TAG, "Continue journey FAILED code=" + error.getLocalizedMessage());
            }
        });

    }

    public static void stopJourney(long journeyId, Location location) {
        if (D) Log.d(TAG, "stopJourney journey=" + journeyId + " location=" + location);

        Journey journey = Journey.findById(journeyId);

        // update journey with new waypoint
        Waypoint lastWaypoint = journey.getCurrentWaypoint();
        Waypoint newWaypoint = new Waypoint(calculateDistance(location, lastWaypoint),
                calculateTimeSince(lastWaypoint), TrackingState.COMPLETED, location);
        newWaypoint.setJourney(journey);
        newWaypoint.save();

        WaypointSnapshot waypointSnapshot = newWaypoint.toSnapshot();

        journey.getWaypoints().add(newWaypoint);
        journey.setTotalDistance(journey.getTotalDistance() + waypointSnapshot.getDistance());
        journey.setTotalDuration(journey.getTotalDuration() + waypointSnapshot.getDuration());
        journey.setState(TrackingState.COMPLETED);
        journey.setCompleted(new Timestamp(DateTimeUtils.currentTimeMillis()));
        journey.save();

        ((ScuffApplication) ScuffApplication.getContext()).setJourney(null);

        JourneySnapshot journeySnapshot = journey.toSnapshot();
        journeySnapshot.getWaypointIds().add(waypointSnapshot.getWaypointId());

        DataPacket outgoingData = new DataPacket();
        outgoingData.getJourneySnapshots().put(journeySnapshot.getJourneyId(), journeySnapshot);
        outgoingData.getWaypointSnapshots().put(waypointSnapshot.getWaypointId(), waypointSnapshot);

        ScuffServerInterface client = ServerInterfaceGenerator.createService(ScuffServerInterface.class, Constants.SERVER_URL);
        client.updateJourney(journeySnapshot.getJourneyId(), outgoingData, new Callback<DataPacket>() {
            @Override
            public void success(DataPacket incomingData, Response response) {
                if (D) Log.d(TAG, "Stop journey SUCCESS code=" + response.getStatus());
                syncData(incomingData);
                /*ScuffApplication application = ((ScuffApplication) ScuffApplication.getContext());
                application.setJourney(null);*/
            }

            @Override
            public void failure(RetrofitError error) {
                if (D) Log.d(TAG, "Stop journey FAILED code=" + error.getLocalizedMessage());
            }
        });
    }

    // ***** would be possible race condition here so save data locally and dont store retrieved data *****
    public static List<Ticket> recordJourney(final long journeyId, Location location) {
        if (D) Log.d(TAG, "recordJourney journey="+journeyId+" location="+location);

        Journey journey = Journey.findById(journeyId);

        // update journey with new waypoint
        Waypoint lastWaypoint = journey.getCurrentWaypoint();
        // TODO date is wrong as was created on the server and now updated on client
        Waypoint newWaypoint = new Waypoint(calculateDistance(location, lastWaypoint),
                calculateTimeSince(lastWaypoint), TrackingState.RECORDING, location);
        newWaypoint.setJourney(journey);
        newWaypoint.save();

        WaypointSnapshot waypointSnapshot = newWaypoint.toSnapshot();

        journey.getWaypoints().add(newWaypoint);
        journey.setTotalDistance(journey.getTotalDistance() + waypointSnapshot.getDistance());
        journey.setTotalDuration(journey.getTotalDuration() + waypointSnapshot.getDuration());
        journey.setState(TrackingState.RECORDING);
        journey.save();

        ScuffApplication application = ((ScuffApplication) ScuffApplication.getContext());
        application.setJourney(journey);

        JourneySnapshot journeySnapshot = journey.toSnapshot();
        journeySnapshot.getWaypointIds().add(waypointSnapshot.getWaypointId());

        DataPacket outgoingData = new DataPacket();
        outgoingData.getJourneySnapshots().put(journeySnapshot.getJourneyId(), journeySnapshot);
        outgoingData.getWaypointSnapshots().put(waypointSnapshot.getWaypointId(), waypointSnapshot);

        ScuffServerInterface client = ServerInterfaceGenerator.createService(ScuffServerInterface.class, Constants.SERVER_URL);
        client.updateJourney(journeySnapshot.getJourneyId(), outgoingData, new Callback<DataPacket>() {
            @Override
            public void success(DataPacket incomingData, Response response) {
                if (D) Log.d(TAG, "Record journey SUCCESS code=" + response.getStatus());
                // should only be children and tickets
                Map<String, Map> organisedData = syncData(incomingData);
                // pass on tickets if any retrieved
                Map<Long, Ticket> receivedTickets = organisedData.get(TI);
                if (receivedTickets != null) {
                    // refresh cached journey
                    ScuffApplication application = ((ScuffApplication) ScuffApplication.getContext());
                    application.setJourney(Journey.findById(journeyId));
                    // post received tickets to ui
                    EventBus.getDefault().post(new TicketEvent(receivedTickets.values()));
                }
            }

            @Override
            public void failure(RetrofitError error) {
                if (D) Log.d(TAG, "Record journey FAILED code=" + error.getLocalizedMessage());
            }
        });
        return null;
    }

    private static Map<String, Map> syncData(DataPacket packet) {
        if (D) Log.d(TAG, "syncing data packet=" + packet);
        
        Map<Long, Coordinator> cachedCoordinators = new HashMap<>();
        Map<Long, Coordinator> cachedInstitutions = new HashMap<>();
        Map<Long, Coordinator> cachedAdults = new HashMap<>();
        Map<Long, Waypoint> cachedWaypoints = new HashMap<>();
        Map<Long, Journey> cachedJourneys = new HashMap<>();
        Map<Long, Route> cachedRoutes = new HashMap<>();
        Map<Long, Place> cachedPlaces = new HashMap<>();
        Map<Long, Child> cachedChildren = new HashMap<>();
        Map<Long, Ticket> cachedTickets = new HashMap<>();
        Map<Long, Stamp> cachedStamps = new HashMap<>();

        Map<Long, PlaceSnapshot> placeSnapshots = packet.getPlaceSnapshots();
        Map<Long, InstitutionSnapshot> institutionSnapshots = packet.getInstitutionSnapshots();
        Map<Long, RouteSnapshot> routeSnapshots = packet.getRouteSnapshots();
        Map<Long, JourneySnapshot> journeySnapshots = packet.getJourneySnapshots();
        Map<Long, WaypointSnapshot> waypointSnapshots = packet.getWaypointSnapshots();
        Map<Long, ChildSnapshot> childSnapshots = packet.getChildSnapshots();
        Map<Long, AdultSnapshot> adultSnapshots = packet.getAdultSnapshots();
        Map<Long, TicketSnapshot> ticketSnapshots = packet.getTicketSnapshots();
        Map<Long, StampSnapshot> stampSnapshots = packet.getStampSnapshots();

        Set<Long> modifiedAdultIds = new HashSet<>();
        Set<Long> modifiedInstitutionIds = new HashSet<>();
        Set<Long> modifiedChildIds = new HashSet<>();
        Set<Long> modifiedRouteIds = new HashSet<>();
        Set<Long> modifiedJourneyIds = new HashSet<>();
        Set<Long> modifiedTicketIds = new HashSet<>();

        // save/refresh entities
        // ADULTS
        for (long dId : adultSnapshots.keySet()) {
            AdultSnapshot s = adultSnapshots.get(dId);
            if (D) Log.d(TAG, "adult snapshot=" + s);
            Coordinator adult = Coordinator.findById(s.getCoordinatorId());
            if (adult == null) {
                PersonalData data = s.getPersonalData();
                data.save();
                adult = new Coordinator();
                adult.refresh(s);
                adult.setPersonalData(data);
                adult.save();
                modifiedAdultIds.add(adult.getCoordinatorId());
            } else if (s.isDirty(adult.getLastModified())) {
                PersonalData data = adult.getPersonalData();
                data.refresh(s.getPersonalData());
                data.save();
                adult.refresh(s);
                adult.setPersonalData(data);
                adult.save();
                modifiedAdultIds.add(adult.getCoordinatorId());
            }
            // not null and not dirty -> don't save
            cachedAdults.put(adult.getCoordinatorId(), adult);
            cachedCoordinators.put(adult.getCoordinatorId(), adult);
        }
        // INSTITUTIONS
        for (long iId : institutionSnapshots.keySet()) {
            InstitutionSnapshot s = institutionSnapshots.get(iId);
            if (D) Log.d(TAG, "institution snapshot=" + s);
            Coordinator institution = Coordinator.findById(s.getCoordinatorId());
            if (institution == null) {
                InstitutionData data = s.getInstitutionData();
                data.save();
                institution = new Coordinator();
                institution.refresh(s);
                institution.setInstitutionData(data);
                institution.save();
                modifiedInstitutionIds.add(institution.getCoordinatorId());
            } else if (s.isDirty(institution.getLastModified())) {
                InstitutionData data = institution.getInstitutionData();
                data.refresh(s.getInstitutionData());
                data.save();
                institution.refresh(s);
                institution.setInstitutionData(data);
                institution.save();
                modifiedInstitutionIds.add(institution.getCoordinatorId());
            }
            cachedInstitutions.put(institution.getCoordinatorId(), institution);
            cachedCoordinators.put(institution.getCoordinatorId(), institution);
        }
        // CHILDREN
        for (long cId : childSnapshots.keySet()) {
            ChildSnapshot s = childSnapshots.get(cId);
            if (D) Log.d(TAG, "child snapshot=" + s);
            Child child = Child.findById(s.getChildId());
            if (child == null) {
                ChildData data = s.getChildData();
                data.save();
                child = new Child();
                child.refresh(s);
                child.setChildData(data);
                child.save();
                modifiedChildIds.add(child.getChildId());
            } else if (s.isDirty(child.getLastModified())) {
                ChildData data = child.getChildData();
                data.refresh(s.getChildData());
                data.save();
                child.refresh(s);
                child.setChildData(data);
                child.save();
                modifiedChildIds.add(child.getChildId());
            }
            cachedChildren.put(child.getChildId(), child);
        }
        // ROUTES
        // only modified to be set active/inactive
        for (long rId : routeSnapshots.keySet()) {
            RouteSnapshot s = routeSnapshots.get(rId);
            if (D) Log.d(TAG, "route snapshot=" + s);
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
        for (long jId : journeySnapshots.keySet()) {
            JourneySnapshot s = journeySnapshots.get(jId);
            if (D) Log.d(TAG, "journey snapshot=" + s);
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
        for (long wId : waypointSnapshots.keySet()) {
            WaypointSnapshot s = waypointSnapshots.get(wId);
            if (D) Log.d(TAG, "waypoint snapshot=" + s);
            Waypoint waypoint = Waypoint.findById(s.getWaypointId());
            if (waypoint == null) {
                waypoint = new Waypoint(s);
                waypoint.save();
            }
            cachedWaypoints.put(waypoint.getWaypointId(), waypoint);
        }
        // TICKETS
        // modified with stamp
        for (long tId : ticketSnapshots.keySet()) {
            TicketSnapshot s = ticketSnapshots.get(tId);
            if (D) Log.d(TAG, "ticket snapshot=" + s);
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
        for (long pId : placeSnapshots.keySet()) {
            PlaceSnapshot s = placeSnapshots.get(pId);
            if (D) Log.d(TAG, "place snapshot=" + s);
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
        for (long pId : stampSnapshots.keySet()) {
            StampSnapshot s = stampSnapshots.get(pId);
            if (D) Log.d(TAG, "stamp snapshot=" + s);
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
        for (long id : modifiedAdultIds) {
            doAdultRelationships(cachedAdults.get(id), adultSnapshots.get(id), organised);
        }
        // INSTITUTIONS
        for (long id : modifiedInstitutionIds) {
            doInstitutionRelationships(cachedInstitutions.get(id), institutionSnapshots.get(id), organised);
        }
        // CHILDREN
        for (long id : modifiedChildIds) {
            doChildrenRelationships(cachedChildren.get(id), childSnapshots.get(id), organised);
        }
        // ROUTES
        for (long id : modifiedRouteIds) {
            doRouteRelationships(cachedRoutes.get(id), routeSnapshots.get(id), organised);
        }
        // JOURNEYS
        for (long id : modifiedJourneyIds) {
            doJourneyRelationships(cachedJourneys.get(id), journeySnapshots.get(id), organised);
        }
        // TICKETS
        for (long id : modifiedTicketIds) {
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
    
    private static Journey getJourney(long id, Map<String, Map> caches) {
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

    private static Waypoint getWaypoint(long id, Map<String, Map> caches) {
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
        Set<Long> incomingWaypointIds = incomingData.getWaypointIds();
        if (!incomingWaypointIds.contains(nz.co.scuff.data.util.Constants.LONG_COLLECTION_NOT_RETRIEVED)) {
            Iterator itr = existingData.getWaypoints().iterator();
            while (itr.hasNext()) {
                Waypoint waypoint = (Waypoint) itr.next();
                if (!incomingWaypointIds.contains(waypoint.getWaypointId())) {
                    itr.remove();
                    waypoint.delete();
                }
            }
            for (long id : incomingWaypointIds) {
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
        // CURRENT JOURNEYS
        /*
        Set<String> incomingJourneyIds = incomingData.getCurrentJourneyIds();
        if (!incomingJourneyIds.contains(nz.co.scuff.data.util.Constants.STRING_COLLECTION_NOT_RETRIEVED)) {

            Iterator itr = existingData.getCurrentJourneyRelationships().iterator();
            while (itr.hasNext()) {
                JourneyRelationship relationship = (JourneyRelationship) itr.next();
                Journey cached = getJourney(relationship.getJourney().getJourneyId(), caches);
                if (!incomingJourneyIds.contains(cached.getJourneyId())) {
                    itr.remove();
                    relationship.delete();
                }
            }
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
        // PAST JOURNEYS
        incomingJourneyIds = incomingData.getPastJourneyIds();
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
        */

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
        // TODO PARENTS

        existingData.save();

    }

    private static void doAdultRelationships(Coordinator existingData, AdultSnapshot incomingData,
                                             Map<String, Map> caches) {
        if (D) Log.d(TAG, "adult relationships existing=" + existingData +" snapshot="+incomingData);

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
        if (D) Log.d(TAG, "coordinator relationships existing=" + existingData +" snapshot="+incomingData);

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
                    cached.getFriendRelationships().add(relationship);
                    cached.save();
                }
            }
        }
        // CURRENT JOURNEYS
        Set<Long> incomingJourneyIds = incomingData.getCurrentJourneyIds();
        if (!incomingJourneyIds.contains(nz.co.scuff.data.util.Constants.LONG_COLLECTION_NOT_RETRIEVED)) {

            Iterator itr = existingData.getCurrentJourneyRelationships().iterator();
            while (itr.hasNext()) {
                JourneyRelationship relationship = (JourneyRelationship) itr.next();
                Journey cached = getJourney(relationship.getJourney().getJourneyId(), caches);
                if (!incomingJourneyIds.contains(cached.getJourneyId())) {
                    itr.remove();
                    relationship.delete();
                }
            }
            for (long id : incomingJourneyIds) {
                Journey cached = getJourney(id, caches);
                JourneyRelationship relationship = new JourneyRelationship(existingData, cached);
                if (!existingData.getCurrentJourneyRelationships().contains(relationship)) {
                    relationship.save();
                    existingData.getCurrentJourneyRelationships().add(relationship);
                    //cached.save();
                }
            }
        }
        // PAST JOURNEYS
        incomingJourneyIds = incomingData.getPastJourneyIds();
        if (!incomingJourneyIds.contains(nz.co.scuff.data.util.Constants.LONG_COLLECTION_NOT_RETRIEVED)) {

            for (long id : incomingJourneyIds) {
                Journey cached = getJourney(id, caches);
                JourneyRelationship relationship = new JourneyRelationship(existingData, cached);
                if (!existingData.getPastJourneyRelationships().contains(relationship)) {
                    relationship.save();
                    existingData.getPastJourneyRelationships().add(relationship);
                    //cached.save();
                }
            }
        }

    }

    public static Collection<Journey> getActiveJourneys(long adultId, ArrayList<Long> watchedJourneyIds) {
        if (D) Log.d(TAG, "get active journeys for adult="+adultId+" watchedJourneyIds="+watchedJourneyIds);

        // fresh journey search
        ScuffServerInterface client = ServerInterfaceGenerator.createService(ScuffServerInterface.class, Constants.SERVER_URL);
        DataPacket packet = client.getJourneys(adultId, watchedJourneyIds, true);
        Map<String, Map> organised = syncData(packet);
        Map<String, Journey> journeys = organised.get(JO);
        return journeys.values();
    }

    public static Collection<Ticket> requestTickets(long journeyId, List<Long> passengerIds) throws ResourceException {
        if (D) Log.d(TAG, "issueTickets for journey="+journeyId+" and ids=" + passengerIds);

        // TODO ensure only one request per child per journey (done at server currently)
        ScuffServerInterface client = ServerInterfaceGenerator.createService(ScuffServerInterface.class, Constants.SERVER_URL, new DefaultErrorHandler());
        DataPacket packet = client.issueTickets(journeyId, passengerIds);
        Map<String, Map> organised = syncData(packet);
        Map<String, Ticket> tickets = organised.get(TI);
        return tickets.values();

    }

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

    public static Coordinator getUser(String email) throws ResourceException {
        if (D) Log.d(TAG, "getUser by email=" + email);

        // check locally first
        Coordinator currentCoordinator = Coordinator.findByEmail(email);
        // if found, request changes from server. if not found retrieve user
        long lastModified = currentCoordinator == null ? 0L : currentCoordinator.getLastRefresh().getTime();
        ScuffServerInterface client = ServerInterfaceGenerator.createService(ScuffServerInterface.class, Constants.SERVER_URL);
        DataPacket packet = client.getDriver(email, lastModified);
        if (D) Log.d(TAG, "received data packet=" + packet);
        syncData(packet);

        // retrieve refreshed adult from database
        return Coordinator.findByEmail(email);
    }

}
