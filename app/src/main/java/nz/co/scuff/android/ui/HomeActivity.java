package nz.co.scuff.android.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Set;

import nz.co.scuff.android.R;
import nz.co.scuff.android.data.ScuffDatasource;
import nz.co.scuff.android.util.DialogHelper;
import nz.co.scuff.android.util.SchoolAdapter;
import nz.co.scuff.data.family.Driver;
import nz.co.scuff.data.school.School;
import nz.co.scuff.android.util.ScuffApplication;

public class HomeActivity extends Activity {

    private static final String TAG = "HomeActivity";
    private static final boolean D = true;

    private boolean initialising = false;
    private Context context;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        context = this;

        /*Driver driver = (Driver)getIntent().getSerializableExtra(Constants.USER_KEY);
        ((ScuffApplication) getApplicationContext()).setDriver(driver);*/

        // Create global configuration and initialize ImageLoader with this config
        // TODO caching not enabled by default
        /*ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(config);*/

        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Loading profile...");

        // populate data for testing
        //populateSchools();
        initialise();

    }

    // TODO log in with google+ etc
    private void initialise() {
        if (D) Log.d(TAG, "initialising");

        if (!initialising) {
            initialising = true;
            ScuffApplication scuffContext = (ScuffApplication) getApplicationContext();
            Driver driver = scuffContext.getDriver();
            if (driver == null) {
                if (D) Log.d(TAG, "loading profile");
                new AsyncSendMessage().execute();
            }
        }
    }

    private class AsyncSendMessage extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... params) {
            if (D) Log.d(TAG, "starting background load");

            // TODO user name from accounts
            Driver driver = ScuffDatasource.getDriver("callum@gmail.com");
            int resultCode = -1;
            if (driver != null) {
                ((ScuffApplication) getApplicationContext()).setDriver(driver);
                resultCode = 1;
            }
            return resultCode;
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
            initialising = false;

            if (resultCode == 1) {
                populateSchools();
            } else {
                DialogHelper.dialog(context, "Error loading profile", "Argh there was a problem loading your profile");
            }
        }
    }

    private void populateSchools() {
        if (D) Log.d(TAG, "populating school");

        final Driver driver = ((ScuffApplication) getApplicationContext()).getDriver();
        final TextView nameLabel = (TextView) findViewById(R.id.name_label);
        nameLabel.setText(driver.getFirstName());

        final Set<School> schools = driver.getSchools();
        Spinner schoolSpinner = (Spinner) findViewById(R.id.school_spinner);
        ArrayAdapter<School> dataAdapter = new SchoolAdapter(this,
                android.R.layout.simple_spinner_item, new ArrayList<>(schools));
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        schoolSpinner.setAdapter(dataAdapter);
        schoolSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                School selectedSchool = (School)parent.getItemAtPosition(position);
                ((ScuffApplication) getApplicationContext()).setSchool(selectedSchool);

/*                // TODO scheduling
                if (!driver.isDrivingForSchoolSoon(selectedSchool)) {
                    // disable "driver" button
                    findViewById(R.id.driver_button).setEnabled(false);
                } else {
                    findViewById(R.id.driver_button).setEnabled(true);
                }
                if (!driver.isChildrenAtThisSchool(selectedSchool)) {
                    // disable "passenger" button
                    findViewById(R.id.passenger_button).setEnabled(false);
                } else {
                    findViewById(R.id.passenger_button).setEnabled(true);
                }*/
                // TODO when scheduled -> journey activity
                // TODO when not scheduled -> routes/schedules activity
                findViewById(R.id.driver_button).setEnabled(true);
                findViewById(R.id.passenger_button).setEnabled(true);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
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
