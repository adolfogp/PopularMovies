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
import mx.com.adolfogarcia.popularmovies.databinding.MovieListItemBinding;

import static mx.com.adolfogarcia.popularmovies.data.MovieContract.CachedMovieEntry;

// TODO: Reconsider package
public class MovieAdapter extends CursorAdapter {

    /**
     * Identifies messages written to the log.
     */
    private static final String LOG_TAG = MovieAdapter.class.getSimpleName();

    /**
     * The width of the poster image measured in <i>Device Independent Pixels</i>.
     */
    public static final int POSTER_THUMBNAIL_WIDTH_DIP = 180; // TODO - Use constant in resources and change also the constant in the detail view model.

    private final RestfulServiceConfiguration mConfiguration;

    public MovieAdapter(RestfulServiceConfiguration configuration
            , Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
        mConfiguration = configuration;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        MovieListItemBinding binding =
                DataBindingUtil.inflate(layoutInflater, R.layout.list_item_movie, parent, false);
        binding.getRoot().setTag(binding);
        return binding.getRoot();
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        MovieListItemBinding binding = (MovieListItemBinding) view.getTag();
        // TODO: use the query's indices
        int idx = cursor.getColumnIndex(CachedMovieEntry.COLUMN_POSTER_PATH);
        String posterImageUrl = mConfiguration.getBestFittingPosterUrl(
                cursor.getString(idx)
                , POSTER_THUMBNAIL_WIDTH_DIP);
        // FIXME: Calculate only once and cache the value for the following calls
        int optimalWidthPixels = (int) Math.ceil(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP
                , POSTER_THUMBNAIL_WIDTH_DIP
                , context.getResources().getDisplayMetrics()));
        Picasso.with(context)
                .load(posterImageUrl)
                .resize(optimalWidthPixels, 0)
                .placeholder(R.anim.poster_loading)
                .error(R.drawable.logo_the_movie_db_180dp)
                .into(binding.posterImageView);
    }

}
