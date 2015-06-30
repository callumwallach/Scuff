package nz.co.scuff.android.ui;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

import org.joda.time.DateTimeUtils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import de.greenrobot.event.EventBus;
import nz.co.scuff.android.R;
import nz.co.scuff.android.data.ScuffDatasource;
import nz.co.scuff.android.data.TicketQueue;
import nz.co.scuff.android.event.RecordEvent;
import nz.co.scuff.android.event.TicketEvent;
import nz.co.scuff.android.service.DriverAlarmReceiver;
import nz.co.scuff.android.service.DriverIntentService;
import nz.co.scuff.android.ui.fragment.ChildrenFragment;
import nz.co.scuff.android.util.CommandType;
import nz.co.scuff.data.base.Coordinator;
import nz.co.scuff.data.family.Child;
import nz.co.scuff.data.institution.Route;
import nz.co.scuff.data.journey.Stamp;
import nz.co.scuff.data.journey.Ticket;
import nz.co.scuff.data.util.TrackingState;
import nz.co.scuff.data.journey.Journey;
import nz.co.scuff.android.util.Constants;
import nz.co.scuff.android.util.DialogHelper;
import nz.co.scuff.android.util.ScuffApplication;

public class DriverHomeActivity extends BaseActionBarActivity
        implements ChildrenFragment.OnFragmentInteractionListener {

    private static final String TAG = "DriverHomeActivity";
    private static final boolean D = true;

    private static final int DRIVER_ALARM = 0;

    private Coordinator owner;
    private Coordinator agent;
    private Route route;

    private Set<Ticket> receivedTickets = new HashSet<>();

    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (D) Log.d(TAG, "onCreate");

        ScuffApplication scuffApplication = (ScuffApplication)getApplication();
        Journey journey = null;

        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            // restore state
            if (D) Log.d(TAG, "restoring state");
            long journeyId = savedInstanceState.getLong(Constants.JOURNEY_ID_KEY);
            journey = Journey.findById(journeyId);
            this.owner = journey.getOwner();
            this.agent = journey.getAgent();
            this.route = journey.getRoute();
            // TODO get tickets from DB
            scuffApplication.setJourney(journey);
        } else {
            Bundle extras = getIntent().getExtras();
            this.owner = Coordinator.findById(extras.getLong(Constants.OWNER_ID_KEY));
            this.agent = Coordinator.findById(extras.getLong(Constants.AGENT_ID_KEY));
            this.route = Route.findById(extras.getLong(Constants.ROUTE_ID_KEY));
        }
        setContentView(R.layout.activity_driver_home);

        initialiseMap();

        // TODO check lifecycles and object creation etc
/*
        // TODO alter to getCoordinator().getFriends().getChildren();
        Set<Child> children = scuffContext.getCoordinator().getChildren();
        ChildrenFragment childrenFragment = ChildrenFragment.newInstance(new ArrayList<>(children));
        getSupportFragmentManager().beginTransaction().replace(R.id.mapSlideOver, childrenFragment).commit();
*/
/*        Set<Ticket> issuedTickets = journey.getIssuedTickets();
        Set<Ticket> stampedTickets = journey.getStampedTickets();
        ChildrenFragment childrenFragment = ChildrenFragment.newInstance(new ArrayList<>(issuedTickets));
        getSupportFragmentManager().beginTransaction().replace(R.id.mapSlideOver, childrenFragment).commit();*/

        // if this.journey == null check database for incomplete journey
        // TODO complete any found or delete them
        if (journey == null) {
            if (D) Log.d(TAG, "no active journey... checking for incomplete journeys in db");
            for (Journey j : Journey.findIncomplete()) {
                // TODO restart tracking based on how old journey is?
                // TODO connect up GPSBootReceiver
                // cancel outstanding alarms
                //stopJourney(j.getJourneyId());
                AlarmManager alarmManager = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
                Intent alarmIntent = new Intent(this, DriverAlarmReceiver.class);
                PendingIntent pendingAlarmIntent = PendingIntent.getBroadcast(this, DRIVER_ALARM, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                alarmManager.cancel(pendingAlarmIntent);
                // clean up incomplete journeys - close/delete them
                j.setState(TrackingState.COMPLETED);
                ScuffDatasource.cleanUpIncompleteJourney(j);
            }
        }

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
        if (D) Log.d(TAG, "onSaveInstanceState");

        super.onSaveInstanceState(outState);
        // save journey state if one in progress
        Journey journey = ((ScuffApplication) this.getApplication()).getJourney();
        if (journey != null) {
            outState.putLong(Constants.JOURNEY_ID_KEY, journey.getJourneyId());
        }
    }

    private void initialiseMap() {

        checkIfGooglePlayEnabled();

        if (D) Log.d(TAG, "Initialise map");

        if (googleMap == null) {
            googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.googleMap)).getMap();
        }

        // TODO change this to custom icon
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
/*        this.googleMap.addMarker(new MarkerOptions()
                .position(myLatlng)
                .title("Bus location")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_icon)));*/
        this.googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLatlng, 15));
        CircleOptions options = new CircleOptions()
                .center(myLatlng)
                .radius(myLocation.getAccuracy())
                .strokeWidth(0.5f)
                .strokeColor(Color.BLUE);
        this.googleMap.addCircle(options);

    }

/*    private void updateMap(Location location) {
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
    }*/

    public void recordJourney(View v) {
        if (D) Log.d(TAG, "recordJourney");

        if (!checkIfGooglePlayEnabled()) {
            DialogHelper.errorToast(this, "Google Maps is not available, please check your connection settings");
            return;
        }

        Journey journey = ((ScuffApplication) this.getApplication()).getJourney();
        if (journey == null) {
            // create new journey
            ScuffApplication scuffContext = ((ScuffApplication) getApplicationContext());

            // TODO check location to ensure they are near start of route

            long nowMillis = DateTimeUtils.currentTimeMillis();
            //String journeyId = scuffContext.getAppId() + ":" + nowMillis;
            //Coordinator adult = (Coordinator)((Spinner) findViewById(R.id.driver_spinner)).getSelectedItem();
            //Route route = (Route)((Spinner) findViewById(R.id.route_spinner)).getSelectedItem();

            journey = new Journey();
            //journey.setJourneyId(journeyId);
            journey.setAppId(scuffContext.getAppId());
            journey.setSource("Android");
            journey.setTotalDistance(0);
            journey.setTotalDuration(0);
            journey.setCreated(new Timestamp(nowMillis));
            journey.setCompleted(null);
            journey.setState(TrackingState.COMPLETED);

            journey.setOwner(this.owner);
            journey.setAgent(this.agent);
            journey.setGuide(scuffContext.getCoordinator());

            journey.setOrigin(this.route.getOrigin());
            journey.setDestination(this.route.getDestination());
            journey.setRoute(this.route);

            // cache in app state
            ((ScuffApplication) this.getApplication()).setJourney(journey);
            if (D) Log.d(TAG, "new journey id=" + journey.getId());
        }

        switch (journey.getState()) {
            case COMPLETED:
                // start journey -> send journey + initial waypoint
                journey.setState(TrackingState.RECORDING);
                startJourney(journey.getJourneyId());
                break;
            case RECORDING:
                // already recording so pause
                // pause journey -> stop waypoint updates
                journey.setState(TrackingState.PAUSED);
                pauseJourney(journey.getJourneyId());
                break;
            case PAUSED:
                // recording and paused -> resume
                // continue journey -> continue waypoints
                journey.setState(TrackingState.RECORDING);
                continueJourney(journey.getJourneyId());
                break;
            default:

        }
        updateControls(journey.getState());

    }

    private void startJourney(long journeyId) {
        if (D) Log.d(TAG, "startJourney journey="+journeyId);

        // processLocation direct for journey start
        Intent driverIntent = new Intent(this, DriverIntentService.class);
        driverIntent.putExtra(Constants.JOURNEY_COMMAND_KEY, CommandType.START);
        driverIntent.putExtra(Constants.JOURNEY_ID_KEY, journeyId);
        startService(driverIntent);

    }

    public void pauseJourney(long journeyId) {
        if (D) Log.d(TAG, "pauseJourney journey="+journeyId);

        // pause (cancel) local location update requests
        stopIntervalRecording(journeyId);

        // send pause command to server
        Intent newIntent = new Intent(this, DriverIntentService.class);
        newIntent.putExtra(Constants.JOURNEY_COMMAND_KEY, CommandType.PAUSE);
        newIntent.putExtra(Constants.JOURNEY_ID_KEY, journeyId);
        startService(newIntent);

    }

    public void continueJourney(long journeyId) {
        if (D) Log.d(TAG, "continueJourney journey="+journeyId);

        // processLocation direct for journey continue
        Intent driverIntent = new Intent(this, DriverIntentService.class);
        driverIntent.putExtra(Constants.JOURNEY_COMMAND_KEY, CommandType.CONTINUE);
        driverIntent.putExtra(Constants.JOURNEY_ID_KEY, journeyId);
        startService(driverIntent);

    }

    public void stopJourney(View v) {
        Journey journey = ((ScuffApplication) this.getApplication()).getJourney();
        stopJourney(journey.getJourneyId());
    }

    private void stopJourney(long journeyId) {
        if (D) Log.d(TAG, "stopJourney journey="+journeyId);

        // check as direct from ui
        if (!checkIfGooglePlayEnabled()) {
            DialogHelper.errorToast(this, "Google Maps is not available, please check your connection settings");
            return;
        }

        stopIntervalRecording(journeyId);

        // signal stop journey to server
        Intent newIntent = new Intent(this, DriverIntentService.class);
        newIntent.putExtra(Constants.JOURNEY_COMMAND_KEY, CommandType.STOP);
        newIntent.putExtra(Constants.JOURNEY_ID_KEY, journeyId);
        startService(newIntent);

        updateControls(TrackingState.COMPLETED);

    }

    private void startIntervalRecording(long journeyId) {

        AlarmManager alarmManager = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);

        SharedPreferences sps = getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);
        int recordInterval = sps.getInt(Constants.PREFERENCES_RECORD_LOCATION_INTERVAL_KEY,
                Constants.RECORD_LOCATION_INTERVAL);

        // followed by periodic updates of waypoints (uses LocationIntentService)
        Intent alarmIntent = new Intent(this, DriverAlarmReceiver.class);
        alarmIntent.putExtra(Constants.JOURNEY_ID_KEY, journeyId);
        PendingIntent pendingAlarmIntent = PendingIntent.getBroadcast(this, DRIVER_ALARM, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis() + (recordInterval * 1000),
                recordInterval * 1000, pendingAlarmIntent);

    }

    private void stopIntervalRecording(long journeyId) {

        AlarmManager alarmManager = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(this, DriverAlarmReceiver.class);
        PendingIntent pendingAlarmIntent = PendingIntent.getBroadcast(this, DRIVER_ALARM, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.cancel(pendingAlarmIntent);

    }

    private void processReceivedTickets(Collection<Ticket> tickets) {
        for (Ticket ticket : tickets) {
            this.receivedTickets.add(ticket);
            Child child = ticket.getChild();
            DialogHelper.toast(this, "Incoming passenger: "+child.getChildData().getFirstName()+" "+child.getChildData().getLastName());
            stampTicket(ticket);
        }
    }

    private void stampTicket(Ticket ticket) {
        TicketQueue.add(ticket.getTicketId());
    }

    public void onEventMainThread(RecordEvent event) {
        if (D) Log.d(TAG, "Main thread message start recording event=" + event);
        startIntervalRecording(event.getJourneyId());
    }

    public void onEventMainThread(TicketEvent event) {
        if (D) Log.d(TAG, "Main thread message ticket received event=" + event);
        processReceivedTickets(event.getTickets());
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
        if (D) Log.d(TAG, "onStop");
        EventBus.getDefault().unregister(this);

        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void onFragmentInteraction(Collection<Child> children) {
        //DialogHelper.toast(this, child.getFirstName());
    }
}
