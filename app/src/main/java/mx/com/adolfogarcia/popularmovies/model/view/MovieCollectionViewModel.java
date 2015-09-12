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
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;

import org.parceler.Parcel;
import org.parceler.Transient;

import static org.parceler.Parcel.Serialization;

import de.greenrobot.event.EventBus;
import mx.com.adolfogarcia.popularmovies.data.RestfulServiceConfiguration;
import mx.com.adolfogarcia.popularmovies.model.domain.Movie;
import mx.com.adolfogarcia.popularmovies.model.event.MovieSelectionEvent;
import mx.com.adolfogarcia.popularmovies.net.FetchConfigurationTask;
import mx.com.adolfogarcia.popularmovies.net.FetchMovieTask;

import static android.os.AsyncTask.Status;

/**
 * View model for the movie collection's view. Provides data and behaviour.
 * If an item is clicked (see {@link #onItemClick(AdapterView, View, int, long)}),
 * a {@link MovieSelectionEvent} is published on the {@link EventBus}.
 * When reconstructing (deserializaing), make sure you set the transient
 * attributes: {@link #setContext(Context)} and
 * {@link #setConfiguration(RestfulServiceConfiguration)}.
 *
 * @autor Jesús Adolfo García Pasquel
 */
@Parcel(Serialization.BEAN)
public class MovieCollectionViewModel implements AdapterView.OnItemClickListener
        , AbsListView.OnScrollListener {

    /**
     * Number of movies yet to be displayed before requesting more data to
     * be downloaded.
     */
    public static final int REMAINING_ITEMS_BEFORE_LOAD = 10;

    /**
     * Identifies messages written to the log by this class.
     */
    private static final String LOG_TAG =
            MovieCollectionViewModel.class.getSimpleName();

    private Context mContext;

    private RestfulServiceConfiguration mConfiguration;

    /**
     * Current movie downloading task. A reference is kept to avoid
     * creating multiple download tasks for the same page.
     */
    private FetchMovieTask mFetchMovieTask = null;

    /**
     * Creates a new instance of {@link Movie} with the default values for
     * all its attributes.
     */
    public MovieCollectionViewModel() {
        // Empty bean constructor.
    }

    // TODO: Try to create the ViewModel with Dagger.
    public MovieCollectionViewModel(Context context
            , RestfulServiceConfiguration configuration) {
        mContext = context;
        mConfiguration = configuration;
    }

    public void updateApiConfig() {
        if (mConfiguration == null) {
            throw new IllegalStateException("The configuration may not be null.");
        }
        FetchConfigurationTask fetchConfigurationTask =
                new FetchConfigurationTask(mConfiguration);
        fetchConfigurationTask.execute();
    }

    // TODO: If no connection, notify user (before calls to this method).
    /**
     * Downloads the next page of movie data from
     * <a href="https://www.themoviedb.org/">themoviedb.org</a>, if there are
     * still pages to be downloaded. Does nothing otherwise.
     */
    public void downloadNextMoviePage() {
        if (mContext == null || mConfiguration == null) {
            throw new IllegalStateException("The context and configuration may not be null.");
        }
        if (mConfiguration.getTotalMoviePagesAvailable()
                <= mConfiguration.getLastMoviePageRetrieved()) {
            Log.i(LOG_TAG, "No more movie pages to download.");
            return;
        }
        if (mFetchMovieTask != null
                && mFetchMovieTask.getStatus() == Status.RUNNING) {
            Log.d(LOG_TAG, "Still downloading movie page. Ignoring request.");
            return;
        }
        // TODO: Get new taks using Dagger
        mFetchMovieTask = new FetchMovieTask(mContext, mConfiguration);
        mFetchMovieTask.execute(mConfiguration.getLastMoviePageRetrieved() + 1);
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
        if (totalItemCount - visibleItemCount
                <= firstVisibleItem + REMAINING_ITEMS_BEFORE_LOAD) {
            downloadNextMoviePage();
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        // Not used. Only included to implement OnScrollListener
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
}
