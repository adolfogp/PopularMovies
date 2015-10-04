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

import android.os.AsyncTask;

import org.apache.commons.lang3.BooleanUtils;

import static mx.com.adolfogarcia.popularmovies.data.MovieContract.CachedMovieEntry;

/**
 * Implementation of {@link FetchMoviePageTaskFactory} for the user's favorite
 * movies sorted by local id in ascending order.
 *
 * @author Jesús Adolfo García Pasquel
 */
public class FetchFavoriteMoviePageTaskFactory implements FetchMoviePageTaskFactory {

    /**
     * Used on the query to {@link mx.com.adolfogarcia.popularmovies.data.MovieProvider},
     * to order the results by ascending local identifier.
     */
    private static final String ORDER_BY_ID_ASCENDING = CachedMovieEntry._ID + " ASC";

    @Override
    public AsyncTask<Integer, ?, ?> newFetchMovieTask() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getRestApiSortOrder() {
        return null;
    }

    @Override
    public String getMovieProviderSortOrder() {
        return ORDER_BY_ID_ASCENDING;
    }

    @Override
    public String getMovieProviderSelectionClause() {
        return CachedMovieEntry.COLUMN_USER_FAVORITE + " = ?";
    }

    @Override
    public String[] getMovieProviderSelectionArguments() {
        return new String[]{Integer.toString(BooleanUtils.toInteger(true))};
    }
}
