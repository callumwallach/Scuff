package nz.co.scuff.android.data;

import android.provider.BaseColumns;

/**
 * Created by Callum on 17/04/2015.
 */
public final class JourneyContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public JourneyContract() {}

    /* Inner class that defines the table contents */
    public static abstract class JourneyEntry implements BaseColumns {
        public static final String TABLE_NAME = "ACTIVE_JOURNEYS";
        public static final String COLUMN_NAME_JOURNEY_ID = "journeyid";
        public static final String COLUMN_NAME_APP_ID = "appid";
        public static final String COLUMN_NAME_SCHOOL_ID = "schoolid";
        public static final String COLUMN_NAME_DRIVER_ID = "driverid";
        public static final String COLUMN_NAME_ROUTE_ID = "routeid";
        public static final String COLUMN_NAME_LATITUDE = "latitude";
        public static final String COLUMN_NAME_LONGITUDE = "longitude";
        public static final String COLUMN_NAME_SPEED = "speed";
        public static final String COLUMN_NAME_BEARING = "bearing";
        public static final String COLUMN_NAME_DISTANCE = "distance";
        public static final String COLUMN_NAME_PROVIDER = "provider";
        public static final String COLUMN_NAME_ACCURACY = "accuracy";
        public static final String COLUMN_NAME_ALTITUDE = "altitude";
        public static final String COLUMN_NAME_SOURCE = "source";
        public static final String COLUMN_NAME_TIMESTAMP = "timestamp";
        public static final String COLUMN_NAME_ACTIVE = "active";
    }

}
