package nz.co.scuff.android.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.util.Log;

import nz.co.scuff.android.util.Constants;

public class GPSBootReceiver extends BroadcastReceiver {

    private static final String TAG = "GPSBootReceiver";
    private static final boolean D = true;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (D) Log.d(TAG, "onReceive");

/*        // pause (cancel) local location update requests
        AlarmManager alarmManager = (AlarmManager)content.getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(this, DriverAlarmReceiver.class);
        PendingIntent pendingAlarmIntent = PendingIntent.getBroadcast(this, DRIVER_ALARM, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.cancel(pendingAlarmIntent);


        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent gpsTrackerIntent = new Intent(context, RecorderAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, gpsTrackerIntent, 0);

        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);
        int intervalInMinutes = sharedPreferences.getInt("intervalInMinutes", 1);
        Boolean currentlyTracking = sharedPreferences.getBoolean("currentlyTracking", false);

        if (currentlyTracking) {
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime(),
                    intervalInMinutes * 60000, // 60000 = 1 minute,
                    pendingIntent);
        } else {
            alarmManager.cancel(pendingIntent);
        }*/
    }
}