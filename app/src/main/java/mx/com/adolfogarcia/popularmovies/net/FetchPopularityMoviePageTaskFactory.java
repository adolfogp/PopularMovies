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

import java.lang.ref.WeakReference;

import mx.com.adolfogarcia.popularmovies.data.MovieContract;
import mx.com.adolfogarcia.popularmovies.data.RestfulServiceConfiguration;

/**
 * Implementation of {@link FetchMoviePageTaskFactory} for movies sorted by
 * popularity in descending order.
 *
 * @author Jesús Adolfo García Pasquel
 * @see TheMovieDbApi#SORT_BY_POPULARITY
 */
public class FetchPopularityMoviePageTaskFactory
        implements FetchMoviePageTaskFactory {

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
     * Used on the query to {@link mx.com.adolfogarcia.popularmovies.data.MovieProvider},
     * to order the results from most to least popular.
     */
    private static final String ORDER_BY_POPULARITY_DESCENDING =
            MovieContract.CachedMovieEntry.COLUMN_POPULARITY + " DESC";

    /**
     * Creates a new instance of {@link FetchPopularityMoviePageTaskFactory}
     * that creates instances of {@link FetchMoviePageTask} using the
     * provided arguments.
     *
     *  @param configuration the configuration used to access movie pages.
     * @param context the context used to access the provider on which the
     *                movie data will be stored.
     */
    public FetchPopularityMoviePageTaskFactory(
            RestfulServiceConfiguration configuration
            , Context context) {
        mWeakConfiguration = new WeakReference<>(configuration);
        mWeakContext = new WeakReference<>(context);
    }

    @Override
    public String getMovieSortOrder() {
        return ORDER_BY_POPULARITY_DESCENDING;
    }

    @Override
    public AsyncTask<Integer, ?, ?> newFetchMovieTask() {
        return new FetchMoviePageTask(mWeakConfiguration.get()
                , TheMovieDbApi.SORT_BY_POPULARITY
                , mWeakContext.get());
    }

}
