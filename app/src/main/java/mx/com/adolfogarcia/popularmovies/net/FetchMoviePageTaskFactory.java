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

import mx.com.adolfogarcia.popularmovies.data.MovieContract;

/**
 * Implementations of this interface can create {@link android.os.AsyncTask}s
 * capable of retrieving  pages of movies from
 * <a href="https://www.themoviedb.org/">themoviedb.org</a>'s RESTful API ordered
 * by certain criteria, and inserts them into
 * {@link mx.com.adolfogarcia.popularmovies.data.MovieProvider}.
 *
 * @author Jesús Adolfo García Pasquel
 */
public interface FetchMoviePageTaskFactory {

    /**
     * Returns a new instance of {@link AsyncTask} that receives as parameter
     * the number of the page of movies to download, and upon execution
     * retrieves them and inserts them into
     * {@link mx.com.adolfogarcia.popularmovies.data.MovieProvider}.
     *
     * @return a new instance of {@link AsyncTask} that receives as parameter
     *     the number of the page of movies to download, and upon execution
     *     retrieves them and inserts them into
     *     {@link mx.com.adolfogarcia.popularmovies.data.MovieProvider}.
     */
    AsyncTask<Integer, ?, ?> newFetchMovieTask();

    /**
     * Returns an order clause to be used on
     * {@link mx.com.adolfogarcia.popularmovies.data.MovieProvider}, that is
     * equivalent to the criteria by which the movies retrieved by
     * {@link #newFetchMovieTask()} are ordered.
     *
     * @return an equivalente order clause, that may be used on
     *     {@link mx.com.adolfogarcia.popularmovies.data.MovieProvider}.
     */
    String getMovieSortOrder();

}
