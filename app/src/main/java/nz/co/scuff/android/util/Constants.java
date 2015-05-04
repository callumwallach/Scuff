package nz.co.scuff.android.util;

/**
 * Created by Callum on 18/03/2015.
 */
public interface Constants {

    String PREFERENCES = "P";
    String PREFERENCES_INITIALISED = "P_I";
    String PREFERENCES_RECORD_LOCATION_INTERVAL_KEY = "P_RLIK";

    String JOURNEY_KEY = "J_KEY";
    String JOURNEY_COMMAND_KEY = "JC_KEY";
    String JOURNEY_TRACKING_STATE_KEY = "JTS_KEY";

    String USER_KEY = "U_KEY";

    String PASSENGER_ROUTE_KEY = "PR_KEY";
    String PASSENGER_SCHOOL_KEY = "PS_KEY";

    int RECORD_LOCATION_INTERVAL = 15;
    int LISTEN_LOCATION_INTERVAL = 15;

    float MIN_LOCATION_ACCURACY = 500.0f;

    String SERVER_URL = "http://10.0.3.2:8080/scuff";
    String JSON_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

}
