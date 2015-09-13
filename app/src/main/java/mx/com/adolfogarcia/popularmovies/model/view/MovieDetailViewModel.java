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
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.BindingAdapter;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.parceler.Parcel;
import org.parceler.Transient;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import static org.parceler.Parcel.Serialization;

import mx.com.adolfogarcia.popularmovies.BR;
import mx.com.adolfogarcia.popularmovies.R;
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
public class MovieDetailViewModel extends BaseObservable {

    /**
     * Identifies the messages written to the log by this class.
     */
    private static final String LOG_TAG = MovieDetailViewModel.class.getSimpleName();

    /**
     * The name of the UTC time zone.
     */
    public static final String UTC_TIME_ZONE = "UTC";

    /**
     * Projection that includes the movie details to be presented. Used to
     * query {@link mx.com.adolfogarcia.popularmovies.data.MovieProvider}.
     */
    public static final String[] PROJECTION_MOVIE_DETAILS = {
            CachedMovieEntry._ID,
            CachedMovieEntry.COLUMN_ORIGINAL_TITLE,
            CachedMovieEntry.COLUMN_RELEASE_DATE,
            CachedMovieEntry.COLUMN_OVERVIEW,
            CachedMovieEntry.COLUMN_POSTER_PATH,
            CachedMovieEntry.COLUMN_BACKDROP_PATH,
            CachedMovieEntry.COLUMN_VOTE_AVERAGE
    };

    /**
     * Index of {@link CachedMovieEntry#_ID} in {@link #PROJECTION_MOVIE_DETAILS}.
     */
    public static final int COL_ID = 0;

    /**
     * Index of {@link CachedMovieEntry#COLUMN_ORIGINAL_TITLE} in
     * {@link #PROJECTION_MOVIE_DETAILS}.
     */
    public static final int COL_ORIGINAL_TITLE = 1;

    /**
     * Index of {@link CachedMovieEntry#COLUMN_RELEASE_DATE} in
     * {@link #PROJECTION_MOVIE_DETAILS}.
     */
    public static final int COL_RELEASE_DATE = 2;

    /**
     * Index of {@link CachedMovieEntry#COLUMN_OVERVIEW} in
     * {@link #PROJECTION_MOVIE_DETAILS}.
     */
    public static final int COL_OVERVIEW = 3;

    /**
     * Index of {@link CachedMovieEntry#COLUMN_POSTER_PATH} in
     * {@link #PROJECTION_MOVIE_DETAILS}.
     */
    public static final int COL_POSTER_PATH = 4;

    /**
     * Index of {@link CachedMovieEntry#COLUMN_BACKDROP_PATH} in
     * {@link #PROJECTION_MOVIE_DETAILS}.
     */
    public static final int COL_BACKDROP_PATH = 5;

    /**
     * Index of {@link CachedMovieEntry#COLUMN_VOTE_AVERAGE} in
     * {@link #PROJECTION_MOVIE_DETAILS}.
     */
    public static final int COL_VOTE_AVERAGE = 6;

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
        if (movie == null) {
            throw new IllegalArgumentException("The movie may not be null.");
        }
        mMovie = movie;
        // If at least the title is set, notify.
        if (mMovie.getOriginalTitle() != null) {
            notifyPropertyChanged(BR._all);
        }
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
     * Returns the original title for the currently set {@link Movie}, possibly
     * {@code null}.
     *
     * @return the original title for the currently set {@link Movie}, possibly
     *     {@code null}.
     */
    @Bindable
    public String getOriginalTitle() {
        return mMovie != null
                ? mMovie.getOriginalTitle()
                : null;
    }

    /**
     * Returns the release date for the currently set {@link Movie}.
     *
     * @return the release date for the currently set {@link Movie}.
     */
    @Bindable
    public String getReleaseDate() {
        SimpleDateFormat formatter =
                new SimpleDateFormat(mContext.getString(R.string.format_movie_release_date));
        formatter.setTimeZone(TimeZone.getTimeZone(UTC_TIME_ZONE));
        return mMovie != null
                ? formatter.format(mMovie.getReleaseDate())
                : null;
    }

    /**
     * Returns the plot synopsis for the currently set {@link Movie}.
     *
     * @return the plot synopsis for the currently set {@link Movie}.
     */
    @Bindable
    public String getOverview() {
        return mMovie != null
                ? mMovie.getOverview()
                : null;
    }

    @Bindable
    public String getVoteAverage() {
        DecimalFormat formatter =
                new DecimalFormat(mContext.getString(R.string.format_vote_average));
        return mMovie != null
                ? formatter.format(mMovie.getVoteAverage())
                : null;
    }

    @Bindable
    public String getPosterUri() {
        if (mMovie == null) {
            return null;
        }
        return mMovie.getPosterUri() != null
                ? mMovie.getPosterUri().toString()
                : null;
    }

    @BindingAdapter({"bind:posterUri"})
    public static void loadPosterImage(ImageView view, String posterUri) {
        Context context = view.getContext();
        int posterPixelWidth = context.getResources().getDimensionPixelSize(
                R.dimen.movie_poster_thumbnail_width);
        int posterPixelHeight = context.getResources().getDimensionPixelSize(
                R.dimen.movie_poster_thumbnail_height);
        Picasso.with(context)
                .load(posterUri)
                .resize(posterPixelWidth, posterPixelHeight)
                .placeholder(R.anim.poster_loading)
                .error(R.drawable.logo_the_movie_db_180dp)
                .into(view);
    }

    /**
     * Retrieves the data from the cursor passed as argument and sets it onto the
     * {@link MovieDetailViewModel}'s current {@link Movie}. The projection used
     * must be {@link #PROJECTION_MOVIE_DETAILS}.
     *
     * @param cursor the {@link Cursor} containing the data  to load.
     * @throws IllegalStateException if there is no {@link Movie} currently set
     *     in the {@link MovieDetailViewModel}.
     * @throws IllegalArgumentException if the data passed does not belong to
     *     the {@link Movie} currently set (i.e. does not have the same id).
     */
    public void setMovieData(Cursor cursor) {
        if (mMovie == null) {
            throw new IllegalStateException("No movie currently set in MovieDetailViewModel.");
        }
        if (cursor == null) {
            return;
        }
        if (!cursor.moveToFirst()) {
            Log.w(LOG_TAG, "The cursor contains no data. Ignoring.");
            return;
        }
        if (mMovie.getId() != cursor.getLong(COL_ID)) {
            throw new IllegalArgumentException(
                    "The data passed does not belong to movie " + mMovie.getId());
        }
        mMovie.setOriginalTitle(cursor.getString(COL_ORIGINAL_TITLE));
        mMovie.setReleaseDate(cursor.getLong(COL_RELEASE_DATE));
        mMovie.setOverview(cursor.getString(COL_OVERVIEW));
        int posterPixelWidth = mContext.getResources().getDimensionPixelSize(
                R.dimen.movie_poster_thumbnail_width);
        mMovie.setPosterUri(Uri.parse(mConfiguration.getBestFittingPosterUrl(
                cursor.getString(COL_POSTER_PATH), posterPixelWidth)));
        int backdropPixelWidth = mContext.getResources().getDimensionPixelSize(
                R.dimen.movie_backdrop_width);
        mMovie.setBackdropUri(Uri.parse(mConfiguration.getBestFittingPosterUrl(
                cursor.getString(COL_BACKDROP_PATH), backdropPixelWidth)));
        mMovie.setVoteAverage(cursor.getDouble(COL_VOTE_AVERAGE));

        notifyPropertyChanged(BR._all);
    }


}
