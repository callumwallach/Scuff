package nz.co.scuff.android.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;

import nz.co.scuff.android.R;
import nz.co.scuff.android.data.ScuffDatasource;
import nz.co.scuff.data.base.Coordinator;
import nz.co.scuff.android.util.ScuffApplication;
import nz.co.scuff.server.error.ResourceException;

public class HomeActivity extends BaseActivity {

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

        /*Coordinator adult = (Coordinator)getIntent().getSerializableExtra(Constants.USER_KEY);
        ((ScuffApplication) getApplicationContext()).setCoordinator1(adult);*/

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
            /*ScuffApplication scuffContext = (ScuffApplication) getApplicationContext();
            Coordinator coordinator = scuffContext.getCoordinator();
            if (coordinator == null) {*/
            if (D) Log.d(TAG, "loading profile");
            new AsyncSendMessage().execute();
            //}
        }
    }

    private class AsyncSendMessage extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... params) {
            if (D) Log.d(TAG, "starting background load");

            // TODO user name from accounts
            int resultCode = -1;
            try {
                Coordinator coordinator = ScuffDatasource.getUser("callum@gmail.com");
                ((ScuffApplication) getApplicationContext()).setCoordinator(coordinator);
                resultCode = 1;
            } catch (ResourceException re) {
                Log.e(TAG, "Error person not found", re);
                progressDialog.dismiss();
/*                ErrorEvent event = new ErrorEvent(re);
                EventBus.getDefault().post(event);*/
            } catch (Throwable t) {
                Log.e(TAG, "Error:", t);
                progressDialog.dismiss();
/*                ErrorEvent event = new ErrorEvent(new NetworkException("Connection Error", t));
                EventBus.getDefault().post(event);*/
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
/*
            if (resultCode == 1) {
                populateSchools();
            } else {
                DialogHelper.dialog(context, "Error loading profile", "Please contact your system administrator");
            }*/
        }
    }

/*    private void populateSchools() {
        if (D) Log.d(TAG, "populating school");

        final Coordinator coordinator = ((ScuffApplication) getApplicationContext()).getCoordinator();
        final TextView nameLabel = (TextView) findViewById(R.id.name_label);
        nameLabel.setText(coordinator.getPersonalData().getFirstName());

        findViewById(R.id.adult_button).setEnabled(true);
        findViewById(R.id.passenger_button).setEnabled(true);

        *//*final Set<Institution> institutions = adult.getGuidees();
        Spinner schoolSpinner = (Spinner) findViewById(R.id.institution_spinner);
        ArrayAdapter<Institution> dataAdapter = new CoordinatorAdapter(this,
                android.R.layout.simple_spinner_item, new ArrayList<>(institutions));
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        schoolSpinner.setAdapter(dataAdapter);
        schoolSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Institution selectedInstitution = (Institution)parent.getItemAtPosition(position);
                ((ScuffApplication) getApplicationContext()).setInstitution(selectedInstitution);

*//**//*                // TODO scheduling
                if (!adult.isDrivingForSchoolSoon(selectedInstitution)) {
                    // disable "adult" button
                    findViewById(R.id.driver_button).setEnabled(false);
                } else {
                    findViewById(R.id.driver_button).setEnabled(true);
                }
                if (!adult.isChildrenAtThisSchool(selectedInstitution)) {
                    // disable "passenger" button
                    findViewById(R.id.passenger_button).setEnabled(false);
                } else {
                    findViewById(R.id.passenger_button).setEnabled(true);
                }*//**//*
                // TODO when scheduled -> journey activity
                // TODO when not scheduled -> routes/schedules activity
                findViewById(R.id.adult_button).setEnabled(true);
                findViewById(R.id.passenger_button).setEnabled(true);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
*//*
    }*/

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
    }

    public void startAJourney(View v) {
        Intent intent = new Intent(this, DriverJourneyChoiceActivity.class);
        startActivity(intent);
    }

    public void joinAJourney(View v) {
        Intent intent = new Intent(this, PassengerHomeActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

}
