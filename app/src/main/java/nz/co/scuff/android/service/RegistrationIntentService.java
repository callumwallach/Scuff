package nz.co.scuff.android.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Parcelable;
import android.util.Log;

import java.util.HashMap;

import nz.co.scuff.android.data.ScuffDatasource;
import nz.co.scuff.android.ui.HomeActivity;
import nz.co.scuff.android.util.Constants;
import nz.co.scuff.data.base.Coordinator;
import nz.co.scuff.data.family.Child;
import nz.co.scuff.data.institution.Route;
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

        Coordinator coordinator = (Coordinator)intent.getSerializableExtra(Constants.USER_KEY);
        HashMap<Long, Coordinator> schools = (HashMap<Long, Coordinator>) intent.getSerializableExtra(Constants.SCHOOLS_KEY);
        HashMap<Long, Route> routes = (HashMap<Long, Route>) intent.getSerializableExtra(Constants.ROUTES_KEY);
        HashMap<Long, Child> passengers = (HashMap<Long, Child>) intent.getSerializableExtra(Constants.PASSENGERS_KEY);

        // save entities to db
/*        adult.save();
        for (Institution institution : schools.values()) {
            institution.save();
            FriendRelationship friendRelationship = new FriendRelationship(adult, institution);
            friendRelationship.save();
            institution.getCoordinatorCoordinators().add(friendRelationship);
            adult.getDriverSchools().add(friendRelationship);
            institution.save();
        }
        for (Route route : routes.values()) {
            route.save();
            PlaceRelationship placesRelationship = new PlaceRelationship(adult, route);
            placesRelationship.save();
            adult.getCoordinatorRoutes().add(placesRelationship);
        }
        for (Child child : passengers.values()) {
            child.save();
            ParentalRelationship parentalRelationship = new ParentalRelationship(adult, child);
            parentalRelationship.save();
            child.getParents().add(parentalRelationship);
            adult.getChildren().add(parentalRelationship);
        }
        adult.save();*/




        // send to server
        DataPacket packet = new DataPacket();

        ScuffDatasource.postRegistration(packet);

        // proceed to home activity
        Intent homeIntent = new Intent(this, HomeActivity.class);
        homeIntent.putExtra(Constants.USER_KEY, (Parcelable) coordinator);
        startActivity(homeIntent);

    }

}
