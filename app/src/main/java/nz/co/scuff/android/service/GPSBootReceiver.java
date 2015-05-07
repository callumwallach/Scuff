package nz.co.scuff.android.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class GPSBootReceiver extends BroadcastReceiver {

    private static final String TAG = "GPSBootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

/*        Log.d(TAG, "onReceive");

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