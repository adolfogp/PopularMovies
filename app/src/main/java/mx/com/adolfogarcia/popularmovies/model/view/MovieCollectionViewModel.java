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

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import mx.com.adolfogarcia.popularmovies.data.RestfulServiceConfiguration;
import mx.com.adolfogarcia.popularmovies.net.FetchConfigurationTask;
import mx.com.adolfogarcia.popularmovies.net.FetchMovieTask;

/**
 * View model for the movie collection's view. Provides data and behaviour.
 *
 * Created by Jesús Adolfo García Pasquel on 8/09/15.
 */
public class MovieCollectionViewModel implements AdapterView.OnItemClickListener {

    /**
     * Identifies messages written to the log by this class.
     */
    private static final String LOG_TAG =
            MovieCollectionViewModel.class.getSimpleName();

    private final Context mContext;

    private final RestfulServiceConfiguration mConfiguration;

    // TODO: Try to create the ViewModel with Dagger.
    public MovieCollectionViewModel(Context context
            , RestfulServiceConfiguration configuration) {
        mContext = context;
        mConfiguration = configuration;
    }

    public void updateApiConfig() {
        FetchConfigurationTask fetchConfigurationTask =
                new FetchConfigurationTask(mContext, mConfiguration);
        fetchConfigurationTask.execute();
    }

    public void updateMovies() {
        // TODO: Get new taks using Dagger (especially since this may move elsewhere to get the pages)
        FetchMovieTask fetchMovieTask =
                new FetchMovieTask(mContext, mConfiguration);
        fetchMovieTask.execute();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // TODO: Implement
        Log.d(LOG_TAG, "Clicked");
    }

}
