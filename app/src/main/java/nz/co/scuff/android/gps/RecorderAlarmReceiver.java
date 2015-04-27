package nz.co.scuff.android.gps;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import nz.co.scuff.android.util.Constants;

// make sure we use a WakefulBroadcastReceiver so that we acquire a partial wakelock
public class RecorderAlarmReceiver extends WakefulBroadcastReceiver {

    private static final String TAG = "RecorderAlarmReceiver";
    private static final boolean D = true;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive");

        int state = intent.getExtras().getInt(Constants.TRACKING_STATE_TYPE);
        String journeyId = intent.getExtras().getString(Constants.DRIVER_JOURNEY_ID);

        if (D) Log.d(TAG, "stateType=" + state + " journeyId="+journeyId);

        Intent newIntent = new Intent(context, LocationRecorderService.class);
        newIntent.putExtra(Constants.TRACKING_STATE_TYPE, state);
        newIntent.putExtra(Constants.DRIVER_JOURNEY_ID, journeyId);

        switch (state) {
            case Constants.TRACKING_STATE_RECORDING:
                if (D) Log.d(TAG, "starting location service");
                context.startService(newIntent);
                break;
            case Constants.TRACKING_STATE_PAUSED:
            case Constants.TRACKING_STATE_STOPPED:
            default:
                // do nothing, should never get here anyway
                Log.e(TAG, "Error invalid state["+state+"]");
                break;
        }

    }

}