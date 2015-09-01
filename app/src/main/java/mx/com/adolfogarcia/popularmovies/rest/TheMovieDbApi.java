package mx.com.adolfogarcia.popularmovies.rest;

import mx.com.adolfogarcia.popularmovies.model.transport.GeneralConfigurationJsonModel;
import retrofit.Call;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Declaration of the RESTful API provided by <a href="https://www.themoviedb.org/">themoviedb.org</a>
 * for use with <a href="http://square.github.io/retrofit/">Retrofit</a>.
 *
 * @author Jesús Adolfo García Pasquel
 */
public interface TheMovieDbApi {

    /**
     * The API's base URL.
     */
    String BASE_URL = "https://api.themoviedb.org/3";

    /**
     * Get the system wide configuration information.
     *
     * @param apiKey the key required to access the services.
     * @return a {@link Call} that can retrieve the system wide configuration information.
     */
    @GET("/3/configuration")
    Call<GeneralConfigurationJsonModel> getConfiguration(@Query("api_key") String apiKey);

}
