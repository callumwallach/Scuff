package nz.co.scuff.server;

import java.util.ArrayList;
import java.util.List;

import nz.co.scuff.data.family.snapshot.DriverSnapshot;
import nz.co.scuff.data.journey.snapshot.BusSnapshot;
import nz.co.scuff.data.journey.snapshot.JourneySnapshot;
import nz.co.scuff.data.journey.snapshot.TicketSnapshot;
import nz.co.scuff.data.util.DataPacket;
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

    @POST("/driving/journeys")
    void postJourney(@Body JourneySnapshot journey, Callback<TicketSnapshot> cb);

    @PUT("/driving/journeys/{id}")
    void updateJourney(@Path("id") String journeyId, @Body JourneySnapshot journey, Callback<TicketSnapshot> cb);

    @POST("/driving/journeys/{id}/tickets")
    void postTickets(@Path("id") String journeyId, @Body List<TicketSnapshot> snapshots, Callback<Response> cb);

/*    @GET("/journeys/{id}")
    Journey getJourney(@Path("id") String journeyId);

    @GET("/journeys")
    List<Journey> getJourneys(@Query("routeId") String routeId, @Query("schoolId") String schoolId);*/

    @GET("/walking/{id}")
    BusSnapshot getActiveBus(@Path("id") String busId);

    @GET("/walking")
    List<BusSnapshot> getActiveBuses(@Query("routeId") long routeId, @Query("schoolId") long schoolId);

    /*@GET("/walking/{id}")
    JourneySnapshot getActiveJourneySnapshot(@Path("id") String journeyId, @Query("prune") boolean prune);

    @GET("/walking")
    List<JourneySnapshot> getActiveJourneySnapshots(@Query("routeId") String routeId, @Query("schoolId") String schoolId, @Query("prune") boolean prune);
*/
/*    @GET("/profiles/{id}")
    DriverSnapshot getDriverSnapshot(@Path("id") long id);


    @GET("/profiles")
    DriverSnapshot getDriverSnapshotByEmail(@Query("email") String email);
*/

    @GET("/profiles/{email}")
    DataPacket getDriver(@Path("email") String email);

    @GET("/register/schools")
    DataPacket getSchools(@Query("latitude") double latitude, @Query("longitude") double longitude, @Query("radius") int radius);

    @POST("/register")
    void postRegistration(@Body DataPacket packet, Callback<Response> cb);

}
