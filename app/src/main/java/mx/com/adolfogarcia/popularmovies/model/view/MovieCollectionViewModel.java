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

import static org.parceler.Parcel.Serialization;

import de.greenrobot.event.EventBus;
import mx.com.adolfogarcia.popularmovies.PopularMoviesApplication;
import mx.com.adolfogarcia.popularmovies.data.MovieContract;
import mx.com.adolfogarcia.popularmovies.data.RestfulServiceConfiguration;
import mx.com.adolfogarcia.popularmovies.model.domain.Movie;
import mx.com.adolfogarcia.popularmovies.model.event.MovieSelectionEvent;
import mx.com.adolfogarcia.popularmovies.model.event.SortOrderSelectionEvent;
import mx.com.adolfogarcia.popularmovies.net.FetchConfigurationTask;
import mx.com.adolfogarcia.popularmovies.net.FetchMoviePageTaskFactory;
import mx.com.adolfogarcia.popularmovies.view.adapter.LabeledItem;
import static mx.com.adolfogarcia.popularmovies.data.MovieContract.CachedMovieEntry;

import static android.os.AsyncTask.Status;

/**
 * View model for the movie collection's view. Provides data and behaviour.
 * If an item is clicked (see {@link #onItemClick(AdapterView, View, int, long)}),
 * a {@link MovieSelectionEvent} is published on the {@link EventBus}.
 * In order for this class to work, the {@link #mWeakContext},
 * {@link #mWeakConfiguration} and {@link #mSortOrderOptions} must be injected.
 * When creating or reconstructing (deserializaing), make sure you inject
 * those values.
 *
 * @autor Jesús Adolfo García Pasquel
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
     * Limit of unseen data that triggers the download of a new page of movies.
     * The threshold is given in screens of unseen data. If less than
     * {@link #DOWNLOAD_THRESHOLD} screens full of movies remain unseen, more
     * data should be downloaded.
     */
    private static final int DOWNLOAD_THRESHOLD = 2;


    @Inject WeakReference<Context> mWeakContext; // TODO - Remove if not needed

    /**
     * The RESTFul API's configuration.
     */
    @Inject WeakReference<RestfulServiceConfiguration> mWeakConfiguration;

    /**
     * The movie sort order criteria the user may choose from.
     */
    @Inject LabeledItem<FetchMoviePageTaskFactory>[] mSortOrderOptions;

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

    public void updateApiConfig() {
        requireNonNullConfiguration();
        FetchConfigurationTask fetchConfigurationTask =
                new FetchConfigurationTask(mWeakConfiguration.get());
        fetchConfigurationTask.execute();
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
     * Sets the index of the currently selected sort order option.
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
        Log.v(LOG_TAG, "Selected sort order index: " + idx); // TODO: Delete this line
        if (configuration.getSelectedSortOrderIndex() == idx) {
            Log.d(LOG_TAG, "Ignoring sort order change.");
            return;
        }
        configuration.setSelectedSortOrderIndex(idx);
        configuration.setLastMoviePageRetrieved(0);
        // Delete all cached movies
        context.getContentResolver().delete(CachedMovieEntry.CONTENT_URI, null, null);
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
        if (configuration.getTotalMoviePagesAvailable()
                <= configuration.getLastMoviePageRetrieved()) {
            Log.i(LOG_TAG, "No more movie pages to download.");
            return;
        }
        if (mFetchMoviePageTask != null
                && mFetchMoviePageTask.getStatus() == Status.RUNNING) {
            Log.d(LOG_TAG, "Still downloading movie page. Ignoring request.");
            return;
        }
        mFetchMoviePageTask = getSelectedSortOrderTaskFactory().newFetchMovieTask();
        mFetchMoviePageTask.execute(configuration.getLastMoviePageRetrieved() + 1);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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
     * Returns an order clause to be used on
     * {@link mx.com.adolfogarcia.popularmovies.data.MovieProvider}, that
     * corresponds to the currently selected item from
     * {@link #getSortOrderOptions()}.
     *
     * @return an order clause to be used on
     *     {@link mx.com.adolfogarcia.popularmovies.data.MovieProvider}.
     */
    public String getSelectedSortOrderClause() {
        return getSelectedSortOrderTaskFactory().getMovieSortOrder();
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
