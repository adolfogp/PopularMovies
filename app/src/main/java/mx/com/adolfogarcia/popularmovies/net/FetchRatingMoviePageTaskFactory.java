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

package mx.com.adolfogarcia.popularmovies.net;

import android.content.Context;
import android.os.AsyncTask;

import org.apache.commons.lang3.BooleanUtils;

import java.lang.ref.WeakReference;

import mx.com.adolfogarcia.popularmovies.data.RestfulServiceConfiguration;
import static mx.com.adolfogarcia.popularmovies.data.MovieContract.CachedMovieEntry;

/**
 * Implementation of {@link FetchMoviePageTaskFactory} for movies sorted by
 * vote average in descending order.
 *
 * @author Jesús Adolfo García Pasquel
 * @see TheMovieDbApi#SORT_BY_USER_RATING
 */
public class FetchRatingMoviePageTaskFactory implements FetchMoviePageTaskFactory {

    /**
     * Used on the query to {@link mx.com.adolfogarcia.popularmovies.data.MovieProvider},
     * to order the results from highest to lowest rated.
     */
    private static final String ORDER_BY_VOTE_AVERAGE_DESCENDING =
            CachedMovieEntry.COLUMN_VOTE_AVERAGE + " DESC"
            + ", " + CachedMovieEntry._ID + " ASC";

    /**
     * The configuration of the RESTful API.
     */
    private WeakReference<RestfulServiceConfiguration> mWeakConfiguration;

    /**
     * The {@link Context} used to access
     * {@link mx.com.adolfogarcia.popularmovies.data.MovieProvider}.
     */
    private WeakReference<Context> mWeakContext;

    /**
     * Creates a new instance of {@link FetchRatingMoviePageTaskFactory}
     * that creates instances of {@link FetchMoviePageTask} using the
     * provided arguments.
     *
     *  @param configuration the configuration used to access movie pages.
     * @param context the context used to access the provider on which the
     *                movie data will be stored.
     */
    public FetchRatingMoviePageTaskFactory(
            RestfulServiceConfiguration configuration
            , Context context) {
        mWeakConfiguration = new WeakReference<>(configuration);
        mWeakContext = new WeakReference<>(context);
    }

    @Override
    public String getRestApiSortOrder() {
        return TheMovieDbApi.SORT_BY_USER_RATING;
    }

    @Override
    public String getMovieProviderSelectionClause() {
        return CachedMovieEntry.COLUMN_HIGHEST_RATED + " = ?";
    }

    @Override
    public String[] getMovieProviderSelectionArguments() {
        return new String[] {Integer.toString(BooleanUtils.toInteger(true))};
    }

    @Override
    public String getMovieProviderSortOrder() {
        return ORDER_BY_VOTE_AVERAGE_DESCENDING;
    }

    @Override
    public AsyncTask<Integer, ?, ?> newFetchMovieTask() {
        return new FetchMoviePageTask(mWeakConfiguration.get()
                , TheMovieDbApi.SORT_BY_USER_RATING
                , mWeakContext.get());
    }

}
