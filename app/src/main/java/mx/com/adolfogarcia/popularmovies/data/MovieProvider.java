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
import android.net.wifi.WifiEnterpriseConfig;
import android.support.annotation.NonNull;

import static mx.com.adolfogarcia.popularmovies.data.MovieContract.CachedMovieEntry;
import static mx.com.adolfogarcia.popularmovies.data.MovieContract.CachedMovieVideoEntry;
import static mx.com.adolfogarcia.popularmovies.data.MovieContract.CachedMovieReviewEntry;

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
     * Identifies a query for all the cached videos of all movies.
     */
    static final int CACHED_VIDEO = 300;


    /**
     * Identifies a query for the videos of a specific cached movie, by the
     * movie's identifier in
     * <a href="https://www.themoviedb.org/">themoviedb.org</a>'s RESTful API.
     */
    static final int CACHED_MOVIE_VIDEO = 310;

    /**
     * Identifies a query for a specific cached movie video by id.
     */
    static final int CACHED_VIDEO_ID = 400;

    /**
     * Identifies a query for all the reviews of all movies.
     */
    static final int CACHED_REVIEW = 500;

    /**
     * Identifies a query for the reviews of a specific cached movie, by the movie's identifier in
     * <a href="https://www.themoviedb.org/">themoviedb.org</a>'s RESTful API.
     */
    static final int CACHED_MOVIE_REVIEW = 510;

    /**
     * Identifies a query for a specific cached movie review by id.
     */
    static final int CACHED_REVIEW_ID = 600;


    /**
     * Selection for a cached movie queried by id.
     */
    private static final String SELECTION_CACHED_MOVIE_ID =
            CachedMovieEntry.TABLE_NAME + "." + CachedMovieEntry._ID + " = ? ";

    /**
     * Selection for a cached movie video queried by id.
     */
    private static final String SELECTION_CACHED_VIDEO_ID =
            CachedMovieVideoEntry.TABLE_NAME + "." + CachedMovieVideoEntry._ID + " = ? ";

    /**
     * Selection for a cached movie review queried by id.
     */
    private static final String SELECTION_CACHED_REVIEW_ID =
            CachedMovieReviewEntry.TABLE_NAME + "." + CachedMovieReviewEntry._ID + " = ? ";

    /**
     * Selection for the videos of a specific cached movie, queried by the
     * movie's identifier in
     * <a href="https://www.themoviedb.org/">themoviedb.org</a>'s RESTful API.
     */
    private static final String SELECTION_CACHED_MOVIE_VIDEOS =
            CachedMovieEntry.TABLE_NAME + "." + CachedMovieEntry._ID + " = ? ";

    /**
     * Selection for the reviews of a specific cached movie, queried by the
     * movie's identifier in
     * <a href="https://www.themoviedb.org/">themoviedb.org</a>'s RESTful API.
     */
    private static final String SELECTION_CACHED_MOVIE_REVIEWS =
            CachedMovieEntry.TABLE_NAME + "." + CachedMovieEntry._ID + " = ? ";

    /**
     * Used to match URIs to queries and their result type.
     */
    private static UriMatcher sUriMatcher = buildUriMatcher();

    /**
     * Used to query cached movie data.
     */
    private static SQLiteQueryBuilder sMovieQueryBuilder;

    static {
        sMovieQueryBuilder = new SQLiteQueryBuilder();
        sMovieQueryBuilder.setTables(CachedMovieEntry.TABLE_NAME);
    }

    /**
     * Used to query cached movie video data.
     */
    private static SQLiteQueryBuilder sVideoQueryBuilder;

    static {
        sVideoQueryBuilder = new SQLiteQueryBuilder();
        sVideoQueryBuilder.setTables(CachedMovieVideoEntry.TABLE_NAME);
    }

    /**
     * Used to query cached movie review data.
     */
    private static SQLiteQueryBuilder sReviewQueryBuilder;

    static {
        sReviewQueryBuilder = new SQLiteQueryBuilder();
        sReviewQueryBuilder.setTables(CachedMovieReviewEntry.TABLE_NAME);
    }

    /**
     * Usedto query videos for a particular cached movie.
     */
    private static SQLiteQueryBuilder sMovieVideoQueryBuilder;

    static{
        sMovieVideoQueryBuilder = new SQLiteQueryBuilder();
        sMovieVideoQueryBuilder.setTables(
                CachedMovieEntry.TABLE_NAME
                + " INNER JOIN "
                + CachedMovieVideoEntry.TABLE_NAME
                + " ON " + CachedMovieEntry.TABLE_NAME
                + "." + CachedMovieEntry.COLUMN_API_ID
                + " = " + CachedMovieVideoEntry.TABLE_NAME
                + "." + CachedMovieVideoEntry.COLUMN_MOVIE_API_ID);
    }

    /**
     * Usedto query videos for a particular cached movie.
     */
    private static SQLiteQueryBuilder sMovieReviewQueryBuilder;

    static{
        sMovieReviewQueryBuilder = new SQLiteQueryBuilder();
        sMovieReviewQueryBuilder.setTables(
                CachedMovieEntry.TABLE_NAME
                + " INNER JOIN "
                + CachedMovieReviewEntry.TABLE_NAME
                + " ON " + CachedMovieEntry.TABLE_NAME
                + "." + CachedMovieEntry.COLUMN_API_ID
                + " = " + CachedMovieReviewEntry.TABLE_NAME
                + "." + CachedMovieReviewEntry.COLUMN_MOVIE_API_ID);
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
     * @see #CACHED_VIDEO
     * @see #CACHED_VIDEO_ID
     * @see #CACHED_REVIEW
     * @see #CACHED_REVIEW_ID
     */
    static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY
                , MovieContract.PATH_MOVIE
                , MovieProvider.CACHED_MOVIE);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY
                , MovieContract.PATH_MOVIE + "/#"
                , MovieProvider.CACHED_MOVIE_ID);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY
                , MovieContract.PATH_MOVIE_VIDEO
                , MovieProvider.CACHED_VIDEO);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY
                , MovieContract.PATH_MOVIE_VIDEO + "/#"
                , MovieProvider.CACHED_VIDEO_ID);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY
                , MovieContract.PATH_MOVIE_REVIEW
                , MovieProvider.CACHED_REVIEW);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY
                , MovieContract.PATH_MOVIE_REVIEW + "/#"
                , MovieProvider.CACHED_REVIEW_ID);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY
                , MovieContract.PATH_MOVIE + "/#/" + MovieContract.PATH_MOVIE_VIDEO
                , MovieProvider.CACHED_MOVIE_VIDEO);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY
                , MovieContract.PATH_MOVIE + "/#/" + MovieContract.PATH_MOVIE_REVIEW
                , MovieProvider.CACHED_MOVIE_REVIEW);
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
            case CACHED_VIDEO:
                return CachedMovieVideoEntry.CONTENT_TYPE;
            case CACHED_VIDEO_ID:
                return CachedMovieVideoEntry.CONTENT_ITEM_TYPE;
            case CACHED_REVIEW:
                return CachedMovieReviewEntry.CONTENT_TYPE;
            case CACHED_REVIEW_ID:
                return CachedMovieReviewEntry.CONTENT_ITEM_TYPE;
            case CACHED_MOVIE_VIDEO:
                return CachedMovieVideoEntry.CONTENT_TYPE;
            case CACHED_MOVIE_REVIEW:
                return CachedMovieReviewEntry.CONTENT_TYPE;
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
            case CACHED_VIDEO:
                retCursor = getAllVideos(projection, selection, selectionArgs, sortOrder);
                break;
            case CACHED_VIDEO_ID:
                retCursor = getVideoById(uri, projection);
                break;
            case CACHED_REVIEW:
                retCursor = getAllReviews(projection, selection, selectionArgs, sortOrder);
                break;
            case CACHED_REVIEW_ID:
                retCursor = getReviewById(uri, projection);
                break;
            case CACHED_MOVIE_VIDEO:
                retCursor = getMovieVideos(uri, projection);
                break;
            case CACHED_MOVIE_REVIEW:
                retCursor = getMovieReviews(uri, projection);
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
        return sMovieQueryBuilder.query(
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
        return sMovieQueryBuilder.query(
                mOpenHelper.getReadableDatabase()
                , projection
                , SELECTION_CACHED_MOVIE_ID // selection
                , new String[] {id}  // selectionArgs
                , null // groupBy
                , null // having
                , null  // sortOrder
        );
    }

    /**
     * Queries the database for all registered movie videos.
     *
     * @param projection the columns to return.
     * @param selection the <i>WHERE</i> clause.
     * @param selectionArgs the values for the arguments used in {@code selection}.
     * @param sortOrder how the rows sould be ordered.
     * @return a {@link Cursor} for the result.
     */
    private Cursor getAllVideos(String[] projection
            , String selection
            , String[] selectionArgs
            , String sortOrder) {
        return sVideoQueryBuilder.query(
                mOpenHelper.getReadableDatabase()
                , projection
                , selection
                , selectionArgs
                , null // groupBy
                , null // having
                , sortOrder);
    }

    /**
     * Queries the database for the movie video with the id contained in the URI.
     *
     * @param uri the URI used to query, containing the id of the movie video.
     * @param projection the columns to return.
     * @return a {@link Cursor} for the result.
     */
    private Cursor getVideoById(Uri uri, String[] projection) {
        String id = Long.toString(ContentUris.parseId(uri));
        return sVideoQueryBuilder.query(
                mOpenHelper.getReadableDatabase()
                , projection
                , SELECTION_CACHED_VIDEO_ID // selection
                , new String[] {id}  // selectionArgs
                , null // groupBy
                , null // having
                , null  // sortOrder
        );
    }

    /**
     * Queries the database for all registered movie reviews.
     *
     * @param projection the columns to return.
     * @param selection the <i>WHERE</i> clause.
     * @param selectionArgs the values for the arguments used in {@code selection}.
     * @param sortOrder how the rows sould be ordered.
     * @return a {@link Cursor} for the result.
     */
    private Cursor getAllReviews(String[] projection
            , String selection
            , String[] selectionArgs
            , String sortOrder) {
        return sReviewQueryBuilder.query(
                mOpenHelper.getReadableDatabase()
                , projection
                , selection
                , selectionArgs
                , null // groupBy
                , null // having
                , sortOrder);
    }

    /**
     * Queries the database for the movie review with the id contained in the URI.
     *
     * @param uri the URI used to query, containing the id of the movie review.
     * @param projection the columns to return.
     * @return a {@link Cursor} for the result.
     */
    private Cursor getReviewById(Uri uri, String[] projection) {
        String id = Long.toString(ContentUris.parseId(uri));
        return sReviewQueryBuilder.query(
                mOpenHelper.getReadableDatabase()
                , projection
                , SELECTION_CACHED_REVIEW_ID // selection
                , new String[] {id}  // selectionArgs
                , null // groupBy
                , null // having
                , null  // sortOrder
        );
    }

    /**
     * Queries the database for all the videos related to a movie with the
     * movie's id contained in the URI.
     *
     * @param uri the URI used to query, containing the id of the movie.
     * @param projection the columns to return.
     * @return a {@link Cursor} for the result.
     */
    private Cursor getMovieVideos(Uri uri, String[] projection) {
        String id = Long.toString(CachedMovieEntry.getMovieIdFromUri(uri));
        return sMovieVideoQueryBuilder.query(
                mOpenHelper.getReadableDatabase()
                , projection
                , SELECTION_CACHED_MOVIE_VIDEOS
                , new String[] {id}
                , null // groupBy
                , null // having
                , null  // sortOrder
        );
    }

    /**
     * Queries the database for all the reviews related to a movie with the
     * movie's id contained in the URI.
     *
     * @param uri the URI used to query, containing the id of the movie.
     * @param projection the columns to return.
     * @return a {@link Cursor} for the result.
     */
    private Cursor getMovieReviews(Uri uri, String[] projection) {
        String id = Long.toString(CachedMovieEntry.getMovieIdFromUri(uri));
        return sMovieReviewQueryBuilder.query(
                mOpenHelper.getReadableDatabase()
                , projection
                , SELECTION_CACHED_MOVIE_REVIEWS
                , new String[] {id}
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
            case CACHED_VIDEO:
                rowId = db.insert(CachedMovieVideoEntry.TABLE_NAME, null, values);
                if (rowId != -1) {
                    resultUri = CachedMovieVideoEntry.buildMovieVideoUri(rowId);
                } else {
                    throw new android.database.SQLException("Insertion failed. " + uri);
                }
                break;
            case CACHED_REVIEW:
                rowId = db.insert(CachedMovieReviewEntry.TABLE_NAME, null, values);
                if (rowId != -1) {
                    resultUri = CachedMovieReviewEntry.buildMovieReviewUri(rowId);
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
        switch (sUriMatcher.match(uri)) {
            case CACHED_MOVIE:
                return bulkInsert(uri, values, CachedMovieEntry.TABLE_NAME);
            case CACHED_VIDEO:
                return bulkInsert(uri, values, CachedMovieVideoEntry.TABLE_NAME);
            case CACHED_REVIEW:
                return bulkInsert(uri, values, CachedMovieReviewEntry.TABLE_NAME);
            default:
                return super.bulkInsert(uri, values);
        }
    }

    private int bulkInsert(@NonNull Uri uri
            , @NonNull ContentValues[] values
            , @NonNull String table) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        db.beginTransaction();
        int insertionCount = 0;
        try {
            for (ContentValues value : values) {
                long id = db.insert(table, null, value);
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
                        db.update(CachedMovieEntry.TABLE_NAME
                                , values, selection, selectionArgs);
                break;
            case CACHED_VIDEO:
                rowsAffected =
                        db.update(CachedMovieVideoEntry.TABLE_NAME
                                , values, selection, selectionArgs);
                break;
            case CACHED_REVIEW:
                rowsAffected =
                        db.update(CachedMovieReviewEntry.TABLE_NAME
                                , values, selection, selectionArgs);
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
            case CACHED_VIDEO:
                rowsAffected =
                        db.delete(CachedMovieVideoEntry.TABLE_NAME
                                , selection
                                , selectionArgs);
                break;
            case CACHED_REVIEW:
                rowsAffected =
                        db.delete(CachedMovieReviewEntry.TABLE_NAME
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
