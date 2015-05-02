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
import nz.co.scuff.data.journey.Snapshot;
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

    public static long saveJourney(Journey journey) {
        if (D) Log.d(TAG, "save journey="+journey);
        return journey.save();
    }

    public static long saveSnapshot(Snapshot snapshot) {
        if (D) Log.d(TAG, "save journey snapshot="+ snapshot);
        return snapshot.save();
    }

    public static void deleteSnapshot(Snapshot snapshot) {
        if (D) Log.d(TAG, "delete journey snapshot="+ snapshot);
        snapshot.delete();
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
    public static Snapshot getSnapshot(String journeyId) {
        if (D) Log.d(TAG, "get snapshot for journey=" + journeyId);

        ScuffServerInterface client = ServerInterfaceGenerator.createService(ScuffServerInterface.class, Constants.SERVER_URL);
        return client.getSnapshot(journeyId);
    }

    public static List<Snapshot> getSnapshots(String routeId, String schoolId) {
        if (D) Log.d(TAG, "get snapshots for route=" + routeId + " school=" + schoolId);

        ScuffServerInterface client = ServerInterfaceGenerator.createService(ScuffServerInterface.class, Constants.SERVER_URL);
        return client.getSnapshotsByRouteAndSchool(routeId, schoolId);
    }

/*    public static Waypoint getLastKnownLocation(Journey journey) {
        if (D) Log.d(TAG, "get current waypoint for journey="+journey);

        PassengerServerInterface client = ServerInterfaceGenerator.createService(PassengerServerInterface.class, Constants.SERVER_URL);
        return client.getLastKnownLocation(journey.getRouteId(), journey.getSchoolId());
    }*/

/*    public static List<Waypoint> getLastLocation(String journeyId) {
        if (D) Log.d(TAG, "get last location for journey="+journeyId);

        ScuffServerInterface client = ServerInterfaceGenerator.createService(ScuffServerInterface.class, Constants.SERVER_URL);
        return client.getLastLocation(journeyId);

    }*/
}
