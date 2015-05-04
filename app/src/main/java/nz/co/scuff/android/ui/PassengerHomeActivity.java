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
import java.util.List;
import java.util.Set;

import de.greenrobot.event.EventBus;
import nz.co.scuff.android.R;
import nz.co.scuff.android.gps.PassengerAlarmReceiver;
import nz.co.scuff.android.util.Constants;
import nz.co.scuff.android.util.SnapshotEvent;
import nz.co.scuff.data.family.Child;
import nz.co.scuff.data.family.Parent;
import nz.co.scuff.data.family.Person;
import nz.co.scuff.data.journey.JourneySnapshot;
import nz.co.scuff.data.school.Route;
import nz.co.scuff.data.school.School;
import nz.co.scuff.android.util.DialogHelper;
import nz.co.scuff.android.util.ScuffApplication;


public class PassengerHomeActivity extends FragmentActivity
        implements SchoolsFragment.OnFragmentInteractionListener,
        ChildrenFragment.OnFragmentInteractionListener {

    private static final String TAG = "PassengerHomeActivity";
    private static final boolean D = true;

    private static final int PASSENGER_ALARM = 1;

    private GoogleMap googleMap;
    private boolean newRouteSelected;
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



        ScuffApplication scuffContext = (ScuffApplication)getApplicationContext();
        Parent driver = scuffContext.getDriver();
        LinearLayout mapSlideOver = (LinearLayout)findViewById(R.id.mapSlideOver);
        Set<Child> children = driver.getChildren();
/*
        ChildrenFragment childrenFragment = ChildrenFragment.newInstance(new ArrayList<>(children));
        getSupportFragmentManager().beginTransaction().add(R.id.mapSlideOver, childrenFragment).commit();
*/

        for (Child child : children) {
            Button button = new Button(this);
            Drawable profilePix;
            if (child.getPicture() != null) {
                // load from disk
                String fileLocation = getFilesDir() + "/" + child.getPicture();
                if (D) Log.d(TAG, "loading profile image from file location = "+ fileLocation);
                profilePix = Drawable.createFromPath(fileLocation);
            } else {
                // default images based on gender
                profilePix = child.getGender() == Person.Gender.MALE ?
                        getResources().getDrawable(R.drawable.male_blank_icon) : getResources().getDrawable(R.drawable.female_blank_icon);
            }
            button.setBackground(profilePix);
            button.setText(child.getFirstName());
            //button.setGravity(Gravity.BOTTOM);
            button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            button.setTextColor(Color.BLACK);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
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
                newRouteSelected = true;
                listenForBus(selectedRoute);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
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

        if (this.googleMap == null) {
            this.googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.googleMap)).getMap();
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

        LatLng myLatlng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());

        if (D) Log.d(TAG, "My latlng = " + myLatlng);

        this.googleMap.clear();
/*        this.googleMap.addMarker(new MarkerOptions()
                .position(myLatlng)
                .title("My location")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.home_icon)));*/
        this.googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLatlng, 15));

    }

    private void updateMap(List<JourneySnapshot> snapshots) {
        if (D) Log.d(TAG, "Updating map with snapshots="+ snapshots);

        this.googleMap.clear();

        if (snapshots.isEmpty()) {
            // no active buses on this route
            if (this.newRouteSelected) DialogHelper.dialog(this, "Bus not found", "There are currently no active buses on this route");
            this.newRouteSelected = false;
            return;
        }

/*        Location myLocation = this.googleMap.getMyLocation();
        LatLng myLatlng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
        this.googleMap.addMarker(new MarkerOptions()
                .position(myLatlng)
                .title("My location")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.home_icon)));
        this.googleMap.addCircle(new CircleOptions().center(myLatlng).radius(myLocation.getAccuracy()));*/

        for (JourneySnapshot snapshot : snapshots) {
            if (D) Log.d(TAG, "Bus location = " + snapshot);
            LatLng busLatlng = new LatLng(snapshot.getLatitude(), snapshot.getLongitude());
            this.googleMap.addMarker(new MarkerOptions()
                    .position(busLatlng)
                    .title("Bus position")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_icon)));
            if (this.newRouteSelected) {
                this.googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(busLatlng, 15));
                newRouteSelected = false;
            }
        }

    }

    public void onEventMainThread(SnapshotEvent event) {
        if (D) Log.d(TAG, "Main thread message waypoint event="+event);
        updateMap(event.getSnapshots());
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

    public void onFragmentInteraction(Child child) {
        DialogHelper.toast(this, child.getFirstName());
    }

}

