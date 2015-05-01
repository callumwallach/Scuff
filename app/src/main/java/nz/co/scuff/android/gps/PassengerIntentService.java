package nz.co.scuff.android.gps;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import de.greenrobot.event.EventBus;
import nz.co.scuff.android.data.JourneyDatasource;
import nz.co.scuff.android.util.Constants;
import nz.co.scuff.android.util.WaypointEvent;
import nz.co.scuff.data.journey.Waypoint;

public class PassengerIntentService extends IntentService {

    private static final String TAG = "PassengerIntentService";
    private static final boolean D = true;

    public PassengerIntentService() {
        super("PassengerIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (D) Log.d(TAG, "onHandleIntent");

        String routeId = intent.getExtras().getString(Constants.PASSENGER_ROUTE_KEY);
        String schoolId = intent.getExtras().getString(Constants.PASSENGER_SCHOOL_KEY);

        // TODO optimise to avoid DB lookup on server
        Waypoint waypoint = JourneyDatasource.getCurrentWaypoint(routeId, schoolId);

        EventBus.getDefault().post(new WaypointEvent(waypoint));

    }

}