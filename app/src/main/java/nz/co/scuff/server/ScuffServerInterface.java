package nz.co.scuff.server;

import java.util.List;

import nz.co.scuff.data.util.DataPacket;
import nz.co.scuff.server.error.ResourceException;
import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by Callum on 26/04/2015.
 */
public interface ScuffServerInterface {

    // driving
    @POST("/driving/journeys")
    void startJourney(@Body DataPacket packet, Callback<DataPacket> cb);

    @PUT("/driving/journeys/{id}")
    void updateJourney(@Path("id") long journeyId, @Body DataPacket packet, Callback<DataPacket> cb);

    // walking
    @GET("/walking/journeys")
    DataPacket getJourneys(@Query("adultId") long adultId, @Query("watchedIds[]") List<Long> watchedIds, @Query("active") boolean active);

    /*@GET("/walking/buses")
    List<BusSnapshot> getActiveBuses(@Query("routeId") long routeId, @Query("schoolId") long schoolId);*/

/*
    @GET("/walking/buses")
    DataPacket getActiveJourneys(@Query("routeId") long routeId, @Query("schoolId") long schoolId);
*/

    @POST("/walking/journeys/{id}/tickets")
    DataPacket issueTickets(@Path("id") long journeyId, @Body List<Long> childIds) throws ResourceException;

    /*@POST("/walking/buses/{id}/tickets")
    List<TicketSnapshot> issueTickets(@Path("id") String journeyId, @Body List<Long> passengerIds) throws ResourceException;*/

    // accounts
    @GET("/account/drivers/{email}")
    DataPacket getDriver(@Path("email") String email, @Query("lastChecked") long lastChecked) throws ResourceException;

/*
    @GET("/account/schools")
    DataPacket getSchools(@Query("latitude") double latitude, @Query("longitude") double longitude, @Query("radius") int radius);
*/

    // registration

    @POST("/register")
    void postRegistration(@Body DataPacket packet, Callback<Response> cb);

}
