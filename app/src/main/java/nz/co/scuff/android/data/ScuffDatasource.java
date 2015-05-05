package nz.co.scuff.android.data;

import android.location.Location;
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.Seconds;

import java.sql.Timestamp;
import java.util.List;

import nz.co.scuff.android.util.Constants;
import nz.co.scuff.android.util.ScuffApplication;
import nz.co.scuff.android.util.ServerInterfaceGenerator;
import nz.co.scuff.data.family.Child;
import nz.co.scuff.data.family.ChildParent;
import nz.co.scuff.data.family.snapshot.ChildSnapshot;
import nz.co.scuff.data.family.Parent;
import nz.co.scuff.data.family.snapshot.ParentSnapshot;
import nz.co.scuff.data.journey.JourneySnapshot;
import nz.co.scuff.data.school.ChildSchool;
import nz.co.scuff.data.school.ParentRoute;
import nz.co.scuff.data.school.ParentSchool;
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

    public static int startJourney(Location location) {
        Journey journey = ((ScuffApplication) ScuffApplication.getContext()).getJourney();
        if (D) Log.d(TAG, "startJourney journey="+journey+" location="+location);

        //journey.save();
        Waypoint waypoint = new Waypoint(journey.getJourneyId()+":"+DateTimeUtils.currentTimeMillis(),
                0, 0, TrackingState.RECORDING, location);
        waypoint.setJourney(journey);
        waypoint.save();

        journey.addWaypoint(waypoint);
        journey.save();

        if (D) Log.d(TAG, "posting journey="+journey);

        ScuffServerInterface client = ServerInterfaceGenerator.createService(ScuffServerInterface.class, Constants.SERVER_URL);
        client.postJourney(journey, new Callback<Response>() {
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

        journey.addWaypoint(newWaypoint);
        journey.setTotalDistance(journey.getTotalDistance() + newWaypoint.getDistance());
        journey.setTotalDuration(journey.getTotalDuration() + newWaypoint.getDuration());
        journey.setState(TrackingState.PAUSED);
        journey.save();

        // populate journey POJO
        Journey transferJourney = new Journey();
        transferJourney.setJourneyId(journey.getJourneyId());
        transferJourney.setTotalDistance(journey.getTotalDistance());
        transferJourney.setTotalDuration(journey.getTotalDuration());
        transferJourney.setState(journey.getState());
        transferJourney.addWaypoint(newWaypoint);

        ScuffServerInterface client = ServerInterfaceGenerator.createService(ScuffServerInterface.class, Constants.SERVER_URL);
        client.postJourney(transferJourney, new Callback<Response>() {
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

        journey.addWaypoint(newWaypoint);
        journey.setTotalDistance(journey.getTotalDistance() + newWaypoint.getDistance());
        journey.setTotalDuration(journey.getTotalDuration() + newWaypoint.getDuration());
        journey.setState(TrackingState.RECORDING);
        journey.save();

        // populate journey POJO
        Journey transferJourney = new Journey();
        transferJourney.setJourneyId(journey.getJourneyId());
        transferJourney.setTotalDistance(journey.getTotalDistance());
        transferJourney.setTotalDuration(journey.getTotalDuration());
        transferJourney.setState(journey.getState());
        transferJourney.addWaypoint(newWaypoint);

        ScuffServerInterface client = ServerInterfaceGenerator.createService(ScuffServerInterface.class, Constants.SERVER_URL);
        client.postJourney(transferJourney, new Callback<Response>() {
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

        journey.addWaypoint(newWaypoint);
        journey.setCompleted(new Timestamp(DateTimeUtils.currentTimeMillis()));
        journey.setTotalDistance(journey.getTotalDistance() + newWaypoint.getDistance());
        journey.setTotalDuration(journey.getTotalDuration() + newWaypoint.getDuration());
        journey.setState(TrackingState.COMPLETED);
        journey.save();

        // clear cache
        ((ScuffApplication) ScuffApplication.getContext()).setJourney(null);

        // populate journey POJO
        Journey transferJourney = new Journey();
        transferJourney.setJourneyId(journey.getJourneyId());
        transferJourney.setCompleted(journey.getCompleted());
        transferJourney.setTotalDistance(journey.getTotalDistance());
        transferJourney.setTotalDuration(journey.getTotalDuration());
        transferJourney.setState(journey.getState());
        transferJourney.addWaypoint(newWaypoint);

        ScuffServerInterface client = ServerInterfaceGenerator.createService(ScuffServerInterface.class, Constants.SERVER_URL);
        client.postJourney(transferJourney, new Callback<Response>() {
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

        journey.addWaypoint(newWaypoint);
        journey.setTotalDistance(journey.getTotalDistance() + newWaypoint.getDistance());
        journey.setTotalDuration(journey.getTotalDuration() + newWaypoint.getDuration());
        journey.save();

        // populate journey POJO
        Journey transferJourney = new Journey();
        transferJourney.setJourneyId(journey.getJourneyId());
        transferJourney.setTotalDistance(journey.getTotalDistance());
        transferJourney.setTotalDuration(journey.getTotalDuration());
        transferJourney.setState(journey.getState());
        transferJourney.addWaypoint(newWaypoint);

        ScuffServerInterface client = ServerInterfaceGenerator.createService(ScuffServerInterface.class, Constants.SERVER_URL);
        client.postJourney(transferJourney, new Callback<Response>() {
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

    // may be null (esp if tracking a bus and then bus completes journey
    public static JourneySnapshot getSnapshot(String journeyId) {
        if (D) Log.d(TAG, "get snapshot for journey=" + journeyId);

        ScuffServerInterface client = ServerInterfaceGenerator.createService(ScuffServerInterface.class, Constants.SERVER_URL);
        return client.getSnapshot(journeyId);
    }

    public static List<JourneySnapshot> getSnapshots(String routeId, String schoolId) {
        if (D) Log.d(TAG, "get snapshots for route=" + routeId + " school=" + schoolId);

        ScuffServerInterface client = ServerInterfaceGenerator.createService(ScuffServerInterface.class, Constants.SERVER_URL);
        return client.getSnapshotsByRouteAndSchool(routeId, schoolId);
    }

    public static long saveJourney(Journey journey) {
        if (D) Log.d(TAG, "save journey="+journey);
        return journey.save();
    }

    public static long saveSnapshot(JourneySnapshot snapshot) {
        if (D) Log.d(TAG, "save journey snapshot="+ snapshot);
        return snapshot.save();
    }

    public static void deleteSnapshot(JourneySnapshot snapshot) {
        if (D) Log.d(TAG, "delete journey snapshot="+ snapshot);
        snapshot.delete();
    }

    public static Parent getParentByEmail(String email) {
        if (D) Log.d(TAG, "getParent by email="+email);

        ScuffServerInterface client = ServerInterfaceGenerator.createService(ScuffServerInterface.class, Constants.SERVER_URL);
        ParentSnapshot parentSnapshot = client.getParentByEmail(email);

        Parent parent = Parent.findParentByPK(parentSnapshot.getParentId());
        if (parent != null) {
            // already in db
            return parent;
        }
        parent = new Parent(parentSnapshot);
        parent.save();

        for (SchoolSnapshot schoolSnapshot : parentSnapshot.getSchools()) {
            School school = new School(schoolSnapshot);
            school.save();
            ParentSchool parentSchool = new ParentSchool(parent, school);
            parentSchool.save();
            school.addParentSchool(parentSchool);
            parent.addParentSchool(parentSchool);
            for (RouteSnapshot routeSnapshot : schoolSnapshot.getRoutes()) {
                Route route = new Route(routeSnapshot);
                route.setSchool(school);
                route.save();
                school.addRoute(route);
            }
            school.save();
        }

        for (ChildSnapshot childSnapshot : parentSnapshot.getChildren()) {
            Child child = new Child(childSnapshot);
            child.save();
            ChildParent childParent = new ChildParent(child, parent);
            childParent.save();
            child.addChildParent(childParent);
            parent.addChildParent(childParent);
            // all schools should already be in the db (from parent)
            for (SchoolSnapshot schoolSnapshot : childSnapshot.getSchools()) {
                School foundSchool = School.findBySchoolId(schoolSnapshot.getSchoolId());
                ChildSchool childSchool = new ChildSchool(child, foundSchool);
                child.addChildSchool(childSchool);
                foundSchool.addChildSchool(childSchool);
                foundSchool.save();
            }
            // all schools should already be in the db (from parent)
            for (ParentSnapshot otherParentSnapshot : childSnapshot.getParents()) {
                // if im not the parent
                if (otherParentSnapshot.getParentId() != parent.getParentId()) {
                    Parent otherParent = new Parent(otherParentSnapshot);
                    otherParent.save();
                    ChildParent childOtherParent = new ChildParent(child, otherParent);
                    childOtherParent.save();
                    child.addChildParent(childOtherParent);
                    otherParent.addChildParent(childOtherParent);
                    otherParent.save();
                }
            }
            child.save();
        }

        // all routes should already be in the db (from school)
        for (RouteSnapshot routeSnapshot : parentSnapshot.getRoutes()) {
            Route foundRoute = Route.findByRouteId(routeSnapshot.getRouteId());
            ParentRoute parentRoute = new ParentRoute(parent, foundRoute);
            parentRoute.save();
            foundRoute.addParentRoute(parentRoute);
            parent.addParentRoute(parentRoute);
            foundRoute.save();
        }
        parent.save();

        return parent;
    }
}
