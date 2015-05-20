package nz.co.scuff.android.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import java.util.List;

import nz.co.scuff.android.data.ScuffDatasource;
import nz.co.scuff.android.util.Constants;
import nz.co.scuff.data.journey.Ticket;
import nz.co.scuff.server.error.ResourceNotFoundException;

public class TicketIntentService extends IntentService {

    private static final String TAG = "TicketIntentService";
    private static final boolean D = true;

    private Handler handler;

    public TicketIntentService() {
        super("TicketIntentService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (D) Log.d(TAG, "onHandleIntent");

        String journeyId = intent.getStringExtra(Constants.JOURNEY_KEY);
        List<Long> passengerIds = (List<Long>)intent.getSerializableExtra(Constants.PASSENGERS_KEY);
        if (D) for (Long id : passengerIds) Log.d(TAG, "processing ticket " + id);
        try {
            List<Ticket> tickets = ScuffDatasource.requestTickets(journeyId, passengerIds);
            // TODO do something with them perhaps
        } catch (ResourceNotFoundException e) {
            // journey was completed before tickets issued. notify ui
            Log.e(TAG, "Selected journey was not found", e);
        }
    }

}