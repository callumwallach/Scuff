package nz.co.scuff.android;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
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

import nz.co.scuff.data.family.Passenger;
import nz.co.scuff.data.family.Family;
import nz.co.scuff.data.school.Route;
import nz.co.scuff.data.school.School;
import nz.co.scuff.test.JourneyDatasource;
import nz.co.scuff.util.Constants;
import nz.co.scuff.util.DialogHelper;
import nz.co.scuff.util.ScuffContextProvider;


public class PassengerHomeActivity extends FragmentActivity
        implements SchoolsFragment.OnFragmentInteractionListener, ChildrenFragment.OnFragmentInteractionListener {

    private static final String TAG = "PassengerHomeActivity";
    private static final boolean D = true;

    private static final String SCHOOL_CHOICE_FRAGMENT_DIALOG = "SN";

    private GoogleMap googleMap;
    //private School school;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_home);

        populateRoutes();


/*        Set<School> schools = family.getSchools();
        if (schools.size() > 1) {
            SchoolsFragment dialog = SchoolsFragment.newInstance(new ArrayList<>(schools));
            dialog.show(getSupportFragmentManager(), SCHOOL_CHOICE_FRAGMENT_DIALOG);
        } else {
            this.school = schools.iterator().next();
        }*/



        Family family = ((ScuffContextProvider)getApplicationContext()).getFamily();
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

        School school = ((ScuffContextProvider)getApplicationContext()).getSchool();
        Spinner routeSpinner = (Spinner)findViewById(R.id.route_spinner);
        ArrayAdapter<Route> dataAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, new ArrayList<>(school.getRoutes()));
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        routeSpinner.setAdapter(dataAdapter);
        routeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (D) Log.d(TAG, "item "+position+" selected");

                SharedPreferences.Editor editor = getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE).edit();
                editor.putString(Constants.PASSENGER_ROUTE_ID, parent.getItemAtPosition(position).toString());
                editor.apply();

                if (googleMap == null) {
                    googleMap = ((MapFragment)getFragmentManager().findFragmentById(R.id.googleMap)).getMap();
                }
                if (googleMap != null) {
                    setUpMap();
                }
                //GPSDatasource.get();
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
        LatLng myLatlng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());

        if (D) Log.d(TAG, "My latlng = " + myLatlng);

        /*this.googleMap.moveCamera(CameraUpdateFactory.newLatLng(myLatlng));
        this.googleMap.animateCamera(CameraUpdateFactory.zoomTo(16));*/
        this.googleMap.addMarker(new MarkerOptions()
                .position(myLatlng)
                .title("Current position")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.home_icon)));

        Location busLocation = JourneyDatasource.get();
        if (D) Log.d(TAG, "Bus location = " + busLocation);
        if (busLocation == null) {
            // no active buses on this route
            DialogHelper.dialog(this, "Bus not found", "There are currently no active buses on this route");
            return;
        }

        LatLng busLatlng = new LatLng(busLocation.getLatitude(), busLocation.getLongitude());
        if (D) Log.d(TAG, "Bus latlng = " + busLatlng);

        //this.googleMap.moveCamera(CameraUpdateFactory.newLatLng(busLatlng));
        //this.googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        this.googleMap.addMarker(new MarkerOptions()
                .position(busLatlng)
                .title("Bus position")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_icon)));
        this.googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(busLatlng, 15));

    }

    public void onFragmentInteraction(School school) {
        //this.school = school;
        DialogHelper.toast(this, school.getName());
    }

    public void onFragmentInteraction(Passenger passenger) {
        DialogHelper.toast(this, passenger.getName());
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

}
