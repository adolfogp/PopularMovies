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

/**
 * Manages the creation and maintenance of the local movie database.
 *
 * @author Jesús Adolfo García Pasquel.
 */
public class MovieDbHelper extends SQLiteOpenHelper {

    /**
     * Version number of the dabase.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Name of the SQLite database file.
     */
    static final String DATABASE_NAME = "movie.db";

    /**
     * Statement used to create the table that holds the movie data.
     */
    private static final String SQL_CREATE_MOVIE_TABLE =
            "CREATE TABLE " + CachedMovieEntry.TABLE_NAME + " ("
            + CachedMovieEntry._ID + " INTEGER, "
            + CachedMovieEntry.COLUMN_ORIGINAL_TITLE + " TEXT NOT NULL, "
            + CachedMovieEntry.COLUMN_RELEASE_DATE + " INTEGER, "
            + CachedMovieEntry.COLUMN_OVERVIEW + " TEXT, "
            + CachedMovieEntry.COLUMN_BACKDROP_PATH + " TEXT, "
            + CachedMovieEntry.COLUMN_POSTER_PATH + " TEXT, "
            + CachedMovieEntry.COLUMN_POPULARITY + " REAL NOT NULL, "
            + CachedMovieEntry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL, "
            + "PRIMARY KEY (" + CachedMovieEntry._ID + ") ON CONFLICT REPLACE"
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
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + CachedMovieEntry.TABLE_NAME);
        onCreate(db);
    }

}
