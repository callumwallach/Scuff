package nz.co.scuff.android;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import nz.co.scuff.data.family.Family;
import nz.co.scuff.data.school.School;
import nz.co.scuff.test.TestDataProvider;
import nz.co.scuff.android.util.Constants;
import nz.co.scuff.android.util.ScuffApplication;


public class HomeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // populate data for testing
        loadTestData();
        populateSchools();

    }

    private void loadTestData() {
        Family family = ((ScuffApplication)getApplicationContext()).getFamily();
        if (family == null){
            TestDataProvider.populateTestData();
            family = TestDataProvider.getFamily();
            ((ScuffApplication) getApplicationContext()).setFamily(family);
        }
        final TextView welcome = (TextView) findViewById(R.id.family_label);
        welcome.setText(family.getName() + " family");

    }

    private void populateSchools() {

        final Family family = ((ScuffApplication) getApplicationContext()).getFamily();
        Spinner schoolSpinner = (Spinner) findViewById(R.id.school_spinner);
        ArrayAdapter<School> dataAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, new ArrayList<>(family.getAllSchools()));
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        schoolSpinner.setAdapter(dataAdapter);
        schoolSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                School selectedSchool = (School)parent.getItemAtPosition(position);
                SharedPreferences.Editor editor = getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE).edit();
                editor.putString(Constants.DRIVER_SCHOOL_ID, selectedSchool.toString());
                editor.putString(Constants.PASSENGER_SCHOOL_ID, selectedSchool.toString());
                editor.apply();
                ((ScuffApplication) getApplicationContext()).setSchool(selectedSchool);

                if (!family.getDriversSchools().contains(selectedSchool)) {
                    // disable "driver" button
                    findViewById(R.id.driver_button).setEnabled(false);
                } else {
                    findViewById(R.id.driver_button).setEnabled(true);
                }
                if (!family.getPassengersSchools().contains(selectedSchool)) {
                    // disable "passenger" button
                    findViewById(R.id.passenger_button).setEnabled(false);
                } else {
                    findViewById(R.id.passenger_button).setEnabled(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        loadTestData();
    }

    public void goDriver(View v) {
        Intent intent = new Intent(this, DriverHomeActivity.class);
        startActivity(intent);
    }

    public void goPassenger(View v) {
        Intent intent = new Intent(this, PassengerHomeActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

}
