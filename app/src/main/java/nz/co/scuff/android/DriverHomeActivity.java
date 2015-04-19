package nz.co.scuff.android;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.UUID;

import nz.co.scuff.android.gps.RecorderAlarmReceiver;
import nz.co.scuff.android.gps.RecorderService;
import nz.co.scuff.android.gps.UploaderAlarmReceiver;
import nz.co.scuff.data.family.Family;
import nz.co.scuff.data.family.Driver;
import nz.co.scuff.data.school.Route;
import nz.co.scuff.data.school.School;
import nz.co.scuff.util.Constants;
import nz.co.scuff.util.DialogHelper;
import nz.co.scuff.util.ScuffContextProvider;

public class DriverHomeActivity extends FragmentActivity {

    private static final String TAG = "DriverHomeActivity";
    private static final boolean D = true;

    private static final String EMPTY_STRING = "";
    private static final int RECORDER_ALARM = 0;
    private static final int UPLOADER_ALARM = 1;

    private GoogleMap googleMap;

    //private boolean recording = false;
    //private boolean paused = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_home);

        populateDrivers();
        //populateRoutes();

        SharedPreferences sps = getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);
        updateControls(sps.getInt(Constants.DRIVER_TRACKING_STATE, Constants.TRACKING_STATE_STOPPED));
        //recording = sharedPreferences.getInt(Constants.DRIVER_TRACKING_STATE, Constants.TRACKING_STATE_STOPPED);

        boolean initialised = sps.getBoolean(Constants.PREFERENCES_INITIALISED, false);
        if (!initialised) {
            Family family = ((ScuffContextProvider) getApplicationContext()).getFamily();
            SharedPreferences.Editor editor = sps.edit();
            editor.putBoolean(Constants.PREFERENCES_INITIALISED, true);
            editor.putInt(Constants.PREFERENCES_RECORD_LOCATION_INTERVAL_KEY, Constants.RECORD_LOCATION_INTERVAL);
            editor.putLong(Constants.DRIVER_APP_ID, family.getId());
            editor.apply();
        }

    }

    private void populateDrivers() {

        final Family family = ((ScuffContextProvider) getApplicationContext()).getFamily();
        final School school = ((ScuffContextProvider) getApplicationContext()).getSchool();

        Spinner driverSpinner = (Spinner) findViewById(R.id.driver_spinner);

        ArrayAdapter<Driver> dataAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, new ArrayList<>(family.getDriversForSchool(school)));
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        driverSpinner.setAdapter(dataAdapter);
        driverSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Driver selectedDriver = (Driver)parent.getItemAtPosition(position);
                SharedPreferences.Editor editor = getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE).edit();
                editor.putString(Constants.DRIVER_DRIVER_ID, selectedDriver.toString());
                editor.apply();
                populateRoutes(school, selectedDriver);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

    }

    private void populateRoutes(School school, Driver driver) {

        Spinner routeSpinner = (Spinner)findViewById(R.id.route_spinner);
        ArrayAdapter<Route> dataAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, new ArrayList<>(driver.getRoutesForSchool(school)));
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        routeSpinner.setAdapter(dataAdapter);
        routeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences.Editor editor = getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE).edit();
                editor.putString(Constants.DRIVER_ROUTE_ID, parent.getItemAtPosition(position).toString());
                editor.apply();

                if (googleMap == null) {
                    googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.googleMap)).getMap();
                }
                if (googleMap != null) {
                    setUpMap();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void setUpMap() {

        if (D) Log.d(TAG, "Setting up map");

        this.googleMap.setMyLocationEnabled(true);
        this.googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, true);
        Location myLocation = locationManager.getLastKnownLocation(provider);

        if (D) Log.d(TAG, "My location = " + myLocation);
        if (myLocation == null) {
            // GPS not turned on? use school location
            DialogHelper.dialog(this, "GPS is not active", "Please turn GPS on or wait for a stronger signal");
            return;
        }

        /*double latitude = (myLocation == null ? this.school.getLatitude() : myLocation.getLatitude());
        double longitude = (myLocation == null ? this.school.getLongitude() : myLocation.getLongitude());
        LatLng latlng = new LatLng(latitude, longitude);*/
        LatLng driverLatlng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());

        if (D) Log.d(TAG, "Driver latlng = " + driverLatlng);

        this.googleMap.addMarker(new MarkerOptions()
                .position(driverLatlng)
                .title("Bus position")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_icon)));
        this.googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(driverLatlng, 15));

    }

    public void recordJourney(View v) {
        Log.d(TAG, "recordJourney");

        if (!checkIfGooglePlayEnabled()) {
            DialogHelper.errorToast(this, "Google Maps is not available, please try again in a few moments");
            return;
        }

        SharedPreferences sps = getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);
        int currentState = sps.getInt(Constants.DRIVER_TRACKING_STATE, Constants.TRACKING_STATE_STOPPED);

        switch (currentState) {
            case Constants.TRACKING_STATE_STOPPED:
                // start a new journey
                String journeyId = ((ScuffContextProvider) getApplicationContext()).getFamily().getId() + ":" +
                        sps.getString(Constants.DRIVER_DRIVER_ID, "ERROR") + ":" +
                        DateTime.now().getMillis();
                sps.edit().putFloat(Constants.DRIVER_ACCUMULATED_DISTANCE, 0f)
                        .putBoolean(Constants.PREFERENCES_INITIALISED, true)
                        .putString(Constants.DRIVER_JOURNEY_ID, journeyId)
                        .apply();

                startRecording();
                startUploading();

                sps.edit().putInt(Constants.DRIVER_TRACKING_STATE, Constants.TRACKING_STATE_RECORDING)
                        .apply();
                break;
            case Constants.TRACKING_STATE_RECORDING:
                // already recording so pause
                stopRecording();
                stopUploading();
                sps.edit().putInt(Constants.DRIVER_TRACKING_STATE, Constants.TRACKING_STATE_PAUSED)
                        .apply();
                break;
            case Constants.TRACKING_STATE_PAUSED:
                // paused journey, resume
                // recording and paused -> resume
                startRecording();
                sps.edit().putInt(Constants.DRIVER_TRACKING_STATE, Constants.TRACKING_STATE_RECORDING)
                        .apply();
                break;
            default:

        }
        int newState = sps.getInt(Constants.DRIVER_TRACKING_STATE, Constants.TRACKING_STATE_STOPPED);
        updateControls(newState);


/*        if (recording && !paused) {
            // pause alarm
            recorderIntent.putExtra(Constants.TRACKING_STATE_TYPE, Constants.TRACKING_STATE_PAUSED);
            alarmManager.cancel(recorderAlarmIntent);
            recordButton.setBackgroundResource(R.drawable.orange_button);
            recordButton.setText(R.string.resume_button);
            paused = true;
        } else {
            // recording and paused -> resume
            // not recording -> start
            int recordInterval = sps.getInt(Constants.PREFERENCES_RECORD_LOCATION_INTERVAL_KEY,
                    Constants.RECORD_LOCATION_INTERVAL);
            recorderIntent.putExtra(Constants.TRACKING_STATE_TYPE, Constants.TRACKING_STATE_RECORDING);
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
            sps.edit().putBoolean(Constants.DRIVER_TRACKING_STATE, true)
                    .putFloat(Constants.DRIVER_ACCUMULATED_DISTANCE, 0f)
                    .putBoolean(Constants.PREFERENCES_INITIALISED, true)
                    .putString(Constants.DRIVER_JOURNEY_ID, UUID.randomUUID().toString())
                    .apply();

            // start uploader service via alarm
            Intent uploaderIntent = new Intent(this, UploaderAlarmReceiver.class);
            PendingIntent uploaderAlarmIntent = PendingIntent.getBroadcast(this, UPLOADER_ALARM, uploaderIntent, 0);

            int uploadInterval = sps.getInt(Constants.PREFERENCES_UPLOAD_LOCATION_INTERVAL_KEY,
                    Constants.UPLOAD_LOCATION_INTERVAL);
            uploaderIntent.putExtra(Constants.TRACKING_STATE_TYPE, Constants.TRACKING_STATE_RECORDING);

            alarmManager.setRepeating(
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime(),
                    uploadInterval * 1000, // 1000 = 1 second
                    uploaderAlarmIntent);
            recording = true;
        }*/

    }

    private void startRecording() {

        AlarmManager alarmManager = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);

        SharedPreferences sps = getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);
        int recordInterval = sps.getInt(Constants.PREFERENCES_RECORD_LOCATION_INTERVAL_KEY,
                Constants.RECORD_LOCATION_INTERVAL);

        Intent recorderIntent = new Intent(this, RecorderAlarmReceiver.class);
        PendingIntent recorderAlarmIntent = PendingIntent.getBroadcast(this, RECORDER_ALARM, recorderIntent, 0);
        recorderIntent.putExtra(Constants.TRACKING_STATE_TYPE, Constants.TRACKING_STATE_RECORDING);
        // start alarm to record location data to local db
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(),
                recordInterval * 1000, recorderAlarmIntent);
    }

    private void startUploading() {

        AlarmManager alarmManager = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);

        SharedPreferences sps = getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);
        int uploadInterval = sps.getInt(Constants.PREFERENCES_UPLOAD_LOCATION_INTERVAL_KEY,
                Constants.UPLOAD_LOCATION_INTERVAL);

        Intent uploaderIntent = new Intent(this, UploaderAlarmReceiver.class);
        PendingIntent uploaderAlarmIntent = PendingIntent.getBroadcast(this, UPLOADER_ALARM, uploaderIntent, 0);
        uploaderIntent.putExtra(Constants.TRACKING_STATE_TYPE, Constants.TRACKING_STATE_RECORDING);
        // start alarm to upload data to server
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(),
                uploadInterval * 1000, uploaderAlarmIntent);
    }

    private void stopRecording() {

        AlarmManager alarmManager = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);

        Intent recorderIntent = new Intent(this, RecorderAlarmReceiver.class);
        PendingIntent recorderAlarmIntent = PendingIntent.getBroadcast(this, RECORDER_ALARM, recorderIntent, 0);
        alarmManager.cancel(recorderAlarmIntent);
    }

    private void stopUploading() {

        AlarmManager alarmManager = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);

        Intent uploaderIntent = new Intent(this, UploaderAlarmReceiver.class);
        PendingIntent uploaderAlarmIntent = PendingIntent.getBroadcast(this, UPLOADER_ALARM, uploaderIntent, 0);
        alarmManager.cancel(uploaderAlarmIntent);
    }

    public void finaliseJourney(View v) {
        Log.d(TAG, "finaliseJourney");

        if (!checkIfGooglePlayEnabled()) {
            DialogHelper.errorToast(this, "Google Maps is not available, please try again in a few moments");
            return;
        }

        SharedPreferences sps = getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);

        stopRecording();
        // finalise journey
        Intent newIntent = new Intent(this, RecorderService.class);
        newIntent.putExtra(Constants.TRACKING_STATE_TYPE, Constants.TRACKING_STATE_STOPPED);
        newIntent.putExtra(Constants.DRIVER_JOURNEY_ID, sps.getString(Constants.DRIVER_JOURNEY_ID, EMPTY_STRING));
        startService(newIntent);

/*        AlarmManager alarmManager = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);

        Intent recorderIntent = new Intent(this, RecorderAlarmReceiver.class);
        recorderIntent.putExtra(Constants.TRACKING_STATE_TYPE, Constants.TRACKING_STATE_STOPPED);
        PendingIntent recorderAlarmIntent = PendingIntent.getBroadcast(this, RECORDER_ALARM, recorderIntent, 0);
        // stop alarm
        alarmManager.cancel(recorderAlarmIntent);*/
        // manually finalise journey


        // stop service to read location data and send to server
        stopUploading();
        /*Intent uploaderIntent = new Intent(this, UploaderAlarmReceiver.class);
        uploaderIntent.putExtra(Constants.TRACKING_STATE_TYPE, Constants.TRACKING_STATE_STOPPED);
        PendingIntent uploaderAlarmIntent = PendingIntent.getBroadcast(this, UPLOADER_ALARM, uploaderIntent, 0);
        // stop alarm
        alarmManager.cancel(uploaderAlarmIntent);*/

        sps.edit().putInt(Constants.DRIVER_TRACKING_STATE, Constants.TRACKING_STATE_STOPPED)
                .putString(Constants.DRIVER_JOURNEY_ID, EMPTY_STRING)
                .apply();
        updateControls(Constants.TRACKING_STATE_STOPPED);

        //recording = false;
    }

/*    private void startJourney() {
        Log.d(TAG, "startJourney");

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);
        int interval = sharedPreferences.getInt(Constants.PREFERENCES_RECORD_LOCATION_INTERVAL_KEY, Constants.RECORD_LOCATION_INTERVAL);
        int journeyId = sharedPreferences.getInt(Constants.DRIVER_JOURNEY_ID, 0);

        Intent intent = new Intent(this, JourneyAlarmReceiver.class);
        intent.putExtra(Constants.TRACKING_STATE_TYPE, Constants.TRACKING_STATE_RECORDING);
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
        intent.putExtra(Constants.TRACKING_STATE_TYPE, Constants.TRACKING_STATE_STOPPED);
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
            editor.putBoolean(Constants.DRIVER_TRACKING_STATE, false)
                    .putString(Constants.DRIVER_JOURNEY_ID, EMPTY_STRING);
            stopJourney();
        } else {
            recording = true;
            editor.putBoolean(Constants.DRIVER_TRACKING_STATE, true)
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

    private void updateControls(int state) {
        Log.d(TAG, "update controls to state="+state);

        Button start = (Button) findViewById(R.id.record_tracking_button);
        Button stop = (Button) findViewById(R.id.stop_tracking_button);

        switch( state ) {
            case Constants.TRACKING_STATE_RECORDING:
                start.setEnabled(true);
                start.setBackgroundResource(R.drawable.green_button);
                start.setText(R.string.pause_button);
                stop.setEnabled(true);
                break;
            case Constants.TRACKING_STATE_PAUSED:
                start.setEnabled(true);
                start.setBackgroundResource(R.drawable.orange_button);
                start.setText(R.string.resume_button);
                stop.setEnabled(true);
                break;
            case Constants.TRACKING_STATE_STOPPED:
                start.setEnabled(true);
                start.setBackgroundResource(R.drawable.green_button);
                start.setText(R.string.start_button);
                stop.setEnabled(false);
                break;
            default:
                Log.e(TAG, "Shouldn't happen");
        }

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

        //updateControls();

        //setTrackingButtonState();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
