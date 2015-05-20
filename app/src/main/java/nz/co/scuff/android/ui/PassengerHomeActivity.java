package nz.co.scuff.android.ui;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.greenrobot.event.EventBus;
import nz.co.scuff.android.R;
import nz.co.scuff.android.service.PassengerAlarmReceiver;
import nz.co.scuff.android.service.PassengerIntentService;
import nz.co.scuff.android.service.TicketIntentService;
import nz.co.scuff.android.ui.fragment.ChildrenDialogFragment;
import nz.co.scuff.android.ui.fragment.ChildrenFragment;
import nz.co.scuff.android.util.Constants;
import nz.co.scuff.android.ui.adapter.RouteAdapter;
import nz.co.scuff.android.event.JourneyEvent;
import nz.co.scuff.data.family.Passenger;
import nz.co.scuff.data.journey.Journey;
import nz.co.scuff.data.school.Route;
import nz.co.scuff.data.school.School;
import nz.co.scuff.android.util.DialogHelper;
import nz.co.scuff.android.util.ScuffApplication;


public class PassengerHomeActivity extends BaseFragmentActivity
        implements ChildrenFragment.OnFragmentInteractionListener, ChildrenDialogFragment.OnFragmentInteractionListener, GoogleMap.OnMarkerClickListener {

    private static final String TAG = "PassengerHomeActivity";
    private static final boolean D = true;

    private static final int PASSENGER_ALARM = 1;

    private GoogleMap googleMap;
    private boolean newRouteSelected;

    private School school;
    private Route route;

    private Journey journeyClicked;
    private List<Journey> journeys;
    private Map<Marker, Journey> markerMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (D) Log.d(TAG, "onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_home);

        this.school = ((ScuffApplication) getApplicationContext()).getSchool();
        this.journeys = new ArrayList<>();
        this.markerMap = new HashMap<>();

        setupMap();
        setupRoutes();

        ScuffApplication scuffContext = (ScuffApplication) getApplicationContext();
        Set<Passenger> children = scuffContext.getDriver().getChildren();

        //ChildrenFragment childrenFragment = ChildrenFragment.newInstance(new ArrayList<>(children));
        //getSupportFragmentManager().beginTransaction().replace(R.id.mapSlideOver, childrenFragment).commit();

        /*LinearLayout mapSlideOver = (LinearLayout)findViewById(R.id.mapSlideOver);

        GridView gridView = (GridView)mapSlideOver.findViewById(android.R.id.list);
        List<Passenger> passengers = new ArrayList<>(children);
        final PassengerMultiChoiceAdapter adapter = new PassengerMultiChoiceAdapter(savedInstanceState, passengers);
        adapter.setAdapterView(gridView);*/
        /*adapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                DialogHelper.toast(getApplicationContext(), "Item click: " + adapter.getItem(position).getFirstName());
            }
        });*/

/*
        LinearLayout mapSlideOver = (LinearLayout)findViewById(R.id.mapSlideOver);
        for (Passenger child : children) {
            ImageButton iButton;
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
        }*/

    }

    private void setupMap() {
        if (D) Log.d(TAG, "Initialise map");

        if (this.googleMap == null) {
            this.googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.googleMap)).getMap();
        }

        this.googleMap.setMyLocationEnabled(true);
        this.googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        this.googleMap.setOnMarkerClickListener(this);

        markHome();

    }

    private void setupRoutes() {
        if (D) Log.d(TAG, "setupRoutes");

        Spinner routeSpinner = (Spinner) findViewById(R.id.route_spinner);
        ArrayAdapter<Route> dataAdapter = new RouteAdapter(this,
                android.R.layout.simple_spinner_item, new ArrayList<>(this.school.getRoutes()));
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        routeSpinner.setAdapter(dataAdapter);
        routeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (D) Log.d(TAG, "item " + position + " selected");
                route = (Route) parent.getItemAtPosition(position);
                newRouteSelected = true;

                watchForBuses();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    private void markHome() {
        if (D) Log.d(TAG, "markHome");

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

    private void markBuses(List<Journey> journeys) {
        if (D) Log.d(TAG, "Updating map with buses=" + journeys);

        this.googleMap.clear();

        if (journeys.isEmpty()) {
            // no active buses on this route
            if (this.newRouteSelected)
                DialogHelper.dialog(this, "Bus not found", "There are currently no active buses on this route");
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

        for (Journey journey : journeys) {
            if (D) Log.d(TAG, "Bus location = " + journey);
            LatLng busLatlng = new LatLng(journey.getCurrentWaypoint().getLatitude(), journey.getCurrentWaypoint().getLongitude());
            Marker busMarker = this.googleMap.addMarker(new MarkerOptions()
                    .position(busLatlng)
                    .title("Bus position")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_icon)));
            this.markerMap.put(busMarker, journey);
            if (this.newRouteSelected) {
                this.googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(busLatlng, 15));
                newRouteSelected = false;
            }
        }
    }

    private void watchForBuses() {
        if (D) Log.d(TAG, "Watching for buses on route=" + this.route);

        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

        // send direct one first
        Intent directIntent = new Intent(this, PassengerIntentService.class);
        directIntent.putExtra(Constants.PASSENGER_ROUTE_ID_KEY, this.route.getRouteId());
        directIntent.putExtra(Constants.PASSENGER_SCHOOL_ID_KEY, ((ScuffApplication) getApplicationContext()).getSchool().getSchoolId());
        startService(directIntent);

        // start new listener (for current route) using FLAG_CANCEL_CURRENT we cancel other intents for old routes
        Intent alarmIntent = new Intent(this, PassengerAlarmReceiver.class);
        alarmIntent.putExtra(Constants.PASSENGER_ROUTE_ID_KEY, this.route.getRouteId());
        alarmIntent.putExtra(Constants.PASSENGER_SCHOOL_ID_KEY, ((ScuffApplication) getApplicationContext()).getSchool().getSchoolId());
        PendingIntent pendingAlarmIntent = PendingIntent.getBroadcast(this, PASSENGER_ALARM, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        // cancel outstanding
        alarmManager.cancel(pendingAlarmIntent);
        // then restart
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(),
                Constants.LISTEN_LOCATION_INTERVAL * 1000, pendingAlarmIntent);

    }

    public void onEventMainThread(JourneyEvent event) {
        if (D) Log.d(TAG, "Main thread message waypoint event=" + event);
        this.journeys = event.getJourneys();
        markBuses(this.journeys);
    }

/*    public void onEventMainThread(SelectionEvent event) {
        if (D) Log.d(TAG, "Main thread message selection event=" + event);

        ArrayList<Long> idsOfChildrenTravelling = new ArrayList<>();
        for (Object o : event.getItems()) {
            Passenger child = (Passenger)o;
            idsOfChildrenTravelling.add(child.getPersonId());
        }

        Intent ticketIntent = new Intent(this, TicketIntentService.class);
        ticketIntent.putExtra(Constants.JOURNEY_KEY, this.journeyClicked.getJourneyId());
        ticketIntent.putExtra(Constants.PASSENGERS_KEY, idsOfChildrenTravelling);
        startService(ticketIntent);

    }*/

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (D) Log.d(TAG, "onMarkerClick");

        this.journeyClicked = this.markerMap.get(marker);
        assert(this.journeyClicked != null);
        //DialogHelper.toast(this, bus.getJourneyId());

        /*ScuffApplication scuffContext = (ScuffApplication) getApplicationContext();
        Set<Passenger> children = scuffContext.getDriver().getChildren();
        ArrayList<Long> idsOfChildrenTravelling = new ArrayList<>();
        for (Passenger child : children) {
            idsOfChildrenTravelling.add(child.getPersonId());
        }

        Intent ticketIntent = new Intent(this, TicketIntentService.class);
        ticketIntent.putExtra(Constants.JOURNEY_KEY, bus.getJourneyId());
        ticketIntent.putExtra(Constants.PASSENGERS_KEY, idsOfChildrenTravelling);
        startService(ticketIntent);*/

        ScuffApplication scuffContext = (ScuffApplication) getApplicationContext();
        Set<Passenger> children = scuffContext.getDriver().getChildren();

        Intent intent = new Intent(this, PassengerSelectionActivity.class);
        intent.putExtra(Constants.JOURNEY_KEY, (Parcelable) journeyClicked);
        intent.putParcelableArrayListExtra(Constants.PASSENGERS_KEY, new ArrayList<Parcelable>(children));
        startActivity(intent);

        /*SwipeLayout swipeLayout = (SwipeLayout)findViewById(R.id.swipe2);
        swipeLayout.open();*/

        /*ChildrenDialogFragment dialog = ChildrenDialogFragment.newInstance(new ArrayList<>(children));
        dialog.show(getSupportFragmentManager(), "fragment");*/

        return false;
    }

    @Override
    protected void onStart() {
        if (D) Log.d(TAG, "onStart");
        super.onStart();
        EventBus.getDefault().register(this);

    }

    @Override
    protected void onRestart() {
        if (D) Log.d(TAG, "onRestart");
        super.onStart();
        watchForBuses();
    }

    @Override
    protected void onStop() {
        if (D) Log.d(TAG, "onStop");
// stop polling for buses
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(this, PassengerAlarmReceiver.class);
        PendingIntent pendingAlarmIntent = PendingIntent.getBroadcast(this, PASSENGER_ALARM, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.cancel(pendingAlarmIntent);

        EventBus.getDefault().unregister(this);

        super.onStop();
    }

    public void onFragmentInteraction(Collection<Passenger> children) {
        if (D) Log.d(TAG, "Post pending passengers=" + children);

        // add pending ticket to buses on current route

        // add passenger ticket to journey and post
        /*Intent directIntent = new Intent(this, TicketIntentService.class);
        directIntent.putExtra(Constants.PASSENGER_ROUTE_ID_KEY, this.route.getRouteId());
        directIntent.putExtra(Constants.PASSENGER_SCHOOL_ID_KEY, ((ScuffApplication) getApplicationContext()).getSchool().getSchoolId());
        startService(directIntent);*/

        //DialogHelper.toast(this, childrengetFirstName());

        ArrayList<Long> idsOfChildrenTravelling = new ArrayList<>();
        for (Passenger child : children) {
            idsOfChildrenTravelling.add(child.getPersonId());
        }

        Intent ticketIntent = new Intent(this, TicketIntentService.class);
        ticketIntent.putExtra(Constants.JOURNEY_KEY, this.journeyClicked.getJourneyId());
        ticketIntent.putExtra(Constants.PASSENGERS_KEY, idsOfChildrenTravelling);
        startService(ticketIntent);

        this.journeyClicked = null;
    }

}



