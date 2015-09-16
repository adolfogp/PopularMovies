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

import mx.com.adolfogarcia.popularmovies.PopularMoviesApplication;
import mx.com.adolfogarcia.popularmovies.R;
import mx.com.adolfogarcia.popularmovies.databinding.MovieDetailFragmentBinding;
import mx.com.adolfogarcia.popularmovies.model.domain.Movie;
import mx.com.adolfogarcia.popularmovies.model.view.MovieDetailViewModel;

import static mx.com.adolfogarcia.popularmovies.data.MovieContract.CachedMovieEntry;

/**
 * Displays detailed information for a given {@link Movie}. New instances of
 * this class must be created with the factory method
 * {@link #newInstance(Movie)}.
 *
 * @author Jesús Adolfo García Pasquel
 */
public class MovieDetailFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Identifies the {@link Loader} that retrieves the movie details cached in
     * the local database.
     */
    private static final int MOVIE_DETAIL_LOADER_ID = 532232;

    /**
     * Key used to access the {@link Movie} specified as argument at creation
     * time.
     * @see #newInstance(Movie)
     */
    private static final String ARG_MOVIE = "arg_movie";

    /**
     * Key used to save and retrieve the serialized {@link #mViewModel}.
     */
    private static final String STATE_VIEW_MODEL = "state_view_model";

    /**
     * Provides data and behaviour to the {@link MovieDetailFragment}.
     */
    private MovieDetailViewModel mViewModel;

    /**
     * Binds the view to the view model.
     * @see MovieDetailViewModel
     */
    private MovieDetailFragmentBinding mBinding = null;

    /**
     * Creates a new instance of {@link MovieDetailFragment} for the specified
     * movie. You must use this factory method to create new instances.
     *
     * @param movie the for which the details will be displayed.
     * @return A new instance of fragment MovieDetailFragment.
     */
    public static MovieDetailFragment newInstance(Movie movie) {
        Bundle args = new Bundle();
        args.putParcelable(ARG_MOVIE, Parcels.wrap(movie));
        MovieDetailFragment fragment = new MovieDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() == null) {
            throw new IllegalStateException("No movie specified as Fragment argument.");
        }
        restoreState(savedInstanceState);
    }

    /**
     * Returns a new {@link MovieDetailViewModel} based on the movie data passed
     * in the {@link Fragment}'s arguments.
     *
     * @return a new {@link MovieDetailViewModel} based on the movie data passed
     *     in the {@link Fragment}'s arguments.
     */
    private MovieDetailViewModel newViewModel() {
        Movie movie = Parcels.unwrap(getArguments().getParcelable(ARG_MOVIE));
        MovieDetailViewModel viewModel = new MovieDetailViewModel();
        ((PopularMoviesApplication) getActivity().getApplication())
                .getComponent().inject(mViewModel);
        viewModel.setMovie(movie);
        return viewModel;
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
        getLoaderManager().initLoader(MOVIE_DETAIL_LOADER_ID, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater
                , R.layout.fragment_movie_detail
                , container
                , false);
        if (mViewModel == null) {
            mViewModel = newViewModel();
        }
        mBinding.setViewModel(mViewModel);
        return mBinding.getRoot();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this.getActivity()
                , CachedMovieEntry.buildMovieUri(mViewModel.getMovie().getId())
                , MovieDetailViewModel.PROJECTION_MOVIE_DETAILS
                , null
                , null
                , null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mViewModel.setMovieData(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mViewModel.setMovieData(null);
    }

}
