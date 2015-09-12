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

package mx.com.adolfogarcia.popularmovies.model.view;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import org.parceler.Parcel;
import org.parceler.Transient;

import static org.parceler.Parcel.Serialization;

import mx.com.adolfogarcia.popularmovies.data.MovieContract;
import mx.com.adolfogarcia.popularmovies.data.RestfulServiceConfiguration;
import mx.com.adolfogarcia.popularmovies.model.domain.Movie;
import static mx.com.adolfogarcia.popularmovies.data.MovieContract.CachedMovieEntry;

/**
 * View model for the movie detail's view. Provides data and behaviour.
 * When reconstructing (deserializaing), make sure you set the transient
 * attributes, for example {@link #setContext(Context)} and
 * {@link #setConfiguration(RestfulServiceConfiguration)}.
 *
 * @author Jesús Adolfo García Pasquel
 */
@Parcel(Serialization.BEAN)
public class MovieDetailViewModel { // TODO - Implement Observable, notify of changes to the view

    /**
     * Identifies the messages written to the log by this class.
     */
    private static final String LOG_TAG = MovieDetailViewModel.class.getSimpleName();

    private static final int MOVIE_POSTER_WIDTH_DIP = 180; // TODO: Use a resource constant and change the value in the adapter too

    private static final int MOVIE_BACKDROP_WIDTH_DIP = 360; // TODO: Use a resource constant and change the value in the adapter too

    /**
     * Projection that includes the movie details to be presented. Used to
     * query {@link mx.com.adolfogarcia.popularmovies.data.MovieProvider}.
     */
    public static final String[] PROJECTION_MOVIE_DETAILS = {
            CachedMovieEntry.COLUMN_ORIGINAL_TITLE,
            CachedMovieEntry.COLUMN_RELEASE_DATE,
            CachedMovieEntry.COLUMN_OVERVIEW,
            CachedMovieEntry.COLUMN_POSTER_PATH,
            CachedMovieEntry.COLUMN_BACKDROP_PATH,
            CachedMovieEntry.COLUMN_VOTE_AVERAGE
    };

    /**
     * Index of {@link CachedMovieEntry#COLUMN_ORIGINAL_TITLE} in
     * {@link #PROJECTION_MOVIE_DETAILS}
     */
    public static final int COL_ORIGINAL_TITLE = 0;

    /**
     * Index of {@link CachedMovieEntry#COLUMN_RELEASE_DATE} in
     * {@link #PROJECTION_MOVIE_DETAILS}
     */
    public static final int COL_RELEASE_DATE = 1;

    /**
     * Index of {@link CachedMovieEntry#COLUMN_OVERVIEW} in
     * {@link #PROJECTION_MOVIE_DETAILS}
     */
    public static final int COL_OVERVIEW = 2;

    /**
     * Index of {@link CachedMovieEntry#COLUMN_POSTER_PATH} in
     * {@link #PROJECTION_MOVIE_DETAILS}
     */
    public static final int COL_POSTER_PATH = 3;

    /**
     * Index of {@link CachedMovieEntry#COLUMN_BACKDROP_PATH} in
     * {@link #PROJECTION_MOVIE_DETAILS}
     */
    public static final int COL_BACKDROP_PATH = 4;

    /**
     * Index of {@link CachedMovieEntry#COLUMN_VOTE_AVERAGE} in
     * {@link #PROJECTION_MOVIE_DETAILS}
     */
    public static final int COL_VOTE_AVERAGE = 5;

    /**
     * The movie for which the detail data is being shown.
     */
    private Movie mMovie;

    private Context mContext;

    private RestfulServiceConfiguration mConfiguration;

    public Movie getMovie() {
        return mMovie;
    }

    public void setMovie(Movie movie) {
        loadMovieData(movie);
        mMovie = movie;
    }

    @Transient
    public Context getContext() {
        return mContext;
    }

    public void setContext(Context context) {
        mContext = context;
    }

    @Transient
    public RestfulServiceConfiguration getConfiguration() {
        return mConfiguration;
    }

    public void setConfiguration(RestfulServiceConfiguration configuration) {
        mConfiguration = configuration;
    }

    /**
     * Queries {@link mx.com.adolfogarcia.popularmovies.data.MovieProvider} for
     * the movie with the id assigned to the instance passed as argument and
     * sets its attributes to the values returned. This is performed only if the
     * {@link Movie}'s original title is {@code null}.
     *
     * @param movie
     * @see #PROJECTION_MOVIE_DETAILS
     */
    private void loadMovieData(Movie movie) {
        // Verify the data has not been loaded already
        if (movie.getId() > 0 &&  movie.getOriginalTitle() != null) {
            return; // The rest of the values may be null
        }
        if (mContext == null || mConfiguration == null) {
            throw new IllegalStateException("The context and configuration may not be null.");
        }
        Cursor cursor = mContext.getContentResolver().query(
                MovieContract.CachedMovieEntry.buildMovieUri(movie.getId())
                , PROJECTION_MOVIE_DETAILS
                , null
                , null
                , null);
        if (!cursor.moveToFirst()) {
            Log.e(LOG_TAG, "No movie was found for id: " + movie.getId());
            return;
        }
        movie.setOriginalTitle(cursor.getString(COL_ORIGINAL_TITLE));
        movie.setReleaseDate(cursor.getLong(COL_RELEASE_DATE));
        movie.setOverview(cursor.getString(COL_OVERVIEW));
        movie.setPoster(Uri.parse(mConfiguration.getBestFittingPosterUrl(
                cursor.getString(COL_POSTER_PATH), MOVIE_POSTER_WIDTH_DIP)));
        movie.setBackdrop(Uri.parse(mConfiguration.getBestFittingPosterUrl(
                cursor.getString(COL_BACKDROP_PATH), MOVIE_BACKDROP_WIDTH_DIP)));
        movie.setVoteAverage(cursor.getDouble(COL_VOTE_AVERAGE));
    }

}
