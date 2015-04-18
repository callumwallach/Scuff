package nz.co.scuff.test;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Callum on 17/04/2015.
 */
public class JourneyDBHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 5;
    public static final String DATABASE_NAME = "scuff.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String REAL_TYPE = " REAL";
    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_ACTIVE_JOURNEYS =
            "CREATE TABLE " + JourneyContract.ActiveJourneyEntry.TABLE_NAME + " (" +
                    JourneyContract.ActiveJourneyEntry._ID + " INTEGER PRIMARY KEY," +
                    JourneyContract.ActiveJourneyEntry.COLUMN_NAME_ID + TEXT_TYPE + COMMA_SEP +
                    JourneyContract.ActiveJourneyEntry.COLUMN_NAME_APP_ID + TEXT_TYPE + COMMA_SEP +
                    JourneyContract.ActiveJourneyEntry.COLUMN_NAME_SCHOOL_ID + TEXT_TYPE + COMMA_SEP +
                    JourneyContract.ActiveJourneyEntry.COLUMN_NAME_DRIVER_ID + TEXT_TYPE + COMMA_SEP +
                    JourneyContract.ActiveJourneyEntry.COLUMN_NAME_ROUTE_ID + TEXT_TYPE + COMMA_SEP +
                    JourneyContract.ActiveJourneyEntry.COLUMN_NAME_JOURNEY_ID + TEXT_TYPE + COMMA_SEP +
                    JourneyContract.ActiveJourneyEntry.COLUMN_NAME_LATITUDE + TEXT_TYPE + COMMA_SEP +
                    JourneyContract.ActiveJourneyEntry.COLUMN_NAME_LONGITUDE + TEXT_TYPE + COMMA_SEP +
                    JourneyContract.ActiveJourneyEntry.COLUMN_NAME_SPEED + REAL_TYPE + COMMA_SEP +
                    JourneyContract.ActiveJourneyEntry.COLUMN_NAME_BEARING + REAL_TYPE + COMMA_SEP +
                    JourneyContract.ActiveJourneyEntry.COLUMN_NAME_DISTANCE + REAL_TYPE + COMMA_SEP +
                    JourneyContract.ActiveJourneyEntry.COLUMN_NAME_PROVIDER + TEXT_TYPE + COMMA_SEP +
                    JourneyContract.ActiveJourneyEntry.COLUMN_NAME_ACCURACY + REAL_TYPE + COMMA_SEP +
                    JourneyContract.ActiveJourneyEntry.COLUMN_NAME_ALTITUDE + REAL_TYPE + COMMA_SEP +
                    JourneyContract.ActiveJourneyEntry.COLUMN_NAME_SOURCE + TEXT_TYPE + COMMA_SEP +
                    JourneyContract.ActiveJourneyEntry.COLUMN_NAME_TIMESTAMP + TEXT_TYPE +
                    " )";

    private static final String SQL_DELETE_ACTIVE_JOURNEYS =
            "DROP TABLE IF EXISTS " + JourneyContract.ActiveJourneyEntry.TABLE_NAME;

    private static final String SQL_CREATE_COMPLETED_JOURNEYS =
            "CREATE TABLE " + JourneyContract.CompletedJourneyEntry.TABLE_NAME + " (" +
                    JourneyContract.CompletedJourneyEntry._ID + " INTEGER PRIMARY KEY," +
                    JourneyContract.CompletedJourneyEntry.COLUMN_NAME_ID + TEXT_TYPE + COMMA_SEP +
                    JourneyContract.CompletedJourneyEntry.COLUMN_NAME_APP_ID + TEXT_TYPE + COMMA_SEP +
                    JourneyContract.CompletedJourneyEntry.COLUMN_NAME_SCHOOL_ID + TEXT_TYPE + COMMA_SEP +
                    JourneyContract.CompletedJourneyEntry.COLUMN_NAME_DRIVER_ID + TEXT_TYPE + COMMA_SEP +
                    JourneyContract.CompletedJourneyEntry.COLUMN_NAME_ROUTE_ID + TEXT_TYPE + COMMA_SEP +
                    JourneyContract.CompletedJourneyEntry.COLUMN_NAME_JOURNEY_ID + TEXT_TYPE + COMMA_SEP +
                    JourneyContract.CompletedJourneyEntry.COLUMN_NAME_LATITUDE + TEXT_TYPE + COMMA_SEP +
                    JourneyContract.CompletedJourneyEntry.COLUMN_NAME_LONGITUDE + TEXT_TYPE + COMMA_SEP +
                    JourneyContract.CompletedJourneyEntry.COLUMN_NAME_SPEED + REAL_TYPE + COMMA_SEP +
                    JourneyContract.CompletedJourneyEntry.COLUMN_NAME_BEARING + REAL_TYPE + COMMA_SEP +
                    JourneyContract.CompletedJourneyEntry.COLUMN_NAME_DISTANCE + REAL_TYPE + COMMA_SEP +
                    JourneyContract.CompletedJourneyEntry.COLUMN_NAME_PROVIDER + TEXT_TYPE + COMMA_SEP +
                    JourneyContract.CompletedJourneyEntry.COLUMN_NAME_ACCURACY + REAL_TYPE + COMMA_SEP +
                    JourneyContract.CompletedJourneyEntry.COLUMN_NAME_ALTITUDE + REAL_TYPE + COMMA_SEP +
                    JourneyContract.CompletedJourneyEntry.COLUMN_NAME_SOURCE + TEXT_TYPE + COMMA_SEP +
                    JourneyContract.CompletedJourneyEntry.COLUMN_NAME_TIMESTAMP + TEXT_TYPE +
                    " )";

    private static final String SQL_DELETE_COMPLETED_JOURNEYS =
            "DROP TABLE IF EXISTS " + JourneyContract.CompletedJourneyEntry.TABLE_NAME;

    public JourneyDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ACTIVE_JOURNEYS);
        db.execSQL(SQL_CREATE_COMPLETED_JOURNEYS);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ACTIVE_JOURNEYS);
        db.execSQL(SQL_DELETE_COMPLETED_JOURNEYS);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}