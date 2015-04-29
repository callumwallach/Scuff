package nz.co.scuff.server;

import java.util.List;

import nz.co.scuff.data.journey.Journey;
import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

/**
 * Created by Callum on 26/04/2015.
 */
public interface ScuffServerInterface {

    @POST("/journeys/start")
    void start(@Body Journey journey, Callback<Response> cb);

    @POST("/journeys/update")
    void update(@Body Journey journey, Callback<Response> cb);

/*    @POST("/journeys/start")
    void startJourney(@Body Journey journey, Callback<Response> cb);

    @POST("/journeys/pause")
    void pauseJourney(@Body Journey journey, Callback<Response> cb);

    @POST("/journeys/continue")
    void continueJourney(@Body Journey journey, Callback<Response> cb);

    @POST("/journeys/stop")
    void stopJourney(@Body Journey journey, Callback<Response> cb);

    @POST("/journeys/record")
    void recordJourney(@Body Journey journey, Callback<Response> cb);*/

    @GET("/journeys/{journeyId}")
    List<Journey> getJourney(@Path("journeyId") String journeyId);

    @GET("/journeys/{journeyId}/waypoint")
    List<Journey> getWaypoint(@Path("journeyId") String journeyId);

}
