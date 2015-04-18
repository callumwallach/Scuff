package nz.co.scuff.test;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

import nz.co.scuff.util.Constants;
import nz.co.scuff.util.ScuffContextProvider;

/**
 * Created by Callum on 17/04/2015.
 */
public final class JourneyDatasource {

    private static final String TAG = "JourneyDatasource";
    private static final boolean D = true;

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

    public static long post(Location location) {

        SharedPreferences sp = ScuffContextProvider.getContext().
                getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);

        ContentValues values = new ContentValues();
        values.put(JourneyContract.ActiveJourneyEntry.COLUMN_NAME_APP_ID,
                sp.getString(Constants.DRIVER_APP_ID, null));
        values.put(JourneyContract.ActiveJourneyEntry.COLUMN_NAME_SCHOOL_ID,
                sp.getString(Constants.DRIVER_SCHOOL_ID, null));
        values.put(JourneyContract.ActiveJourneyEntry.COLUMN_NAME_DRIVER_ID,
                sp.getString(Constants.DRIVER_DRIVER_ID, null));
        values.put(JourneyContract.ActiveJourneyEntry.COLUMN_NAME_ROUTE_ID,
                sp.getString(Constants.DRIVER_ROUTE_ID, null));
        values.put(JourneyContract.ActiveJourneyEntry.COLUMN_NAME_JOURNEY_ID,
                sp.getString(Constants.DRIVER_JOURNEY_ID, null));
        values.put(JourneyContract.ActiveJourneyEntry.COLUMN_NAME_LATITUDE, location.getLatitude());
        values.put(JourneyContract.ActiveJourneyEntry.COLUMN_NAME_LONGITUDE, location.getLongitude());
        values.put(JourneyContract.ActiveJourneyEntry.COLUMN_NAME_SPEED, location.getSpeed());
        values.put(JourneyContract.ActiveJourneyEntry.COLUMN_NAME_BEARING, location.getBearing());
        values.put(JourneyContract.ActiveJourneyEntry.COLUMN_NAME_DISTANCE,
                calculateTotalDistance(location));
        values.put(JourneyContract.ActiveJourneyEntry.COLUMN_NAME_PROVIDER, location.getProvider());
        values.put(JourneyContract.ActiveJourneyEntry.COLUMN_NAME_ACCURACY, location.getAccuracy());
        values.put(JourneyContract.ActiveJourneyEntry.COLUMN_NAME_ALTITUDE, location.getAltitude());
        values.put(JourneyContract.ActiveJourneyEntry.COLUMN_NAME_SOURCE, "Android");
        values.put(JourneyContract.ActiveJourneyEntry.COLUMN_NAME_TIMESTAMP,
                DateTime.now().toString(ISODateTimeFormat.dateTime()));

        JourneyDBHelper dbHelper = new JourneyDBHelper(ScuffContextProvider.getContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        return db.insert(JourneyContract.ActiveJourneyEntry.TABLE_NAME, null, values);
    }

    public static Location get() {

        JourneyDBHelper dbHelper = new JourneyDBHelper(ScuffContextProvider.getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        SharedPreferences sp = ScuffContextProvider.getContext().
                getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);
        String selection = JourneyContract.ActiveJourneyEntry.COLUMN_NAME_SCHOOL_ID +
                "=? and " + JourneyContract.ActiveJourneyEntry.COLUMN_NAME_ROUTE_ID + "=?";
        String[] selectionArgs = { sp.getString(Constants.PASSENGER_SCHOOL_ID, null),
                sp.getString(Constants.PASSENGER_ROUTE_ID, null) };
        if (D) Log.d(TAG, "selectionArgs=" + selectionArgs[0] + " " + selectionArgs[1]);
        String sortOrder = JourneyContract.ActiveJourneyEntry.COLUMN_NAME_TIMESTAMP + " DESC";

        Cursor cursor = db.query(
                JourneyContract.ActiveJourneyEntry.TABLE_NAME,  // The table to query
                null,                                       // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        cursor.moveToFirst();
        String provider = cursor.getString(cursor.getColumnIndexOrThrow(JourneyContract.ActiveJourneyEntry.COLUMN_NAME_PROVIDER));
        Location location = new Location(provider);
        location.setLatitude(Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow(JourneyContract.ActiveJourneyEntry.COLUMN_NAME_LATITUDE))));
        location.setLongitude(Double.parseDouble(cursor.getString(cursor.getColumnIndexOrThrow(JourneyContract.ActiveJourneyEntry.COLUMN_NAME_LONGITUDE))));
        location.setSpeed(cursor.getFloat(cursor.getColumnIndexOrThrow(JourneyContract.ActiveJourneyEntry.COLUMN_NAME_SPEED)));
        location.setBearing(cursor.getFloat(cursor.getColumnIndexOrThrow(JourneyContract.ActiveJourneyEntry.COLUMN_NAME_BEARING)));
        location.setAccuracy(cursor.getFloat(cursor.getColumnIndexOrThrow(JourneyContract.ActiveJourneyEntry.COLUMN_NAME_ACCURACY)));
        location.setAltitude(cursor.getFloat(cursor.getColumnIndexOrThrow(JourneyContract.ActiveJourneyEntry.COLUMN_NAME_ALTITUDE)));
        location.setTime(DateTime.parse(cursor.getString(cursor.getColumnIndexOrThrow(JourneyContract.ActiveJourneyEntry.COLUMN_NAME_TIMESTAMP))).getMillis());

        return location;
    }
}
