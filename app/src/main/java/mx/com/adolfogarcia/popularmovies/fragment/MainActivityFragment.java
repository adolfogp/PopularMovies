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

package mx.com.adolfogarcia.popularmovies.fragment;

import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import static mx.com.adolfogarcia.popularmovies.data.MovieContract.CachedMovieEntry;

import javax.inject.Inject;

import mx.com.adolfogarcia.popularmovies.Configuration;
import mx.com.adolfogarcia.popularmovies.PopularMoviesApplication;
import mx.com.adolfogarcia.popularmovies.R;
import mx.com.adolfogarcia.popularmovies.adapter.MovieAdapter;
import mx.com.adolfogarcia.popularmovies.databinding.MainFragmentBinding;
import mx.com.adolfogarcia.popularmovies.net.FetchMoviesTask;

/**
 * A placeholder fragment containing a simple view.
 *
 * @author Jesús Adolfo García Pasquel
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = MainActivityFragment.class.getSimpleName();

    private static final int MY_LOADER_ID = 2576;

    private MainFragmentBinding mBinding = null;

    private MovieAdapter mMovieAdapter;

    @Inject Configuration mConfiguration;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((PopularMoviesApplication) getActivity().getApplication()).getComponent().inject(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(MY_LOADER_ID, null, this);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false);
        // TODO: Set View Models for mBinding, move events to the View Models

        Cursor cursor = getActivity().getContentResolver().query(
                CachedMovieEntry.CONTENT_URI, null, null, null, null); // FIXME: Wrong query
        mMovieAdapter = new MovieAdapter(getActivity(), cursor, 0);
        mBinding.gridview.setAdapter(mMovieAdapter); // TODO: Verify if this can be done with DataBinding
        mBinding.gridview.setOnItemClickListener(
                (adapterView, view, position, l) -> Log.d(LOG_TAG, "Clicked.")); // TODO: Move to DataBinding
        return mBinding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies(); // FIXME: Remove from here
    }

    private void updateMovies() {
        // TODO: Get new taks using Dart (especially since this may move elsewhere to get the pages)
        FetchMoviesTask weatherTask = new FetchMoviesTask(getActivity(), mConfiguration);
        weatherTask.execute();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // TODO: Specify projection (place projection in Adapter class)
        // TODO: Specify order
        return new CursorLoader(this.getActivity(), CachedMovieEntry.CONTENT_URI
                , null, null, null, null);
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
