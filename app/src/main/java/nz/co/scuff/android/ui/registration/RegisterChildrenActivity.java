package nz.co.scuff.android.ui.registration;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import de.greenrobot.event.EventBus;
import nz.co.scuff.android.R;
import nz.co.scuff.android.data.ScuffDatasource;
import nz.co.scuff.android.util.Constants;
import nz.co.scuff.android.util.DialogHelper;
import nz.co.scuff.android.util.RouteAdapter;
import nz.co.scuff.android.util.SchoolEvent;
import nz.co.scuff.android.util.SchoolAdapter;
import nz.co.scuff.data.family.Driver;
import nz.co.scuff.data.family.Passenger;
import nz.co.scuff.data.family.Person;
import nz.co.scuff.data.relationship.DriverPassenger;
import nz.co.scuff.data.relationship.PassengerRoute;
import nz.co.scuff.data.relationship.PassengerSchool;
import nz.co.scuff.data.school.Route;
import nz.co.scuff.data.school.School;

public class RegisterChildrenActivity extends Activity {

    private static final String TAG = "RegisterChildren";
    private static final boolean D = true;

    private Context context;
    private ProgressDialog progressDialog;
    private boolean loading = false;

    private Driver driver;
    private HashMap<Long, School> schools;
    private HashMap<Long, Route> routes;
    private HashMap<Long, Passenger> passengers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_children);

        this.driver = (Driver) getIntent().getSerializableExtra(Constants.USER_KEY);
        this.schools = new HashMap<>();
        this.routes = new HashMap<>();
        this.passengers = new HashMap<>();

        this.progressDialog = new ProgressDialog(this);
        this.progressDialog.setIndeterminate(true);
        this.progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        this.progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        this.progressDialog.setCancelable(false);

        loadSchools();

    }

    private void loadSchools() {
        if (D) Log.d(TAG, "loading schools");

        if (!loading) {
            loading = true;
            new AsyncSendMessage().execute();
        }
    }

    private class AsyncSendMessage extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... params) {
            if (D) Log.d(TAG, "starting background load");

            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            String provider = locationManager.getBestProvider(criteria, true);
            Location myLocation = locationManager.getLastKnownLocation(provider);

            double latitude = 65.9667;
            double longitude = -18.5333;
            if (myLocation != null) {
                latitude = myLocation.getLatitude();
                longitude = myLocation.getLongitude();
            }

            List<School> fetchedSchools = ScuffDatasource.getSchools(latitude, longitude);
            EventBus.getDefault().post(new SchoolEvent(fetchedSchools));
            return fetchedSchools.size();
        }

        @Override
        protected void onPreExecute() {
            if (D) Log.d(TAG, "pre load");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Integer resultCode) {
            if (D) Log.d(TAG, "post load");
            progressDialog.dismiss();
            loading = false;
            if (resultCode <= 0) {
                DialogHelper.dialog(context, "No schools found", "Check with your local school to make sure they are registered with Scuff");
            }
        }
    }

    public void onEventMainThread(SchoolEvent event) {
        if (D) Log.d(TAG, "Main thread school event="+event);
        populateSchools(event.getSchools());
    }

    private void populateSchools(List<School> schools) {
        if (D) Log.d(TAG, "populating schools="+schools);

        Spinner schoolSpinner = (Spinner) findViewById(R.id.school_spinner);
        ArrayAdapter<School> dataAdapter = new SchoolAdapter(this,
                android.R.layout.simple_spinner_item, new ArrayList<>(schools));
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        schoolSpinner.setAdapter(dataAdapter);
        schoolSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                School selectedSchool = (School) parent.getItemAtPosition(position);
                populateRoutes(selectedSchool.getRoutes());
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void populateRoutes(Set<Route> routes) {
        if (D) Log.d(TAG, "populating routes="+routes);

        Spinner routeSpinner = (Spinner) findViewById(R.id.route_spinner);
        ArrayAdapter<Route> dataAdapter = new RouteAdapter(this,
                android.R.layout.simple_spinner_item, new ArrayList<>(routes));
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        routeSpinner.setAdapter(dataAdapter);
        routeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Route selectedRoute = (Route) parent.getItemAtPosition(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    public void doNext(View v) {

        // go to next page -> add children
        Intent intent = new Intent(this, RegisterDriverActivity.class);
        intent.putExtra(Constants.USER_KEY, this.driver);
        intent.putExtra(Constants.SCHOOLS_KEY, this.schools);
        intent.putExtra(Constants.ROUTES_KEY, this.routes);
        intent.putExtra(Constants.PASSENGERS_KEY, this.passengers);
        startActivity(intent);

    }

    public void doAdd(View v) {

        Passenger passenger = new Passenger();
        String fname = ((EditText)findViewById(R.id.child_fname)).getText().toString();
        String lname = ((EditText)findViewById(R.id.child_lname)).getText().toString();
        String classroom = ((EditText)findViewById(R.id.child_classroom)).getText().toString();
        int age = Integer.parseInt(((EditText) findViewById(R.id.child_age)).getText().toString());
        boolean male = ((RadioButton)findViewById(R.id.male_radioButton)).isChecked();

        passenger.setFirstName(fname);
        passenger.setLastName(lname);
        /*passenger.setClassroom(classroom);*/
        /*passenger.setAge(age);*/
        passenger.setGender(male ? Person.Gender.MALE : Person.Gender.FEMALE);

        School school = (School)((Spinner) findViewById(R.id.school_spinner)).getSelectedItem();
        Route route = (Route)((Spinner) findViewById(R.id.route_spinner)).getSelectedItem();

        PassengerSchool passengerSchool = new PassengerSchool(passenger, school);
        PassengerRoute passengerRoute = new PassengerRoute(passenger, route);
        passenger.getPassengerSchools().add(passengerSchool);
        passenger.getPassengerRoutes().add(passengerRoute);

        DriverPassenger driverPassenger = new DriverPassenger(this.driver, passenger);
        this.driver.getDriverPassengers().add(driverPassenger);

        LinearLayout header = ((LinearLayout) findViewById(R.id.header));
        header.setOrientation(LinearLayout.HORIZONTAL);
        Button childButton = new Button(this);
        childButton.setText(passenger.getFirstName());

        header.addView(childButton);

        this.schools.put(school.getSchoolId(), school);
        this.routes.put(route.getRouteId(), route);
        this.passengers.put(passenger.getPersonId(), passenger);

        // clear fields
        ((EditText) findViewById(R.id.child_fname)).setText("");
        ((EditText) findViewById(R.id.child_lname)).setText("");
        ((EditText) findViewById(R.id.child_classroom)).setText("");
        ((EditText) findViewById(R.id.child_age)).setText("");
        ((RadioButton) findViewById(R.id.male_radioButton)).setChecked(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

}
