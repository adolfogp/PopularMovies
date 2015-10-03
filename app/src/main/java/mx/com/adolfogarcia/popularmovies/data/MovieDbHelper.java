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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import static mx.com.adolfogarcia.popularmovies.data.MovieContract.CachedMovieEntry;
import static mx.com.adolfogarcia.popularmovies.data.MovieContract.CachedMovieVideoEntry;
import static mx.com.adolfogarcia.popularmovies.data.MovieContract.CachedMovieReviewEntry;

/**
 * Manages the creation and maintenance of the local movie database.
 *
 * @author Jesús Adolfo García Pasquel.
 */
public class MovieDbHelper extends SQLiteOpenHelper {

    /**
     * Version number of the dabase.
     */
    public static final int DATABASE_VERSION = 2;

    /**
     * Name of the SQLite database file.
     */
    public static final String DATABASE_NAME = "movie.db";

    /**
     * Statement used to create the table that holds the movie data.
     */
    private static final String SQL_CREATE_MOVIE_TABLE =
            "CREATE TABLE " + CachedMovieEntry.TABLE_NAME + " ("
            + CachedMovieEntry._ID + " INTEGER PRIMARY KEY, "
            + CachedMovieEntry.COLUMN_API_ID + " INTEGER, "
            + CachedMovieEntry.COLUMN_ORIGINAL_TITLE + " TEXT NOT NULL, "
            + CachedMovieEntry.COLUMN_RELEASE_DATE + " INTEGER, "
            + CachedMovieEntry.COLUMN_OVERVIEW + " TEXT, "
            + CachedMovieEntry.COLUMN_BACKDROP_PATH + " TEXT, "
            + CachedMovieEntry.COLUMN_POSTER_PATH + " TEXT, "
            + CachedMovieEntry.COLUMN_POPULARITY + " REAL NOT NULL, "
            + CachedMovieEntry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL, "
            + CachedMovieEntry.COLUMN_MOST_POPULAR + " BOOLEAN NOT NULL DEFAULT 0, "
            + CachedMovieEntry.COLUMN_HIGHEST_RATED + " BOOLEAN NOT NULL DEFAULT 0, "
            + CachedMovieEntry.COLUMN_USER_FAVORITE + " BOOLEAN NOT NULL DEFAULT 0, "
            + "UNIQUE (" + CachedMovieEntry.COLUMN_API_ID + ") ON CONFLICT REPLACE"
            + ");";
    // TODO: Instead of ON CONFLICT REPLACE, take care of conflicts on insertion.
    //       That is, use INSERT OR REPLACE when re-downloading data and
    //       mind keeping the value of columns: COLUMN_MOST_POPULAR,
    //       COLUMN_HIGHEST_RATED and COLUMN_USER_FAVORITE.
    //       Add tests to verify this is the case.


    /**
     * Statement used to create the table that holds the movie related
     * video data.
     */
    private static final String SQL_CREATE_MOVIE_VIDEO_TABLE =
            "CREATE TABLE " + CachedMovieVideoEntry.TABLE_NAME + " ("
            + CachedMovieVideoEntry._ID + " INTEGER PRIMARY KEY, "
            + CachedMovieVideoEntry.COLUMN_MOVIE_API_ID + " INTEGER NOT NULL, "
            + CachedMovieVideoEntry.COLUMN_API_ID + " TEXT NOT NULL, "
            + CachedMovieVideoEntry.COLUMN_LANGUAGE + " TEXT, "
            + CachedMovieVideoEntry.COLUMN_KEY + " TEXT, "
            + CachedMovieVideoEntry.COLUMN_NAME + " TEXT, "
            + CachedMovieVideoEntry.COLUMN_SITE + " TEXT, "
            + CachedMovieVideoEntry.COLUMN_SIZE + " INTEGER, "
            + CachedMovieVideoEntry.COLUMN_TYPE + " TEXT, "
            + "UNIQUE (" + CachedMovieVideoEntry.COLUMN_MOVIE_API_ID + ", "
                    + CachedMovieVideoEntry.COLUMN_API_ID + ") ON CONFLICT REPLACE"
            + ");";

    /**
     * Statement used to create the table that holds the movie reviews.
     */
    private static final String SQL_CREATE_MOVIE_REVIEW_TABLE =
            "CREATE TABLE " + CachedMovieReviewEntry.TABLE_NAME + " ("
            + CachedMovieReviewEntry._ID + " INTEGER PRIMARY KEY, "
            + CachedMovieReviewEntry.COLUMN_MOVIE_API_ID + " INTEGER NOT NULL, "
            + CachedMovieReviewEntry.COLUMN_API_ID + " TEXT NOT NULL, "
            + CachedMovieReviewEntry.COLUMN_AUTHOR + " TEXT, "
            + CachedMovieReviewEntry.COLUMN_CONTENT + " TEXT, "
            + CachedMovieReviewEntry.COLUMN_URL + " TEXT, "
            + "UNIQUE (" + CachedMovieReviewEntry.COLUMN_MOVIE_API_ID + ", "
                    + CachedMovieReviewEntry.COLUMN_API_ID + ") ON CONFLICT REPLACE"
            + ");";

    /**
     * Creates a new instance of {@link MovieDbHelper}.
     *
     * @param context {@link Context} to create or open the database with.
     */
    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_MOVIE_TABLE);
        db.execSQL(SQL_CREATE_MOVIE_VIDEO_TABLE);
        db.execSQL(SQL_CREATE_MOVIE_REVIEW_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + CachedMovieEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + CachedMovieVideoEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + CachedMovieReviewEntry.TABLE_NAME);
        onCreate(db);
    }

}
