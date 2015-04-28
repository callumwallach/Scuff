package nz.co.scuff.android.util;

/**
 * Created by Callum on 18/03/2015.
 */
public interface Constants {

    String PREFERENCES = "P";
    String PREFERENCES_INITIALISED = "P_I";
    String PREFERENCES_RECORD_LOCATION_INTERVAL_KEY = "P_RLIK";

    String DRIVER_TRACKING_STATE = "D_TR";
    String DRIVER_APP_ID = "D_APID";
    String DRIVER_JOURNEY_ID = "D_JID";
    String DRIVER_SCHOOL_ID = "D_SCID";
    String DRIVER_DRIVER_ID = "D_DRID";
    String DRIVER_ROUTE_ID = "D_ROID";

    String PASSENGER_SCHOOL_ID = "P_SCID";
    String PASSENGER_ROUTE_ID = "P_ROID";

    int RECORD_LOCATION_INTERVAL = 15;

    String TRACKING_STATE_TYPE = "TST";
    int TRACKING_STATE_RECORDING = 0;
    int TRACKING_STATE_PAUSED = 1;
    int TRACKING_STATE_STOPPED = 2;

    String TRACKING_ACTION_TYPE = "TAT";
    int TRACKING_ACTION_START = 0;
    int TRACKING_ACTION_PAUSE = 1;
    int TRACKING_ACTION_CONTINUE = 2;
    int TRACKING_ACTION_STOP = 3;

    float MIN_LOCATION_ACCURACY = 500.0f;

    String MALE_PROFILE_BLANK = "male_profile_blank.jpg";
    String FEMALE_PROFILE_BLANK = "female_profile_blank.jpg";

}
