package nz.co.scuff.android.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import nz.co.scuff.android.util.Constants;

public class ProfileIntentService extends IntentService {

    private static final String TAG = "ProfileIntentService";
    private static final boolean D = true;

    public ProfileIntentService() {
        super("ProfileIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (D) Log.d(TAG, "onHandleIntent");

        String userId = intent.getExtras().getString(Constants.USER_KEY);

        // UI IS WAITING
        // check local db
        // if found check for updates from server
        // if not there retrieve from server (first log in)
        // store in db
        // return user
        // RELEASE UI

    }

}