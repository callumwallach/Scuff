package nz.co.scuff.android.data;

import android.location.Location;
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.Seconds;

import java.sql.Timestamp;

import nz.co.scuff.android.util.Constants;
import nz.co.scuff.android.util.ScuffApplication;
import nz.co.scuff.android.util.ServerInterfaceGenerator;
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
public final class JourneyDatasource {

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
        client.start(journey, new Callback<Response>() {
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
        Waypoint lastWaypoint = Waypoint.getLastWaypoint(journey);
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
        client.update(transferJourney, new Callback<Response>() {
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
        client.update(transferJourney, new Callback<Response>() {
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
        Waypoint lastWaypoint = Waypoint.getLastWaypoint(journey);
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
        client.update(transferJourney, new Callback<Response>() {
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
        Waypoint lastWaypoint = Waypoint.getLastWaypoint(journey);
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
        client.update(transferJourney, new Callback<Response>() {
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

    public static Location get() {

        ScuffServerInterface client = ServerInterfaceGenerator.createService(ScuffServerInterface.class, Constants.SERVER_URL);
        //client.getWaypoint();


/*        JourneyDBHelper dbHelper = new JourneyDBHelper(ScuffApplication.getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        SharedPreferences sp = ScuffApplication.getContext().
                getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);
        String selection = JourneyContract.JourneyEntry.COLUMN_NAME_SCHOOL_ID +
                "=? and " + JourneyContract.JourneyEntry.COLUMN_NAME_ROUTE_ID +
                "=? and " + JourneyContract.JourneyEntry.COLUMN_NAME_STATE + "=?";
        String[] selectionArgs = { sp.getString(Constants.PASSENGER_SCHOOL_ID, null),
                sp.getString(Constants.PASSENGER_ROUTE_ID, null), "1"};
        if (D) Log.d(TAG, "selectionArgs=" + selectionArgs[0] + " " + selectionArgs[1]);
        String sortOrder = JourneyContract.JourneyEntry.COLUMN_NAME_TIMESTAMP + " DESC";

        Cursor cursor = db.query(
                JourneyContract.JourneyEntry.TABLE_NAME,  // The table to query
                null,                                       // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        Location location = null;
        if (cursor.moveToFirst()) {
            String provider = cursor.getString(cursor.getColumnIndexOrThrow(JourneyContract.JourneyEntry.COLUMN_NAME_PROVIDER));
            location = new Location(provider);
            location.setLatitude(Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow(JourneyContract.JourneyEntry.COLUMN_NAME_LATITUDE))));
            location.setLongitude(Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow(JourneyContract.JourneyEntry.COLUMN_NAME_LONGITUDE))));
            location.setSpeed(cursor.getFloat(cursor.getColumnIndexOrThrow(JourneyContract.JourneyEntry.COLUMN_NAME_SPEED)));
            location.setBearing(cursor.getFloat(cursor.getColumnIndexOrThrow(JourneyContract.JourneyEntry.COLUMN_NAME_BEARING)));
            location.setAccuracy(cursor.getFloat(cursor.getColumnIndexOrThrow(JourneyContract.JourneyEntry.COLUMN_NAME_ACCURACY)));
            location.setAltitude(cursor.getFloat(cursor.getColumnIndexOrThrow(JourneyContract.JourneyEntry.COLUMN_NAME_ALTITUDE)));
            location.setTime(DateTime.parse(cursor.getString(cursor.getColumnIndexOrThrow(JourneyContract.JourneyEntry.COLUMN_NAME_TIMESTAMP))).getMillis());
        }
        return location;*/
        return null;

    }
}
