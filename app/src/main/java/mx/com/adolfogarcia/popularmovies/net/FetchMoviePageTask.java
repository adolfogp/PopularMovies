/*
 * Copyright 2015 Jesús Adolfo García Pasquel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mx.com.adolfogarcia.popularmovies.net;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import mx.com.adolfogarcia.popularmovies.data.RestfulServiceConfiguration;
import mx.com.adolfogarcia.popularmovies.model.transport.MoviePageJsonModel;

import static mx.com.adolfogarcia.popularmovies.data.MovieContract.CachedMovieEntry;
import mx.com.adolfogarcia.popularmovies.model.transport.MovieJsonModel;
import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Task that retrieves a page of movies from
 * <a href="https://www.themoviedb.org/">themoviedb.org</a>'s RESTful API ordered
 * by the criteria specified at construction time, and inserts them into
 * {@link mx.com.adolfogarcia.popularmovies.data.MovieProvider}. For example,
 * the order criteria may be {@link TheMovieDbApi#SORT_BY_USER_RATING} and
 * we may specify we want page three as an argument
 * {@code myFetchMoviePageTask.execute(3)}.
 *
 * @author Jesús Adolfo García Pasquel
 */
public class FetchMoviePageTask extends AsyncTask<Integer, Void, Void> {

    /**
     * Identifies the messages written to the log by this class.
     */
    private static final String LOG_TAG = FetchMoviePageTask.class.getSimpleName();

    /**
     * The configuration of the RESTful API.
     */
    private WeakReference<RestfulServiceConfiguration> mWeakConfiguration;

    /**
     * The {@link Context} used to access
     * {@link mx.com.adolfogarcia.popularmovies.data.MovieProvider}.
     */
    private WeakReference<Context> mWeakContext;

    /**
     * The order criteria by which the movies to be retrieved from
     * <a href="https://www.themoviedb.org/">themoviedb.org</a> are sorted.
     *
     * @see TheMovieDbApi#SORT_BY_POPULARITY
     * @see TheMovieDbApi#SORT_BY_USER_RATING
     */
    private String mOrderCriteria;

    /**
     * Creates a new instance of {@link FetchMoviePageTask} that uses the
     * provided {@link RestfulServiceConfiguration} to access
     * <a href="https://www.themoviedb.org/">themoviedb.org</a>'s RESTful API
     * with the specified order criteria and uses to {@link Context} to
     * access {@link mx.com.adolfogarcia.popularmovies.data.MovieProvider}.
     *
     *  @param configuration the configuration used to access movie pages.
     * @param orderCriteria the order criteria for the query to the RESTful API.
     * @param context the context used to access the provider on which the
     *                movie data will be stored.
     */
    public FetchMoviePageTask(RestfulServiceConfiguration configuration
            , String orderCriteria
            , Context context) {
        if (configuration == null) {
            throw new IllegalArgumentException("The Configuration may not be null");
        }
        if (context == null) {
            throw new IllegalArgumentException("The Context may not be null");
        }
        mWeakConfiguration = new WeakReference<>(configuration);
        mWeakContext = new WeakReference<>(context);
        mOrderCriteria = orderCriteria;
    }

    @Override
    protected Void doInBackground(Integer... params) {
        Log.d(LOG_TAG, "Starting download of movie page: " + params[0]);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(TheMovieDbApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        TheMovieDbApi service = retrofit.create(TheMovieDbApi.class);
        Call<MoviePageJsonModel> configCall = service.getMoviePage(
                mWeakConfiguration.get().getMovieApiKey()
                , mOrderCriteria
                , params[0]
        );
        try {
            Response<MoviePageJsonModel> response = configCall.execute();
            if (response.isSuccess()) {
                Log.i(LOG_TAG, "Successfully downloaded movie page " + params[0]);
                insertMoviesInProvider(response.body());
            } else {
                Log.w(LOG_TAG, "Failed to download movie page " + params[0]);
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error getting movie page " + params[0], e);
        }
        return null;
    }

    /**
     * Inserts the movies in the page retrieved from
     * <a href="https://www.themoviedb.org/">themoviedb.org</a>'s RESTful API
     * into
     * {@link mx.com.adolfogarcia.popularmovies.data.MovieProvider}.
     *
     * @param response the reply from the RESTful API.
     */
    private void insertMoviesInProvider(MoviePageJsonModel response) {
        List<MovieJsonModel> movieList = response.getMovies();
        ContentValues[] cvArray = new ContentValues[movieList.size()];
        for (int i = 0; i < cvArray.length; i++) {
            MovieJsonModel movie = movieList.get(i);
            ContentValues contentValues = new ContentValues();
            contentValues.put(CachedMovieEntry.COLUMN_API_ID
                    , movie.getId());
            contentValues.put(CachedMovieEntry.COLUMN_ORIGINAL_TITLE
                    , movie.getOriginalTitle());
            contentValues.put(CachedMovieEntry.COLUMN_RELEASE_DATE
                    , movie.getReleaseDateEpochTimeUtc());
            contentValues.put(CachedMovieEntry.COLUMN_OVERVIEW
                    , movie.getOverview());
            contentValues.put(CachedMovieEntry.COLUMN_BACKDROP_PATH
                    , movie.getBackdropPath());
            contentValues.put(CachedMovieEntry.COLUMN_POPULARITY
                    , movie.getPopularity());
            contentValues.put(CachedMovieEntry.COLUMN_VOTE_AVERAGE
                    , movie.getVoteAverage());
            contentValues.put(CachedMovieEntry.COLUMN_POSTER_PATH
                    , movie.getPosterPath());
            cvArray[i] = contentValues;
        }
        if (cvArray.length > 0 && mWeakContext != null && mWeakConfiguration != null) {
            mWeakContext.get().getContentResolver().bulkInsert(
                    CachedMovieEntry.CONTENT_URI, cvArray);
            mWeakConfiguration.get().setTotalMoviePagesAvailable(response.getTotalPages());
            mWeakConfiguration.get().setLastMoviePageRetrieved(response.getPageNumber());
        }
    }

}
