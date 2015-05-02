package nz.co.scuff.server;

import java.util.List;

import nz.co.scuff.data.journey.Journey;
import nz.co.scuff.data.journey.Snapshot;
import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by Callum on 26/04/2015.
 */
public interface ScuffServerInterface {

    @POST("/journeys")
    void postJourney(@Body Journey journey, Callback<Response> cb);

    @GET("/journeys/{id}")
    Journey getJourney(@Path("id") String journeyId);

    @GET("/journeys")
    List<Journey> getJourneys(@Query("routeId") String routeId, @Query("schoolId") String schoolId);

    @GET("/journeys/snapshots/{id}")
    Snapshot getSnapshot(@Path("id") String journeyId);

    @GET("/journeys/snapshots")
    List<Snapshot> getSnapshotsByRouteAndSchool(@Query("routeId") String routeId, @Query("schoolId") String schoolId);

/*
    @GET("/journey/{id}/last")
    List<WaypointSnapshot> getJourneyLastKnownWaypointSnapshot(@Path("id") String journeyId);
*/

}
