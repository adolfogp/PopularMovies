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

package mx.com.adolfogarcia.popularmovies.view.adapter;

import android.content.Context;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

import com.squareup.picasso.Picasso;

import mx.com.adolfogarcia.popularmovies.R;
import mx.com.adolfogarcia.popularmovies.data.RestfulServiceConfiguration;
import mx.com.adolfogarcia.popularmovies.databinding.MoviePosterListItemBinding;

import static mx.com.adolfogarcia.popularmovies.data.MovieContract.CachedMovieEntry;

/**
 * {@link CursorAdapter} that provides movie posters to the
 * {@link android.widget.AdapterView}, representing entries in
 * {@link mx.com.adolfogarcia.popularmovies.data.MovieProvider}. The poster
 * images are loaded from <a href="https://www.themoviedb.org/">themoviedb.org</a>'s
 * RESTful API, so an instance of {@link RestfulServiceConfiguration} is required.
 * The items are defined by a cursor to data provided by
 * {@link mx.com.adolfogarcia.popularmovies.data.MovieProvider}, and the projection
 * must be {@link #PROJECTION_MOVIE_POSTERS}.
 *
 * @author Jesús Adolfo García Pasquel
 */
public class MoviePosterAdapter extends CursorAdapter {

    /**
     * Identifies messages written to the log.
     */
    private static final String LOG_TAG = MoviePosterAdapter.class.getSimpleName();

    /**
     * Projection that includes the movie details to be presented. Used to
     * query {@link mx.com.adolfogarcia.popularmovies.data.MovieProvider}.
     */
    public static final String[] PROJECTION_MOVIE_POSTERS = {
            CachedMovieEntry._ID,
            CachedMovieEntry.COLUMN_POSTER_PATH
    };

    /**
     * Index of {@link CachedMovieEntry#_ID} in {@link #PROJECTION_MOVIE_POSTERS}.
     */
    public static final int COL_ID = 0;

    /**
     * Index of {@link CachedMovieEntry#COLUMN_POSTER_PATH} in
     * {@link #PROJECTION_MOVIE_POSTERS}.
     */
    public static final int COL_POSTER_PATH = 1;

    /**
     * The optimal width of a movie poster in pixels, calculated for the device.
     * 0 if it has not been initialized.
     */
    private static int sOptimalWidthPixels = -1;

    private final RestfulServiceConfiguration mConfiguration;

    public MoviePosterAdapter(RestfulServiceConfiguration configuration
            , Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
        mConfiguration = configuration;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        MoviePosterListItemBinding binding =
                DataBindingUtil.inflate(layoutInflater, R.layout.list_item_movie_poster, parent, false);
        binding.getRoot().setTag(binding);
        return binding.getRoot();
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        int posterPixelWidth = context.getResources().getDimensionPixelSize(
                R.dimen.movie_poster_thumbnail_width);
        int posterPixelHeight = context.getResources().getDimensionPixelSize(
                R.dimen.movie_poster_thumbnail_height);
        MoviePosterListItemBinding binding =
                (MoviePosterListItemBinding) view.getTag();
        String posterImageUrl = mConfiguration.getBestFittingPosterUrl(
                cursor.getString(COL_POSTER_PATH)
                , posterPixelWidth);
        Picasso.with(context)
                .load(posterImageUrl)
                .resize(posterPixelWidth, posterPixelHeight)
                .placeholder(R.anim.poster_loading)
                .error(R.drawable.logo_the_movie_db_180dp)
                .into(binding.posterImageView);
    }

}
