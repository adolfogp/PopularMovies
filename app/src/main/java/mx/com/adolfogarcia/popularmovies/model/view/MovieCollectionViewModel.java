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
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;

import org.parceler.Parcel;
import org.parceler.Transient;

import java.lang.ref.WeakReference;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import mx.com.adolfogarcia.popularmovies.data.RestfulServiceConfiguration;
import mx.com.adolfogarcia.popularmovies.model.domain.Movie;
import mx.com.adolfogarcia.popularmovies.model.event.MovieSelectionEvent;
import mx.com.adolfogarcia.popularmovies.model.event.SortOrderSelectionEvent;
import mx.com.adolfogarcia.popularmovies.net.FetchConfigurationTask;
import mx.com.adolfogarcia.popularmovies.net.FetchMoviePageTaskFactory;
import mx.com.adolfogarcia.popularmovies.view.adapter.LabeledItem;

import static android.os.AsyncTask.Status;
import static mx.com.adolfogarcia.popularmovies.data.MovieContract.CachedMovieEntry;
import static org.parceler.Parcel.Serialization;

/**
 * View model for the movie collection's view. Provides data and behaviour.
 * If an item is clicked (see {@link #onItemClick(AdapterView, View, int, long)}),
 * a {@link MovieSelectionEvent} is published on the {@link EventBus}.
 * In order for this class to work, the {@link #mWeakContext},
 * {@link #mWeakConfiguration} and {@link #mSortOrderOptions} must be injected.
 * When creating or reconstructing (deserializaing), make sure you inject
 * those values.
 *
 * @author Jesús Adolfo García Pasquel
 */
@Parcel(Serialization.BEAN)
public class MovieCollectionViewModel implements AdapterView.OnItemClickListener
        , AbsListView.OnScrollListener, AdapterView.OnItemSelectedListener {

    /**
     * Identifies messages written to the log by this class.
     */
    private static final String LOG_TAG =
            MovieCollectionViewModel.class.getSimpleName();

    /**
     * Number of milliseconds in 24 hours.
     * @see #isApiConfigOld()
     */
    private static final long ONE_DAY_MILLISECONDS = 86400000L;

    /**
     * Limit of unseen data that triggers the download of a new page of movies.
     * The threshold is given in screens of unseen data. If less than
     * {@link #DOWNLOAD_THRESHOLD} screens full of movies remain unseen, more
     * data should be downloaded.
     */
    private static final int DOWNLOAD_THRESHOLD = 2;

    /**
     * Reference to the {@link Context} used to access the
     * {@link mx.com.adolfogarcia.popularmovies.data.MovieProvider}.
     */
    @Inject WeakReference<Context> mWeakContext;

    /**
     * The RESTFul API's configuration.
     */
    @Inject WeakReference<RestfulServiceConfiguration> mWeakConfiguration;

    /**
     * The movie sort order criteria the user may choose from.
     */
    @Inject LabeledItem<FetchMoviePageTaskFactory>[] mSortOrderOptions;


    /**
     * The position of the currently selected movie, possibly
     * {@link AdapterView#INVALID_POSITION}.
     */
    private int mSelectedPosition = AdapterView.INVALID_POSITION;

    /**
     * Current movie downloading task. A reference is kept to avoid
     * creating multiple download tasks for the same page.
     */
    private AsyncTask<Integer, ?, ?> mFetchMoviePageTask = null;

    /**
     * Creates a new instance of {@link MovieCollectionViewModel} with the
     * default values for all its attributes.
     */
    public MovieCollectionViewModel() {
        // Empty bean constructor.
    }

    public int getSelectedPosition() {
        return mSelectedPosition;
    }

    public void setSelectedPosition(int selectedPosition) {
        mSelectedPosition = selectedPosition;
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

    /**
     * Returns {@code true} if the RESTful API configuration information was
     * cached 24 hours ago or more, {@code false} otherwise.
     *
     * @return {@code true} if the RESTful API configuration information was
     *     cached 24 hours ago or more, {@code false} otherwise.
     * @see #updateApiConfig()
     */
    public boolean isApiConfigOld() {
        requireNonNullConfiguration();
        return mWeakConfiguration.get().getLastUpdateTime()
                < System.currentTimeMillis() - ONE_DAY_MILLISECONDS;
    }

    /**
     * Downloads the <a href="https://www.themoviedb.org/">themoviedb.org</a>'s
     * API configuration and caches it.
     */
    public void updateApiConfig() {
        requireNonNullConfiguration();
        FetchConfigurationTask fetchConfigurationTask =
                new FetchConfigurationTask(mWeakConfiguration.get());
        fetchConfigurationTask.execute();
    }

    /**
     * Deletes all cached movie data, resets the last page of movies
     * downloaded back to zero and clears the selected item position
     * (sets it to {@link AdapterView#INVALID_POSITION}).
     */
    public void deleteCachedMovieData() {
        // TODO - Keep user favorite movies, only remove the flags for most_popular
        //        most_popular or highest_rated, and set them back later, if
        //        found among the pages for most popular or highest rated.
        requireNonNullConfiguration();
        requireNonNullContext();
        mWeakContext.get().getContentResolver()
                .delete(CachedMovieEntry.CONTENT_URI, null, null);
        mWeakConfiguration.get().clearLastMoviePageRetrieved();
        mSelectedPosition = AdapterView.INVALID_POSITION;
    }

    /**
     * Returns the {@link FetchMoviePageTaskFactory} for the currently selected
     * sort order option.
     *
     * @return the {@link FetchMoviePageTaskFactory} for the currently selected
     *     sort order option.
     * @see #getSortOrderOptions()
     */
    public FetchMoviePageTaskFactory getSelectedSortOrderTaskFactory() {
        int idx = getSelectedSortOrderIndex();
        return mSortOrderOptions[idx].getItem();
    }

    /**
     * Returns the index of the currently selected sort order option.
     *
     * @return the index of the currently selected sort order option.
     * @see #getSortOrderOptions()
     */
    public int getSelectedSortOrderIndex() {
        requireNonNullConfiguration();
        return mWeakConfiguration.get().getSelectedSortOrderIndex();
    }

    /**
     * Sets the index of the currently selected sort order option. If different
     * that the currently selected index, the cached movie data is discarded and
     * a new page of movie data is downloaded.
     *
     * @param idx the value to set as the currently selected sort order option.
     */
    public void setSelectedSortOrderIndex(int idx) {
        if (idx < 0 || idx >= mSortOrderOptions.length) {
            throw new IndexOutOfBoundsException();
        }
        requireNonNullConfiguration();
        requireNonNullContext();
        RestfulServiceConfiguration configuration = mWeakConfiguration.get();
        Context context = mWeakContext.get();
        if (configuration.getSelectedSortOrderIndex() == idx) {
            Log.d(LOG_TAG, "Ignoring sort order change.");
            return;
        }
        mSelectedPosition = AdapterView.INVALID_POSITION;
        mFetchMoviePageTask = null;
        configuration.setSelectedSortOrderIndex(idx);
        downloadNextMoviePage();
        EventBus.getDefault().post(new SortOrderSelectionEvent());
    }

    // TODO: If no connection, notify user (before calls to this method).
    /**
     * Downloads the next page of movie data from
     * <a href="https://www.themoviedb.org/">themoviedb.org</a>, if there are
     * still pages to be downloaded. Does nothing otherwise.
     */
    public void downloadNextMoviePage() {
        requireNonNullConfiguration();
        RestfulServiceConfiguration configuration = mWeakConfiguration.get();
        if (mFetchMoviePageTask != null
                && mFetchMoviePageTask.getStatus() == Status.RUNNING) {
            Log.d(LOG_TAG, "Still downloading movie page. Ignoring request.");
            return;
        }
        FetchMoviePageTaskFactory taskFactory = getSelectedSortOrderTaskFactory();
        final int lastPageRetrieved =
                configuration.getLastMoviePageRetrieved(taskFactory.getRestApiSortOrder());
        if (configuration.getTotalMoviePagesAvailable() <= lastPageRetrieved) {
            Log.i(LOG_TAG, "No more movie pages to download.");
            return;
        }
        mFetchMoviePageTask = getSelectedSortOrderTaskFactory().newFetchMovieTask();
        mFetchMoviePageTask.execute(lastPageRetrieved + 1);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mSelectedPosition = position;
        Movie selectedMovie = new Movie();
        selectedMovie.setId(id);
        EventBus.getDefault().post(new MovieSelectionEvent(selectedMovie));
    }

    @Override
    public void onScroll(AbsListView view
            , int firstVisibleItem
            , int visibleItemCount
            , int totalItemCount) {
        // Download new items if: unseen < DOWNLOAD_THRESHOLD
        boolean reachedNewDataDownloadThreshold =
                totalItemCount - visibleItemCount - firstVisibleItem
                        < DOWNLOAD_THRESHOLD * visibleItemCount;
        if (totalItemCount > 0 && reachedNewDataDownloadThreshold) {
            downloadNextMoviePage();
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        // Not used. Only included to implement OnScrollListener
    }

    @Transient
    public LabeledItem<FetchMoviePageTaskFactory>[] getSortOrderOptions() {
        return mSortOrderOptions;
    }

    /**
     * Returns the selection clause (<i>WHERE</i> clause) to be used on
     * {@link mx.com.adolfogarcia.popularmovies.data.MovieProvider}, that
     * corresponds to the currently selected item from
     * {@link #getSortOrderOptions()}.
     *
     * @return a selection clause to be used on
     *     {@link mx.com.adolfogarcia.popularmovies.data.MovieProvider}.
     */
    public String getSelectionClause() {
        return getSelectedSortOrderTaskFactory().getMovieProviderSelectionClause();
    }

    /**
     * Returns the arguments to be used on the selection clause to be used on
     * {@link mx.com.adolfogarcia.popularmovies.data.MovieProvider}, that
     * corresponds to the currently selected item from
     * {@link #getSortOrderOptions()}.
     *
     * @return an order clause to be used on
     *     {@link mx.com.adolfogarcia.popularmovies.data.MovieProvider}.
     */
    public String[] getSelectionArguments() {
        return getSelectedSortOrderTaskFactory().getMovieProviderSelectionArguments();
    }

    /**
     * Returns an order clause to be used on
     * {@link mx.com.adolfogarcia.popularmovies.data.MovieProvider}, that
     * corresponds to the currently selected item from
     * {@link #getSortOrderOptions()}.
     *
     * @return an order clause to be used on
     *     {@link mx.com.adolfogarcia.popularmovies.data.MovieProvider}.
     */
    public String getSortOrderClause() {
        return getSelectedSortOrderTaskFactory().getMovieProviderSortOrder();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        setSelectedSortOrderIndex(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Log.w(LOG_TAG, "No sort order selected, keeping last.");
    }

}
