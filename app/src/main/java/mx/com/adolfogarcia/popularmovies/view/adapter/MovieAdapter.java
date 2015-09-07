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
import android.content.SharedPreferences;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

import com.squareup.picasso.Picasso;

import java.util.Set;

import mx.com.adolfogarcia.popularmovies.R;
import mx.com.adolfogarcia.popularmovies.databinding.MovieListItemBinding;
import mx.com.adolfogarcia.popularmovies.view.fragment.MainActivityFragment;

import static mx.com.adolfogarcia.popularmovies.data.MovieContract.CachedMovieEntry;
import static mx.com.adolfogarcia.popularmovies.view.fragment.MainActivityFragment.*;

// TODO: Reconsider package
public class MovieAdapter extends CursorAdapter {

    /**
     * Identifies messages written to the log.
     */
    private static final String LOG_TAG = MovieAdapter.class.getSimpleName();

    public MovieAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
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
        String posterImageUrl =
                getImageBaseUrl(context)
                + getBestPosterThumbnailSizeName(context)
                + cursor.getString(idx);
        // FIXME: Calculate only once and cache the value for the following calls
        int optimalWidthPixels = (int) Math.ceil(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP
                , POSTER_THUMBNAIL_WIDTH_DIP
                , context.getResources().getDisplayMetrics()));
        Picasso.with(context).load(posterImageUrl).resize(optimalWidthPixels, 0).into(binding.posterImageView);
    }

    // TODO: Place constant in a class in package data
    /**
     * The width of the poster image measured in <i>Device Independent Pixels</i>.
     */
    public static final int POSTER_THUMBNAIL_WIDTH_DIP = 180;

    // TODO: Place method in a class in package data (singleton handled with Dagger, have Dagger give it the Context)
    public String getImageBaseUrl(Context context) {
        // TODO: Calculate best and set it in a static variable to avoid recalculation
        SharedPreferences settings =
                PreferenceManager.getDefaultSharedPreferences(context);
        return settings.getString(PREFERENCES_KEY_IMAGE_URL
                , PREFERENCES_DEFAULT_IMAGE_URL);
    }

    // TODO: Place method in a class in package data (singleton handled with Dagger, have Dagger give it the Context)
    /**
     * Returns the name of the poster thumbnail size provided by
     * <a href="https://www.themoviedb.org/">themoviedb.org/</a>'s API that
     * best fits the device, or {@code null} if none is found.
     *
     * @param context used to determine the best fit by converting DIPs to pixels.
     * @return the name of the size that fits the device best or or {@code null}
     *     if none is found.
     */
    public String getBestPosterThumbnailSizeName(Context context) {
        // TODO: Calculate best and set it in an instance variable to avoid recalculation
        int optimalWidthPixels = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP
                , POSTER_THUMBNAIL_WIDTH_DIP
                , context.getResources().getDisplayMetrics());
        SharedPreferences settings =
                PreferenceManager.getDefaultSharedPreferences(context);
        Set<String> apiPosterSizes = settings.getStringSet(PREFERENCES_KEY_POSTER_SIZES
                , PREFERENCES_DEFAULT_POSTER_SIZES);
        if (apiPosterSizes == null) {
            Log.e(LOG_TAG, "No poster sizes found");
            return null;
        }

        int minDiff = Integer.MAX_VALUE;
        String bestSizeName = null;
        for (String sizeName : apiPosterSizes) {
            try {
                int size = Integer.parseInt(sizeName.substring(1));
                int diff = Math.abs(optimalWidthPixels - size);
                if (minDiff > diff) {
                    minDiff = diff;
                    bestSizeName = sizeName;
                }
            } catch (NumberFormatException nfe) {
                Log.w(LOG_TAG, "Ignoring poster size: " + sizeName);
            }
        }
        return bestSizeName;
    }

}
