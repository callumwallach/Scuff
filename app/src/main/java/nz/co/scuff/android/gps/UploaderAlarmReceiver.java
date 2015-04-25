package nz.co.scuff.android.gps;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import nz.co.scuff.android.util.Constants;

// make sure we use a WakefulBroadcastReceiver so that we acquire a partial wakelock
public class UploaderAlarmReceiver extends WakefulBroadcastReceiver {

    private static final String TAG = "UploaderAlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive");

/*        String driverName = intent.getStringExtra(Constants.DRIVER_DRIVER_ID);
        String routeName = intent.getStringExtra(Constants.DRIVER_ROUTE_ID);

        Intent newIntent = new Intent(context, RecorderService.class);
        newIntent.putExtra(Constants.DRIVER_DRIVER_ID, driverName);
        newIntent.putExtra(Constants.DRIVER_ROUTE_ID, routeName);

        context.startService(newIntent);*/

        Bundle bundle = intent.getExtras();
        int state = bundle.getInt(Constants.TRACKING_STATE_TYPE);
        Intent newIntent = new Intent(context, UploaderService.class);
        newIntent.putExtra(Constants.TRACKING_STATE_TYPE, state);

        switch (state) {
            case Constants.TRACKING_STATE_RECORDING:
                context.startService(newIntent);
                break;
            case Constants.TRACKING_STATE_PAUSED:
                // do nothing - not recording
                break;
            case Constants.TRACKING_STATE_STOPPED:
                //context.startService(newIntent);
                break;
            default:
                break;
        }

    }

}