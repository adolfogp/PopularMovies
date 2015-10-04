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

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import org.parceler.Parcels;

import mx.com.adolfogarcia.popularmovies.PopularMoviesApplication;
import mx.com.adolfogarcia.popularmovies.R;
import mx.com.adolfogarcia.popularmovies.databinding.MovieDetailFragmentBinding;
import mx.com.adolfogarcia.popularmovies.model.domain.Movie;
import mx.com.adolfogarcia.popularmovies.model.domain.Trailer;
import mx.com.adolfogarcia.popularmovies.model.view.MovieDetailViewModel;

import static mx.com.adolfogarcia.popularmovies.data.MovieContract.CachedMovieEntry;
import static mx.com.adolfogarcia.popularmovies.model.view.MovieDetailViewModel.MovieDetailQuery;
import static mx.com.adolfogarcia.popularmovies.model.view.MovieDetailViewModel.MovieTrailerQuery;
import static mx.com.adolfogarcia.popularmovies.model.view.MovieDetailViewModel.MovieReviewQuery;

/**
 * Displays detailed information for a given {@link Movie}. New instances of
 * this class must be created with the factory method
 * {@link #newInstance(Movie)}.
 *
 * @author Jesús Adolfo García Pasquel
 */
public class MovieDetailFragment extends Fragment {

    /**
     * Identifies the messages written to the log by this class.
     */
    private static final String LOG_TAG = MovieDetailFragment.class.getSimpleName();

    /**
     * The MIME type for plain text.
     */
    private static final String PLAIN_TEXT_MEDIA_TYPE = "text/plain";

    /**
     * Identifies the {@link Loader} that retrieves the movie details cached in
     * the local database.
     */
    private static final int MOVIE_DETAIL_LOADER_ID = 532232;

    /**
     * Identifies the {@link Loader} that retrieves the trailer videos cached in
     * the local database for the movie specified to the {@code Fragment}.
     */
    private static final int MOVIE_TRAILER_LOADER_ID = 244185;

    /**
     * Identifies the {@link Loader} that retrieves the reviews cached in
     * the local database for the movie specified to the {@code Fragment}.
     */
    private static final int MOVIE_REVIEW_LOADER_ID = 950426;

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
     * Lets the user share the first movie trailer, if available.
     */
    private ShareActionProvider mShareActionProvider = null;

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
        this.setHasOptionsMenu(true);
        if (getArguments() == null) {
            throw new IllegalStateException("No movie specified as Fragment argument.");
        }
        this.restoreState(savedInstanceState);
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
                .getComponent().inject(viewModel);
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
        getLoaderManager().initLoader(MOVIE_DETAIL_LOADER_ID, null
                , new MovieLoaderCallbacks());
        getLoaderManager().initLoader(MOVIE_TRAILER_LOADER_ID, null
                , new TrailerLoaderCallbacks());
        getLoaderManager().initLoader(MOVIE_REVIEW_LOADER_ID, null
                , new ReviewLoaderCallbacks());
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
        // TODO: Bind onClick with Data Binding library. At this moment, it breaks compilation
        mBinding.checkboxFavorite.setOnClickListener(
                (v) -> mViewModel.onClickFavorite((CheckBox) v));
        return mBinding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_movie_detail, menu);
        MenuItem item = menu.findItem(R.id.menu_item_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        mShareActionProvider.setShareIntent(getShareFirstTrailerIntent());
    }

    /**
     * Returns an {@link Intent} that can be used to share the first movie
     * trailer or {@code null} if there are no trailers (or there is no app on
     * the device that may be used to share).
     *
     * @return an {@link Intent} that can be used to share the first movie
     *     trailer or {@code null} if one is not available.
     */
    private Intent getShareFirstTrailerIntent() {
        if (mViewModel == null || mViewModel.getTrailers().isEmpty()) {
            return null;
        }
        Trailer firstTrailer = mViewModel.getTrailers().get(0);
        Context context = getActivity();
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, firstTrailer.getVideoUri().toString());
        intent.setType(PLAIN_TEXT_MEDIA_TYPE);
        if (intent.resolveActivity(context.getPackageManager()) == null) {
            Log.w(LOG_TAG, "Unable to create share intent. No application available to share.");
            intent = null;
        }
        return intent;
    }

    /**
     * Handles the callbacks for the {@link Loader} that retrieves the movie's
     * details from the {@code ContentProvider}. When loading is finished,
     * sets them on the {@link MovieDetailFragment#mViewModel} of the associated
     * {@link MovieDetailFragment}.
     */
    private class MovieLoaderCallbacks implements LoaderManager.LoaderCallbacks<Cursor> {

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(MovieDetailFragment.this.getActivity()
                    , CachedMovieEntry.buildMovieUri(mViewModel.getMovie().getId())
                    , MovieDetailQuery.PROJECTION
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

    /**
     * Handles the callbacks for the {@link Loader} that retrieves the movie's
     * trailer videos from the {@code ContentProvider}. When loading is finished,
     * sets them on the {@link MovieDetailFragment#mViewModel} of the associated
     * {@link MovieDetailFragment}.
     */
    private class TrailerLoaderCallbacks implements LoaderManager.LoaderCallbacks<Cursor> {

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(MovieDetailFragment.this.getActivity()
                    // TODO: Load only trailers and from YouTube - use query parameters
                    , CachedMovieEntry.buildMovieVideosUri(mViewModel.getMovie().getId())
                    , MovieTrailerQuery.PROJECTION
                    , null
                    , null
                    , MovieTrailerQuery.SORT_ORDER);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            mViewModel.setMovieTrailerData(data);
            // Set the share intent, if the provider has already been loaded
            if (mShareActionProvider != null) {
                mShareActionProvider.setShareIntent(getShareFirstTrailerIntent());
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            mViewModel.setMovieTrailerData(null);
        }
    }

    /**
     * Handles the callbacks for the {@link Loader} that retrieves the movie's
     * reviews from the {@code ContentProvider}. When loading is finished,
     * sets them on the {@link MovieDetailFragment#mViewModel} of the associated
     * {@link MovieDetailFragment}.
     */
    private class ReviewLoaderCallbacks implements LoaderManager.LoaderCallbacks<Cursor> {

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(MovieDetailFragment.this.getActivity()
                    , CachedMovieEntry.buildMovieReviewsUri(mViewModel.getMovie().getId())
                    , MovieReviewQuery.PROJECTION
                    , null
                    , null
                    , MovieReviewQuery.SORT_ORDER);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            mViewModel.setMovieReviewData(data);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            mViewModel.setMovieReviewData(null);
        }
    }

}
