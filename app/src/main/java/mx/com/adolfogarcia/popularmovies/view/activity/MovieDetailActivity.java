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

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import org.parceler.Parcels;

import mx.com.adolfogarcia.popularmovies.R;
import mx.com.adolfogarcia.popularmovies.model.domain.Movie;
import mx.com.adolfogarcia.popularmovies.view.fragment.MovieCollectionFragment;
import mx.com.adolfogarcia.popularmovies.view.fragment.MovieDetailFragment;

/**
 * Shows the details of a {@link Movie} passed as an extra in the {@link Intent},
 * using the key {@link #EXTRA_MOVIE}. The only attribute that the {@link Movie}
 * must have assigned is {@link Movie#getId()}.
 *
 * @author Jesús Adolfo García Pasquel
 */
public class MovieDetailActivity extends AppCompatActivity {

    /**
     * Key used to access the {@link Movie} to show, from the {@link Intent}'s
     * extras.
     */
    public static final String EXTRA_MOVIE = "extra_movie";

    /**
     * Identifies the messages written to the log by this class.
     */
    private static final String LOG_TAG = MovieDetailActivity.class.getSimpleName();

    /**
     * Identifies the {@code Fragment} used to present the details of a movie.
     */
    private static final String MOVIE_DETAIL_FRAGMENT_TAG =
            MovieDetailFragment.class.getCanonicalName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Movie movie = Parcels.unwrap(getIntent().getParcelableExtra(EXTRA_MOVIE));
        if (movie == null) {
            throw new IllegalArgumentException(
                    "The movie specified in the Intent may not be null.");
        }
        setContentView(R.layout.activity_movie_detail);
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.findFragmentByTag(MOVIE_DETAIL_FRAGMENT_TAG) == null) {
            MovieDetailFragment movieDetailFragment =
                    MovieDetailFragment.newInstance(movie);
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.add(R.id.movie_detail_container
                    , movieDetailFragment
                    , MOVIE_DETAIL_FRAGMENT_TAG);
            transaction.commit();
        }
    }

}
