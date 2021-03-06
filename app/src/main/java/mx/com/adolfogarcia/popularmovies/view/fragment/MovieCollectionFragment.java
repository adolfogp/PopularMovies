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

package mx.com.adolfogarcia.popularmovies.view.fragment;

import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.parceler.Parcels;

import java.lang.ref.WeakReference;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import mx.com.adolfogarcia.popularmovies.PopularMoviesApplication;
import mx.com.adolfogarcia.popularmovies.R;
import mx.com.adolfogarcia.popularmovies.data.RestfulServiceConfiguration;
import mx.com.adolfogarcia.popularmovies.databinding.MovieCollectionFragmentBinding;
import mx.com.adolfogarcia.popularmovies.model.event.SortOrderSelectionEvent;
import mx.com.adolfogarcia.popularmovies.model.view.MovieCollectionViewModel;
import mx.com.adolfogarcia.popularmovies.net.FetchMoviePageTaskFactory;
import mx.com.adolfogarcia.popularmovies.view.adapter.LabeledItem;
import mx.com.adolfogarcia.popularmovies.view.adapter.MoviePosterAdapter;

import static mx.com.adolfogarcia.popularmovies.data.MovieContract.CachedMovieEntry;

/**
 * Displays a collection of movie posters in a grid, retrieving the information
 * from <a href="https://www.themoviedb.org/">themoviedb.org</a>.
 *
 * @author Jesús Adolfo García Pasquel
 */
public class MovieCollectionFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Identifies the messages written to the log by this class.
     */
    private static final String LOG_TAG =
            MovieCollectionFragment.class.getSimpleName();

    /**
     * Identifies the {@link Loader} that retrieves the movie data cached in
     * the local database.
     */
    private static final int MOVIE_COLLECTION_LOADER_ID = 723452;

    /**
     * Key used to save and retrieve the serialized {@link #mViewModel}.
     */
    private static final String STATE_VIEW_MODEL = "state_view_model";

    /**
     * The configuration information required to retrieve movie data and images
     * using <a href="https://www.themoviedb.org/">themoviedb.org</a>'s RESTful
     * API.
     */
    @Inject WeakReference<RestfulServiceConfiguration> mWeakConfiguration;

    /**
     * Binds the view to the view model.
     * @see MovieCollectionViewModel
     */
    private MovieCollectionFragmentBinding mBinding = null;

    /**
     * Adapter that provides the {@link View}s for presenting each movie.
     */
    private MoviePosterAdapter mMoviePosterAdapter;

    /**
     * View model that provides data and behaviour to the
     * {@link MovieCollectionFragment}.
     */
    private MovieCollectionViewModel mViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((PopularMoviesApplication) getActivity().getApplication())
                .getComponent().inject(this);
        setHasOptionsMenu(true);
        restoreState(savedInstanceState);
        if (mViewModel == null) {
            mViewModel = new MovieCollectionViewModel();
            ((PopularMoviesApplication) getActivity().getApplication())
                    .getComponent().inject(mViewModel);
        }
    }

    /**
     * Loads the previous state, stored in the {@link Bundle} passed as argument,
     * into to {@link MovieCollectionFragment}. {@link #mViewModel} in particular.
     * If the argument is {@code null}, nothing is done.
     *
     * @param savedInstanceState the {@link MovieCollectionFragment}'s previous
     *                           state.
     */
    private void restoreState(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            return;
        }
        mViewModel = Parcels.unwrap(savedInstanceState.getParcelable(STATE_VIEW_MODEL));
        ((PopularMoviesApplication) getActivity().getApplication())
                .getComponent().inject(mViewModel);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(STATE_VIEW_MODEL, Parcels.wrap(mViewModel));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(MOVIE_COLLECTION_LOADER_ID, null, this);
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        requireNonNullConfiguration();
        mBinding = DataBindingUtil.inflate(inflater
                , R.layout.fragment_movie_collection
                , container
                , false);
        mBinding.setViewModel(mViewModel);
        mMoviePosterAdapter =
                new MoviePosterAdapter(mWeakConfiguration.get(), getActivity(), null, 0);
        mBinding.posterGridView.setAdapter(mMoviePosterAdapter);
        mBinding.posterGridView.setOnItemClickListener(mViewModel);
        mBinding.posterGridView.setOnScrollListener(mViewModel);
        if (mViewModel.isApiConfigOld()) {
            Log.i(LOG_TAG, "Old local data. Redownloading.");
            mViewModel.updateApiConfig();
            mViewModel.deleteCachedMovieData();
            mViewModel.downloadNextMoviePage();
        }
        return mBinding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_movie_collection, menu);
        MenuItem item = menu.findItem(R.id.menu_item_sort_order);
        Spinner spinner = (Spinner) MenuItemCompat.getActionView(item);
        ArrayAdapter<LabeledItem<FetchMoviePageTaskFactory>> adapter =
                new ArrayAdapter<>(getActivity()
                        , R.layout.list_item_sort_order
                        , mViewModel.getSortOrderOptions());
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(mViewModel);
        spinner.setSelection(mViewModel.getSelectedSortOrderIndex());
    }

    @Override
    public void onResume() {
        EventBus.getDefault().register(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    /**
     * Restarts the {@link Loader}, so the movies are presented in the newly
     * selected order.
     *
     * @param event the change of sort order event.
     */
    public void onEvent(SortOrderSelectionEvent event) {
        Log.i(LOG_TAG, "Restarting loader");
        getLoaderManager().restartLoader(MOVIE_COLLECTION_LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this.getActivity()
                , CachedMovieEntry.CONTENT_URI
                , MoviePosterAdapter.PROJECTION_MOVIE_POSTERS
                , mViewModel.getSelectionClause()
                , mViewModel.getSelectionArguments()
                , mViewModel.getSortOrderClause());
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // If no movie pages have been downloaded, download the first page.
        if (data.getCount() == 0) {
            Log.i(LOG_TAG, "Downloading movies because of no local data.");
            mViewModel.downloadNextMoviePage();
        }
        Cursor oldCursor = mMoviePosterAdapter.swapCursor(data);
        if (oldCursor != null) {
            oldCursor.close();
        }
        // Scroll to the last selected item if reloading after an event
        // that causes the first item to be shown (e.g. configuration change).
        // If reloading because of new movie page downloads, do not scroll back
        // to selection.
        if (mBinding.posterGridView.getFirstVisiblePosition() == 0
                && mViewModel.getSelectedPosition() != AdapterView.INVALID_POSITION) {
            mBinding.posterGridView.smoothScrollToPosition(
                    mViewModel.getSelectedPosition());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Cursor oldCursor = mMoviePosterAdapter.swapCursor(null);
        if (oldCursor != null) {
            oldCursor.close();
        }
    }
}
