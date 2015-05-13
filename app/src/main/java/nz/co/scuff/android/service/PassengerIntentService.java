package nz.co.scuff.android.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import java.util.List;

import de.greenrobot.event.EventBus;
import nz.co.scuff.android.data.ScuffDatasource;
import nz.co.scuff.android.util.Constants;
import nz.co.scuff.android.util.BusEvent;
import nz.co.scuff.data.journey.Bus;

public class PassengerIntentService extends IntentService {

    private static final String TAG = "PassengerIntentService";
    private static final boolean D = true;

    public PassengerIntentService() {
        super("PassengerIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (D) Log.d(TAG, "onHandleIntent");

        long routeId = intent.getExtras().getLong(Constants.PASSENGER_ROUTE_ID_KEY);
        long schoolId = intent.getExtras().getLong(Constants.PASSENGER_SCHOOL_ID_KEY);

        if (D) Log.d(TAG, "retrieving fresh bus snapshots from server (search by route and school)");
        List<Bus> fetchedBuses = ScuffDatasource.getActiveBuses(routeId, schoolId);
        // unlikely but may have more than one bus active for a route and school at any given time

        EventBus.getDefault().post(new BusEvent(fetchedBuses));

        // check locally
/*
        List<Bus> cachedBuses = Bus.findByRouteAndSchool(routeId, schoolId);
        if (D) Log.d(TAG, "found local buses="+cachedBuses);
*/

/*        // if found check for expiry
        ListIterator<Journey> journeyIterator = localJourneys.listIterator();
        while (journeyIterator.hasNext()) {
            JourneySnapshot localSnapshot = journeyIterator.next();
            if (D) Log.d(TAG, "checking expiry="+localSnapshot.getExpiry()+" vs now="+new Timestamp(DateTimeUtils.currentTimeMillis()));
            if (localSnapshot.getExpiry().before(new Timestamp(DateTime.now().getMillis()))) {
                if (D) Log.d(TAG, "expire locally stored journey snapshot="+localSnapshot);
                journeyIterator.remove();
                ScuffDatasource.deleteSnapshot(localSnapshot);
            }
        }*/

/*        List<Bus> busesToPost = new ArrayList<>();
        if (cachedBuses.isEmpty()) {
            // not stored locally (ie. this is first look up or none active or refresh) so go to server
            if (D) Log.d(TAG, "retrieving fresh bus snapshots from server (search by route and school)");
            List<Bus> fetchedBuses = ScuffDatasource.getActiveBuses(routeId, schoolId);
            // unlikely but may have more than one bus active for a route and school at any given time
            busesToPost.addAll(fetchedBuses);
        } else {
            // have copies locally, so read update from server using pk
            // TODO optimise for multiple buses (currently multiple network accesses)
            if (D) Log.d(TAG, "using locally stored bus ids");
            for (Bus cachedBus : cachedBuses) {
                if (D) Log.d(TAG, "retrieving fresh bus snapshot from server (search by bus/journey id)");
                Journey fetchedBus = ScuffDatasource.getActiveJourney(cachedBus.getJourneyId(), true);
                if (fetchedBus != null) {
                    if (D) Log.d(TAG, "updating stored bus snapshot=" + cachedBus);
                    busesToPost.add(cachedBus);
                } else {
                    // journey must have completed, so update status
                    ScuffDatasource.deleteSnapshot(cachedBus);
                }
            }
        }*/
/*
        EventBus.getDefault().post(new BusEvent(busesToPost));
*/

    }

}