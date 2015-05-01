package nz.co.scuff.server;

import nz.co.scuff.data.journey.Journey;
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
public interface DriverServerInterface {

    @POST("/journeys")
    void postJourney(@Body Journey journey, Callback<Response> cb);

    @GET("/journeys/{id}")
    Journey getJourney(@Path("id") String journeyId);

    @GET("/journeys")
    Journey getJourney(@Query("routeId") String routeId, @Query("schoolId") String schoolId);

}
