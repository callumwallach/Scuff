package nz.co.scuff.android.ui.registration;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

import nz.co.scuff.android.R;
import nz.co.scuff.android.util.Constants;
import nz.co.scuff.data.family.Driver;
import nz.co.scuff.data.family.Person;

public class RegisterGuardianActivity extends Activity {

    private static final String TAG = "Registration";
    private static final boolean D = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_guardian);
    }


    public void doNext(View v) {

        // go to next page -> add children
        Driver driver = new Driver();
        String fname = ((EditText)findViewById(R.id.driver_fname)).getText().toString();
        String lname = ((EditText)findViewById(R.id.driver_lname)).getText().toString();
        String email = ((EditText)findViewById(R.id.driver_email)).getText().toString();
        String phone = ((EditText)findViewById(R.id.driver_phone)).getText().toString();
        String address = ((EditText)findViewById(R.id.driver_address)).getText().toString();
        boolean male = ((RadioButton)findViewById(R.id.male_radioButton)).isChecked();

        driver.setFirstName(fname);
        driver.setLastName(lname);
        driver.setEmail(email);
        driver.setPhone(phone);
        /*driver.setAddress(address);*/
        driver.setGender(male ? Person.Gender.MALE : Person.Gender.FEMALE);

        /*Intent registrationIntent = new Intent(this, RegistrationIntentService.class);
        registrationIntent.putExtra(Constants.USER_KEY, this.driver);
        registrationIntent.putExtra(Constants.SCHOOLS_KEY, this.schools);
        registrationIntent.putExtra(Constants.ROUTES_KEY, this.routes);
        registrationIntent.putExtra(Constants.PASSENGERS_KEY, this.passengers);
        startService(registrationIntent);*/

        Intent intent = new Intent(this, RegisterChildrenActivity.class);
        intent.putExtra(Constants.USER_KEY, driver);
        startActivity(intent);

    }

    public void selectPhoto(View v) {
        // TODO add profile pic
        // http://www.theappguruz.com/blog/android-take-photo-camera-gallery-code-sample/
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
