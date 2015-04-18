package nz.co.scuff.android;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.util.ArrayList;
import java.util.UUID;

import nz.co.scuff.android.gps.RecorderAlarmReceiver;
import nz.co.scuff.android.gps.UploaderAlarmReceiver;
import nz.co.scuff.android.gps.UploaderService;
import nz.co.scuff.data.family.Family;
import nz.co.scuff.data.family.Parent;
import nz.co.scuff.data.school.Route;
import nz.co.scuff.util.Constants;
import nz.co.scuff.util.DialogHelper;
import nz.co.scuff.util.ScuffContextProvider;

public class DriverHomeActivity extends FragmentActivity {

    private static final String TAG = "DriverHomeActivity";
    private static final boolean D = true;

    private static final String EMPTY_STRING = "";
    private static final int RECORDER_ALARM = 0;
    private static final int UPLOADER_ALARM = 1;

    private boolean recording = false;
    private boolean paused = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_home);

        populateDrivers();
        populateRoutes();

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);
        recording = sharedPreferences.getBoolean(Constants.DRIVER_CURRENTLY_TRACKING, false);

        boolean initialised = sharedPreferences.getBoolean(Constants.PREFERENCES_INITIALISED, false);
        if (!initialised) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(Constants.PREFERENCES_INITIALISED, true);
            editor.putInt(Constants.PREFERENCES_RECORD_LOCATION_INTERVAL_KEY, Constants.RECORD_LOCATION_INTERVAL);
            editor.putString(Constants.DRIVER_APP_ID, UUID.randomUUID().toString());
            editor.apply();
        }

    }

    private void populateDrivers() {

        Family family = ((ScuffContextProvider) getApplicationContext()).getFamily();
        Spinner driverSpinner = (Spinner) findViewById(R.id.driver_spinner);
        ArrayAdapter<Parent> dataAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, new ArrayList<>(family.getParents()));
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        driverSpinner.setAdapter(dataAdapter);
        driverSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences.Editor editor = getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE).edit();
                editor.putString(Constants.DRIVER_DRIVER_ID, parent.getItemAtPosition(position).toString());
                editor.apply();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

    }

    private void populateRoutes() {

        Family family = ((ScuffContextProvider)getApplicationContext()).getFamily();
        Spinner routeSpinner = (Spinner)findViewById(R.id.route_spinner);
        ArrayAdapter<Route> dataAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, new ArrayList<>(family.getSchools().iterator().next().getRoutes()));
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        routeSpinner.setAdapter(dataAdapter);
        routeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences.Editor editor = getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE).edit();
                editor.putString(Constants.DRIVER_ROUTE_ID, parent.getItemAtPosition(position).toString());
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public void recordJourney(View v) {
        Log.d(TAG, "recordJourney");

        if (!checkIfGooglePlayEnabled()) {
            DialogHelper.errorToast(this, "Google Maps is not available, please try again in a few moments");
            return;
        }

        SharedPreferences sps = getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);
        AlarmManager alarmManager = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);

        Intent recorderIntent = new Intent(this, RecorderAlarmReceiver.class);
        PendingIntent recorderAlarmIntent = PendingIntent.getBroadcast(this, RECORDER_ALARM, recorderIntent, 0);

        Button recordButton = (Button)v.findViewById(R.id.record_tracking_button);

        if (recording && !paused) {
            // pause alarm
            recorderIntent.putExtra(Constants.TRACKING_STATE_TYPE, Constants.TRACKING_STATE_PAUSE);
            alarmManager.cancel(recorderAlarmIntent);
            recordButton.setBackgroundResource(R.drawable.orange_button);
            recordButton.setText(R.string.resume_button);
            paused = true;
        } else {
            // recording and paused -> resume
            // not recording -> start
            int recordInterval = sps.getInt(Constants.PREFERENCES_RECORD_LOCATION_INTERVAL_KEY,
                    Constants.RECORD_LOCATION_INTERVAL);
            recorderIntent.putExtra(Constants.TRACKING_STATE_TYPE, Constants.TRACKING_STATE_RECORD);
            // start alarm to record location data to local db
            alarmManager.setRepeating(
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime(),
                    recordInterval * 1000, // 1000 = 1 second
                    recorderAlarmIntent);
            if (paused) {
                recordButton.setBackgroundResource(R.drawable.green_button);
                recordButton.setText(R.string.pause_button);
                paused = false;
            }
        }

        if (!recording) {
            // initialise
            sps.edit().putBoolean(Constants.DRIVER_CURRENTLY_TRACKING, true)
                    .putFloat(Constants.DRIVER_ACCUMULATED_DISTANCE, 0f)
                    .putBoolean(Constants.PREFERENCES_INITIALISED, true)
                    .putString(Constants.DRIVER_JOURNEY_ID, UUID.randomUUID().toString())
                    .apply();

            // start uploader service via alarm
            Intent uploaderIntent = new Intent(this, UploaderAlarmReceiver.class);
            PendingIntent uploaderAlarmIntent = PendingIntent.getBroadcast(this, UPLOADER_ALARM, uploaderIntent, 0);

            int uploadInterval = sps.getInt(Constants.PREFERENCES_UPLOAD_LOCATION_INTERVAL_KEY,
                    Constants.UPLOAD_LOCATION_INTERVAL);
            uploaderIntent.putExtra(Constants.TRACKING_STATE_TYPE, Constants.TRACKING_STATE_RECORD);

            alarmManager.setRepeating(
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime(),
                    uploadInterval * 1000, // 1000 = 1 second
                    uploaderAlarmIntent);
            recording = true;
        }

    }

    public void finaliseJourney(View v) {
        Log.d(TAG, "finaliseJourney");

        if (!checkIfGooglePlayEnabled()) {
            DialogHelper.errorToast(this, "Google Maps is not available, please try again in a few moments");
            return;
        }

        SharedPreferences sps = getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);

        AlarmManager alarmManager = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);

        Intent recorderIntent = new Intent(this, RecorderAlarmReceiver.class);
        recorderIntent.putExtra(Constants.TRACKING_STATE_TYPE, Constants.TRACKING_STATE_FINALISE);
        PendingIntent recorderAlarmIntent = PendingIntent.getBroadcast(this, RECORDER_ALARM, recorderIntent, 0);
        // stop alarm
        alarmManager.cancel(recorderAlarmIntent);

        // stop service to read location data and send to server
        Intent uploaderIntent = new Intent(this, UploaderAlarmReceiver.class);
        uploaderIntent.putExtra(Constants.TRACKING_STATE_TYPE, Constants.TRACKING_STATE_FINALISE);
        PendingIntent uploaderAlarmIntent = PendingIntent.getBroadcast(this, UPLOADER_ALARM, uploaderIntent, 0);
        // stop alarm
        alarmManager.cancel(uploaderAlarmIntent);

        sps.edit().putBoolean(Constants.DRIVER_CURRENTLY_TRACKING, false)
                .putString(Constants.DRIVER_JOURNEY_ID, EMPTY_STRING).apply();

        recording = false;
    }

/*    private void startJourney() {
        Log.d(TAG, "startJourney");

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);
        int interval = sharedPreferences.getInt(Constants.PREFERENCES_RECORD_LOCATION_INTERVAL_KEY, Constants.RECORD_LOCATION_INTERVAL);
        int journeyId = sharedPreferences.getInt(Constants.DRIVER_JOURNEY_ID, 0);

        Intent intent = new Intent(this, JourneyAlarmReceiver.class);
        intent.putExtra(Constants.TRACKING_STATE_TYPE, Constants.TRACKING_STATE_RECORD);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, journeyId, intent, 0);

        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime(),
                interval * 1000, // 1000 = 1 second
                pendingIntent);
    }

    private void stopJourney() {
        Log.d(TAG, "stopJourney");

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);
        int journeyId = sharedPreferences.getInt(Constants.DRIVER_JOURNEY_ID, 0);

        Intent intent = new Intent(this, JourneyAlarmReceiver.class);
        intent.putExtra(Constants.TRACKING_STATE_TYPE, Constants.TRACKING_STATE_FINALISE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, journeyId, intent, 0);

        AlarmManager alarmManager = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);

    }*/

    // called when trackingButton is tapped
/*    protected void trackLocation(View v) {

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (!checkIfGooglePlayEnabled()) {
            return;
        }

        if (recording) {
            recording = false;
            editor.putBoolean(Constants.DRIVER_CURRENTLY_TRACKING, false)
                    .putString(Constants.DRIVER_JOURNEY_ID, EMPTY_STRING);
            stopJourney();
        } else {
            recording = true;
            editor.putBoolean(Constants.DRIVER_CURRENTLY_TRACKING, true)
                    .putFloat(Constants.DRIVER_ACCUMULATED_DISTANCE, 0f)
                    .putBoolean(Constants.PREFERENCES_INITIALISED, true)
                    .putString(Constants.DRIVER_JOURNEY_ID, UUID.randomUUID().toString());
            startJourney();
        }

        editor.apply();
        setTrackingButtonState();
    }*/

    private boolean checkIfGooglePlayEnabled() {
        return GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS;
    }

/*    private void setTrackingButtonState() {
        if (recording) {
            trackingButton.setBackgroundResource(R.drawable.green_button);
            trackingButton.setTextColor(Color.BLACK);
            trackingButton.setText(R.string.stop_button);
        } else {
            trackingButton.setBackgroundResource(R.drawable.red_button);
            trackingButton.setTextColor(Color.WHITE);
            trackingButton.setText(R.string.start_button);
        }
    }*/

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();

        //setTrackingButtonState();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
