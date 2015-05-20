package nz.co.scuff.server;

import java.util.List;

import nz.co.scuff.data.journey.snapshot.BusSnapshot;
import nz.co.scuff.data.journey.snapshot.JourneySnapshot;
import nz.co.scuff.data.journey.snapshot.TicketSnapshot;
import nz.co.scuff.data.util.DataPacket;
import nz.co.scuff.server.error.ResourceNotFoundException;
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
    void postJourney(@Body JourneySnapshot journey, Callback<List<TicketSnapshot>> cb);

    @PUT("/driving/journeys/{id}")
    void updateJourney(@Path("id") String journeyId, @Body JourneySnapshot journey, Callback<List<TicketSnapshot>> cb);

    // walking

    /*@GET("/walking/buses")
    List<BusSnapshot> getActiveBuses(@Query("routeId") long routeId, @Query("schoolId") long schoolId);*/

    @GET("/walking/buses")
    DataPacket getActiveJourneys(@Query("routeId") long routeId, @Query("schoolId") long schoolId);

    @POST("/walking/buses/{id}/tickets")
    List<TicketSnapshot> requestTickets(@Path("id") String journeyId, @Body List<Long> passengerIds) throws ResourceNotFoundException;

    //accounts

    @GET("/account/drivers/{email}")
    DataPacket getDriver(@Path("email") String email);

    @GET("/account/schools")
    DataPacket getSchools(@Query("latitude") double latitude, @Query("longitude") double longitude, @Query("radius") int radius);

    // registration

    @POST("/register")
    void postRegistration(@Body DataPacket packet, Callback<Response> cb);

}
