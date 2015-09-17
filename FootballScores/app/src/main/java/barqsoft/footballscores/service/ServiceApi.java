package barqsoft.footballscores.service;

import barqsoft.footballscores.model.FixtureList;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Query;

/**
 * Created by sknutti on 9/16/15.
 */
public interface ServiceApi {
    @GET("/fixtures")
    void fetchData(@Header("X-Auth-Token") String token, @Query("timeFrame") String timeframe, Callback<FixtureList> response);
}
