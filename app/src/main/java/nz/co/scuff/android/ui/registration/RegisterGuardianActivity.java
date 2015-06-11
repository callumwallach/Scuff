package nz.co.scuff.android.ui.registration;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import nz.co.scuff.android.R;

public class RegisterGuardianActivity extends Activity {

    private static final String TAG = "Registration";
    private static final boolean D = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_guardian);
    }


    public void doNext(View v) {

/*        // go to next page -> add children
        Coordinator adult = new Coordinator();
        String fname = ((EditText)findViewById(R.id.driver_fname)).getText().toString();
        String lname = ((EditText)findViewById(R.id.driver_lname)).getText().toString();
        String email = ((EditText)findViewById(R.id.driver_email)).getText().toString();
        String phone = ((EditText)findViewById(R.id.driver_phone)).getText().toString();
        String address = ((EditText)findViewById(R.id.driver_address)).getText().toString();
        boolean male = ((RadioButton)findViewById(R.id.male_radioButton)).isChecked();

        adult.setFirstName(fname);
        adult.setLastName(lname);
        adult.setEmail(email);
        adult.setPhone(phone);
        *//*adult.setAddress(address);*//*
        adult.setGender(male ? Person.Gender.MALE : Person.Gender.FEMALE);

        *//*Intent registrationIntent = new Intent(this, RegistrationIntentService.class);
        registrationIntent.putExtra(Constants.USER_KEY, this.adult);
        registrationIntent.putExtra(Constants.SCHOOLS_KEY, this.schools);
        registrationIntent.putExtra(Constants.ROUTES_KEY, this.routes);
        registrationIntent.putExtra(Constants.PASSENGERS_KEY, this.passengers);
        startService(registrationIntent);*//*

        Intent intent = new Intent(this, RegisterChildrenActivity.class);
        intent.putExtra(Constants.USER_KEY, (Parcelable) adult);
        startActivity(intent);*/

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
