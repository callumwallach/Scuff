package nz.co.scuff.android.gps;

import android.content.Intent;
import android.location.Location;
import android.util.Log;

import de.greenrobot.event.EventBus;
import nz.co.scuff.android.data.JourneyDatasource;
import nz.co.scuff.android.util.CommandType;
import nz.co.scuff.android.util.Constants;
import nz.co.scuff.android.util.LocationEvent;

public class DriverLocationIntentService extends BaseLocationIntentService {

    private static final String TAG = "DriverLocIntentService";
    private static final boolean D = true;

    private CommandType commandType;
    private boolean recording;

    public DriverLocationIntentService() { }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (D) Log.d(TAG, "onHandleIntent");

        this.commandType = (CommandType)intent.getExtras().getSerializable(Constants.JOURNEY_COMMAND_KEY);
        this.recording = intent.getExtras().getSerializable(Constants.JOURNEY_TRACKING_STATE_KEY) != null;

        super.onHandleIntent(intent);
    }

    @Override
    public void onLocationChanged(Location location) {
        if (D) Log.d(TAG, "onLocationChanged");
        if (location != null) {
            if (D) Log.d(TAG, "position: " + location.getLatitude() + ", " + location.getLongitude() + " accuracy: " + location.getAccuracy());
            // have desired accuracy so quit service, onDestroy will be called and stop our location updates
            if (location.getAccuracy() < Constants.MIN_LOCATION_ACCURACY) {
                stopLocationUpdates();
                // post location to event bus
                EventBus.getDefault().post(new LocationEvent(location));
                saveAndUploadLocation(location);
            }
        }
    }

    public void saveAndUploadLocation(Location location) {
        if (D) Log.d(TAG, "Save and upload location="+location);

        if (this.recording) {
            JourneyDatasource.recordJourney(location);
        } else {
            switch (this.commandType) {
                case START:
                    // create journey + create waypoint
                    JourneyDatasource.startJourney(location);
                    break;
                case PAUSE:
                    // pause journey + create waypoint
                    JourneyDatasource.pauseJourney(location);
                    break;
                case CONTINUE:
                    // update journey + create waypoint
                    JourneyDatasource.continueJourney(location);
                    break;
                case STOP:
                    // close journey + create waypoint
                    JourneyDatasource.stopJourney(location);
                    break;
                default:
                    // do nothing
                    Log.e(TAG, "Ops this should never happen");
            }
        }
    }

}
