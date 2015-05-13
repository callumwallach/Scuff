package nz.co.scuff.android.ui;

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
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.joda.time.DateTime;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Set;

import de.greenrobot.event.EventBus;
import nz.co.scuff.android.R;
import nz.co.scuff.android.data.ScuffDatasource;
import nz.co.scuff.android.service.DriverAlarmReceiver;
import nz.co.scuff.android.service.DriverIntentService;
import nz.co.scuff.android.ui.fragment.ChildrenFragment;
import nz.co.scuff.android.util.CommandType;
import nz.co.scuff.android.util.LocationEvent;
import nz.co.scuff.data.family.Passenger;
import nz.co.scuff.data.util.TrackingState;
import nz.co.scuff.data.journey.Journey;
import nz.co.scuff.data.school.Route;
import nz.co.scuff.android.util.Constants;
import nz.co.scuff.android.util.DialogHelper;
import nz.co.scuff.android.util.ScuffApplication;

public class DriverHomeActivity extends FragmentActivity
        implements ChildrenFragment.OnFragmentInteractionListener {

    private static final String TAG = "DriverHomeActivity";
    private static final boolean D = true;

    private static final int DRIVER_ALARM = 0;

    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            // restore state
            Journey savedState = (Journey)savedInstanceState.getSerializable(Constants.JOURNEY_KEY);
            assert(savedState != null);
            assert(!savedState.getState().equals(TrackingState.COMPLETED));
            ((ScuffApplication) this.getApplication()).setJourney(savedState);
        }
        setContentView(R.layout.activity_driver_home);

        initialiseMap();

        // TODO check lifecycles and object creation etc
        ScuffApplication scuffContext = (ScuffApplication)getApplication();
        Route route = scuffContext.getDriver().getScheduledRoute();
        ((TextView) findViewById(R.id.route_label)).setText("Route: " + route.getName());

        Set<Passenger> children = route.getSchool().getPassengers();

        ChildrenFragment childrenFragment = ChildrenFragment.newInstance(new ArrayList<>(children));
        getSupportFragmentManager().beginTransaction().replace(R.id.mapSlideOver, childrenFragment).commit();

        Journey journey = scuffContext.getJourney();

        // if this.journey == null check database for incomplete journey
        // TODO complete any found or delete them
        if (journey == null) {
            for (Journey j : Journey.findIncomplete()) {
                // TODO restart tracking based on how old journey is?
                // TODO connect up GPSBootReceiver
                // clean up incomplete journeys - close/delete them
                j.setState(TrackingState.COMPLETED);
                ScuffDatasource.cleanUpIncompleteJourney(j);
            }
        }

        //populateRoutes(scuffContext.getSchool(), scuffContext.getDriver());

        updateControls(journey == null ? TrackingState.COMPLETED : journey.getState());

        SharedPreferences sps = getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);
        boolean initialised = sps.getBoolean(Constants.PREFERENCES_INITIALISED, false);
        if (!initialised) {
            sps.edit().putBoolean(Constants.PREFERENCES_INITIALISED, true)
                    .putInt(Constants.PREFERENCES_RECORD_LOCATION_INTERVAL_KEY, Constants.RECORD_LOCATION_INTERVAL)
                    .apply();
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // save journey state if one in progress
        Journey journey = ((ScuffApplication) this.getApplication()).getJourney();
        if (journey != null) {
            assert(!journey.getState().equals(TrackingState.COMPLETED));
            outState.putSerializable(Constants.JOURNEY_KEY, journey);
        }
    }

/*    private void populateRoutes(School school, Parent driver) {

        Spinner routeSpinner = (Spinner)findViewById(R.id.route_spinner);
        ArrayAdapter<Route> dataAdapter = new ArrayAdapter<>(this,
                // TODO get routes i am driving for as per schedule
                android.R.layout.simple_spinner_item, new ArrayList<>(driver.getRoutesForSchool(school)));
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        routeSpinner.setAdapter(dataAdapter);
        routeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> driver, View view, int position, long id) {}

            @Override
            public void onNothingSelected(AdapterView<?> driver) {}
        });

    }*/

    private void initialiseMap() {
        if (D) Log.d(TAG, "Initialise map");

        if (googleMap == null) {
            googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.googleMap)).getMap();
        }

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
        LatLng myLatlng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());

        if (D) Log.d(TAG, "My latlng = " + myLatlng);

        this.googleMap.clear();
        this.googleMap.addMarker(new MarkerOptions()
                .position(myLatlng)
                .title("Bus location")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_icon)));
        this.googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLatlng, 15));
        this.googleMap.addCircle(new CircleOptions().center(myLatlng).radius(myLocation.getAccuracy()));

    }

    private void updateMap(Location location) {
        if (D) Log.d(TAG, "Updating map with location="+location);

        LatLng busLatlng = new LatLng(location.getLatitude(), location.getLongitude());

        this.googleMap.clear();
        this.googleMap.addMarker(new MarkerOptions()
                .position(busLatlng)
                .title("Bus position")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_icon)));
        this.googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(busLatlng, 15));

    }

    public void onEventMainThread(LocationEvent event) {
        if (D) Log.d(TAG, "Main thread message location event="+event);
        // update new location on map
        updateMap(event.getLocation());
    }

    public void recordJourney(View v) {
        if (D) Log.d(TAG, "recordJourney");

        if (!checkIfGooglePlayEnabled()) {
            DialogHelper.errorToast(this, "Google Maps is not available, please try again in a " +
                    "few moments or check your connection if the problem persists");
            return;
        }

        Journey journey = ((ScuffApplication) this.getApplication()).getJourney();
        if (journey == null) {
            // create new journey
            ScuffApplication scuffContext = ((ScuffApplication) getApplicationContext());

            // TODO check location to ensure they are near start of route

            long nowMillis = DateTime.now().getMillis();
            String journeyId = scuffContext.getAppId() + ":" + nowMillis;
            //Driver driver = (Driver)((Spinner) findViewById(R.id.driver_spinner)).getSelectedItem();
            //Route route = (Route)((Spinner) findViewById(R.id.route_spinner)).getSelectedItem();

            journey = new Journey();
            journey.setJourneyId(journeyId);
            journey.setAppId(scuffContext.getAppId());
            journey.setSchool(scuffContext.getSchool());
            journey.setDriver(scuffContext.getDriver());
            journey.setRoute(scuffContext.getDriver().getScheduledRoute());
            journey.setSource("Android");
            journey.setTotalDistance(0);
            journey.setTotalDuration(0);
            journey.setCreated(new Timestamp(nowMillis));
            journey.setCompleted(null);
            journey.setState(TrackingState.COMPLETED);
            // cache in app state
            ((ScuffApplication) this.getApplication()).setJourney(journey);
            if (D) Log.d(TAG, "new journey id=" + journey.getId());
        }

        switch (journey.getState()) {
            case COMPLETED:
                // start journey -> send journey + initial waypoint
                journey.setState(TrackingState.RECORDING);
                startJourney();
                break;
            case RECORDING:
                // already recording so pause
                // pause journey -> stop waypoint updates
                journey.setState(TrackingState.PAUSED);
                pauseJourney();
                break;
            case PAUSED:
                // recording and paused -> resume
                // continue journey -> continue waypoints
                journey.setState(TrackingState.RECORDING);
                continueJourney();
                break;
            default:

        }
        updateControls(journey.getState());

    }

    private void startJourney() {
        if (D) Log.d(TAG, "startJourney");

        AlarmManager alarmManager = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);

        SharedPreferences sps = getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);
        int recordInterval = sps.getInt(Constants.PREFERENCES_RECORD_LOCATION_INTERVAL_KEY,
                Constants.RECORD_LOCATION_INTERVAL);

        // processLocation direct for journey start
        Intent driverIntent = new Intent(this, DriverIntentService.class);
        driverIntent.putExtra(Constants.JOURNEY_COMMAND_KEY, CommandType.START);
        startService(driverIntent);

        // followed by periodic updates of waypoints (uses LocationIntentService)
        Intent alarmIntent = new Intent(this, DriverAlarmReceiver.class);
        PendingIntent pendingAlarmIntent = PendingIntent.getBroadcast(this, DRIVER_ALARM, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + (recordInterval * 1000),
                recordInterval * 1000, pendingAlarmIntent);

    }

    public void pauseJourney() {
        if (D) Log.d(TAG, "pauseJourney");

        // pause (cancel) local location update requests
        AlarmManager alarmManager = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(this, DriverAlarmReceiver.class);
        PendingIntent pendingAlarmIntent = PendingIntent.getBroadcast(this, DRIVER_ALARM, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.cancel(pendingAlarmIntent);

        // send pause command to server
        Intent newIntent = new Intent(this, DriverIntentService.class);
        newIntent.putExtra(Constants.JOURNEY_COMMAND_KEY, CommandType.PAUSE);
        startService(newIntent);

    }

    public void continueJourney() {
        if (D) Log.d(TAG, "continueJourney");

        AlarmManager alarmManager = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);

        SharedPreferences sps = getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);
        int recordInterval = sps.getInt(Constants.PREFERENCES_RECORD_LOCATION_INTERVAL_KEY,
                Constants.RECORD_LOCATION_INTERVAL);

        // processLocation direct for journey continue
        Intent driverIntent = new Intent(this, DriverIntentService.class);
        driverIntent.putExtra(Constants.JOURNEY_COMMAND_KEY, CommandType.CONTINUE);
        startService(driverIntent);

        // followed by periodic updates of waypoints (uses LocationIntentService)
        Intent alarmIntent = new Intent(this, DriverAlarmReceiver.class);
        PendingIntent pendingAlarmIntent = PendingIntent.getBroadcast(this, DRIVER_ALARM, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + (recordInterval * 1000),
                recordInterval * 1000, pendingAlarmIntent);

    }

    public void stopJourney(View v) {
        Log.d(TAG, "stopJourney");

        // check as direct from ui
        // TODO more robust
        if (!checkIfGooglePlayEnabled()) {
            DialogHelper.errorToast(this, "Google Maps is not available, please try again in a few moments");
            return;
        }

        // cancel local location update requests
        AlarmManager alarmManager = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(this, DriverAlarmReceiver.class);
        PendingIntent pendingAlarmIntent = PendingIntent.getBroadcast(this, DRIVER_ALARM, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.cancel(pendingAlarmIntent);

        ((ScuffApplication) this.getApplication()).getJourney().setState(TrackingState.COMPLETED);

        // signal stop journey to server
        Intent newIntent = new Intent(this, DriverIntentService.class);
        newIntent.putExtra(Constants.JOURNEY_COMMAND_KEY, CommandType.STOP);
        startService(newIntent);

        updateControls(TrackingState.COMPLETED);

    }

    private boolean checkIfGooglePlayEnabled() {
        return GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS;
    }

    private void updateControls(TrackingState state) {
        Log.d(TAG, "update controls to state="+state);

        Button start = (Button) findViewById(R.id.record_tracking_button);
        Button stop = (Button) findViewById(R.id.stop_tracking_button);

        switch( state ) {
            case RECORDING:
                start.setEnabled(true);
                start.setBackgroundResource(R.drawable.green_button);
                start.setText(R.string.pause_button);
                stop.setEnabled(true);
                break;
            case PAUSED:
                start.setEnabled(true);
                start.setBackgroundResource(R.drawable.yellow_button);
                start.setText(R.string.resume_button);
                stop.setEnabled(true);
                break;
            case COMPLETED:
                start.setEnabled(true);
                start.setBackgroundResource(R.drawable.green_button);
                start.setText(R.string.start_button);
                stop.setEnabled(false);
                break;
        }

    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();

        EventBus.getDefault().register(this);

/*        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            // Create a dialog here that requests the user to enable GPS, and
            // TODO use an intent with the android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS action
            // to take the user to the Settings screen to enable GPS when they click "OK"
            DialogHelper.dialog(this, "GPS is not active", "Please turn GPS on or wait for a stronger signal");
        }*/
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void onFragmentInteraction(Passenger child) {
        DialogHelper.toast(this, child.getFirstName());
    }
}
