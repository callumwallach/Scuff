package nz.co.scuff.server;

import java.util.List;

import nz.co.scuff.data.family.snapshot.DriverSnapshot;
import nz.co.scuff.data.journey.snapshot.BusSnapshot;
import nz.co.scuff.data.journey.snapshot.JourneySnapshot;
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

    @POST("/driving/journeys")
    void postJourney(@Body JourneySnapshot journey, Callback<Response> cb);

/*    @GET("/journeys/{id}")
    Journey getJourney(@Path("id") String journeyId);

    @GET("/journeys")
    List<Journey> getJourneys(@Query("routeId") String routeId, @Query("schoolId") String schoolId);*/

    @GET("/walking/{id}")
    BusSnapshot getActiveBusSnapshot(@Path("id") String busId);

    @GET("/walking")
    List<BusSnapshot> getActiveBusSnapshots(@Query("routeId") long routeId, @Query("schoolId") long schoolId);

    /*@GET("/walking/{id}")
    JourneySnapshot getActiveJourneySnapshot(@Path("id") String journeyId, @Query("prune") boolean prune);

    @GET("/walking")
    List<JourneySnapshot> getActiveJourneySnapshots(@Query("routeId") String routeId, @Query("schoolId") String schoolId, @Query("prune") boolean prune);
*/
    @GET("/profiles/{id}")
    DriverSnapshot getDriverSnapshot(@Path("id") long id);

    @GET("/profiles")
    DriverSnapshot getDriverSnapshotByEmail(@Query("email") String email);

}
