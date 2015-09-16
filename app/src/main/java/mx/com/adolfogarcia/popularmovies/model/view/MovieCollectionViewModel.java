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
import mx.com.adolfogarcia.popularmovies.data.RestfulServiceConfiguration;
import mx.com.adolfogarcia.popularmovies.model.domain.Movie;
import mx.com.adolfogarcia.popularmovies.model.event.MovieSelectionEvent;
import mx.com.adolfogarcia.popularmovies.net.FetchConfigurationTask;
import mx.com.adolfogarcia.popularmovies.net.FetchMoviePageTaskFactory;
import mx.com.adolfogarcia.popularmovies.view.adapter.LabeledItem;

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

    @Inject WeakReference<RestfulServiceConfiguration> mWeakConfiguration;

    @Inject LabeledItem<FetchMoviePageTaskFactory>[] mSortOrderOptions;

    private FetchMoviePageTaskFactory mSelectedFetchMoviePageTaskFactory;

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
        mFetchMoviePageTask = mSelectedFetchMoviePageTaskFactory.newFetchMovieTask();
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


    public String getSelectedSortOrder() {
        if (mSelectedFetchMoviePageTaskFactory == null) {
            // FIXME: Get last selection from settings
            mSelectedFetchMoviePageTaskFactory = mSortOrderOptions[0].getItem(); // FIXME: Wrong assignment
        }
        return mSelectedFetchMoviePageTaskFactory.getMovieSortOrder();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mSelectedFetchMoviePageTaskFactory =
                ((LabeledItem<FetchMoviePageTaskFactory>) parent.getSelectedItem()).getItem();
        // FIXME: check if selection is different to previous
        // FIXME: delete cached movies
        // FIXME: Set current page of movies to 0
        // FIXME: Retrieve next page of movies
        // FIXME: save new selection
        // FIXME: Fire event to restart loader
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Log.w(LOG_TAG, "No sort order selected, keeping last.");
    }

}
