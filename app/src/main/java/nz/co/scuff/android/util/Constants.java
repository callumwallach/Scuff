package nz.co.scuff.android.util;

/**
 * Created by Callum on 18/03/2015.
 */
public interface Constants {

    String PREFERENCES = "P";
    String PREFERENCES_INITIALISED = "P_I";
    String PREFERENCES_RECORD_LOCATION_INTERVAL_KEY = "P_RLIK";

    String JOURNEY_COMMAND_KEY = "JC_KEY";
    String JOURNEY_TRACKING_STATE_KEY = "JTS_KEY";

    String USER_KEY = "U_KEY";
    String SCHOOLS_KEY = "SS_KEY";
    String ROUTES_KEY = "RS_KEY";
    String ROUTE_KEY = "R_KEY";
    String PASSENGERS_KEY = "PS_KEY";
    String BUS_KEY = "B_KEY";
    String TICKETS_KEY = "TS_KEY";

    String JOURNEY_ID_KEY = "JID_KEY";
    String COORDINATOR_ID_KEY = "CID_KEY";
    String WATCHED_JOURNEYS_ID_KEY = "W_JIDS_KEY";
    String OWNER_ID_KEY = "OID_KEY";
    String AGENT_ID_KEY = "AID_KEY";
    String ROUTE_ID_KEY = "RID_KEY";

    String PARENT_ACTIVITY_CLASS_NAME = "PACN";

    int RECORD_LOCATION_INTERVAL = 10;
    int LISTEN_LOCATION_INTERVAL = 10;

    int SECONDS_TO_DISPLAY_COMPLETED_JOURNEYS = 60;

    float MIN_LOCATION_ACCURACY = 50.0f;

    String SERVER_URL = "http://10.0.3.2:8080/scuff";
    String JSON_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    int DEFAULT_RADIUS = 3000;

}
