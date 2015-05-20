package nz.co.scuff.android.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.HashMap;

import nz.co.scuff.android.data.ScuffDatasource;
import nz.co.scuff.android.ui.HomeActivity;
import nz.co.scuff.android.util.Constants;
import nz.co.scuff.data.family.Driver;
import nz.co.scuff.data.family.Passenger;
import nz.co.scuff.data.relationship.DriverPassenger;
import nz.co.scuff.data.relationship.DriverRoute;
import nz.co.scuff.data.relationship.DriverSchool;
import nz.co.scuff.data.school.Route;
import nz.co.scuff.data.school.School;
import nz.co.scuff.data.util.DataPacket;

public class RegistrationIntentService extends IntentService {

    private static final String TAG = "RegIntentService";
    private static final boolean D = true;

    public RegistrationIntentService() {
        super("RegistrationIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (D) Log.d(TAG, "onHandleIntent");

        Driver driver = (Driver)intent.getSerializableExtra(Constants.USER_KEY);
        HashMap<Long, School> schools = (HashMap<Long, School>) intent.getSerializableExtra(Constants.SCHOOLS_KEY);
        HashMap<Long, Route> routes = (HashMap<Long, Route>) intent.getSerializableExtra(Constants.ROUTES_KEY);
        HashMap<Long, Passenger> passengers = (HashMap<Long, Passenger>) intent.getSerializableExtra(Constants.PASSENGERS_KEY);

        // save entities to db
        driver.save();
        for (School school : schools.values()) {
            school.save();
            DriverSchool driverSchool = new DriverSchool(driver, school);
            driverSchool.save();
            school.getDriverSchools().add(driverSchool);
            driver.getDriverSchools().add(driverSchool);
            school.save();
        }
        for (Route route : routes.values()) {
            route.save();
            DriverRoute driverRoute = new DriverRoute(driver, route);
            driverRoute.save();
            driver.getDriverRoutes().add(driverRoute);
        }
        for (Passenger passenger : passengers.values()) {
            passenger.save();
            DriverPassenger driverPassenger = new DriverPassenger(driver, passenger);
            driverPassenger.save();
            passenger.getDriverPassengers().add(driverPassenger);
            driver.getDriverPassengers().add(driverPassenger);
        }
        driver.save();




        // send to server
        DataPacket packet = new DataPacket();

        ScuffDatasource.postRegistration(packet);

        // proceed to home activity
        Intent homeIntent = new Intent(this, HomeActivity.class);
        homeIntent.putExtra(Constants.USER_KEY, (Parcelable)driver);
        startActivity(homeIntent);

    }

}
