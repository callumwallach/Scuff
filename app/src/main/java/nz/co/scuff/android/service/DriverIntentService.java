package nz.co.scuff.android.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import nz.co.scuff.android.util.CommandType;
import nz.co.scuff.android.util.Constants;
import nz.co.scuff.data.util.TrackingState;

public class DriverIntentService extends IntentService {

    private static final String TAG = "DriverIntentService";
    private static final boolean D = true;

    public DriverIntentService() {
        super("DriverIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (D) Log.d(TAG, "onHandleIntent");

        // can either be a command or tracking intent
        Intent locationIntent = new Intent(this, DriverLocationIntentService.class);
        Object commandType = intent.getExtras().getSerializable(Constants.JOURNEY_COMMAND_KEY);
        Object trackingState = intent.getExtras().getSerializable(Constants.JOURNEY_TRACKING_STATE_KEY);
        if (trackingState != null) {
            locationIntent.putExtra(Constants.JOURNEY_TRACKING_STATE_KEY, (TrackingState)trackingState);
        } else if (commandType != null) {
            locationIntent.putExtra(Constants.JOURNEY_COMMAND_KEY, (CommandType)commandType);
        }
        startService(locationIntent);

        // if started from the alarm then release its wake lock
        DriverAlarmReceiver.completeWakefulIntent(intent);
    }

}