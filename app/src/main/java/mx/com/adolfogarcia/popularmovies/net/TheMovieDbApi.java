package mx.com.adolfogarcia.popularmovies.net;

import mx.com.adolfogarcia.popularmovies.model.transport.MoviePageJsonModel;
import mx.com.adolfogarcia.popularmovies.model.transport.GeneralConfigurationJsonModel;
import retrofit.Call;
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
    String BASE_URL = "https://api.themoviedb.org/";

    /**
     * Keyword that determines that the movie data should be sorted by
     * popularity and be given in descending order.
     */
    String SORT_BY_POPULARITY = "popularity.desc";

    /**
     * Keyword that determines that the movie data should be sorted by user
     * rating and be given in descending order.
     */
    String SORT_BY_USER_RATING = "vote_average.desc";

    /**
     * Get the system wide configuration information.
     *
     * @param apiKey the key required to access the services.
     * @return a {@link Call} that can retrieve the system wide configuration information.
     */
    @GET("/3/configuration")
    Call<GeneralConfigurationJsonModel> getConfiguration(@Query("api_key") String apiKey);

    /**
     * Get a page worth of results from the list of movied provided by the
     * servides, sorted according to the criteria given.
     *
     * @param apiKey the key required to access the services.
     * @param orderCriteria the keyword that determines how the movies should be
     *                      sorted (e.g. {@link #SORT_BY_POPULARITY}).
     * @param page the number of the page to retrieve (first page index: 1).
     * @return a {@link Call} that can retrieve the page worth of movie data.
     * @see #SORT_BY_POPULARITY
     * @see #SORT_BY_USER_RATING
     */
    @GET("/3/discover/movie")
    Call<MoviePageJsonModel> getMoviePage(@Query("api_key") String apiKey
            , @Query("sort_by") String orderCriteria
            , @Query("page") int page);

}
