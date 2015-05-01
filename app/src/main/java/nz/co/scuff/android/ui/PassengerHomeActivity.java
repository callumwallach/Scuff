package nz.co.scuff.android.ui;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Set;

import de.greenrobot.event.EventBus;
import nz.co.scuff.android.R;
import nz.co.scuff.android.gps.PassengerAlarmReceiver;
import nz.co.scuff.android.util.Constants;
import nz.co.scuff.android.util.LocationEvent;
import nz.co.scuff.android.util.WaypointEvent;
import nz.co.scuff.data.family.Passenger;
import nz.co.scuff.data.family.Family;
import nz.co.scuff.data.journey.Journey;
import nz.co.scuff.data.journey.Waypoint;
import nz.co.scuff.data.school.Route;
import nz.co.scuff.data.school.School;
import nz.co.scuff.android.data.JourneyDatasource;
import nz.co.scuff.android.util.DialogHelper;
import nz.co.scuff.android.util.ScuffApplication;


public class PassengerHomeActivity extends FragmentActivity
        implements SchoolsFragment.OnFragmentInteractionListener,
        ChildrenFragment.OnFragmentInteractionListener {

    private static final String TAG = "PassengerHomeActivity";
    private static final boolean D = true;

    private static final int PASSENGER_ALARM = 1;

    private Journey journey;
    private GoogleMap googleMap;
    //private School school;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_home);

        initialiseMap();
        populateRoutes();


/*        Set<School> schools = family.getSchools();
        if (schools.size() > 1) {
            SchoolsFragment dialog = SchoolsFragment.newInstance(new ArrayList<>(schools));
            dialog.show(getSupportFragmentManager(), SCHOOL_CHOICE_FRAGMENT_DIALOG);
        } else {
            this.school = schools.iterator().next();
        }*/



        Family family = ((ScuffApplication)getApplicationContext()).getFamily();
        LinearLayout mapSlideOver = (LinearLayout)findViewById(R.id.mapSlideOver);
        Set<Passenger> passengers = family.getPassengers();
/*
        ChildrenFragment childrenFragment = ChildrenFragment.newInstance(new ArrayList<>(children));
        getSupportFragmentManager().beginTransaction().add(R.id.mapSlideOver, childrenFragment).commit();
*/

        for (Passenger c : passengers) {
            Button button = new Button(this);
            String fileLocation = getFilesDir() + "/" + c.getPix();
            if (D) Log.d(TAG, "file location = "+ fileLocation);
            button.setBackground(Drawable.createFromPath(getFilesDir() + "/" + c.getPix()));
            button.setText(c.getName());
            //button.setGravity(Gravity.BOTTOM);
            button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            button.setTextColor(Color.BLACK);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.bottomMargin = 10;
            button.setLayoutParams(layoutParams);
            mapSlideOver.addView(button);
        }

    }

    private void populateRoutes() {

        School school = ((ScuffApplication)getApplicationContext()).getSchool();
        Spinner routeSpinner = (Spinner)findViewById(R.id.route_spinner);
        ArrayAdapter<Route> dataAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, new ArrayList<>(school.getRoutes()));
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        routeSpinner.setAdapter(dataAdapter);
        routeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (D) Log.d(TAG, "item " + position + " selected");
                Route selectedRoute = (Route) parent.getItemAtPosition(position);
                listenForBus(selectedRoute);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

    }

    private void listenForBus(Route route) {
        if (D) Log.d(TAG, "Listen for bus");

        AlarmManager alarmManager = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);

        // start new listener (for current route) using FLAG_CANCEL_CURRENT we cancel other intents for old routes
        Intent alarmIntent = new Intent(this, PassengerAlarmReceiver.class);
        alarmIntent.putExtra(Constants.PASSENGER_ROUTE_KEY, route.getName());
        alarmIntent.putExtra(Constants.PASSENGER_SCHOOL_KEY, ((ScuffApplication) getApplicationContext()).getSchool().getName());
        PendingIntent pendingAlarmIntent = PendingIntent.getBroadcast(this, PASSENGER_ALARM, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        // cancel outstanding
        alarmManager.cancel(pendingAlarmIntent);
        // then restart
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(),
                Constants.LISTEN_LOCATION_INTERVAL * 1000, pendingAlarmIntent);

    }

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
                .title("My location")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.home_icon)));
        this.googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLatlng, 15));

    }

    private void updateMap(Waypoint waypoint) {
        if (D) Log.d(TAG, "Updating map with location="+waypoint);

/*        Route route = (Route) ((Spinner) findViewById(R.id.route_spinner)).getSelectedItem();
        // if first time or a change to the route drop down
        if (this.journey == null || !this.journey.getRouteId().equals(route.getName())) {
            School school = ((ScuffApplication) ScuffApplication.getContext()).getSchool();
            this.journey = JourneyDatasource.getJourney(route.getName(), school.getName());
        }*/

        /*Waypoint waypoint = JourneyDatasource.getCurrentWaypoint(this.journey);*/
        if (waypoint == null) {
            // no active buses on this route
            DialogHelper.dialog(this, "Bus not found", "There are currently no active buses on this route");
            return;
        }

        this.googleMap.clear();
        Location myLocation = this.googleMap.getMyLocation();
        LatLng myLatlng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
        this.googleMap.addMarker(new MarkerOptions()
                .position(myLatlng)
                .title("My location")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.home_icon)));
        if (D) Log.d(TAG, "Bus location = " + waypoint);
        LatLng busLatlng = new LatLng(waypoint.getLatitude(), waypoint.getLongitude());
        this.googleMap.addMarker(new MarkerOptions()
                .position(busLatlng)
                .title("Bus position")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_icon)));
        this.googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(busLatlng, 15));

    }

    public void onEventMainThread(WaypointEvent event) {
        if (D) Log.d(TAG, "Main thread message waypoint event="+event);
        updateMap(event.getWaypoint());
    }

/*    public void onEventMainThread(LocationEvent event) {
        if (D) Log.d(TAG, "Main thread message location event="+event);
        updateMap(event.getLocation());
    }*/

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);

        AlarmManager alarmManager = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(this, PassengerAlarmReceiver.class);
        PendingIntent pendingAlarmIntent = PendingIntent.getBroadcast(this, PASSENGER_ALARM, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.cancel(pendingAlarmIntent);

        super.onStop();
    }

    public void onFragmentInteraction(School school) {
        //this.school = school;
        DialogHelper.toast(this, school.getName());
    }

    public void onFragmentInteraction(Passenger passenger) {
        DialogHelper.toast(this, passenger.getName());
    }

}

