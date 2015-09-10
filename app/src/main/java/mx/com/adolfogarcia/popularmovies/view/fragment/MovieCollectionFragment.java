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

import javax.inject.Inject;

import mx.com.adolfogarcia.popularmovies.PopularMoviesApplication;
import mx.com.adolfogarcia.popularmovies.R;
import mx.com.adolfogarcia.popularmovies.data.RestfulServiceConfiguration;
import mx.com.adolfogarcia.popularmovies.databinding.MovieCollectionFragmentBinding;
import mx.com.adolfogarcia.popularmovies.model.view.MovieCollectionViewModel;
import mx.com.adolfogarcia.popularmovies.view.adapter.MovieAdapter;

import static mx.com.adolfogarcia.popularmovies.data.MovieContract.CachedMovieEntry;

/**
 * Displays a collection of movie posters in a grid, retrieving the information
 * from <a href="https://www.themoviedb.org/">themoviedb.org</a>.
 *
 * @author Jesús Adolfo García Pasquel
 */
public class MovieCollectionFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Identifies the messages written to the log by this class.
     */
    private static final String LOG_TAG = MovieCollectionFragment.class.getSimpleName();

    /**
     * Identifies the {@link Loader} that retrieves the movie data cached in
     * the local database.
     */
    private static final int MOVIE_LOADER_ID = 2576;

    /**
     * Binds the view to the view model.
     * @see mx.com.adolfogarcia.popularmovies.model.view.MovieCollectionViewModel
     */
    private MovieCollectionFragmentBinding mBinding = null;

    /**
     * Adapter that provides the {@link View}s for presenting each movie.
     */
    private MovieAdapter mMovieAdapter;

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
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(MOVIE_LOADER_ID, null, this);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater
                , R.layout.fragment_movie_collection
                , container
                , false);
        mViewModel = new MovieCollectionViewModel(getActivity(), mConfiguration);
        mBinding.setViewModel(mViewModel); // TODO: Try to get view model instance from with dagger.
        Cursor cursor = getActivity().getContentResolver().query(
                CachedMovieEntry.CONTENT_URI, null, null, null, null); // FIXME: Wrong query
        mMovieAdapter = new MovieAdapter(mConfiguration, getActivity(), cursor, 0);
        mBinding.posterGridView.setAdapter(mMovieAdapter);
        mBinding.posterGridView.setOnItemClickListener(mBinding.getViewModel());
        return mBinding.getRoot();
    }

    @Override
    public void onStart() { //TODO: Remove updates from here
        super.onStart();
        // TODO: Check last date sync, download config if necessary and after, update movies
        mViewModel.updateApiConfig();
        mViewModel.updateMovies();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // TODO: Specify projection (place projection in Adapter class)
        // TODO: Specify order
        return new CursorLoader(this.getActivity(), CachedMovieEntry.CONTENT_URI
                , null, null, null, null);  // FIXME: Wrong query
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mMovieAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMovieAdapter.swapCursor(null);
    }
}
