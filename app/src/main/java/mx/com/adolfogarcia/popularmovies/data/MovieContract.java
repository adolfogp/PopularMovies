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

package mx.com.adolfogarcia.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * The tables and columns for the movie database.
 *
 * @author Jesús Adolfo García Pasquel
 */
public final class MovieContract {

    /**
     * Identifies the content provider.
     */
    public static final String CONTENT_AUTHORITY =
            "mx.com.adolfogarcia.popularmovies.provider";

    /**
     * Base for all of this app's content provider URIs.
     */
    public static final Uri BASE_CONTENT_URI =
            Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Path for cached movie data.
     */
    public static final String PATH_CACHED_MOVIE = "cached_movie";

    /**
     * This class only provides constants and utility methods.
     */
    private MovieContract() {
        // Empty constructor
    }

    /**
     * Defines the contents of the table holding cached movie data.
     *
     * @author Jesús Adolfo García Pasquel
     * @see <a href="http://docs.themoviedb.apiary.io/">The Movie Database API</a>.
     */
    public static final class CachedMovieEntry implements BaseColumns {

        /**
         * Base URI for cached movie data.
         */
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CACHED_MOVIE).build();

        /**
         * Type for {@code content:} URIs with a directories of chached movies.
         */
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/" + CONTENT_AUTHORITY + "/" + PATH_CACHED_MOVIE;

        /**
         * Type for {@code content:} URIs with a single cached movie.
         */
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + "/" + CONTENT_AUTHORITY + "/" + PATH_CACHED_MOVIE;

        /**
         * Name of the table containing cached movie data.
         */
        public static final String TABLE_NAME = "cached_movie";

        /**
         * The movie's id in
         * <a href="https://www.themoviedb.org/">themoviedb.org</a>'s RESTful API.
         */
        public static final String COLUMN_API_ID = "api_id";

        /**
         * The movie's original title.
         */
        public static final String COLUMN_ORIGINAL_TITLE = "original_title";

        /**
         * The movie's release date. A {@code long} representing the date
         * in Epock time format with milliseconds and UTC time zone.
         */
        public static final String COLUMN_RELEASE_DATE = "release_date";

        /**
         * The movie's plot synopsis.
         */
        public static final String COLUMN_OVERVIEW = "overview";

        /**
         * Relative path to the backdrop image.
         */
        public static final String COLUMN_BACKDROP_PATH = "backdrop_path";

        /**
         * Relative path to the poster image.
         */
        public static final String COLUMN_POSTER_PATH = "poster_path";

        /**
         * Popularity rating.
         */
        public static final String COLUMN_POPULARITY = "popularity";

        /**
         * User rating.
         */
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";

        /**
         * Indicates that the move appears in the pages for most popular movies
         * downloaded so far.
         */
        public static final String COLUMN_MOST_POPULAR = "most_popular";

        /**
         * Indicates that the move appears in the pages for highest rated movies
         * downloaded so far.
         */
        public static final String COLUMN_HIGHEST_RATED = "highest_rated";

        /**
         * Indicates that the movie was marked as favorite by the user.
         */
        public static final String COLUMN_USER_FAVORITE = "user_favorite";

        /**
         * Returns the URI for a particular movie given its id.
         *
         * @param id the movie's identifier.
         * @return the URI for the movie with the specified id.
         */
        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }

}
