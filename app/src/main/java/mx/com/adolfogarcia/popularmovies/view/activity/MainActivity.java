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

package mx.com.adolfogarcia.popularmovies.view.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import de.greenrobot.event.EventBus;
import mx.com.adolfogarcia.popularmovies.R;
import mx.com.adolfogarcia.popularmovies.model.event.MovieSelectionEvent;
import mx.com.adolfogarcia.popularmovies.view.fragment.MovieCollectionFragment;
import mx.com.adolfogarcia.popularmovies.view.fragment.MovieDetailFragment;

/**
 * Presents the user a collection of movies to select from and shows
 * the details of the selected movie.
 *
 * @author Jesús Adolfo García Pasquel
 */
public class MainActivity extends AppCompatActivity {

    /**
     * Identifies the messages written to the log by this class.
     */
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    /**
     * Identifies the {@code Fragment} used to present the collection of
     * movies.
     */
    private static final String MOVIE_COLLECTION_FRAGMENT_TAG =
            MovieCollectionFragment.class.getCanonicalName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.findFragmentByTag(MOVIE_COLLECTION_FRAGMENT_TAG) == null) {
            MovieCollectionFragment movieCollectionFragment =
                    new MovieCollectionFragment();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.add(R.id.movie_collection_container
                    , movieCollectionFragment
                    , MOVIE_COLLECTION_FRAGMENT_TAG);
            transaction.commit();
        }
    }

    @Override
    protected void onResume() {
        EventBus.getDefault().register(this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    /**
     * Displays the detail view of the selected movie.
     *
     * @param event the movie selection event.
     */
    public void onEvent(MovieSelectionEvent event) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        MovieDetailFragment movieDetailFragment =
                MovieDetailFragment.newInstance(event.getSelectedMovie());
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.movie_collection_container, movieDetailFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
