package nz.co.scuff.android.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import nz.co.scuff.android.util.Constants;

public class PassengerAlarmReceiver extends BroadcastReceiver {

    private static final String TAG = "PassengerAlarmReceiver";
    private static final boolean D = true;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (D) Log.d(TAG, "Passenger alarm onReceive");

        long coordinatorId = intent.getExtras().getLong(Constants.COORDINATOR_ID_KEY);

        Intent locationIntent = new Intent(context, PassengerIntentService.class);
        locationIntent.putExtra(Constants.COORDINATOR_ID_KEY, coordinatorId);
        context.startService(locationIntent);
    }

}
