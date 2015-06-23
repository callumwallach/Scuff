package nz.co.scuff.android.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;

import nz.co.scuff.android.util.Constants;

public class PassengerAlarmReceiver extends BroadcastReceiver {

    private static final String TAG = "PassengerAlarmReceiver";
    private static final boolean D = true;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (D) Log.d(TAG, "Passenger alarm onReceive");

        long coordinatorId = intent.getLongExtra(Constants.COORDINATOR_ID_KEY, -1);
        ArrayList<Long> watchedJourneyIds = (ArrayList<Long>)intent.getSerializableExtra(Constants.WATCHED_JOURNEYS_ID_KEY);

        Intent locationIntent = new Intent(context, PassengerIntentService.class);
        locationIntent.putExtra(Constants.COORDINATOR_ID_KEY, coordinatorId);
        locationIntent.putExtra(Constants.WATCHED_JOURNEYS_ID_KEY, watchedJourneyIds);
        context.startService(locationIntent);
    }

}
