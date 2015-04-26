package nz.co.scuff.android.data;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Parcel;
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.format.ISODateTimeFormat;

import java.util.HashMap;

import nz.co.scuff.android.util.Constants;
import nz.co.scuff.android.util.ServerInterfaceGenerator;
import nz.co.scuff.android.util.ScuffContextProvider;
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

    private static float calculateTotalDistance(Location location) {

        SharedPreferences sharedPreferences = ScuffContextProvider.getContext().
                getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        float totalDistanceInMeters = sharedPreferences.getFloat(Constants.DRIVER_ACCUMULATED_DISTANCE, 0f);
        boolean initialised = sharedPreferences.getBoolean(Constants.PREFERENCES_INITIALISED, false);

        if (!initialised) {
            editor.putBoolean(Constants.PREFERENCES_INITIALISED, true);
        } else {
            Location previousLocation = new Location("");
            previousLocation.setLatitude(sharedPreferences.getFloat(Constants.DRIVER_PREVIOUS_LAT, 0f));
            previousLocation.setLongitude(sharedPreferences.getFloat(Constants.DRIVER_PREVIOUS_LONG, 0f));

            float distance = location.distanceTo(previousLocation);
            totalDistanceInMeters += distance;
            editor.putFloat(Constants.DRIVER_ACCUMULATED_DISTANCE, totalDistanceInMeters);
        }

        editor.putFloat(Constants.DRIVER_PREVIOUS_LAT, (float)location.getLatitude());
        editor.putFloat(Constants.DRIVER_PREVIOUS_LONG, (float) location.getLongitude());
        editor.apply();

        return totalDistanceInMeters;
    }

    public static int startJourney(String journeyId, Location location) {
        if (D) Log.d(TAG, "startJourney location="+location);

        SharedPreferences sp = ScuffContextProvider.getContext()
                .getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);

/*        HashMap<String, Object> journey = new HashMap<>();
        journey.put(JourneyContract.JourneyEntry.COLUMN_NAME_JOURNEY_ID, sp.getString(Constants.DRIVER_JOURNEY_ID, "ERROR"));
        journey.put(JourneyContract.JourneyEntry.COLUMN_NAME_APP_ID, sp.getLong(Constants.DRIVER_APP_ID, -1L));
        journey.put(JourneyContract.JourneyEntry.COLUMN_NAME_SCHOOL_ID, sp.getString(Constants.DRIVER_SCHOOL_ID, "ERROR"));
        journey.put(JourneyContract.JourneyEntry.COLUMN_NAME_DRIVER_ID, sp.getString(Constants.DRIVER_DRIVER_ID, "ERROR"));
        journey.put(JourneyContract.JourneyEntry.COLUMN_NAME_ROUTE_ID, sp.getString(Constants.DRIVER_ROUTE_ID, "ERROR"));
        journey.put(JourneyContract.JourneyEntry.COLUMN_NAME_SOURCE, "Android");
        journey.put(JourneyContract.JourneyEntry.COLUMN_NAME_STATE, 1);

        journey.put(JourneyContract.JourneyEntry.COLUMN_NAME_WAYPOINT_ID, location.getLatitude());
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
        Journey journey = new Journey();
        journey.setId(journeyId);
        journey.setAppId(sp.getLong(Constants.DRIVER_APP_ID, -1));
        journey.setSchoolId(sp.getString(Constants.DRIVER_SCHOOL_ID, "ERROR"));
        journey.setDriverId(sp.getString(Constants.DRIVER_DRIVER_ID, "ERROR"));
        journey.setRouteId(sp.getString(Constants.DRIVER_ROUTE_ID, "ERROR"));
        journey.setSource("Android");
        journey.setState(Journey.TrackingState.RECORDING);
        //journey.addWaypoint(new Waypoint(journeyId+":"+DateTimeUtils.currentTimeMillis(), 0, Constants.TRACKING_STATE_RECORDING, location));

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

        // populate journey POJO
        Journey journey = new Journey();
        journey.setId(journeyId);
        journey.setState(Journey.TrackingState.PAUSED);
        journey.addWaypoint(new Waypoint(journeyId+":"+DateTimeUtils.currentTimeMillis(), calculateTotalDistance(location), Constants.TRACKING_STATE_PAUSED, location));

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

        // populate journey POJO
        Journey journey = new Journey();
        journey.setId(journeyId);
        journey.setState(Journey.TrackingState.RECORDING);
        journey.addWaypoint(new Waypoint(journeyId+":"+DateTimeUtils.currentTimeMillis(), calculateTotalDistance(location), Constants.TRACKING_STATE_RECORDING, location));

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

        // populate journey POJO
        Journey journey = new Journey();
        journey.setId(journeyId);
        journey.setState(Journey.TrackingState.COMPLETED);
        //journey.addWaypoint(new Waypoint(journeyId+":"+DateTimeUtils.currentTimeMillis(), calculateTotalDistance(location), Constants.TRACKING_STATE_STOPPED, location));

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

        // populate journey POJO
        Journey journey = new Journey();
        journey.setId(journeyId);
        journey.setState(Journey.TrackingState.RECORDING);
        journey.addWaypoint(new Waypoint(journeyId+":"+DateTimeUtils.currentTimeMillis(), calculateTotalDistance(location), Constants.TRACKING_STATE_RECORDING, location));

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

    public static long recordWaypoint(Location location) {

        SharedPreferences sp = ScuffContextProvider.getContext().
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

        JourneyDBHelper dbHelper = new JourneyDBHelper(ScuffContextProvider.getContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();


/*        Journey journey = new Journey();
        journey.setId("journey1");
        journey.setAppId("app1");
        journey.setSchoolId("school1");
        journey.setDriverId("driver1");
        journey.setRouteId("route1");
        journey.setSource("Android");
        journey.setState(Journey.TrackingState.RECORDING);*/

/*        ScuffServerInterface client = ServerInterfaceGenerator.createService(ScuffServerInterface.class, SERVER_URL);
        client.startJourney(journey, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                if (D) Log.d(TAG, "POST journey SUCCESS code="+response.getStatus());
            }

            @Override
            public void failure(RetrofitError error) {
                if (D) Log.d(TAG, "POST journey FAILED code="+error.getLocalizedMessage());
            }
        });*/

        //final String uploadWebsite = "http://10.0.3.2:8080/scuff/helloworld";
        //final RequestParams requestValues = new RequestParams();
        /*ScuffHttpClient.get(uploadWebsite, requestValues, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, org.apache.http.Header[] headers, byte[] responseBody) {
                if (D) Log.d(TAG, "success=" + statusCode);
                ScuffHttpClient.debugLoopJ("sendLocationDataToWebsite - success", uploadWebsite, requestValues, responseBody, headers, statusCode, null);
            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] errorResponse, Throwable e) {
                if (D) Log.d(TAG, "failure=" + statusCode);
                ScuffHttpClient.debugLoopJ("sendLocationDataToWebsite - failure", uploadWebsite, requestValues, errorResponse, headers, statusCode, e);
            }
        });*/

        Parcel parcel = Parcel.obtain();
        parcel.writeMap(waypointValues);
        parcel.setDataPosition(0);
        ContentValues contentValues = ContentValues.CREATOR.createFromParcel(parcel);

        return db.insert(JourneyContract.JourneyEntry.TABLE_NAME, null, contentValues);
    }

    public static Location get() {

        JourneyDBHelper dbHelper = new JourneyDBHelper(ScuffContextProvider.getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        SharedPreferences sp = ScuffContextProvider.getContext().
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
        return location;
    }
}
