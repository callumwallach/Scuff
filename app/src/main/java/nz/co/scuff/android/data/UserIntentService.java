package nz.co.scuff.android.data;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import de.greenrobot.event.EventBus;
import nz.co.scuff.android.util.Constants;
import nz.co.scuff.android.util.SnapshotEvent;
import nz.co.scuff.data.journey.JourneySnapshot;

public class UserIntentService extends IntentService {

    private static final String TAG = "UserIntentService";
    private static final boolean D = true;

    public UserIntentService() {
        super("UserIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (D) Log.d(TAG, "onHandleIntent");

        String userId = intent.getExtras().getString(Constants.USER_KEY);

        // UI IS WAITING
        // check local db
        // if found check for updates from server
        // if not there retrieve from server (first log in)
        // store in db
        // return user
        // RELEASE UI

/*

        // check locally
        List<JourneySnapshot> localSnapshots = JourneySnapshot.findByRouteAndSchool(routeId, schoolId);
        if (D) Log.d(TAG, "found local journey snapshots="+localSnapshots);

        // if found check for expiry
        ListIterator<JourneySnapshot> snapshotIterator = localSnapshots.listIterator();
        while (snapshotIterator.hasNext()) {
            JourneySnapshot localSnapshot = snapshotIterator.next();
            if (D) Log.d(TAG, "checking expiry="+localSnapshot.getExpiry()+" vs now="+new Timestamp(DateTimeUtils.currentTimeMillis()));
            if (localSnapshot.getExpiry().before(new Timestamp(DateTime.now().getMillis()))) {
                if (D) Log.d(TAG, "expire locally stored journey snapshot="+localSnapshot);
                snapshotIterator.remove();
                ScuffDatasource.deleteSnapshot(localSnapshot);
            }
        }
        List<JourneySnapshot> snapshotsToPost = new ArrayList<>();
        if (localSnapshots.isEmpty()) {
            // not stored locally (ie. this is first look up or none active or refresh) so go to server
            if (D) Log.d(TAG, "retrieving fresh journey snapshots from server (search by route and school)");
            List<JourneySnapshot> retrievedSnapshots = ScuffDatasource.getSnapshots(routeId, schoolId);
            for (JourneySnapshot retrievedSnapshot : retrievedSnapshots) {
                if (D) Log.d(TAG, "saving retrieved snapshot="+retrievedSnapshot);
                ScuffDatasource.saveSnapshot(retrievedSnapshot);
                snapshotsToPost.add(retrievedSnapshot);
            }
        } else {
            // have copies locally, so read update from server using pk
            // TODO optimise for multiple journeys (currently multiple network accesses)
            if (D) Log.d(TAG, "using locally stored journey ids");
            for (JourneySnapshot localSnapshot : localSnapshots) {
                if (D) Log.d(TAG, "retrieving fresh journey snapshot from server (search by journey id)");
                JourneySnapshot retrievedSnapshot = ScuffDatasource.getSnapshot(localSnapshot.getJourneyId());
                if (retrievedSnapshot != null) {
                    if (D) Log.d(TAG, "updating stored journey snapshot=" + localSnapshot);

                    localSnapshot.setLatitude(retrievedSnapshot.getLatitude());
                    localSnapshot.setLongitude(retrievedSnapshot.getLongitude());
                    localSnapshot.setSpeed(retrievedSnapshot.getSpeed());
                    localSnapshot.setBearing(retrievedSnapshot.getBearing());
                    localSnapshot.setAccuracy(retrievedSnapshot.getAccuracy());
                    localSnapshot.setAltitude(retrievedSnapshot.getAltitude());
                    localSnapshot.setState(retrievedSnapshot.getState());
                    localSnapshot.setTimestamp(retrievedSnapshot.getTimestamp());

                    ScuffDatasource.saveSnapshot(localSnapshot);
                    snapshotsToPost.add(localSnapshot);
                } else {
                    // journey must have completed, so delete from active
                    ScuffDatasource.deleteSnapshot(localSnapshot);
                }
            }
        }
        EventBus.getDefault().post(new SnapshotEvent(snapshotsToPost));
*/

    }

}