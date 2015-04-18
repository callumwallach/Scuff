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
import nz.co.scuff.data.family.Parent;
import nz.co.scuff.data.school.School;
import nz.co.scuff.test.TestDataProvider;
import nz.co.scuff.util.Constants;
import nz.co.scuff.util.ScuffContextProvider;


public class HomeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // populate data for testing
        loadTestData();
        populateSchools();

/*        driversList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                final String item = (String) parent.getItemAtPosition(position);
                view.animate().setDuration(2000).alpha(0)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                drivers.remove(item);
                                adapter.notifyDataSetChanged();
                                view.setAlpha(1);
                            }
                        });
            }

        });*/

    }

    private void populateSchools() {

        Family family = ((ScuffContextProvider) getApplicationContext()).getFamily();
        Spinner schoolSpinner = (Spinner) findViewById(R.id.school_spinner);
        ArrayAdapter<School> dataAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, new ArrayList<>(family.getSchools()));
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        schoolSpinner.setAdapter(dataAdapter);
        schoolSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences.Editor editor = getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE).edit();
                editor.putString(Constants.DRIVER_SCHOOL_ID, parent.getItemAtPosition(position).toString());
                editor.putString(Constants.PASSENGER_SCHOOL_ID, parent.getItemAtPosition(position).toString());
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });

    }
/*
    <ListView
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:id="@+id/drivers.list"
    android:layout_gravity="center_horizontal"
    android:layout_weight="1" />
    */

    private void loadTestData() {
        Family family = ((ScuffContextProvider)getApplicationContext()).getFamily();
        if (family == null){
            TestDataProvider.populateTestData();
            family = TestDataProvider.getFamily();
            ((ScuffContextProvider) getApplicationContext()).setFamily(family);
        }
        final TextView welcome = (TextView) findViewById(R.id.family_label);
        welcome.setText(family.getName() + " family");

/*        final ListView driversList = (ListView) findViewById(R.id.drivers_list);
        final ArrayList<Parent> drivers = new ArrayList<>(family.getParents());
        final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, drivers);
        driversList.setAdapter(adapter);*/
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
