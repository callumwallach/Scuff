package nz.co.scuff.server;

import java.util.List;

import nz.co.scuff.data.journey.Waypoint;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by Callum on 26/04/2015.
 */
public interface PassengerServerInterface {

    @GET("/waypoints")
    List<Waypoint> getCurrentWaypoint(@Query("routeId") String routeId, @Query("schoolId") String schoolId);

}
