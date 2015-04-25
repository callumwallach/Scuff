package nz.co.scuff.android.util;

/**
 * Created by Callum on 18/03/2015.
 */
public interface Constants {

    String PREFERENCES = "P";
    String PREFERENCES_INITIALISED = "P_I";
    String PREFERENCES_RECORD_LOCATION_INTERVAL_KEY = "P_RLIK";
    String PREFERENCES_UPLOAD_LOCATION_INTERVAL_KEY = "P_ULIK";

    String DRIVER_TRACKING_STATE = "D_TR";
    String DRIVER_APP_ID = "D_APID";
    String DRIVER_JOURNEY_ID = "D_SEID";
    String DRIVER_ACCUMULATED_DISTANCE = "D_CD";
    String DRIVER_SCHOOL_ID = "D_SCID";
    String DRIVER_DRIVER_ID = "D_DRID";
    String DRIVER_ROUTE_ID = "D_ROID";
    String DRIVER_PREVIOUS_LAT = "D_PLAT";
    String DRIVER_PREVIOUS_LONG = "D_PLONG";

    String PASSENGER_SCHOOL_ID = "P_SCID";
    String PASSENGER_ROUTE_ID = "P_ROID";

    int RECORD_LOCATION_INTERVAL = 15;
    int UPLOAD_LOCATION_INTERVAL = 15;

    String TRACKING_STATE_TYPE = "TST";
    int TRACKING_STATE_RECORDING = 0;
    int TRACKING_STATE_PAUSED = 1;
    int TRACKING_STATE_STOPPED = 2;

    float MIN_LOCATION_ACCURACY = 500.0f;

    String MALE_PROFILE_BLANK = "male_profile_blank.jpg";
    String FEMALE_PROFILE_BLANK = "female_profile_blank.jpg";

}
