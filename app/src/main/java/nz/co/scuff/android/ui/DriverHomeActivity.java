package nz.co.scuff.android.ui;

import android.app.AlarmManager;
import android.app.FragmentManager;
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
import org.joda.time.DateTimeUtils;

import java.sql.Timestamp;
import java.util.ArrayList;

import nz.co.scuff.android.R;
import nz.co.scuff.android.gps.RecorderAlarmReceiver;
import nz.co.scuff.android.gps.CommandRecorderService;
import nz.co.scuff.android.util.TrackingState;
import nz.co.scuff.data.family.Family;
import nz.co.scuff.data.family.Driver;
import nz.co.scuff.data.journey.Journey;
import nz.co.scuff.data.school.Route;
import nz.co.scuff.data.school.School;
import nz.co.scuff.android.util.Constants;
import nz.co.scuff.android.util.DialogHelper;
import nz.co.scuff.android.util.ScuffApplication;

public class DriverHomeActivity extends FragmentActivity {

    private static final String TAG = "DriverHomeActivity";
    private static final boolean D = true;

    private static final String EMPTY_STRING = "";
    private static final int RECORDER_ALARM = 0;

    private RetainedFragment dataFragment;
    private Journey journey;

    private GoogleMap googleMap;

    //private boolean recording = false;
    //private boolean paused = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_home);

        // handle lifecycle events, recreate state
        // http://developer.android.com/guide/topics/resources/runtime-changes.html
        // find the retained fragment on activity restarts
        FragmentManager fm = getFragmentManager();
        dataFragment = (RetainedFragment) fm.findFragmentByTag("data");

        // create the fragment and data the first time
        if (dataFragment == null) {
            // add the fragment
            dataFragment = new RetainedFragment();
            fm.beginTransaction().add(dataFragment, "data").commit();
            // load the data from the web
            // TODO load incomplete journey (if any) from db
            // if (loadFromDB() == null ? new JourneyState() : loaded
            JourneyState journeyState = new JourneyState();
            dataFragment.setData(journeyState);
        }

        populateDrivers();
        //populateRoutes();

        SharedPreferences sps = getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);
        updateControls(sps.getInt(Constants.DRIVER_TRACKING_STATE, Constants.TRACKING_STATE_STOPPED));
        //recording = sharedPreferences.getInt(Constants.DRIVER_TRACKING_STATE, Constants.TRACKING_STATE_STOPPED);

        boolean initialised = sps.getBoolean(Constants.PREFERENCES_INITIALISED, false);
        if (!initialised) {
            Family family = ((ScuffApplication) getApplicationContext()).getFamily();
            SharedPreferences.Editor editor = sps.edit();
            editor.putBoolean(Constants.PREFERENCES_INITIALISED, true);
            editor.putInt(Constants.PREFERENCES_RECORD_LOCATION_INTERVAL_KEY, Constants.RECORD_LOCATION_INTERVAL);
            editor.putLong(Constants.DRIVER_APP_ID, family.getId());
            editor.apply();
        }

    }

    private void populateDrivers() {

        final Family family = ((ScuffApplication) getApplicationContext()).getFamily();
        final School school = ((ScuffApplication) getApplicationContext()).getSchool();

        Spinner driverSpinner = (Spinner) findViewById(R.id.driver_spinner);

        ArrayAdapter<Driver> dataAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, new ArrayList<>(family.getDriversForSchool(school)));
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        driverSpinner.setAdapter(dataAdapter);
        driverSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Driver selectedDriver = (Driver) parent.getItemAtPosition(position);
                SharedPreferences.Editor editor = getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE).edit();
                editor.putString(Constants.DRIVER_DRIVER_ID, selectedDriver.toString());
                editor.apply();
                populateRoutes(school, selectedDriver);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
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
                String journeyId = ((ScuffApplication) getApplicationContext()).getFamily().getId() + ":" +
                        sps.getString(Constants.DRIVER_DRIVER_ID, "ERROR") + ":" +
                        DateTime.now().getMillis();
                sps.edit().putBoolean(Constants.PREFERENCES_INITIALISED, true)
                        .putString(Constants.DRIVER_JOURNEY_ID, journeyId)
                        .apply();

/*                Journey journey = new Journey(journeyId, sp.getLong(Constants.DRIVER_APP_ID, -1), sp.getString(Constants.DRIVER_SCHOOL_ID, "ERROR"),
                        sp.getString(Constants.DRIVER_DRIVER_ID, "ERROR"), sp.getString(Constants.DRIVER_ROUTE_ID, "ERROR"), "Android",
                        0, 0, new Timestamp(DateTimeUtils.currentTimeMillis()), null, TrackingState.RECORDING);*/

                // start journey -> send journey initial waypoint
                startJourney();
                sps.edit().putInt(Constants.DRIVER_TRACKING_STATE, Constants.TRACKING_STATE_RECORDING)
                        .apply();
                break;
            case Constants.TRACKING_STATE_RECORDING:
                // already recording so pause
                // pause journey -> stop waypoint updates
                pauseJourney();
                sps.edit().putInt(Constants.DRIVER_TRACKING_STATE, Constants.TRACKING_STATE_PAUSED)
                        .apply();
                break;
            case Constants.TRACKING_STATE_PAUSED:
                // paused journey, resume
                // recording and paused -> resume
                // continue journey -> continue waypoints
                continueJourney();
                sps.edit().putInt(Constants.DRIVER_TRACKING_STATE, Constants.TRACKING_STATE_RECORDING)
                        .apply();
                break;
            default:

        }
        int newState = sps.getInt(Constants.DRIVER_TRACKING_STATE, Constants.TRACKING_STATE_STOPPED);
        updateControls(newState);

    }

    private void startJourney() {
        if (D) Log.d(TAG, "startJourney");

        AlarmManager alarmManager = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);

        SharedPreferences sps = getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);
        int recordInterval = sps.getInt(Constants.PREFERENCES_RECORD_LOCATION_INTERVAL_KEY,
                Constants.RECORD_LOCATION_INTERVAL);
        String journeyId = sps.getString(Constants.DRIVER_JOURNEY_ID, "ERROR");

        // processLocation direct for journey start
        Intent newIntent = new Intent(this, CommandRecorderService.class);
        newIntent.putExtra(Constants.TRACKING_ACTION_TYPE, Constants.TRACKING_ACTION_START);
        newIntent.putExtra(Constants.DRIVER_JOURNEY_ID, journeyId);
        startService(newIntent);

        // followed by periodic updates of waypoints
        Intent recorderIntent = new Intent(this, RecorderAlarmReceiver.class);
        recorderIntent.putExtra(Constants.TRACKING_STATE_TYPE, Constants.TRACKING_STATE_RECORDING);
        recorderIntent.putExtra(Constants.DRIVER_JOURNEY_ID, journeyId);
        PendingIntent recorderAlarmIntent = PendingIntent.getBroadcast(this, RECORDER_ALARM, recorderIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + (recordInterval * 1000),
                recordInterval * 1000, recorderAlarmIntent);

    }

    public void pauseJourney() {
        if (D) Log.d(TAG, "pauseJourney");

        SharedPreferences sps = getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);

        // pause (cancel) local location update requests
        AlarmManager alarmManager = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
        Intent recorderIntent = new Intent(this, RecorderAlarmReceiver.class);
        recorderIntent.putExtra(Constants.TRACKING_STATE_TYPE, Constants.TRACKING_STATE_PAUSED);
        PendingIntent recorderAlarmIntent = PendingIntent.getBroadcast(this, RECORDER_ALARM, recorderIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.cancel(recorderAlarmIntent);

        // send pause command to server
        Intent newIntent = new Intent(this, CommandRecorderService.class);
        newIntent.putExtra(Constants.TRACKING_ACTION_TYPE, Constants.TRACKING_ACTION_PAUSE);
        newIntent.putExtra(Constants.DRIVER_JOURNEY_ID, sps.getString(Constants.DRIVER_JOURNEY_ID, EMPTY_STRING));
        startService(newIntent);

    }

    public void continueJourney() {
        if (D) Log.d(TAG, "continueJourney");

        AlarmManager alarmManager = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);

        SharedPreferences sps = getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);
        int recordInterval = sps.getInt(Constants.PREFERENCES_RECORD_LOCATION_INTERVAL_KEY,
                Constants.RECORD_LOCATION_INTERVAL);
        String journeyId = sps.getString(Constants.DRIVER_JOURNEY_ID, "ERROR");

        // send pause command to server
        Intent newIntent = new Intent(this, CommandRecorderService.class);
        newIntent.putExtra(Constants.TRACKING_ACTION_TYPE, Constants.TRACKING_ACTION_CONTINUE);
        newIntent.putExtra(Constants.DRIVER_JOURNEY_ID, journeyId);
        startService(newIntent);

        // followed by periodic updates of waypoints
        Intent recorderIntent = new Intent(this, RecorderAlarmReceiver.class);
        recorderIntent.putExtra(Constants.TRACKING_STATE_TYPE, Constants.TRACKING_STATE_RECORDING);
        recorderIntent.putExtra(Constants.DRIVER_JOURNEY_ID, journeyId);
        PendingIntent recorderAlarmIntent = PendingIntent.getBroadcast(this, RECORDER_ALARM, recorderIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + (recordInterval * 1000),
                recordInterval * 1000, recorderAlarmIntent);
    }

    public void stopJourney(View v) {
        Log.d(TAG, "stopJourney");

        if (!checkIfGooglePlayEnabled()) {
            DialogHelper.errorToast(this, "Google Maps is not available, please try again in a few moments");
            return;
        }

        SharedPreferences sps = getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);

        // cancel local location update requests
        AlarmManager alarmManager = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
        Intent recorderIntent = new Intent(this, RecorderAlarmReceiver.class);
        recorderIntent.putExtra(Constants.TRACKING_STATE_TYPE, Constants.TRACKING_STATE_STOPPED);
        PendingIntent recorderAlarmIntent = PendingIntent.getBroadcast(this, RECORDER_ALARM, recorderIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.cancel(recorderAlarmIntent);

        // signal stop journey to server
        Intent newIntent = new Intent(this, CommandRecorderService.class);
        newIntent.putExtra(Constants.TRACKING_ACTION_TYPE, Constants.TRACKING_ACTION_STOP);
        newIntent.putExtra(Constants.DRIVER_JOURNEY_ID, sps.getString(Constants.DRIVER_JOURNEY_ID, EMPTY_STRING));
        startService(newIntent);

        sps.edit().putInt(Constants.DRIVER_TRACKING_STATE, Constants.TRACKING_STATE_STOPPED)
                .remove(Constants.DRIVER_JOURNEY_ID)
                .apply();
        updateControls(Constants.TRACKING_STATE_STOPPED);

    }

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
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // store the data in the fragment
        // TODO not necessary as updating JourneyState as we go. Perhaps just use Journey
        //dataFragment.setData();
    }

}
