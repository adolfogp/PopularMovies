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

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.parceler.Parcels;

import javax.inject.Inject;

import mx.com.adolfogarcia.popularmovies.R;
import mx.com.adolfogarcia.popularmovies.data.RestfulServiceConfiguration;
import mx.com.adolfogarcia.popularmovies.model.domain.Movie;
import mx.com.adolfogarcia.popularmovies.model.view.MovieDetailViewModel;

/**
 * Displays detailed information for a given {@link Movie}. New instances of
 * this class must be created with the factory method
 * {@link #newInstance(Movie)}.
 *
 * @author Jesús Adolfo García Pasquel
 */
public class MovieDetailFragment extends Fragment {

    /**
     * Key used to access the {@link Movie} specified as argument at creation
     * time.
     * @see #newInstance(Movie)
     */
    private static final String ARG_MOVIE = "arg_movie";

    /**
     *
     */
    private static final String STATE_MOVIE = "state_movie";

    /**
     * Provides data and behaviour to the {@link MovieDetailFragment}.
     */
    private MovieDetailViewModel mViewModel;

    /**
     * The configuration information required to retrieve movie data and images
     * using <a href="https://www.themoviedb.org/">themoviedb.org</a>'s RESTful
     * API.
     */
    @Inject RestfulServiceConfiguration mConfiguration;

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
        if (savedInstanceState == null) {
            // TODO: Load the required information from the ContentProvider
            Movie movie = getArguments().getParcelable(ARG_MOVIE);
            mViewModel = new MovieDetailViewModel();
            mViewModel.setContext(this.getActivity());
            mViewModel.setConfiguration(this.mConfiguration);
            mViewModel.setMovie(movie);
        } else {
//            mMovie = savedInstanceState.getParcelable(STATE_MOVIE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_movie_detail, container, false);
    }

}
