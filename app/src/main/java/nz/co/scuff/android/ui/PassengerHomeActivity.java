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
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.joda.time.DateTime;
import org.joda.time.Period;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import de.greenrobot.event.EventBus;
import nz.co.scuff.android.R;
import nz.co.scuff.android.event.TicketEvent;
import nz.co.scuff.android.service.PassengerAlarmReceiver;
import nz.co.scuff.android.service.PassengerIntentService;
import nz.co.scuff.android.service.TicketIntentService;
import nz.co.scuff.android.ui.fragment.ChildrenDialogFragment;
import nz.co.scuff.android.ui.fragment.ChildrenFragment;
import nz.co.scuff.android.util.Constants;
import nz.co.scuff.android.event.JourneyEvent;
import nz.co.scuff.data.family.Child;
import nz.co.scuff.data.journey.Journey;
import nz.co.scuff.android.util.DialogHelper;
import nz.co.scuff.android.util.ScuffApplication;
import nz.co.scuff.data.journey.Ticket;
import nz.co.scuff.data.journey.Waypoint;
import nz.co.scuff.data.util.TrackingState;


public class PassengerHomeActivity extends BaseActionBarActivity
        implements ChildrenFragment.OnFragmentInteractionListener, ChildrenDialogFragment.OnFragmentInteractionListener, GoogleMap.OnMarkerClickListener {

    private static final String TAG = "PassengerHomeActivity";
    private static final boolean D = true;

    private BitmapDescriptor activeBusIcon;
    private BitmapDescriptor completedBusIcon;
    private BitmapDescriptor pausedBusIcon;

    private static final int PASSENGER_ALARM = 1;

    private GoogleMap googleMap;

    private boolean isFirstTime;

    private Journey selectedJourney;
    private Map<Marker, Journey> markerMap;
    private ArrayList<Long> watchedJourneyIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (D) Log.d(TAG, "onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_home);

        //this.institution = ((ScuffApplication) getApplicationContext()).getInstitution();
        //this.journeys = new ArrayList<>();
        this.markerMap = new HashMap<>();
        this.watchedJourneyIds = new ArrayList<>();

        this.isFirstTime = true;

        this.activeBusIcon = BitmapDescriptorFactory.fromResource(R.drawable.active_bus_icon);
        this.completedBusIcon = BitmapDescriptorFactory.fromResource(R.drawable.completed_bus_icon);
        this.pausedBusIcon = BitmapDescriptorFactory.fromResource(R.drawable.paused_bus_icon);

        setupMap();
        checkForBuses();

        //setupRoutes();

        /*ScuffApplication scuffContext = (ScuffApplication) getApplicationContext();
        Set<Child> children = scuffContext.getCoordinator().getChildren();*/

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

/*    private void setupRoutes() {
        if (D) Log.d(TAG, "setupRoutes");

        Spinner routeSpinner = (Spinner) findViewById(R.id.route_spinner);
        ArrayAdapter<Route> dataAdapter = new RouteAdapter(this,
                android.R.layout.simple_spinner_item, new ArrayList<>(this.institution.getRoutes()));
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

    }*/

    private void markHome() {
        if (D) Log.d(TAG, "markHome");

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, true);
        Location myLocation = locationManager.getLastKnownLocation(provider);

        if (D) Log.d(TAG, "My location = " + myLocation);
        if (myLocation == null) {
            // GPS not turned on? use institution location
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
        this.googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLatlng, 14));

    }

    private void markBuses(Collection<Journey> journeys) {
        if (D) Log.d(TAG, "Updating map with buses=" + journeys);

        this.googleMap.clear();

        if (journeys.isEmpty()) {
            if (this.isFirstTime) {
                // no active buses on this route
                //if (this.newRouteSelected)
                DialogHelper.dialog(this, "Bus not found", "There are currently no active journeys");
                this.isFirstTime = false;
                //this.newRouteSelected = false;
            }
        } else {
/*          Location myLocation = this.googleMap.getMyLocation();
            LatLng myLatlng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
            this.googleMap.addMarker(new MarkerOptions()
                    .position(myLatlng)
                    .title("My location")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.home_icon)));
            this.googleMap.addCircle(new CircleOptions().center(myLatlng).radius(myLocation.getAccuracy()));*/

            this.markerMap.clear();
            this.watchedJourneyIds.clear();

            for (Journey journey : journeys) {
                boolean journeyNotComplete = !journey.getState().equals(TrackingState.COMPLETED);
                BitmapDescriptor busIcon;
                switch(journey.getState()) {
                    case COMPLETED:
                        busIcon = completedBusIcon;
                        break;
                    case PAUSED:
                        busIcon = pausedBusIcon;
                        break;
                    default:
                        busIcon = activeBusIcon;
                }
                Waypoint waypoint = journey.getCurrentWaypoint();
                if (D) Log.d(TAG, "Bus location: journey=" + journey.getJourneyId() + " waypoint=" + waypoint);
                LatLng busLatlng = new LatLng(waypoint.getLatitude(), waypoint.getLongitude());
                Marker busMarker = this.googleMap.addMarker(new MarkerOptions()
                        .position(busLatlng)
                        .title(journey.getGuide().getName() + "'s bus")
                        .icon(busIcon));
                this.markerMap.put(busMarker, journey);
                // TODO change date check to asynctask to empty out watched ids
                if (journeyNotComplete || new DateTime(journey.getCompleted()).plus(
                        new Period().withSeconds(Constants.SECONDS_TO_DISPLAY_COMPLETED_JOURNEYS)).isAfterNow()) {
                    this.watchedJourneyIds.add(journey.getJourneyId());
                }
                //if (this.newRouteSelected) {
                //this.googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(busLatlng, 15));
                //newRouteSelected = false;
                //}
            }
            this.isFirstTime = true;
        }
        watchForBuses();
    }

    private void checkForBuses() {
        if (D) Log.d(TAG, "Checking for initial buses");

        long coordinatorId = ((ScuffApplication) getApplicationContext()).getCoordinator().getCoordinatorId();

        // send direct one first
        Intent directIntent = new Intent(this, PassengerIntentService.class);
        directIntent.putExtra(Constants.COORDINATOR_ID_KEY, coordinatorId);
        startService(directIntent);
    }

    private void watchForBuses() {
        if (D) Log.d(TAG, "Watching for buses");

        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        long coordinatorId = ((ScuffApplication) getApplicationContext()).getCoordinator().getCoordinatorId();

        // start new listener (for current route) using FLAG_CANCEL_CURRENT we cancel other intents for old routes
        Intent alarmIntent = new Intent(this, PassengerAlarmReceiver.class);
        alarmIntent.putExtra(Constants.COORDINATOR_ID_KEY, coordinatorId);
        alarmIntent.putExtra(Constants.WATCHED_JOURNEYS_ID_KEY, this.watchedJourneyIds);

        PendingIntent pendingAlarmIntent = PendingIntent.getBroadcast(this, PASSENGER_ALARM, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        // cancel outstanding
        alarmManager.cancel(pendingAlarmIntent);
        alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + Constants.LISTEN_LOCATION_INTERVAL * 1000, pendingAlarmIntent);
    }

    private void processReceivedTickets(Collection<Ticket> tickets) {
        for (Ticket ticket : tickets) {
            Child child = ticket.getChild();
            DialogHelper.toast(this, "Ticket posted for passenger: "+child.getChildData().getFirstName()+" "+child.getChildData().getLastName());
        }
    }

    public void onEventMainThread(JourneyEvent event) {
        if (D) Log.d(TAG, "Main thread message journey received event=" + event);
        markBuses(event.getJourneys());
    }

    public void onEventMainThread(TicketEvent event) {
        if (D) Log.d(TAG, "Main thread message ticket received event=" + event);
        processReceivedTickets(event.getTickets());
    }

/*    public void onEventMainThread(SelectionEvent event) {
        if (D) Log.d(TAG, "Main thread message selection event=" + event);

        ArrayList<Long> idsOfChildrenTravelling = new ArrayList<>();
        for (Object o : event.getItems()) {
            Passenger child = (Passenger)o;
            idsOfChildrenTravelling.add(child.getPersonId());
        }

        Intent ticketIntent = new Intent(this, TicketIntentService.class);
        ticketIntent.putExtra(Constants.JOURNEY_ID_KEY, this.selectedJourney.getJourneyId());
        ticketIntent.putExtra(Constants.PASSENGERS_KEY, idsOfChildrenTravelling);
        startService(ticketIntent);

    }*/

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (D) Log.d(TAG, "onMarkerClick");

        this.selectedJourney = this.markerMap.get(marker);
        assert(this.selectedJourney != null);
        //DialogHelper.toast(this, bus.getJourneyId());

        /*ScuffApplication scuffContext = (ScuffApplication) getApplicationContext();
        Set<Passenger> children = scuffContext.getCoordinator1().getChildren();
        ArrayList<Long> idsOfChildrenTravelling = new ArrayList<>();
        for (Passenger child : children) {
            idsOfChildrenTravelling.add(child.getPersonId());
        }

        Intent ticketIntent = new Intent(this, TicketIntentService.class);
        ticketIntent.putExtra(Constants.JOURNEY_ID_KEY, bus.getJourneyId());
        ticketIntent.putExtra(Constants.PASSENGERS_KEY, idsOfChildrenTravelling);
        startService(ticketIntent);*/

        ScuffApplication scuffContext = (ScuffApplication) getApplicationContext();
        Set<Child> children = scuffContext.getCoordinator().getChildren();

        Intent intent = new Intent(this, PassengerSelectionActivity.class);
        intent.putExtra(Constants.JOURNEY_ID_KEY, this.selectedJourney);
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
        super.onRestart();
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

    public void onFragmentInteraction(Collection<Child> children) {
        if (D) Log.d(TAG, "Post pending passengers=" + children);

        // add pending ticket to buses on current route

        // add passenger ticket to journey and post
        /*Intent directIntent = new Intent(this, TicketIntentService.class);
        directIntent.putExtra(Constants.PASSENGER_ROUTE_ID_KEY, this.route.getRouteId());
        directIntent.putExtra(Constants.PASSENGER_SCHOOL_ID_KEY, ((ScuffApplication) getApplicationContext()).getInstitution().getInstitutionId());
        startService(directIntent);*/

        //DialogHelper.toast(this, childrengetFirstName());

        ArrayList<Long> idsOfChildrenTravelling = new ArrayList<>();
        for (Child child : children) {
            idsOfChildrenTravelling.add(child.getChildId());
        }

        Intent ticketIntent = new Intent(this, TicketIntentService.class);
        ticketIntent.putExtra(Constants.JOURNEY_ID_KEY, this.selectedJourney.getJourneyId());
        ticketIntent.putExtra(Constants.PASSENGERS_KEY, idsOfChildrenTravelling);
        startService(ticketIntent);

        this.selectedJourney = null;
    }

}



