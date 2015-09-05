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

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;

import static mx.com.adolfogarcia.popularmovies.data.MovieContract.MovieEntry;

/**
 * Provides cached movie data, originally retrieved from
 * <a href="https://www.themoviedb.org">themoviedb.org</a>
 *
 * @author Jesús Adolfo García Pasquel
 */
public class MovieProvider extends ContentProvider {

    /**
     * Identifies a query for all movies.
     */
    static final int MOVIE = 100;

    /**
     * Identifies a query for a specific movie, by id.
     */
    static final int MOVIE_ID = 200;

    /**
     * Used to match URIs to queries and their result type.
     */
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    // Initialize the UriMatcher
    static {
        sUriMatcher.addURI(MovieContract.CONTENT_AUTHORITY
                , MovieContract.PATH_MOVIE
                , MovieProvider.MOVIE);
        sUriMatcher.addURI(MovieContract.CONTENT_AUTHORITY
                , MovieContract.PATH_MOVIE + "/#"
                , MovieProvider.MOVIE_ID);
    }

    /**
     * Used to get access an initialize the database.
     */
    private MovieDbHelper mOpenHelper;

    private static final SQLiteQueryBuilder sMovieQueryBuilder;

    static{
        sMovieQueryBuilder = new SQLiteQueryBuilder();
        sMovieQueryBuilder.setTables(MovieEntry.TABLE_NAME);
    }

    /**
     * Selection for a movie queried by id.
     */
    private static final String SELECTION_MOVIE_ID =
            MovieEntry.TABLE_NAME + "." + MovieEntry._ID + " = ? ";

    /**
     * Returns a new instance of {@link UriMatcher} that maps URIs for the movie
     * privider's to constants.
     *
     * @return a new instance of {@link UriMatcher} for use with the movie
     *     privider's URIs.
     * @see #MOVIE
     * @see #MOVIE_ID
     */
    static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY
                , MovieContract.PATH_MOVIE
                , MovieProvider.MOVIE);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY
                , MovieContract.PATH_MOVIE + "/#"
                , MovieProvider.MOVIE_ID);
        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case MOVIE:
                return MovieEntry.CONTENT_TYPE;
            case MOVIE_ID:
                return MovieEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri
            , String[] projection
            , String selection
            , String[] selectionArgs
            , String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case MOVIE:
                retCursor = getAllMovies(projection, sortOrder);
                break;
            case MOVIE_ID:
                retCursor = getMovieById(uri, projection);
                break;
            default:
                throw new UnsupportedOperationException("Unknown: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    /**
     * Queries the database for all registered movies.
     *
     * @param projection the columns to return.
     * @param sortOrder how the rows sould be ordered.
     * @return a {@link Cursor} for the result.
     */
    private Cursor getAllMovies(String[] projection, String sortOrder) {
        return sMovieQueryBuilder.query(
                mOpenHelper.getReadableDatabase()
                , projection
                , null // selection
                , null // selectionArgs
                , null // groupBy
                , null // having
                , sortOrder);
    }

    /**
     * Queries the database for the movie with the id contained in the URI.
     *
     * @param uri the URI used to query, containing the id of the movie.
     * @param projection the columns to return.
     * @return a {@link Cursor} for the result.
     */
    private Cursor getMovieById(Uri uri, String[] projection) {
        String id = Long.toString(ContentUris.parseId(uri));
        return sMovieQueryBuilder.query(
                mOpenHelper.getReadableDatabase()
                , projection
                , SELECTION_MOVIE_ID // selection
                , new String[] {id}  // selectionArgs
                , null // groupBy
                , null // having
                , null  // sortOrder
        );
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Uri resultUri;
        switch (sUriMatcher.match(uri)) {
            case MOVIE:
                long rowId = db.insert(MovieEntry.TABLE_NAME, null, values);
                if ( rowId != -1) {
                    resultUri = MovieEntry.buildMovieUri(rowId);
                } else {
                    throw new android.database.SQLException("Insertion failed. " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return resultUri;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        switch (sUriMatcher.match(uri)) {
            case MOVIE:
                db.beginTransaction();
                int insertionCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            insertionCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return insertionCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    public int update(Uri uri
            , ContentValues values
            , String selection
            , String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rowsAffected;
        switch (sUriMatcher.match(uri)) {
            case MOVIE:
                rowsAffected =
                        db.update(MovieEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown: " + uri);
        }
        // notify listeners
        if (rowsAffected > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsAffected;
    }

    @Override
    public int delete(Uri uri
            , String selection
            , String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rowsAffected;
        switch (sUriMatcher.match(uri)) {
            case MOVIE:
                rowsAffected =
                        db.delete(MovieEntry.TABLE_NAME
                                , selection
                                , selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown: " + uri);
        }
        // notify listeners
        if (rowsAffected > 0 || selection == null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsAffected;
    }

    @Override
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }

}
