package nz.co.scuff.android.ui.registration;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import nz.co.scuff.android.R;
import nz.co.scuff.android.service.RegistrationIntentService;
import nz.co.scuff.android.util.Constants;
import nz.co.scuff.android.ui.adapter.RouteAdapter;
import nz.co.scuff.android.ui.adapter.CoordinatorAdapter;
import nz.co.scuff.data.base.Coordinator;
import nz.co.scuff.data.family.Child;
import nz.co.scuff.data.institution.Route;

public class RegisterDriverActivity extends Activity {

    private static final String TAG = "RegisterDriverActivity";
    private static final boolean D = true;

    private Coordinator coordinator;
    private HashMap<Long, Coordinator> schools;
    private HashMap<Long, Route> routes;
    private HashMap<Long, Child> passengers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_driver);

        this.coordinator = (Coordinator)getIntent().getSerializableExtra(Constants.USER_KEY);
        this.schools = (HashMap<Long, Coordinator>) getIntent().getSerializableExtra(Constants.SCHOOLS_KEY);
        this.routes = (HashMap<Long, Route>) getIntent().getSerializableExtra(Constants.ROUTES_KEY);
        this.passengers = (HashMap<Long, Child>) getIntent().getSerializableExtra(Constants.PASSENGERS_KEY);

        populateSchools(schools.values());

    }

    private void populateSchools(Collection<Coordinator> institutions) {
        if (D) Log.d(TAG, "populating institutions=" + institutions);

        Spinner schoolSpinner = (Spinner) findViewById(R.id.institution_spinner);
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
        });
    }

    private void populateRoutes(Set<Route> routes) {
        if (D) Log.d(TAG, "populating routes="+routes);

        // filter routes from those chosen for children
        ArrayList<Route> availableRoutes = new ArrayList<>();
        for (Route route : routes) {
            if (this.routes.containsKey(route.getRouteId())) {
                availableRoutes.add(route);
            }
        }
        Spinner routeSpinner = (Spinner) findViewById(R.id.route_spinner);
        ArrayAdapter<Route> dataAdapter = new RouteAdapter(this,
                android.R.layout.simple_spinner_item, new ArrayList<>(availableRoutes));
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        routeSpinner.setAdapter(dataAdapter);
        routeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Route selectedRoute = (Route) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public void doAdd(View v) {

        // TODO

    }

    public void doDone(View v) {

        // post registration
        Intent registrationIntent = new Intent(this, RegistrationIntentService.class);
        registrationIntent.putExtra(Constants.USER_KEY, (Parcelable)this.coordinator);
        registrationIntent.putExtra(Constants.SCHOOLS_KEY, this.schools);
        registrationIntent.putExtra(Constants.ROUTES_KEY, this.routes);
        registrationIntent.putExtra(Constants.PASSENGERS_KEY, this.passengers);
        startService(registrationIntent);

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
