package nz.co.scuff.android.service;

import android.content.Intent;
import android.location.Location;
import android.util.Log;

import de.greenrobot.event.EventBus;
import nz.co.scuff.android.data.ScuffDatasource;
import nz.co.scuff.android.util.CommandType;
import nz.co.scuff.android.util.Constants;
import nz.co.scuff.android.event.LocationEvent;

public class DriverLocationIntentService extends BaseLocationIntentService {

    private static final String TAG = "DriverLocIntentService";
    private static final boolean D = true;

    private CommandType commandType;
    private boolean recording;
    private String journeyId;

    public DriverLocationIntentService() { }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (D) Log.d(TAG, "onHandleIntent");

        this.journeyId = intent.getStringExtra(Constants.JOURNEY_KEY);
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
                //EventBus.getDefault().post(new LocationEvent(location));
                saveAndUploadLocation(location);
            }
        }
    }

    public void saveAndUploadLocation(Location location) {
        if (D) Log.d(TAG, "Save and upload location="+location);

        if (this.recording) {
            ScuffDatasource.recordJourney(journeyId, location);
        } else {
            switch (this.commandType) {
                case START:
                    // create journey + create waypoint
                    ScuffDatasource.startJourney(journeyId, location);
                    break;
                case PAUSE:
                    // pause journey + create waypoint
                    ScuffDatasource.pauseJourney(journeyId, location);
                    break;
                case CONTINUE:
                    // update journey + create waypoint
                    ScuffDatasource.continueJourney(journeyId, location);
                    break;
                case STOP:
                    // close journey + create waypoint
                    ScuffDatasource.stopJourney(journeyId, location);
                    break;
                default:
                    // do nothing
                    Log.e(TAG, "Ops this should never happen");
            }
        }
    }

}
