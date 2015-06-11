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
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import de.greenrobot.event.EventBus;
import nz.co.scuff.android.R;
import nz.co.scuff.android.util.Constants;
import nz.co.scuff.android.util.DialogHelper;
import nz.co.scuff.android.ui.adapter.RouteAdapter;
import nz.co.scuff.android.event.SchoolEvent;
import nz.co.scuff.data.base.Coordinator;
import nz.co.scuff.data.family.Child;
import nz.co.scuff.data.institution.Route;

public class RegisterChildrenActivity extends Activity {

    private static final String TAG = "RegisterChildren";
    private static final boolean D = true;

    private Context context;
    private ProgressDialog progressDialog;
    private boolean loading = false;

    private Coordinator coordinator;
    private HashMap<Long, Coordinator> schools;
    private HashMap<Long, Route> routes;
    private HashMap<Long, Child> passengers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_children);

        this.coordinator = (Coordinator) getIntent().getSerializableExtra(Constants.USER_KEY);
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

            /*List<Institution> fetchedInstitutions = null;
            try {
                fetchedInstitutions = ScuffDatasource.getInstitutions(latitude, longitude);
                EventBus.getDefault().post(new SchoolEvent(fetchedInstitutions));
            } catch (ResourceNotFoundException e) {
                Log.e(TAG, "Error schools not found");
            }*/

            //List<Institution> fetchedInstitutions = ScuffDatasource.getSchools(latitude, longitude);
            //return fetchedInstitutions.size();
            return 0;
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
        //populateSchools(event.getInstitutions());
    }

    private void populateSchools(List<Coordinator> institutions) {
        if (D) Log.d(TAG, "populating institutions="+ institutions);

        /*Spinner schoolSpinner = (Spinner) findViewById(R.id.institution_spinner);
        ArrayAdapter<Coordinator> dataAdapter = new CoordinatorAdapter(this,
                android.R.layout.simple_spinner_item, new ArrayList<>(institutions));
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        schoolSpinner.setAdapter(dataAdapter);
        schoolSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Coordinator selectedInstitution = (Coordinator) parent.getItemAtPosition(position);
                populateRoutes(selectedInstitution.getRoutes());
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });*/
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
        intent.putExtra(Constants.USER_KEY, (Parcelable)this.coordinator);
        intent.putExtra(Constants.SCHOOLS_KEY, this.schools);
        intent.putExtra(Constants.ROUTES_KEY, this.routes);
        intent.putExtra(Constants.PASSENGERS_KEY, this.passengers);
        startActivity(intent);

    }

    public void doAdd(View v) {

        Child child = new Child();
        String fname = ((EditText)findViewById(R.id.child_fname)).getText().toString();
        String lname = ((EditText)findViewById(R.id.child_lname)).getText().toString();
        String classroom = ((EditText)findViewById(R.id.child_classroom)).getText().toString();
        int age = Integer.parseInt(((EditText) findViewById(R.id.child_age)).getText().toString());
        boolean male = ((RadioButton)findViewById(R.id.male_radioButton)).isChecked();

/*        child.setFirstName(fname);
        child.setLastName(lname);
        *//*passenger.setClassroom(classroom);*//*
        *//*passenger.setAge(age);*//*
        child.setGender(male ? Person.Gender.MALE : Person.Gender.FEMALE);

        Institution institution = (Institution)((Spinner) findViewById(R.id.school_spinner)).getSelectedItem();
        Route route = (Route)((Spinner) findViewById(R.id.route_spinner)).getSelectedItem();

        PassengerSchool passengerSchool = new PassengerSchool(child, institution);
        PassengerRoute passengerRoute = new PassengerRoute(child, route);
        child.getPassengerSchools().add(passengerSchool);
        child.getPassengerRoutes().add(passengerRoute);

        ParentalRelationship parentalRelationship = new ParentalRelationship(this.adult, child);
        this.adult.getChildren().add(parentalRelationship);

        LinearLayout header = ((LinearLayout) findViewById(R.id.header));
        header.setOrientation(LinearLayout.HORIZONTAL);
        Button childButton = new Button(this);
        childButton.setText(child.getFirstName());

        header.addView(childButton);

        this.schools.put(institution.getInstitutionId(), institution);
        this.routes.put(route.getRouteId(), route);
        this.passengers.put(child.getPersonId(), child);

        // clear fields
        ((EditText) findViewById(R.id.child_fname)).setText("");
        ((EditText) findViewById(R.id.child_lname)).setText("");
        ((EditText) findViewById(R.id.child_classroom)).setText("");
        ((EditText) findViewById(R.id.child_age)).setText("");
        ((RadioButton) findViewById(R.id.male_radioButton)).setChecked(true);*/
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
