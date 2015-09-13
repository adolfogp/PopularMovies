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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.parceler.Parcels;

import javax.inject.Inject;

import mx.com.adolfogarcia.popularmovies.PopularMoviesApplication;
import mx.com.adolfogarcia.popularmovies.R;
import mx.com.adolfogarcia.popularmovies.data.RestfulServiceConfiguration;
import mx.com.adolfogarcia.popularmovies.databinding.MovieCollectionFragmentBinding;
import mx.com.adolfogarcia.popularmovies.model.view.MovieCollectionViewModel;
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
     * Used on the query to {@link mx.com.adolfogarcia.popularmovies.data.MovieProvider},
     * to order the results from most to least popular.
     */
    private static final String ORDER_BY_POPULARITY_DESCENDING =
            CachedMovieEntry.COLUMN_POPULARITY + " DESC";

    /**
     * Binds the view to the view model.
     * @see MovieCollectionViewModel
     */
    private MovieCollectionFragmentBinding mBinding = null;

    /**
     * Adapter that provides the {@link View}s for presenting each movie.
     */
    private MoviePosterAdapter mMoviePosterAdapter;

    private MovieCollectionViewModel mViewModel;

    /**
     * The configuration information required to retrieve movie data and images
     * using <a href="https://www.themoviedb.org/">themoviedb.org</a>'s RESTful
     * API.
     */
    @Inject RestfulServiceConfiguration mConfiguration;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((PopularMoviesApplication) getActivity().getApplication()).getComponent().inject(this);
        restoreState(savedInstanceState);
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
        mViewModel.setConfiguration(mConfiguration);
        mViewModel.setContext(getActivity());
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater
                , R.layout.fragment_movie_collection
                , container
                , false);
        if (mViewModel == null) {
            mViewModel = new MovieCollectionViewModel(getActivity(), mConfiguration); // TODO: Try to get the view model instance from dagger or let dagger inject the configuration directly.
        }
        mBinding.setViewModel(mViewModel);
        mMoviePosterAdapter = new MoviePosterAdapter(mConfiguration, getActivity(), null, 0);
        mBinding.posterGridView.setAdapter(mMoviePosterAdapter);
        mBinding.posterGridView.setOnItemClickListener(mViewModel);
        mBinding.posterGridView.setOnScrollListener(mViewModel);
        return mBinding.getRoot();
    }

    @Override
    public void onStart() { //TODO: Remove updates from here
        super.onStart();
        // TODO: Check last date sync, download config if necessary and after, update movies
        mViewModel.updateApiConfig();
        mViewModel.downloadNextMoviePage();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this.getActivity()
                , CachedMovieEntry.CONTENT_URI
                , MoviePosterAdapter.PROJECTION_MOVIE_POSTERS
                , null
                , null
                , ORDER_BY_POPULARITY_DESCENDING);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Cursor oldCursor = mMoviePosterAdapter.swapCursor(data);
        if (oldCursor != null) {
            oldCursor.close();
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
