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

import android.content.SharedPreferences;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Set;

import static mx.com.adolfogarcia.popularmovies.data.MovieContract.CachedMovieEntry;

import javax.inject.Inject;

import mx.com.adolfogarcia.popularmovies.PopularMoviesApplication;
import mx.com.adolfogarcia.popularmovies.R;
import mx.com.adolfogarcia.popularmovies.data.RestfulServiceConfiguration;
import mx.com.adolfogarcia.popularmovies.view.adapter.MovieAdapter;
import mx.com.adolfogarcia.popularmovies.databinding.MainFragmentBinding;
import mx.com.adolfogarcia.popularmovies.net.FetchConfigurationTask;
import mx.com.adolfogarcia.popularmovies.net.FetchMovieTask;

/**
 * A placeholder fragment containing a simple view.
 *
 * @author Jesús Adolfo García Pasquel
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = MainActivityFragment.class.getSimpleName();

    private static final int MY_LOADER_ID = 2576;

    // TODO: Put preference constants in appropriate class in package data


    private MainFragmentBinding mBinding = null;

    private MovieAdapter mMovieAdapter;

    @Inject RestfulServiceConfiguration mConfiguration;

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
        mMovieAdapter = new MovieAdapter(mConfiguration, getActivity(), cursor, 0);
        mBinding.posterGridView.setAdapter(mMovieAdapter); // TODO: Verify if this can be done with DataBinding
        mBinding.posterGridView.setOnItemClickListener(
                (adapterView, view, position, l) -> Log.d(LOG_TAG, "Clicked.")); // TODO: Move to DataBinding
        return mBinding.getRoot();
    }

    @Override
    public void onStart() { //TODO: Remove updates from here. Do not use the changeListener.
        super.onStart();
        SharedPreferences settings =
                PreferenceManager.getDefaultSharedPreferences(getActivity());
        settings.registerOnSharedPreferenceChangeListener((sharedPreferences, key) -> {
            if (RestfulServiceConfiguration.PREFERENCES_KEY_IMAGE_URL.equals(key)) {
                updateMovies();
            }
        });
        updateApiConfig();
        updateMovies();
    }

    private void updateApiConfig() {
        FetchConfigurationTask fetchConfigurationTask =
                new FetchConfigurationTask(getActivity(), mConfiguration);
        fetchConfigurationTask.execute();
    }

    private void updateMovies() {
        // TODO: Get new taks using Dagger (especially since this may move elsewhere to get the pages)
        FetchMovieTask fetchMovieTask =
                new FetchMovieTask(getActivity(), mConfiguration);
        fetchMovieTask.execute();
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
