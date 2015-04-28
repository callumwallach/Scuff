package nz.co.scuff.android.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.Seconds;

import java.sql.Timestamp;

import nz.co.scuff.android.util.Constants;
import nz.co.scuff.android.util.ServerInterfaceGenerator;
import nz.co.scuff.android.util.ScuffApplication;
import nz.co.scuff.android.util.TrackingState;
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

    private static final String SERVER_URL = "http://10.0.3.2:8080/scuff";

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

/*    private static float calculateTotalDistance(Location location) {

        SharedPreferences sp = ScuffApplication.getContext().
                getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        float totalDistanceInMeters = sp.getFloat(Constants.DRIVER_ACCUMULATED_DISTANCE, 0f);
        boolean initialised = sp.getBoolean(Constants.PREFERENCES_INITIALISED, false);

        if (!initialised) {
            editor.putBoolean(Constants.PREFERENCES_INITIALISED, true);
        } else {
            Location previousLocation = new Location("");
            previousLocation.setLatitude(sp.getFloat(Constants.DRIVER_PREVIOUS_LAT, 0f));
            previousLocation.setLongitude(sp.getFloat(Constants.DRIVER_PREVIOUS_LONG, 0f));

            float distance = location.distanceTo(previousLocation);
            totalDistanceInMeters += distance;
            editor.putFloat(Constants.DRIVER_ACCUMULATED_DISTANCE, totalDistanceInMeters);
        }

        editor.putFloat(Constants.DRIVER_PREVIOUS_LAT, (float)location.getLatitude());
        editor.putFloat(Constants.DRIVER_PREVIOUS_LONG, (float) location.getLongitude());
        editor.apply();

        return totalDistanceInMeters;
    }*/

    public static int startJourney(String journeyId, Location location) {
        if (D) Log.d(TAG, "startJourney location="+location);

        SharedPreferences sp = ScuffApplication.getContext()
                .getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);

/*        HashMap<String, Object> journey = new HashMap<>();
        journey.put(JourneyContract.JourneyEntry.COLUMN_NAME_JOURNEY_ID, sp.getString(Constants.DRIVER_JOURNEY_ID, "ERROR"));
        journey.put(JourneyContract.JourneyEntry.COLUMN_NAME_APP_ID, sp.getLong(Constants.DRIVER_APP_ID, -1L));
        journey.put(JourneyContract.JourneyEntry.COLUMN_NAME_SCHOOL_ID, sp.getString(Constants.DRIVER_SCHOOL_ID, "ERROR"));
        journey.put(JourneyContract.JourneyEntry.COLUMN_NAME_DRIVER_ID, sp.getString(Constants.DRIVER_DRIVER_ID, "ERROR"));
        journey.put(JourneyContract.JourneyEntry.COLUMN_NAME_ROUTE_ID, sp.getString(Constants.DRIVER_ROUTE_ID, "ERROR"));
        journey.put(JourneyContract.JourneyEntry.COLUMN_NAME_SOURCE, "Android");
        journey.put(JourneyContract.JourneyEntry.COLUMN_NAME_STATE, 1);*/

/*        journey.put(JourneyContract.JourneyEntry.COLUMN_NAME_WAYPOINT_ID, location.getLatitude());
        journey.put(JourneyContract.JourneyEntry.COLUMN_NAME_LATITUDE, location.getLatitude());
        journey.put(JourneyContract.JourneyEntry.COLUMN_NAME_LONGITUDE, location.getLongitude());
        journey.put(JourneyContract.JourneyEntry.COLUMN_NAME_SPEED, location.getSpeed());
        journey.put(JourneyContract.JourneyEntry.COLUMN_NAME_BEARING, location.getBearing());
        journey.put(JourneyContract.JourneyEntry.COLUMN_NAME_DISTANCE, calculateTotalDistance(location));
        journey.put(JourneyContract.JourneyEntry.COLUMN_NAME_PROVIDER, location.getProvider());
        journey.put(JourneyContract.JourneyEntry.COLUMN_NAME_ACCURACY, location.getAccuracy());
        journey.put(JourneyContract.JourneyEntry.COLUMN_NAME_ALTITUDE, location.getAltitude());
        journey.put(JourneyContract.JourneyEntry.COLUMN_NAME_TIMESTAMP, DateTime.now().toString(ISODateTimeFormat.dateTime()));*/

        // populate journey POJO
        /*Journey journey = new Journey();
        journey.setJourneyId(journeyId);
        journey.setAppId(sp.getLong(Constants.DRIVER_APP_ID, -1));
        journey.setSchoolId(sp.getString(Constants.DRIVER_SCHOOL_ID, "ERROR"));
        journey.setDriverId(sp.getString(Constants.DRIVER_DRIVER_ID, "ERROR"));
        journey.setRouteId(sp.getString(Constants.DRIVER_ROUTE_ID, "ERROR"));
        journey.setSource("Android");
        journey.setState(Journey.TrackingState.RECORDING);
        //journey.addWaypoint(new Waypoint(journeyId+":"+DateTimeUtils.currentTimeMillis(), 0, Constants.TRACKING_STATE_RECORDING, location));*/

        /*DateTime.now().toDate();*/

        Journey journey = new Journey(journeyId, sp.getLong(Constants.DRIVER_APP_ID, -1), sp.getString(Constants.DRIVER_SCHOOL_ID, "ERROR"),
                sp.getString(Constants.DRIVER_DRIVER_ID, "ERROR"), sp.getString(Constants.DRIVER_ROUTE_ID, "ERROR"), "Android",
                0, 0, new Timestamp(DateTimeUtils.currentTimeMillis()), null, TrackingState.RECORDING);

/*        journey.setJourneyId(journeyId);
        journey.setAppId(sp.getLong(Constants.DRIVER_APP_ID, -1));
        journey.setSchoolId(sp.getString(Constants.DRIVER_SCHOOL_ID, "ERROR"));
        journey.setDriverId(sp.getString(Constants.DRIVER_DRIVER_ID, "ERROR"));
        journey.setRouteId(sp.getString(Constants.DRIVER_ROUTE_ID, "ERROR"));
        journey.setSource("Android");
        journey.setState(Journey.TrackingState.RECORDING);
        journey.setCreated(new Timestamp(DateTimeUtils.currentTimeMillis()));*/
        journey.save();

        Waypoint waypoint = new Waypoint(journeyId+":"+DateTimeUtils.currentTimeMillis(), 0, 0,
                Constants.TRACKING_STATE_RECORDING, location);
        waypoint.setJourney(journey);
        waypoint.save();

        journey.addWaypoint(waypoint);
        journey.save();

        if (D) Log.d(TAG, "posting journey="+journey);

        ScuffServerInterface client = ServerInterfaceGenerator.createService(ScuffServerInterface.class, SERVER_URL);
        client.startJourney(journey, new Callback<Response>() {
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

    public static int pauseJourney(String journeyId, Location location) {
        if (D) Log.d(TAG, "pauseJourney location="+location);

        // update journey with new waypoint
        Journey foundJourney = Journey.findByJourneyId(journeyId);
        Waypoint lastWaypoint = Waypoint.getLastWaypoint(foundJourney);


        Waypoint newWaypoint = new Waypoint(journeyId+":"+DateTimeUtils.currentTimeMillis(),
                calculateDistance(location, lastWaypoint), calculateTimeSince(lastWaypoint),
                Constants.TRACKING_STATE_PAUSED, location);

        newWaypoint.setJourney(foundJourney);
        newWaypoint.save();

        foundJourney.addWaypoint(newWaypoint);
        foundJourney.setTotalDistance(foundJourney.getTotalDistance() + newWaypoint.getDistance());
        foundJourney.setTotalDuration(foundJourney.getTotalDuration() + newWaypoint.getDuration());
        foundJourney.setState(TrackingState.PAUSED);
        foundJourney.save();

        // populate journey POJO
        Journey journey = new Journey();
        journey.setJourneyId(journeyId);
        journey.setTotalDistance(foundJourney.getTotalDistance());
        journey.setTotalDuration(foundJourney.getTotalDuration());
        journey.setState(TrackingState.PAUSED);
        journey.addWaypoint(newWaypoint);

        ScuffServerInterface client = ServerInterfaceGenerator.createService(ScuffServerInterface.class, SERVER_URL);
        client.pauseJourney(journey, new Callback<Response>() {
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

    public static int continueJourney(String journeyId, Location location) {
        if (D) Log.d(TAG, "continueJourney location="+location);

        // update journey with new waypoint
        Journey foundJourney = Journey.findByJourneyId(journeyId);

        Waypoint newWaypoint = new Waypoint(journeyId+":"+DateTimeUtils.currentTimeMillis(),
                0, 0, Constants.TRACKING_STATE_RECORDING, location);

        newWaypoint.setJourney(foundJourney);
        newWaypoint.save();

        foundJourney.addWaypoint(newWaypoint);
        foundJourney.setTotalDistance(foundJourney.getTotalDistance() + newWaypoint.getDistance());
        foundJourney.setTotalDuration(foundJourney.getTotalDuration() + newWaypoint.getDuration());
        foundJourney.setState(TrackingState.RECORDING);
        foundJourney.save();

        // populate journey POJO
        Journey journey = new Journey();
        journey.setJourneyId(journeyId);
        journey.setTotalDistance(foundJourney.getTotalDistance());
        journey.setTotalDuration(foundJourney.getTotalDuration());
        journey.setState(TrackingState.RECORDING);
        journey.addWaypoint(newWaypoint);

        ScuffServerInterface client = ServerInterfaceGenerator.createService(ScuffServerInterface.class, SERVER_URL);
        client.continueJourney(journey, new Callback<Response>() {
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

    public static int stopJourney(String journeyId, Location location) {
        if (D) Log.d(TAG, "stopJourney location="+location);

        // update journey with new waypoint
        Journey foundJourney = Journey.findByJourneyId(journeyId);
        Waypoint lastWaypoint = Waypoint.getLastWaypoint(foundJourney);


        Waypoint newWaypoint = new Waypoint(journeyId+":"+DateTimeUtils.currentTimeMillis(),
                calculateDistance(location, lastWaypoint), calculateTimeSince(lastWaypoint),
                Constants.TRACKING_STATE_STOPPED, location);

        newWaypoint.setJourney(foundJourney);
        newWaypoint.save();

        foundJourney.addWaypoint(newWaypoint);
        foundJourney.setCompleted(new Timestamp(DateTimeUtils.currentTimeMillis()));
        foundJourney.setTotalDistance(foundJourney.getTotalDistance() + newWaypoint.getDistance());
        foundJourney.setTotalDuration(foundJourney.getTotalDuration() + newWaypoint.getDuration());
        foundJourney.setState(TrackingState.COMPLETED);
        foundJourney.save();

        // populate journey POJO
        Journey journey = new Journey();
        journey.setJourneyId(journeyId);
        journey.setCompleted(foundJourney.getCompleted());
        journey.setTotalDistance(foundJourney.getTotalDistance());
        journey.setTotalDuration(foundJourney.getTotalDuration());
        journey.setState(foundJourney.getState());
        journey.addWaypoint(newWaypoint);

        ScuffServerInterface client = ServerInterfaceGenerator.createService(ScuffServerInterface.class, SERVER_URL);
        client.stopJourney(journey, new Callback<Response>() {
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

    public static int recordJourney(String journeyId, Location location) {
        if (D) Log.d(TAG, "recordJourney location="+location);

        // update journey with new waypoint
        Journey foundJourney = Journey.findByJourneyId(journeyId);
        Waypoint lastWaypoint = Waypoint.getLastWaypoint(foundJourney);

        Waypoint newWaypoint = new Waypoint(journeyId+":"+DateTimeUtils.currentTimeMillis(),
                calculateDistance(location, lastWaypoint), calculateTimeSince(lastWaypoint),
                Constants.TRACKING_STATE_RECORDING, location);

        newWaypoint.setJourney(foundJourney);
        newWaypoint.save();

        foundJourney.addWaypoint(newWaypoint);
        foundJourney.setTotalDistance(foundJourney.getTotalDistance() + newWaypoint.getDistance());
        foundJourney.setTotalDuration(foundJourney.getTotalDuration() + newWaypoint.getDuration());
        foundJourney.save();

        // populate journey POJO
        Journey journey = new Journey();
        journey.setJourneyId(journeyId);
        //journey.setState(Journey.TrackingState.RECORDING);
        journey.setTotalDistance(foundJourney.getTotalDistance());
        journey.setTotalDuration(foundJourney.getTotalDuration());
        journey.addWaypoint(newWaypoint);

        ScuffServerInterface client = ServerInterfaceGenerator.createService(ScuffServerInterface.class, SERVER_URL);
        client.recordJourney(journey, new Callback<Response>() {
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

/*    public static int stopJourney(String journeyId, Location location) {

        if (D) Log.d(TAG, "stopJourney journeyId="+journeyId);

        // copy journey from active to completed
        JourneyDBHelper dbHelper = new JourneyDBHelper(ScuffContextProvider.getContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(JourneyContract.JourneyEntry.COLUMN_NAME_STATE, 0);

        String selection = JourneyContract.JourneyEntry.COLUMN_NAME_JOURNEY_ID + "=?";
        String[] selectionArgs = { journeyId };
        return db.update(JourneyContract.JourneyEntry.TABLE_NAME, values, selection,
                selectionArgs);

    }*/

/*    public static long record(Location location) {

        SharedPreferences sp = ScuffApplication.getContext().
                getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);

        if (D) Log.d(TAG, "processLocation waypoint journey=["+sp.getString(Constants.DRIVER_JOURNEY_ID, "ERROR")+"]");

        HashMap<String, Object> waypointValues = new HashMap<>();
        waypointValues.put(JourneyContract.JourneyEntry.COLUMN_NAME_JOURNEY_ID, sp.getString(Constants.DRIVER_JOURNEY_ID, "ERROR"));
        waypointValues.put(JourneyContract.JourneyEntry.COLUMN_NAME_APP_ID, sp.getLong(Constants.DRIVER_APP_ID, -1));
        waypointValues.put(JourneyContract.JourneyEntry.COLUMN_NAME_SCHOOL_ID, sp.getString(Constants.DRIVER_SCHOOL_ID, "ERROR"));
        waypointValues.put(JourneyContract.JourneyEntry.COLUMN_NAME_DRIVER_ID, sp.getString(Constants.DRIVER_DRIVER_ID, "ERROR"));
        waypointValues.put(JourneyContract.JourneyEntry.COLUMN_NAME_ROUTE_ID, sp.getString(Constants.DRIVER_ROUTE_ID, "ERROR"));
        waypointValues.put(JourneyContract.JourneyEntry.COLUMN_NAME_SOURCE, "Android");
        waypointValues.put(JourneyContract.JourneyEntry.COLUMN_NAME_STATE, 1);

        waypointValues.put(JourneyContract.JourneyEntry.COLUMN_NAME_WAYPOINT_ID, location.getLatitude());
        waypointValues.put(JourneyContract.JourneyEntry.COLUMN_NAME_LATITUDE, location.getLatitude());
        waypointValues.put(JourneyContract.JourneyEntry.COLUMN_NAME_LONGITUDE, location.getLongitude());
        waypointValues.put(JourneyContract.JourneyEntry.COLUMN_NAME_SPEED, location.getSpeed());
        waypointValues.put(JourneyContract.JourneyEntry.COLUMN_NAME_BEARING, location.getBearing());
        waypointValues.put(JourneyContract.JourneyEntry.COLUMN_NAME_DISTANCE, calculateTotalDistance(location));
        waypointValues.put(JourneyContract.JourneyEntry.COLUMN_NAME_PROVIDER, location.getProvider());
        waypointValues.put(JourneyContract.JourneyEntry.COLUMN_NAME_ACCURACY, location.getAccuracy());
        waypointValues.put(JourneyContract.JourneyEntry.COLUMN_NAME_ALTITUDE, location.getAltitude());
        waypointValues.put(JourneyContract.JourneyEntry.COLUMN_NAME_TIMESTAMP, DateTime.now().toString(ISODateTimeFormat.dateTime()));

        JourneyDBHelper dbHelper = new JourneyDBHelper(ScuffApplication.getContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Parcel parcel = Parcel.obtain();
        parcel.writeMap(waypointValues);
        parcel.setDataPosition(0);
        ContentValues contentValues = ContentValues.CREATOR.createFromParcel(parcel);

        return db.insert(JourneyContract.JourneyEntry.TABLE_NAME, null, contentValues);
    }*/

    public static Location get() {

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
