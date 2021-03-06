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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.BooleanUtils;
import org.parceler.Parcel;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;

import javax.inject.Inject;

import mx.com.adolfogarcia.popularmovies.BR;
import mx.com.adolfogarcia.popularmovies.R;
import mx.com.adolfogarcia.popularmovies.data.RestfulServiceConfiguration;
import mx.com.adolfogarcia.popularmovies.databinding.MovieReviewListItemBinding;
import mx.com.adolfogarcia.popularmovies.databinding.MovieTrailerListItemBinding;
import mx.com.adolfogarcia.popularmovies.model.domain.Movie;
import mx.com.adolfogarcia.popularmovies.model.domain.Review;
import mx.com.adolfogarcia.popularmovies.model.domain.Trailer;

import static mx.com.adolfogarcia.popularmovies.data.MovieContract.CachedMovieEntry;
import static mx.com.adolfogarcia.popularmovies.data.MovieContract.CachedMovieVideoEntry;
import static mx.com.adolfogarcia.popularmovies.data.MovieContract.CachedMovieReviewEntry;
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
     * The base URL where the movie trailers are found on
     * <a href="https://www.youtube.com">YouTube</a>.
     */
    public static final String YOUTUBE_BASE_URI = "https://www.youtube.com/watch";

    /**
     * Parameter used to define the key of the video to watch on
     * {@link #YOUTUBE_BASE_URI}.
     */
    public static final String YOUTUBE_VIDEO_QUERY_PARAM = "v";

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
     * Returns {@code true} if  the movie is one of the user's favorites,
     * {@code false} otherwise.
     *
     * @return {@code true} if  the movie is one of the user's favorites,
     *     {@code false} otherwise.
     */
    @Bindable
    public boolean isFavorite() {
        return mMovie.isUserFavorite();
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
     * Returns the {@link Trailer}s available for the movie.
     *
     * @return the {@link Trailer}s available for the movie.
     */
    @Bindable
    public List<Trailer> getTrailers() {
        if (mMovie == null) {
            return Collections.emptyList();
        }
        return mMovie.getTrailers() != null
                ? mMovie.getTrailers()
                : Collections.emptyList();
    }

    /**
     * Returns the {@link Review}s available for the movie.
     *
     * @return the {@link Review}s available for the movie.
     */
    @Bindable
    public List<Review> getReviews() {
        if (mMovie == null) {
            return Collections.emptyList();
        }
        return mMovie.getReviews() != null
                ? mMovie.getReviews()
                : Collections.emptyList();
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
     * Removes all views from the {@link LinearLayout} and adds new ones for
     * the specified {@link Trailer}s.
     *
     * @param container {@link LinearLayout} that will contain the trailers.
     * @param trailers the {@link Trailer}s to be placed in the container.
     */
    @BindingAdapter({"bind:trailers"})
    public static void loadTrailerViews(LinearLayout container, List<Trailer> trailers) {
        container.removeAllViews();
        if (trailers == null || trailers.isEmpty()) {
            return;
        }
        LayoutInflater inflater = (LayoutInflater) container.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for (Trailer trailer : trailers) {
            MovieTrailerListItemBinding binding = DataBindingUtil.inflate(inflater
                    , R.layout.list_item_movie_trailer
                    , container
                    , false);
            MovieTrailerListItemViewModel itemViewModel = new MovieTrailerListItemViewModel();
            itemViewModel.setTrailer(trailer);
            binding.setViewModel(itemViewModel);
            container.addView(binding.getRoot());
        }
    }

    /**
     * Removes all views from the {@link LinearLayout} and adds new ones for
     * the specified {@link Review}s.
     *
     * @param container {@link LinearLayout} that will contain the reviews.
     * @param reviews the {@link Review}s to be placed in the container.
     */
    @BindingAdapter({"bind:reviews"})
    public static void loadReviewViews(LinearLayout container, List<Review> reviews) {
        container.removeAllViews();
        if (reviews == null || reviews.isEmpty()) {
            return;
        }
        LayoutInflater inflater = (LayoutInflater) container.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for (Review review : reviews) {
            MovieReviewListItemBinding binding = DataBindingUtil.inflate(inflater
                    , R.layout.list_item_movie_review
                    , container
                    , false);
            MovieReviewListItemViewModel itemViewModel = new MovieReviewListItemViewModel();
            itemViewModel.setReview(review);
            binding.setViewModel(itemViewModel);
            container.addView(binding.getRoot());
        }
    }

    /**
     * Retrieves the data from the cursor passed as argument and sets it onto the
     * {@link MovieDetailViewModel}'s current {@link Movie}. The projection used
     * must be {@link MovieDetailQuery#PROJECTION}. This method
     * also notifies the data binding of the change, so the visual elements can
     * be updated.
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
            Log.w(LOG_TAG, "The cursor contains no data. Ignoring movie details.");
            return;
        }
        if (mMovie.getId() != cursor.getLong(MovieDetailQuery.COL_ID)) {
            throw new IllegalArgumentException(
                    "The data passed does not belong to the movie");
        }
        requireNonNullContext();
        requireNonNullConfiguration();
        RestfulServiceConfiguration configuration = mWeakConfiguration.get();
        Context context = mWeakContext.get();
        mMovie.setApiId(cursor.getLong(MovieDetailQuery.COL_API_ID));
        mMovie.setOriginalTitle(cursor.getString(MovieDetailQuery.COL_ORIGINAL_TITLE));
        mMovie.setReleaseDate(cursor.getLong(MovieDetailQuery.COL_RELEASE_DATE));
        mMovie.setOverview(cursor.getString(MovieDetailQuery.COL_OVERVIEW));
        int posterPixelWidth = context.getResources().getDimensionPixelSize(
                R.dimen.movie_poster_thumbnail_width);
        mMovie.setPosterUri(Uri.parse(configuration.getBestFittingPosterUrl(
                cursor.getString(MovieDetailQuery.COL_POSTER_PATH), posterPixelWidth)));
        int backdropPixelWidth = context.getResources().getDimensionPixelSize(
                R.dimen.movie_backdrop_width);
        mMovie.setBackdropUri(Uri.parse(configuration.getBestFittingPosterUrl(
                cursor.getString(MovieDetailQuery.COL_BACKDROP_PATH), backdropPixelWidth)));
        mMovie.setVoteAverage(cursor.getDouble(MovieDetailQuery.COL_VOTE_AVERAGE));
        mMovie.setUserFavorite(BooleanUtils.toBoolean(
                cursor.getInt(MovieDetailQuery.COL_USER_FAVORITE)));

        notifyPropertyChanged(BR._all);
    }

    /**
     * Retrieves the data from the cursor passed as argument and sets it onto the
     * {@link MovieDetailViewModel}'s current {@link Movie}. The projection used
     * must be {@link MovieTrailerQuery#PROJECTION}. This method
     * also notifies the data binding of the change, so the visual elements can
     * be updated.
     *
     * @param cursor the {@link Cursor} containing the data  to load.
     * @throws IllegalStateException if there is no {@link Movie} currently set
     *     in the {@link MovieDetailViewModel}.
     * @throws IllegalArgumentException if the data passed does not belong to
     *     the {@link Movie} currently set (i.e. does not have the same id in
     *     the RESTful API).
     */
    public void setMovieTrailerData(Cursor cursor) {
        if (mMovie == null) {
            throw new IllegalStateException("No movie currently set in MovieDetailViewModel.");
        }
        if (cursor == null || !cursor.moveToFirst()) {
            Log.d(LOG_TAG, "The cursor contains no trailers.");
            mMovie.setTrailers(Collections.emptyList());
            notifyPropertyChanged(BR.trailers);
            return;
        }
        List<Trailer> trailers = new ArrayList<>();
        do {
            trailers.add(newTrailer(cursor));
        } while (cursor.moveToNext());
        mMovie.setTrailers(trailers);
        notifyPropertyChanged(BR.trailers);
    }

    /**
     * Returns a new instance of {@link Trailer} with the data of the touple
     * currently pointed at by the {@link Cursor} passed as argument. The
     * data of the cursor is expected to appear as in {@link MovieTrailerQuery}.
     *
     * @param cursor the {@link Cursor} from which the data of the
     *     {@link Trailer} will be retrieved.
     * @return  a new instance of {@link Trailer} with the data of the touple
     *     currently pointed at by the {@link Cursor} passed as argument.
     */
    private Trailer newTrailer(Cursor cursor) {
        if (cursor == null) {
            return null;
        }
        Trailer trailer = new Trailer();
        trailer.setId(cursor.getLong(MovieTrailerQuery.COL_ID));
        trailer.setApiId(cursor.getString(MovieTrailerQuery.COL_API_ID));
        trailer.setName(cursor.getString(MovieTrailerQuery.COL_NAME));
        Uri videoUri = Uri.parse(YOUTUBE_BASE_URI).buildUpon()
                .appendQueryParameter(YOUTUBE_VIDEO_QUERY_PARAM
                        , cursor.getString(MovieTrailerQuery.COL_KEY))
                .build();
        trailer.setVideoUri(videoUri);
        return trailer;
    }

    /**
     * Retrieves the data from the cursor passed as argument and sets it onto the
     * {@link MovieDetailViewModel}'s current {@link Movie}. The projection used
     * must be {@link MovieTrailerQuery#PROJECTION}. This method
     * also notifies the data binding of the change, so the visual elements can
     * be updated.
     *
     * @param cursor the {@link Cursor} containing the data  to load.
     * @throws IllegalStateException if there is no {@link Movie} currently set
     *     in the {@link MovieDetailViewModel}.
     * @throws IllegalArgumentException if the data passed does not belong to
     *     the {@link Movie} currently set (i.e. does not have the same id in
     *     the RESTful API).
     */
    public void setMovieReviewData(Cursor cursor) {
        if (mMovie == null) {
            throw new IllegalStateException("No movie currently set in MovieDetailViewModel.");
        }
        if (cursor == null || !cursor.moveToFirst()) {
            Log.d(LOG_TAG, "The cursor contains no reviews.");
            mMovie.setReviews(Collections.emptyList());
            notifyPropertyChanged(BR.reviews);
            return;
        }
        List<Review> reviews = new ArrayList<>();
        do {
            reviews.add(newReview(cursor));
        } while (cursor.moveToNext());
        mMovie.setReviews(reviews);
        notifyPropertyChanged(BR.reviews);
    }

    /**
     * Returns a new instance of {@link Review} with the data of the touple
     * currently pointed at by the {@link Cursor} passed as argument. The
     * data of the cursor is expected to appear as in {@link MovieTrailerQuery}.
     *
     * @param cursor the {@link Cursor} from which the data of the
     *     {@link Trailer} will be retrieved.
     * @return  a new instance of {@link Trailer} with the data of the touple
     *     currently pointed at by the {@link Cursor} passed as argument.
     */
    private Review newReview(Cursor cursor) {
        if (cursor == null) {
            return null;
        }
        Review review = new Review();
        review.setId(cursor.getLong(MovieReviewQuery.COL_ID));
        review.setApiId(cursor.getString(MovieReviewQuery.COL_API_ID));
        review.setAuthor(cursor.getString(MovieReviewQuery.COL_AUTHOR));
        review.setContent(cursor.getString(MovieReviewQuery.COL_CONTENT));
        review.setSourceUri(Uri.parse(cursor.getString(MovieReviewQuery.COL_URL)));
        return review;
    }

    /**
     * Sets or removes the movie from the user's favorites, depending on
     * the value of {@link CheckBox#isChecked()}. This change is performed on
     * the {@code ContentProvider}.
     *
     * @param checkBox the {@link CheckBox} that was clicked.
     */
    public void onClickFavorite(CheckBox checkBox) {
        final boolean checked = checkBox.isChecked();
        if (checked == mMovie.isUserFavorite()) {
            Log.d(LOG_TAG, "Ignoring favorite change request. No change submitted.");
            return;
        }
        Context context = checkBox.getContext();
        ContentValues favoriteMovieValues = new ContentValues();
        favoriteMovieValues.put(CachedMovieEntry.COLUMN_USER_FAVORITE
                , BooleanUtils.toInteger(checked));
        int count = context.getContentResolver().update(
                CachedMovieEntry.CONTENT_URI
                , favoriteMovieValues
                , CachedMovieEntry._ID + "= ?"
                , new String[]{Long.toString(mMovie.getId())});
        if (count != 1) {
            Log.e(LOG_TAG, "Expected 1 movie to change favorite status, but got " + count);
        }
        mMovie.setUserFavorite(checked);
        notifyPropertyChanged(BR.favorite);
    }

    /**
     * Provides information about projection and column indices expected by
     * {@link MovieCollectionViewModel} when setting movie details from a
     * {@link Cursor}.
     */
    public static final class MovieDetailQuery {

        /**
         * Projection that includes the movie details to be presented. Used to
         * query {@link mx.com.adolfogarcia.popularmovies.data.MovieProvider}.
         */
        public static final String[] PROJECTION = {
                CachedMovieEntry._ID,
                CachedMovieEntry.COLUMN_API_ID,
                CachedMovieEntry.COLUMN_ORIGINAL_TITLE,
                CachedMovieEntry.COLUMN_RELEASE_DATE,
                CachedMovieEntry.COLUMN_OVERVIEW,
                CachedMovieEntry.COLUMN_POSTER_PATH,
                CachedMovieEntry.COLUMN_BACKDROP_PATH,
                CachedMovieEntry.COLUMN_VOTE_AVERAGE,
                CachedMovieEntry.COLUMN_USER_FAVORITE
        };

        /**
         * Index of {@link CachedMovieEntry#_ID} in {@link #PROJECTION}.
         */
        public static final int COL_ID = 0;

        /**
         * Index of {@link CachedMovieEntry#COLUMN_API_ID} in
         * {@link #PROJECTION}.
         */
        public static final int COL_API_ID = 1;

        /**
         * Index of {@link CachedMovieEntry#COLUMN_ORIGINAL_TITLE} in
         * {@link #PROJECTION}.
         */
        public static final int COL_ORIGINAL_TITLE = 2;

        /**
         * Index of {@link CachedMovieEntry#COLUMN_RELEASE_DATE} in
         * {@link #PROJECTION}.
         */
        public static final int COL_RELEASE_DATE = 3;

        /**
         * Index of {@link CachedMovieEntry#COLUMN_OVERVIEW} in
         * {@link #PROJECTION}.
         */
        public static final int COL_OVERVIEW = 4;

        /**
         * Index of {@link CachedMovieEntry#COLUMN_POSTER_PATH} in
         * {@link #PROJECTION}.
         */
        public static final int COL_POSTER_PATH = 5;

        /**
         * Index of {@link CachedMovieEntry#COLUMN_BACKDROP_PATH} in
         * {@link #PROJECTION}.
         */
        public static final int COL_BACKDROP_PATH = 6;

        /**
         * Index of {@link CachedMovieEntry#COLUMN_VOTE_AVERAGE} in
         * {@link #PROJECTION}.
         */
        public static final int COL_VOTE_AVERAGE = 7;

        /**
         * Index of {@link CachedMovieEntry#COLUMN_VOTE_AVERAGE} in
         * {@link #PROJECTION}.
         */
        public static final int COL_USER_FAVORITE = 8;

        /**
         * The class only provides constants and utility methods.
         */
        private MovieDetailQuery() {
            // Empty constructor
        }
    }


    /**
     * Provides information about projection and column indices expected by
     * {@link MovieCollectionViewModel} when setting movie trailers from a
     * {@link Cursor}.
     */
    public static final class MovieTrailerQuery {

        /**
         * Projection that includes the details of the movie trailer videos to be presented.
         * Used to query {@link mx.com.adolfogarcia.popularmovies.data.MovieProvider}.
         */
        public static final String[] PROJECTION = {
                CachedMovieVideoEntry.TABLE_NAME + "." + CachedMovieVideoEntry._ID,
                CachedMovieVideoEntry.TABLE_NAME + "." + CachedMovieVideoEntry.COLUMN_API_ID,
                CachedMovieVideoEntry.COLUMN_NAME,
                CachedMovieVideoEntry.COLUMN_KEY
        };

        /**
         * Sort order to be used in the query. Considers the local id of
         * the videos, in ascending order.
         */
        public static final String SORT_ORDER =
                CachedMovieVideoEntry.TABLE_NAME + "." + CachedMovieVideoEntry._ID + " ASC";

        /**
         * Index of {@link CachedMovieVideoEntry#_ID} in {@link #PROJECTION}.
         */
        public static final int COL_ID = 0;

        /**
         * Index of {@link CachedMovieVideoEntry#COLUMN_API_ID} in
         * {@link #PROJECTION}.
         */
        public static final int COL_API_ID = 1;

        /**
         * Index of {@link CachedMovieVideoEntry#COLUMN_NAME} in
         * {@link #PROJECTION}.
         */
        public static final int COL_NAME = 2;

        /**
         * Index of {@link CachedMovieVideoEntry#COLUMN_KEY} in
         * {@link #PROJECTION}.
         */
        public static final int COL_KEY = 3;

        /**
         * The class only provides constants and utility methods.
         */
        private MovieTrailerQuery() {
            // Empty constructor
        }

    }

    /**
     * Provides information about projection and column indices expected by
     * {@link MovieCollectionViewModel} when setting movie reviews from a
     * {@link Cursor}.
     */
    public static final class MovieReviewQuery {

        /**
         * Projection that includes the details of the movie review to be presented.
         * Used to query {@link mx.com.adolfogarcia.popularmovies.data.MovieProvider}.
         */
        public static final String[] PROJECTION = {
                CachedMovieReviewEntry.TABLE_NAME + "." + CachedMovieReviewEntry._ID,
                CachedMovieReviewEntry.TABLE_NAME + "." + CachedMovieReviewEntry.COLUMN_API_ID,
                CachedMovieReviewEntry.COLUMN_AUTHOR,
                CachedMovieReviewEntry.COLUMN_CONTENT,
                CachedMovieReviewEntry.COLUMN_URL
        };

        /**
         * Sort order to be used in the query. Considers the local id of
         * the reviews, in ascending order.
         */
        public static final String SORT_ORDER =
                CachedMovieReviewEntry.TABLE_NAME + "." + CachedMovieReviewEntry._ID + " ASC";

        /**
         * Index of {@link CachedMovieReviewEntry#_ID} in {@link #PROJECTION}.
         */
        public static final int COL_ID = 0;

        /**
         * Index of {@link CachedMovieReviewEntry#COLUMN_API_ID} in
         * {@link #PROJECTION}.
         */
        public static final int COL_API_ID = 1;

        /**
         * Index of {@link CachedMovieReviewEntry#COLUMN_AUTHOR} in
         * {@link #PROJECTION}.
         */
        public static final int COL_AUTHOR = 2;

        /**
         * Index of {@link CachedMovieReviewEntry#COLUMN_CONTENT} in
         * {@link #PROJECTION}.
         */
        public static final int COL_CONTENT = 3;

        /**
         * Index of {@link CachedMovieReviewEntry#COLUMN_URL} in
         * {@link #PROJECTION}.
         */
        public static final int COL_URL = 4;

        /**
         * The class only provides constants and utility methods.
         */
        private MovieReviewQuery() {
            // Empty constructor
        }

    }

}
