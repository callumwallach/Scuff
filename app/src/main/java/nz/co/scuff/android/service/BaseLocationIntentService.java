package nz.co.scuff.android.service;

import android.app.IntentService;
import android.content.Intent;
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

public abstract class BaseLocationIntentService extends IntentService implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final String TAG = "BaseLocIntentService";
    private static final boolean D = true;

    private GoogleApiClient locationClient;

    public BaseLocationIntentService() {
        super("BaseLocationIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (D) Log.d(TAG, "onHandleIntent");
        connectToGooglePlay();
    }

    private void connectToGooglePlay() {
        if (D) Log.d(TAG, "connectToGooglePlay");
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

/*    @Override
    public void onLocationChanged(Location location) {
        if (D) Log.d(TAG, "onLocationChanged");
        if (location != null) {
            if (D) Log.d(TAG, "position: " + location.getLatitude() + ", " + location.getLongitude() + " accuracy: " + location.getAccuracy());
            // have desired accuracy so quit service, onDestroy will be called and stop our location updates
            if (location.getAccuracy() < Constants.MIN_LOCATION_ACCURACY) {
                stopLocationUpdates();
                // post location to event bus
                EventBus.getDefault().post(new LocationEvent(location));
            }
        }
    }*/

    protected void stopLocationUpdates() {
        if (D) Log.d(TAG, "stopLocationUpdates");

        if (locationClient != null && locationClient.isConnected()) {
            com.google.android.gms.common.api.PendingResult<Status> result =
                    LocationServices.FusedLocationApi.removeLocationUpdates(locationClient, this);

            // Callback is asynchronous. Use await() on a background thread or listen for the ResultCallback
            result.setResultCallback(new ResultCallback<Status>() {
                public void onResult(Status status) {
                    if (status.isSuccess()) {
                        if (D) Log.d(TAG, "Stop location updates succeeded: " + status.getStatusMessage());
                    } else if (status.hasResolution()) {
                        // Google provides a way to fix the issue
                        if (D) Log.d(TAG, "Stop location updates failed but may be resolved: " + status.getStatusMessage());
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
    // TODO optimise location requests
    @Override
    public void onConnected(Bundle bundle) {
        if (D) Log.d(TAG, "onConnected");
        LocationRequest locationRequest = LocationRequest.create().
                setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY).
                // interval between updates. ignored because only around for one update?
                setInterval(1000).
                // set to default (6 x interval) as only getting one update
                setFastestInterval(1000);
                /* min distance between locations
                setSmallestDisplacement(10);*/
        com.google.android.gms.common.api.PendingResult<Status> result =
                LocationServices.FusedLocationApi.requestLocationUpdates(locationClient, locationRequest, this);
        // Callback is asynchronous. Use await() on a background thread or listen for the ResultCallback
        result.setResultCallback(new ResultCallback<Status>() {
            public void onResult(Status status) {
                if (status.isSuccess()) {
                    if (D) Log.d(TAG, "Location registration succeeded: " + status.getStatusMessage());
                } else if (status.hasResolution()) {
                    // Google provides a way to fix the issue
                    if (D) Log.d(TAG, "Location registration failed but may be resolved: " + status.getStatusMessage());
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
        if (D) Log.d(TAG, "onConnectionSuspended");
        stopLocationUpdates();
        stopSelf();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (D) Log.d(TAG, "onConnectionFailed");
        stopLocationUpdates();
        stopSelf();
    }

}