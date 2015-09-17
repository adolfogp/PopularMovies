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

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import javax.inject.Inject;

import mx.com.adolfogarcia.popularmovies.BR;
import mx.com.adolfogarcia.popularmovies.R;
import mx.com.adolfogarcia.popularmovies.data.RestfulServiceConfiguration;
import mx.com.adolfogarcia.popularmovies.model.domain.Movie;

import static mx.com.adolfogarcia.popularmovies.data.MovieContract.CachedMovieEntry;
import static org.parceler.Parcel.Serialization;

/**
 * View model for the movie detail's view. Provides data and behaviour.
 * In order for this class to work, the {@link #mWeakContext} and
 * {@link #mWeakConfiguration} must be injected. When creating or reconstructing
 * deserializaing), make sure you inject those values.
 *
 * @author Jesús Adolfo García Pasquel
 */
@Parcel(Serialization.BEAN)
public class MovieDetailViewModel extends BaseObservable {

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
            CachedMovieEntry.COLUMN_API_ID,
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
     * Index of {@link CachedMovieEntry#COLUMN_API_ID} in
     * {@link #PROJECTION_MOVIE_DETAILS}.
     */
    public static final int COL_API_ID = 1;

    /**
     * Index of {@link CachedMovieEntry#COLUMN_ORIGINAL_TITLE} in
     * {@link #PROJECTION_MOVIE_DETAILS}.
     */
    public static final int COL_ORIGINAL_TITLE = 2;

    /**
     * Index of {@link CachedMovieEntry#COLUMN_RELEASE_DATE} in
     * {@link #PROJECTION_MOVIE_DETAILS}.
     */
    public static final int COL_RELEASE_DATE = 3;

    /**
     * Index of {@link CachedMovieEntry#COLUMN_OVERVIEW} in
     * {@link #PROJECTION_MOVIE_DETAILS}.
     */
    public static final int COL_OVERVIEW = 4;

    /**
     * Index of {@link CachedMovieEntry#COLUMN_POSTER_PATH} in
     * {@link #PROJECTION_MOVIE_DETAILS}.
     */
    public static final int COL_POSTER_PATH = 5;

    /**
     * Index of {@link CachedMovieEntry#COLUMN_BACKDROP_PATH} in
     * {@link #PROJECTION_MOVIE_DETAILS}.
     */
    public static final int COL_BACKDROP_PATH = 6;

    /**
     * Index of {@link CachedMovieEntry#COLUMN_VOTE_AVERAGE} in
     * {@link #PROJECTION_MOVIE_DETAILS}.
     */
    public static final int COL_VOTE_AVERAGE = 7;

    /**
     * Identifies the messages written to the log by this class.
     */
    private static final String LOG_TAG = MovieDetailViewModel.class.getSimpleName();

    /**
     * Reference to the {@link Context} used to access resources and convert to
     * devide dependent pixels.
     */
    @Inject WeakReference<Context> mWeakContext;

    /**
     * Reference to the {@link RestfulServiceConfiguration} used to access
     * the movie's poster and backdrop images.
     */
    @Inject WeakReference<RestfulServiceConfiguration> mWeakConfiguration;

    /**
     * The movie for which the detail data is being shown.
     */
    private Movie mMovie;

    /**
     * Creates a new instance of {@link MovieCollectionViewModel} with the
     * default values for all its attributes.
     */
    public MovieDetailViewModel() {
        // Empty bean constructor.
    }

    /**
     * Verifies that {@link #mWeakContext} is not {@code null}, nor the object
     * it references, throws {@link IllegalStateException} otherwise.
     *
     * @throws IllegalStateException if {@link #mWeakContext} or the object it
     *     references are {@code null}.
     */
    private void requireNonNullContext() {
        if (mWeakContext == null || mWeakContext.get() == null) {
            throw new IllegalStateException("The context may not be null.");
        }
    }

    /**
     * Verifies that {@link #mWeakConfiguration} is not {@code null}, nor the
     * object it references, throws {@link IllegalStateException} otherwise.
     *
     * @throws IllegalStateException if {@link #mWeakConfiguration} or the
     *     object it references are {@code null}.
     */
    private void requireNonNullConfiguration() {
        if (mWeakConfiguration == null || mWeakConfiguration.get() == null) {
            throw new IllegalStateException("The configuration may not be null.");
        }
    }


    public Movie getMovie() {
        return mMovie;
    }

    /**
     * Sets the movie for which the detail data is shown. If the
     * {@link Movie}'s original title has been set (is not {@code null}) it
     * also notifies the data binding of the change, otherwise the binding is
     * not notified.
     *
     * @param movie the movie for which the detail data should be shown.
     * @see #setMovieData(Cursor)
     */
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
     * Returns the release date for the currently set {@link Movie}, formatted
     * for display on the UI.
     *
     * @return the release date for the currently set {@link Movie}, formatted
     *     for display on the UI.
     */
    @Bindable
    public String getReleaseDate() {
        requireNonNullContext();
        SimpleDateFormat formatter = new SimpleDateFormat(mWeakContext.get()
                .getString(R.string.format_movie_release_date));
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

    /**
     * Returns the movie's user rating, formatted for display on the UI.
     *
     * @return the movie's user rating, formatted for display on the UI.
     */
    @Bindable
    public String getVoteAverage() {
        requireNonNullContext();
        DecimalFormat formatter = new DecimalFormat(mWeakContext.get()
                .getString(R.string.format_vote_average));
        return mMovie != null
                ? formatter.format(mMovie.getVoteAverage())
                : null;
    }

    /**
     * Returns the URI of the movie's poster image.
     *
     * @return the URI of the movie's poster image.
     */
    @Bindable
    public String getPosterUri() {
        if (mMovie == null) {
            return null;
        }
        return mMovie.getPosterUri() != null
                ? mMovie.getPosterUri().toString()
                : null;
    }

    /**
     * Returns the URI of the movie's backdrop image.
     *
     * @return the URI of the movie's backdrop image.
     */
    @Bindable
    public String getBackdropUri() {
        if (mMovie == null) {
            return null;
        }
        return mMovie.getBackdropUri() != null
                ? mMovie.getBackdropUri().toString()
                : null;
    }

    /**
     * Loads the movie's poster image from the specified URI into the
     * {@link ImageView}. This method is used by the Data Binding Library.
     *
     * @param view {@link ImageView} to place the image into.
     * @param posterUri where the image should be retrieved from.
     */
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
     * Loads the movie's backdrop image from the specified URI into the
     * {@link ImageView}. This method is used by the Data Binding Library.
     *
     * @param view {@link ImageView} to place the image into.
     * @param backdropUri where the backdrop image should be retrieved from.
     */
    @BindingAdapter({"bind:backdropUri"})
    public static void loadBackdropImage(ImageView view, String backdropUri) {
        Context context = view.getContext();
        int backdropPixelWidth = context.getResources().getDimensionPixelSize(
                R.dimen.movie_backdrop_width);
        int backdropPixelHeight = context.getResources().getDimensionPixelSize(
                R.dimen.movie_backdrop_height);
        Picasso.with(context)
                .load(backdropUri)
                .resize(backdropPixelWidth, backdropPixelHeight)
                .placeholder(R.anim.backdrop_loading)
                .error(R.drawable.logo_the_movie_db_360dp)
                .into(view);
    }

    /**
     * Retrieves the data from the cursor passed as argument and sets it onto the
     * {@link MovieDetailViewModel}'s current {@link Movie}. The projection used
     * must be {@link #PROJECTION_MOVIE_DETAILS}. This method also notifies the
     * data binding of the change, so the visual elements chan be updated.
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
        requireNonNullContext();
        requireNonNullConfiguration();
        RestfulServiceConfiguration configuration = mWeakConfiguration.get();
        Context context = mWeakContext.get();
        mMovie.setApiId(cursor.getLong(COL_API_ID));
        mMovie.setOriginalTitle(cursor.getString(COL_ORIGINAL_TITLE));
        mMovie.setReleaseDate(cursor.getLong(COL_RELEASE_DATE));
        mMovie.setOverview(cursor.getString(COL_OVERVIEW));
        int posterPixelWidth = context.getResources().getDimensionPixelSize(
                R.dimen.movie_poster_thumbnail_width);
        mMovie.setPosterUri(Uri.parse(configuration.getBestFittingPosterUrl(
                cursor.getString(COL_POSTER_PATH), posterPixelWidth)));
        int backdropPixelWidth = context.getResources().getDimensionPixelSize(
                R.dimen.movie_backdrop_width);
        mMovie.setBackdropUri(Uri.parse(configuration.getBestFittingPosterUrl(
                cursor.getString(COL_BACKDROP_PATH), backdropPixelWidth)));
        mMovie.setVoteAverage(cursor.getDouble(COL_VOTE_AVERAGE));

        notifyPropertyChanged(BR._all);
    }


}
