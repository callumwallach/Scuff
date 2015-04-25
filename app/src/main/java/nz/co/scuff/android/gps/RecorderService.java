package nz.co.scuff.android.gps;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import nz.co.scuff.android.data.JourneyDatasource;
import nz.co.scuff.android.util.Constants;
import nz.co.scuff.android.util.DialogHelper;

public class RecorderService extends IntentService implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final String TAG = "RecorderService";

    private boolean currentlyProcessingLocation = false;
    private GoogleApiClient locationClient;

    public RecorderService() {
        super("RecorderService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent");
/*
        String driverName = intent.getStringExtra(Constants.DRIVER_DRIVER_ID);
        String routeName = intent.getStringExtra(Constants.DRIVER_ROUTE_ID);

        Intent newIntent = new Intent(context, UploadService.class);
        newIntent.putExtra(Constants.DRIVER_DRIVER_ID, driverName);
        newIntent.putExtra(Constants.DRIVER_ROUTE_ID, routeName);

        context.startService(newIntent);
        */
        int state = intent.getExtras().getInt(Constants.TRACKING_STATE_TYPE);

        switch (state) {
            case Constants.TRACKING_STATE_RECORDING:
                // if still attempting to get location (and alarm called again) ignore
                if (!currentlyProcessingLocation) {
                    currentlyProcessingLocation = true;
                    startTracking();
                }
                break;
            case Constants.TRACKING_STATE_STOPPED:
                // finalise journey
                String journeyId = intent.getExtras().getString(Constants.DRIVER_JOURNEY_ID);
                endJourney(journeyId);
                break;
            case Constants.TRACKING_STATE_PAUSED:
            default:
                DialogHelper.errorToast(this, "invalid PAUSE tracking state");
        }

        // release wake lock
        RecorderAlarmReceiver.completeWakefulIntent(intent);

    }

    private void endJourney(String journeyId) {
        Log.d(TAG, "end journey - start");
        int updatedRows = JourneyDatasource.endJourney(journeyId);
        Log.d(TAG, "end journey - updated ["+updatedRows+"] rows");
    }

    private void startTracking() {
        Log.d(TAG, "startTracking");

        if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS) {
            locationClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            if (!locationClient.isConnected() || !locationClient.isConnecting()) {
                locationClient.connect();
            }
        } else {
            Log.e(TAG, "unable to connect to google play services.");
        }
    }

    protected void sendLocationDataToDB(Location location) {
        Log.d(TAG, "sendLocationDataToDB");
        long rowId = JourneyDatasource.recordWaypoint(location);
        Log.d(TAG, "finished insert id=" + rowId);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged");
        if (location != null) {
            Log.d(TAG, "position: " + location.getLatitude() + ", " + location.getLongitude() + " accuracy: " + location.getAccuracy());
            // have desired accuracy of 500m so quit service, onDestroy will be called and stop our location updates
            if (location.getAccuracy() < Constants.MIN_LOCATION_ACCURACY) {
                stopLocationUpdates();
                sendLocationDataToDB(location);
            }
        }
    }

    private void stopLocationUpdates() {
        Log.d(TAG, "stopLocationUpdates");

        if (locationClient != null && locationClient.isConnected()) {
            com.google.android.gms.common.api.PendingResult<Status> result =
                    LocationServices.FusedLocationApi.removeLocationUpdates(locationClient, this);

            // Callback is asynchronous. Use await() on a background thread or listen for the ResultCallback
            result.setResultCallback(new ResultCallback<Status>() {
                public void onResult(Status status) {
                    if (status.isSuccess()) {
                        Log.d(TAG, "Stop location updates succeeded: " + status.getStatusMessage());
                    } else if (status.hasResolution()) {
                        // Google provides a way to fix the issue
                        Log.d(TAG, "Stop location updates failed but may be resolved: " + status.getStatusMessage());
                    /*status.startResolutionForResult(
                            activity,     // your current activity used to receive the result
                            RESULT_CODE); // the result code you'll look for in your onActivityResult method to retry registering*/
                    } else {
                        // No recovery. Weep softly or inform the user.
                        Log.e(TAG, "Stop location updates failed: " + status.getStatusMessage());
                    }
                }
            });
            locationClient.disconnect();
        }
    }

    /**
     * Called by Location Services when the request to connect the
     * client finishes successfully. At this point, you can
     * request the current location or start periodic updates
     */
    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected");
        LocationRequest locationRequest = LocationRequest.create().
                setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY).
                setInterval(1000).
                setFastestInterval(1000);
        com.google.android.gms.common.api.PendingResult<Status> result =
                LocationServices.FusedLocationApi.requestLocationUpdates(locationClient, locationRequest, this);
        // Callback is asynchronous. Use await() on a background thread or listen for the ResultCallback
        result.setResultCallback(new ResultCallback<Status>() {
            public void onResult(Status status) {
                if (status.isSuccess()) {
                    Log.d(TAG, "Location registration succeeded: " + status.getStatusMessage());
                } else if (status.hasResolution()) {
                    // Google provides a way to fix the issue
                    Log.d(TAG, "Location registration failed but may be resolved: " + status.getStatusMessage());
                    /*status.startResolutionForResult(
                            activity,     // your current activity used to receive the result
                            RESULT_CODE); // the result code you'll look for in your onActivityResult method to retry registering*/
                } else {
                    // No recovery. Weep softly or inform the user.
                    Log.e(TAG, "Location registration failed: " + status.getStatusMessage());
                }
            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(TAG, "onConnectionSuspended");
        stopLocationUpdates();
        stopSelf();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed");
        stopLocationUpdates();
        stopSelf();
    }

}