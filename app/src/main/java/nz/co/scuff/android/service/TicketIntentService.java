package nz.co.scuff.android.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;

import nz.co.scuff.android.data.ScuffDatasource;
import nz.co.scuff.android.util.Constants;
import nz.co.scuff.data.journey.snapshot.TicketSnapshot;

public class TicketIntentService extends IntentService {

    private static final String TAG = "TicketIntentService";
    private static final boolean D = true;

    public TicketIntentService() {
        super("TicketIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (D) Log.d(TAG, "onHandleIntent");

        String journeyId = intent.getStringExtra(Constants.JOURNEY_KEY);
        ArrayList<TicketSnapshot> tickets = intent.getParcelableArrayListExtra(Constants.TICKETS_KEY);
        for (TicketSnapshot ts : tickets) {
            if (D) Log.d(TAG, "processing ticket"+ts);
        }
        ScuffDatasource.postTickets(journeyId, tickets);
    }

}