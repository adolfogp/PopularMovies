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

import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.Set;

import static mx.com.adolfogarcia.popularmovies.data.MovieContract.CachedMovieEntry;

import javax.inject.Inject;

import mx.com.adolfogarcia.popularmovies.Configuration;
import mx.com.adolfogarcia.popularmovies.PopularMoviesApplication;
import mx.com.adolfogarcia.popularmovies.R;
import mx.com.adolfogarcia.popularmovies.adapter.MovieAdapter;
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

    public static final String PREFERENCES_KEY_LAST_UPDATE = "date_last_update";

    public static final long PREFERENCES_DEFAULT_LAST_UPDATE = 0;

    public static final String PREFERENCES_KEY_IMAGE_URL = "image_secure_base_url";

    public static final String PREFERENCES_DEFAULT_IMAGE_URL = "https://image.tmdb.org/t/p/";

    public static final String PREFERENCES_KEY_POSTER_SIZES = "poster_sizes";

    public static final Set<String> PREFERENCES_DEFAULT_POSTER_SIZES = null;

    public static final String PREFERENCES_KEY_BACKDROP_SIZES = "backdrop_sizes";

    public static final Set<String> PREFERENCES_DEFAULT_BACKDROP_SIZES = null;


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
    public void onStart() { //TODO: Remove updates from here. Do not use the changeListener.
        super.onStart();
        SharedPreferences settings =
                PreferenceManager.getDefaultSharedPreferences(getActivity());
        settings.registerOnSharedPreferenceChangeListener((sharedPreferences, key) -> {
            if (PREFERENCES_KEY_IMAGE_URL.equals(key)) {
                updateMovies();
            }
        });
        updateApiConfig();
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
