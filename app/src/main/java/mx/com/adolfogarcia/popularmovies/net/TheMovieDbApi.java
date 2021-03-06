package mx.com.adolfogarcia.popularmovies.net;

import mx.com.adolfogarcia.popularmovies.model.transport.GeneralConfigurationJsonModel;
import mx.com.adolfogarcia.popularmovies.model.transport.MoviePageJsonModel;
import mx.com.adolfogarcia.popularmovies.model.transport.MovieReviewPageJsonModel;
import mx.com.adolfogarcia.popularmovies.model.transport.MovieVideosJsonModel;
import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Declaration of the RESTful API provided by
 * <a href="https://www.themoviedb.org/">themoviedb.org</a> for use with
 * <a href="http://square.github.io/retrofit/">Retrofit</a>.
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
     * Get a page worth of results from the list of movies provided by the
     * services, sorted according to the criteria given.
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

    /**
     * Get the list of videos available for a given movie.
     *
     * @param movieId the id of the movie for which the list of available
     *                videos should be returned.
     * @param apiKey the key required to access the services.
     * @return a {@link Call} that can retrieve the videos related to the movie.
     */
    @GET("/3/movie/{id}/videos")
    Call<MovieVideosJsonModel> getMovieVideos(@Path("id") long movieId
            , @Query("api_key") String apiKey);

    /**
     * Get the requested page of reviews for the specified movie.
     *
     * @param movieId the id of the movie for which the page of reviews should
     *                be returned.
     * @param apiKey the key required to access the services.
     * @param page  the number of the page to retrieve (first page index: 1).
     * @return a {@link Call} that can retrieve the specified page of reviews.
     */
    @GET("/3/movie/{id}/reviews")
    Call<MovieReviewPageJsonModel> getMovieReviews(@Path("id") long movieId
            , @Query("api_key") String apiKey
            , @Query("page") int page);

}
