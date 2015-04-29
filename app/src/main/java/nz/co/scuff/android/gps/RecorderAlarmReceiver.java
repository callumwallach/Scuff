package nz.co.scuff.android.gps;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

// make sure we use a WakefulBroadcastReceiver so that we acquire a partial wakelock
public class RecorderAlarmReceiver extends WakefulBroadcastReceiver {

    private static final String TAG = "RecorderAlarmReceiver";
    private static final boolean D = true;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive");

        Intent newIntent = new Intent(context, LocationRecorderService.class);
        if (D) Log.d(TAG, "starting location service");
        context.startService(newIntent);
    }

}