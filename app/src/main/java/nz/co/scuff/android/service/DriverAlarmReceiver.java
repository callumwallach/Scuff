package nz.co.scuff.android.service;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import nz.co.scuff.android.util.Constants;
import nz.co.scuff.data.util.TrackingState;

// make sure we use a WakefulBroadcastReceiver so that we acquire a partial wakelock
public class DriverAlarmReceiver extends WakefulBroadcastReceiver {

    private static final String TAG = "DriverAlarmReceiver";
    private static final boolean D = true;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (D) Log.d(TAG, "Coordinator alarm onReceive");

        long journeyId = intent.getLongExtra(Constants.JOURNEY_ID_KEY, -1);

        Intent newIntent = new Intent(context, DriverIntentService.class);
        newIntent.putExtra(Constants.JOURNEY_TRACKING_STATE_KEY, TrackingState.RECORDING);
        newIntent.putExtra(Constants.JOURNEY_ID_KEY, journeyId);
        context.startService(newIntent);
    }

}
