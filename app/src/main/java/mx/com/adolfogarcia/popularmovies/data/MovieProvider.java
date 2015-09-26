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

import static mx.com.adolfogarcia.popularmovies.data.MovieContract.CachedMovieEntry;

/**
 * Provides access to the data used by the application. This includes cached
 * movie data, originally retrieved from
 * <a href="https://www.themoviedb.org">themoviedb.org</a>
 *
 * @author Jesús Adolfo García Pasquel
 */
public class MovieProvider extends ContentProvider {

    /**
     * Identifies a query for all cached movies.
     */
    static final int CACHED_MOVIE = 100;

    /**
     * Identifies a query for a specific cached movie, by id.
     */
    static final int CACHED_MOVIE_ID = 200;

    /**
     * Selection for a cached movie queried by id.
     */
    private static final String SELECTION_CACHED_MOVIE_ID =
            CachedMovieEntry.TABLE_NAME + "." + CachedMovieEntry._ID + " = ? ";

    /**
     * Used to match URIs to queries and their result type.
     */
    private static UriMatcher sUriMatcher = buildUriMatcher();

    /**
     * Used to query cached movie data.
     */
    private static SQLiteQueryBuilder sCachedMovieQueryBuilder;

    static {
        sCachedMovieQueryBuilder = new SQLiteQueryBuilder();
        sCachedMovieQueryBuilder.setTables(CachedMovieEntry.TABLE_NAME);
    }

    /**
     * Used to get access and initialize the database.
     */
    private MovieDbHelper mOpenHelper;

    /**
     * Returns a new instance of {@link UriMatcher} that maps URIs to the
     * equivalent constants used by the provider.
     *
     * @return a new instance of {@link UriMatcher}.
     * @see #CACHED_MOVIE
     * @see #CACHED_MOVIE_ID
     */
    static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY
                , MovieContract.PATH_CACHED_MOVIE
                , MovieProvider.CACHED_MOVIE);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY
                , MovieContract.PATH_CACHED_MOVIE + "/#"
                , MovieProvider.CACHED_MOVIE_ID);
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
            case CACHED_MOVIE:
                return CachedMovieEntry.CONTENT_TYPE;
            case CACHED_MOVIE_ID:
                return CachedMovieEntry.CONTENT_ITEM_TYPE;
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
            case CACHED_MOVIE:
                retCursor = getAllMovies(projection, selection, selectionArgs, sortOrder);
                break;
            case CACHED_MOVIE_ID:
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
     * @param selection the <i>WHERE</i> clause.
     * @param selectionArgs the values for the arguments used in {@code selection}.
     * @param sortOrder how the rows sould be ordered.
     * @return a {@link Cursor} for the result.
     */
    private Cursor getAllMovies(String[] projection
            , String selection
            , String[] selectionArgs
            , String sortOrder) {
        return sCachedMovieQueryBuilder.query(
                mOpenHelper.getReadableDatabase()
                , projection
                , selection
                , selectionArgs
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
        return sCachedMovieQueryBuilder.query(
                mOpenHelper.getReadableDatabase()
                , projection
                , SELECTION_CACHED_MOVIE_ID // selection
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
            case CACHED_MOVIE:
                long rowId = db.insert(CachedMovieEntry.TABLE_NAME, null, values);
                if (rowId != -1) {
                    resultUri = CachedMovieEntry.buildMovieUri(rowId);
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
            case CACHED_MOVIE:
                db.beginTransaction();
                int insertionCount = 0;
                try {
                    for (ContentValues value : values) {
                        long id = db.insert(CachedMovieEntry.TABLE_NAME, null, value);
                        if (id != -1) {
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
            case CACHED_MOVIE:
                rowsAffected =
                        db.update(CachedMovieEntry.TABLE_NAME, values, selection, selectionArgs);
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
            case CACHED_MOVIE:
                rowsAffected =
                        db.delete(CachedMovieEntry.TABLE_NAME
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
