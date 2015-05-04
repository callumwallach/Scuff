package nz.co.scuff.android.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.Set;

import nz.co.scuff.android.R;
import nz.co.scuff.android.data.ScuffDatasource;
import nz.co.scuff.data.family.Child;
import nz.co.scuff.data.family.Parent;
import nz.co.scuff.data.school.School;
import nz.co.scuff.android.util.ScuffApplication;

public class HomeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Create global configuration and initialize ImageLoader with this config
        // TODO caching not enabled by default
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(config);

        // populate data for testing
        initialise();
        populateSchools();

    }

    private void initialise() {
        ScuffApplication scuffContext = (ScuffApplication) getApplicationContext();
        Parent driver = scuffContext.getDriver();
        if (driver == null){
            driver = ScuffDatasource.getParentByEmail("callum@gmail.com");
/*            TestDataProvider.populateTestData();
            driver = Parent.load(Parent.class, 1);*/
            scuffContext.setDriver(driver);
        }
        final TextView nameLabel = (TextView) findViewById(R.id.name_label);
        nameLabel.setText(driver.getFirstName());

    }

    private void populateSchools() {

        final Parent driver = ((ScuffApplication) getApplicationContext()).getDriver();
        final Set<School> schools = driver.getSchools();
        final Set<Child> children = driver.getChildren();
        Spinner schoolSpinner = (Spinner) findViewById(R.id.school_spinner);
        ArrayAdapter<School> dataAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, new ArrayList<>(schools));
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        schoolSpinner.setAdapter(dataAdapter);
        schoolSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                School selectedSchool = (School)parent.getItemAtPosition(position);
                ((ScuffApplication) getApplicationContext()).setSchool(selectedSchool);

                // TODO scheduling
                if (!driver.getSchoolsIDriveFor().contains(selectedSchool)) {
                    // disable "driver" button
                    findViewById(R.id.driver_button).setEnabled(false);
                } else {
                    findViewById(R.id.driver_button).setEnabled(true);
                }
                if (!driver.getSchoolsMyChildrenGoTo().contains(selectedSchool)) {
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
        initialise();
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
