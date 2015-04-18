package nz.co.scuff.util;

/**
 * Created by Callum on 18/03/2015.
 */
public interface Constants {

    public static final String PREFERENCES = "P";
    public static final String PREFERENCES_INITIALISED = "P_I";
    public static final String PREFERENCES_RECORD_LOCATION_INTERVAL_KEY = "P_RLIK";
    public static final String PREFERENCES_UPLOAD_LOCATION_INTERVAL_KEY = "P_ULIK";

    public static final String DRIVER_CURRENTLY_TRACKING = "D_TR";
    public static final String DRIVER_APP_ID = "D_APID";
    public static final String DRIVER_JOURNEY_ID = "D_SEID";
    public static final String DRIVER_ACCUMULATED_DISTANCE = "D_CD";
    public static final String DRIVER_SCHOOL_ID = "D_SCID";
    public static final String DRIVER_DRIVER_ID = "D_DRID";
    public static final String DRIVER_ROUTE_ID = "D_ROID";
    public static final String DRIVER_PREVIOUS_LAT = "D_PLAT";
    public static final String DRIVER_PREVIOUS_LONG = "D_PLONG";

    public static final String PASSENGER_SCHOOL_ID = "P_SCID";
    public static final String PASSENGER_ROUTE_ID = "P_ROID";

    public static final int RECORD_LOCATION_INTERVAL = 15;
    public static final int UPLOAD_LOCATION_INTERVAL = 15;

    public static final String TRACKING_STATE_TYPE = "TST";
    public static final int TRACKING_STATE_RECORD = 0;
    public static final int TRACKING_STATE_PAUSE = 1;
    public static final int TRACKING_STATE_FINALISE = 2;

    public static final float MIN_LOCATION_ACCURACY = 500.0f;

    public static final String MALE_PROFILE_BLANK = "male_profile_blank.jpg";
    public static final String FEMALE_PROFILE_BLANK = "female_profile_blank.jpg";

}
