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

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.AndroidTestCase;

import static mx.com.adolfogarcia.popularmovies.data.MovieContract.CachedMovieEntry;
import static mx.com.adolfogarcia.popularmovies.data.MovieContract.CachedMovieVideoEntry;
import static mx.com.adolfogarcia.popularmovies.data.MovieContract.CachedMovieReviewEntry;

import junit.framework.Assert;

import java.util.UUID;

/**
 * Test cases for {@link MovieProvider}.  The code is based on
 * {@code TestProvider} from the sample application <i>Sunshine</i>.
 *
 * @author Jesús Adolfo García Pasquel
 */
public class MovieProviderTest extends AndroidTestCase {

    /**
     * Number of records to insert while testing
     * {@link MovieProvider#bulkInsert(Uri, ContentValues[])}.
     */
    private static final int BULK_INSERT_NUMBER_OF_RECORDS = 10;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllMovieVideos();
        deleteAllMovieReviews();
        deleteAllMovies();
    }

    /**
     * Deletes all movie video entries in the {@link MovieProvider}, and verifies
     * none remain.
     */
    public void deleteAllMovieVideos() {
        mContext.getContentResolver().delete(
                CachedMovieVideoEntry.CONTENT_URI
                , null
                , null);

        Cursor cursor = mContext.getContentResolver().query(
                CachedMovieVideoEntry.CONTENT_URI
                , null
                , null
                , null
                , null);
        Assert.assertEquals("No movie video entries must remain"
                , 0, cursor.getCount());
        cursor.close();
    }

    /**
     * Deletes all movie review entries in the {@link MovieProvider}, and verifies
     * none remain.
     */
    public void deleteAllMovieReviews() {
        mContext.getContentResolver().delete(
                CachedMovieReviewEntry.CONTENT_URI
                , null
                , null);

        Cursor cursor = mContext.getContentResolver().query(
                CachedMovieReviewEntry.CONTENT_URI
                , null
                , null
                , null
                , null);
        Assert.assertEquals("No movie review entries must remain"
                , 0, cursor.getCount());
        cursor.close();
    }

    /**
     * Deletes all movie entries in the {@link MovieProvider}, and verifies
     * none remain.
     */
    public void deleteAllMovies() {
        mContext.getContentResolver().delete(
                CachedMovieEntry.CONTENT_URI
                , null
                , null);

        Cursor cursor = mContext.getContentResolver().query(
                CachedMovieEntry.CONTENT_URI
                , null
                , null
                , null
                , null);
        Assert.assertEquals("No movie entries must remain", 0, cursor.getCount());
        cursor.close();
    }

    /**
     * Verifies that {@link MovieProvider#buildUriMatcher()} creates an
     * {@link android.content.UriMatcher} that maps correctly the URIs to
     * the expected constants. Case for the URI that identifies all movies.
     */
    public void testBuildUriMatcher_allMovies() {
        UriMatcher testMatcher = MovieProvider.buildUriMatcher();
        Assert.assertEquals("All movies URI must match expected constant."
                , testMatcher.match(CachedMovieEntry.CONTENT_URI)
                , MovieProvider.CACHED_MOVIE);
    }

    /**
     * Verifies that {@link MovieProvider#buildUriMatcher()} creates an
     * {@link android.content.UriMatcher} that maps correctly the URIs to
     * the expected constants. Case for the URI that identifies a single movie.
     */
    public void testBuildUriMatcher_singleMovie() {
        UriMatcher testMatcher = MovieProvider.buildUriMatcher();
        Assert.assertEquals("Single movie URI must match expected constant."
                , testMatcher.match(CachedMovieEntry.buildMovieUri(1))
                , MovieProvider.CACHED_MOVIE_ID);
    }

    /**
     * Verifies that {@link MovieProvider#buildUriMatcher()} creates an
     * {@link android.content.UriMatcher} that maps correctly the URIs to
     * the expected constants. Case for the URI that identifies all the
     * videos related to all movie.
     */
    public void testBuildUriMatcher_allVideos() {
        UriMatcher testMatcher = MovieProvider.buildUriMatcher();
        Assert.assertEquals(
                "All videos of a movie URI must match expected constant."
                , testMatcher.match(CachedMovieVideoEntry.CONTENT_URI)
                , MovieProvider.CACHED_VIDEO);
    }

    /**
     * Verifies that {@link MovieProvider#buildUriMatcher()} creates an
     * {@link android.content.UriMatcher} that maps correctly the URIs to
     * the expected constants. Case for the URI that identifies all the
     * reviews of all movies.
     */
    public void testBuildUriMatcher_allReviews() {
        UriMatcher testMatcher = MovieProvider.buildUriMatcher();
        Assert.assertEquals("All movies URI must match expected constant."
                , testMatcher.match(CachedMovieReviewEntry.CONTENT_URI)
                , MovieProvider.CACHED_REVIEW);
    }

    /**
     * Verifies that {@link MovieProvider} is properly registered.
     */
    public void testProviderRegistry() {
        PackageManager pm = mContext.getPackageManager();
        ComponentName componentName = new ComponentName(mContext.getPackageName(),
                MovieProvider.class.getName());
        try {
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);
            Assert.assertEquals(
                    "The MovieProvider's authority must be as in the contract."
                    , MovieContract.CONTENT_AUTHORITY
                    , providerInfo.authority);
        } catch (PackageManager.NameNotFoundException e) {
            Assert.fail("MovieProvider not registered at " + mContext.getPackageName());
        }
    }

    /**
     * Verifies that {@link MovieProvider#getType(Uri)} works properly.
     * Case for the URI that identifies all movies.
     */
    public void testGetType_allMovies() {
        String type = mContext.getContentResolver().getType(
                CachedMovieEntry.CONTENT_URI);
        Assert.assertEquals(
                "The content type for the 'all movies' URI must be of type directory"
                , CachedMovieEntry.CONTENT_TYPE, type);
    }

    /**
     * Verifies that {@link MovieProvider#getType(Uri)} works properly.
     * Case for the URI that identifies a single movie.
     */
    public void testGetType_singleMovies() {
        String type = mContext.getContentResolver().getType(
                CachedMovieEntry.buildMovieUri(1));
        Assert.assertEquals(
                "The content type for the URI of a single movie must be of type entry"
                , CachedMovieEntry.CONTENT_ITEM_TYPE, type);
    }

    /**
     * Verifies that {@link MovieProvider#getType(Uri)} works properly.
     * Case for the URI that identifies all movie videos.
     */
    public void testGetType_allVideos() {
        String type = mContext.getContentResolver().getType(
                CachedMovieVideoEntry.CONTENT_URI);
        Assert.assertEquals(
                "The content type for the 'all videos' URI must be of type directory"
                , CachedMovieVideoEntry.CONTENT_TYPE, type);
    }

    /**
     * Verifies that {@link MovieProvider#getType(Uri)} works properly.
     * Case for the URI that identifies a single movie video.
     */
    public void testGetType_singleVideo() {
        String type = mContext.getContentResolver().getType(
                CachedMovieVideoEntry.buildMovieVideoUri(1));
        Assert.assertEquals(
                "The content type for the URI of a single video must be of type entry"
                , CachedMovieVideoEntry.CONTENT_ITEM_TYPE, type);
    }

    /**
     * Verifies that {@link MovieProvider#getType(Uri)} works properly.
     * Case for the URI that identifies all movie reviews.
     */
    public void testGetType_allReviews() {
        String type = mContext.getContentResolver().getType(
                CachedMovieReviewEntry.CONTENT_URI);
        Assert.assertEquals(
                "The content type for the 'all reviews' URI must be of type directory"
                , CachedMovieReviewEntry.CONTENT_TYPE, type);
    }

    /**
     * Verifies that {@link MovieProvider#getType(Uri)} works properly.
     * Case for the URI that identifies a single movie review.
     */
    public void testGetType_singleReview() {
        String type = mContext.getContentResolver().getType(
                CachedMovieReviewEntry.buildMovieReviewUri(1));
        Assert.assertEquals(
                "The content type for the URI of a single review must be of type entry"
                , CachedMovieReviewEntry.CONTENT_ITEM_TYPE, type);
    }

    /**
     * Verifies that {@link MovieProvider#getType(Uri)} works properly.
     * Case for the URI that identifies the videos related to a particular movie.
     */
    public void testGetType_movieVideos() {
        String type = mContext.getContentResolver().getType(
                CachedMovieEntry.buildMovieVideosUri(1));
        Assert.assertEquals(
                "The content type for the URI of a movie's videos must be of type directory"
                , CachedMovieVideoEntry.CONTENT_TYPE, type);
    }

    /**
     * Verifies that {@link MovieProvider#getType(Uri)} works properly.
     * Case for the URI that identifies the reviews related to a particular movie.
     */
    public void testGetType_movieReviews() {
        String type = mContext.getContentResolver().getType(
                CachedMovieEntry.buildMovieReviewsUri(1));
        Assert.assertEquals(
                "The content type for the URI of a movie's reviews must be of type directory"
                , CachedMovieReviewEntry.CONTENT_TYPE, type);
    }

    /**
     * Verifies that
     * {@link MovieProvider#query(Uri, String[], String, String[], String)}
     * works properly. Case for a query of all movies.
     */
    public void testQuery_allMovies() {
        // insert test records directly into the database
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestUtilities.createMadMaxMovieValues();
        long rowId = TestUtilities.insertMadMaxMovieValues(mContext);
        db.close();

        Cursor cursor = mContext.getContentResolver().query(
                CachedMovieEntry.CONTENT_URI
                , null
                , null
                , null
                , null);
        cursor.moveToFirst();
        TestUtilities.assertRowEquals(testValues, cursor);
    }

    /**
     * Verifies that
     * {@link MovieProvider#query(Uri, String[], String, String[], String)}
     * works properly. Case for a single movie.
     */
    public void testQuery_singleMovie() {
        // insert test records directly into the database
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestUtilities.createMadMaxMovieValues();
        long rowId = TestUtilities.insertMadMaxMovieValues(mContext);
        db.close();

        Cursor cursor = mContext.getContentResolver().query(
                CachedMovieEntry.buildMovieUri(rowId)
                , null
                , null
                , null
                , null);
        cursor.moveToFirst();
        TestUtilities.assertRowEquals(TestUtilities.createMadMaxMovieValues()
                , cursor);
    }

    /**
     * Verifies that
     * {@link MovieProvider#query(Uri, String[], String, String[], String)}
     * works properly. Case for a query of all movie videos.
     */
    public void testQuery_allVideos() {
        // insert test records directly into the database
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestUtilities.createMadMaxMovieVideoValues();
        long rowId = TestUtilities.insertMadMaxMovieVideoValues(mContext);
        db.close();

        Cursor cursor = mContext.getContentResolver().query(
                CachedMovieVideoEntry.CONTENT_URI
                , null
                , null
                , null
                , null);
        cursor.moveToFirst();
        TestUtilities.assertRowEquals(testValues, cursor);
    }

    /**
     * Verifies that
     * {@link MovieProvider#query(Uri, String[], String, String[], String)}
     * works properly. Case for a single movie video.
     */
    public void testQuery_singleVideo() {
        // insert test records directly into the database
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestUtilities.createMadMaxMovieVideoValues();
        long rowId = TestUtilities.insertMadMaxMovieVideoValues(mContext);
        db.close();

        Cursor cursor = mContext.getContentResolver().query(
                CachedMovieVideoEntry.buildMovieVideoUri(rowId)
                , null
                , null
                , null
                , null);
        cursor.moveToFirst();
        TestUtilities.assertRowEquals(testValues, cursor);
    }

    /**
     * Verifies that
     * {@link MovieProvider#query(Uri, String[], String, String[], String)}
     * works properly. Case for a query of all movie reviews.
     */
    public void testQuery_allReviews() {
        // insert test records directly into the database
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestUtilities.createMadMaxMovieReviewValues();
        long rowId = TestUtilities.insertMadMaxMovieReviewValues(mContext);
        db.close();

        Cursor cursor = mContext.getContentResolver().query(
                CachedMovieReviewEntry.CONTENT_URI
                , null
                , null
                , null
                , null);
        cursor.moveToFirst();
        TestUtilities.assertRowEquals(testValues, cursor);
    }

    /**
     * Verifies that
     * {@link MovieProvider#query(Uri, String[], String, String[], String)}
     * works properly. Case for a single movie review.
     */
    public void testQuery_singleReview() {
        // insert test records directly into the database
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestUtilities.createMadMaxMovieReviewValues();
        long rowId = TestUtilities.insertMadMaxMovieReviewValues(mContext);
        db.close();

        Cursor cursor = mContext.getContentResolver().query(
                CachedMovieReviewEntry.buildMovieReviewUri(rowId)
                , null
                , null
                , null
                , null);
        cursor.moveToFirst();
        TestUtilities.assertRowEquals(testValues, cursor);
    }

    /**
     * Verifies that
     * {@link MovieProvider#query(Uri, String[], String, String[], String)}
     * works properly. Case for the videos related to a movie.
     */
    public void testQuery_movieVideos() {
        // insert test records directly into the database
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long rowId = TestUtilities.insertMadMaxMovieValues(mContext);
        TestUtilities.insertMadMaxMovieVideoValues(mContext);
        db.close();

        Cursor cursor = mContext.getContentResolver().query(
                CachedMovieEntry.buildMovieVideosUri(rowId)
                , null
                , null
                , null
                , null);
        cursor.moveToFirst();
        TestUtilities.assertRowEquals(TestUtilities.createMadMaxMovieVideoValues()
                , cursor);
    }

    /**
     * Verifies that
     * {@link MovieProvider#query(Uri, String[], String, String[], String)}
     * works properly. Case for the reviews related to a movie.
     */
    public void testQuery_movieReviews() {
        // insert test records directly into the database
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long rowId = TestUtilities.insertMadMaxMovieValues(mContext);
        TestUtilities.insertMadMaxMovieReviewValues(mContext);
        db.close();

        Cursor cursor = mContext.getContentResolver().query(
                CachedMovieEntry.buildMovieReviewsUri(rowId)
                , null
                , null
                , null
                , null);
        cursor.moveToFirst();
        TestUtilities.assertRowEquals(TestUtilities.createMadMaxMovieReviewValues()
                , cursor);
    }

    /**
     * Verifies that
     * {@link MovieProvider#query(Uri, String[], String, String[], String)}
     * works properly. Case for the reviews related to a movie.
     */
    public void testInsert_movie() {
        ContentValues values = TestUtilities.createMadMaxMovieValues();
        Uri locationUri = mContext.getContentResolver().insert(
                CachedMovieEntry.CONTENT_URI, values);
        long rowId = ContentUris.parseId(locationUri);
        Assert.assertTrue("Provider inserted the movie", rowId != -1);

        // Verify the entry was stored properly
        Cursor cursor = mContext.getContentResolver().query(
                CachedMovieEntry.CONTENT_URI
                , null
                , CachedMovieEntry._ID + " = " + rowId
                , null
                , null);
        cursor.moveToFirst();
        TestUtilities.assertRowEquals(
                TestUtilities.createMadMaxMovieValues()
                , cursor);
        cursor.close();
    }

    /**
     * Verifies that {@link MovieProvider#insert(Uri, ContentValues)} works
     * properly. Case for a video of a movie.
     */
    public void testInsert_video() {
        ContentValues values = TestUtilities.createMadMaxMovieVideoValues();
        Uri locationUri = mContext.getContentResolver().insert(
                CachedMovieVideoEntry.CONTENT_URI, values);
        long rowId = ContentUris.parseId(locationUri);
        Assert.assertTrue("Provider inserted the movie", rowId != -1);

        // Verify the entry was stored properly
        Cursor cursor = mContext.getContentResolver().query(
                CachedMovieVideoEntry.CONTENT_URI
                , null
                , CachedMovieVideoEntry._ID + " = " + rowId
                , null
                , null);
        cursor.moveToFirst();
        TestUtilities.assertRowEquals(
                TestUtilities.createMadMaxMovieVideoValues()
                , cursor);
        cursor.close();
    }

    /**
     * Verifies that {@link MovieProvider#insert(Uri, ContentValues)} works
     * properly. Case for a review of a movie.
     */
    public void testInsert_review() {
        ContentValues values = TestUtilities.createMadMaxMovieReviewValues();
        Uri locationUri = mContext.getContentResolver().insert(
                CachedMovieReviewEntry.CONTENT_URI, values);
        long rowId = ContentUris.parseId(locationUri);
        Assert.assertTrue("Provider inserted the movie", rowId != -1);

        // Verify the entry was stored properly
        Cursor cursor = mContext.getContentResolver().query(
                CachedMovieReviewEntry.CONTENT_URI
                , null
                , CachedMovieReviewEntry._ID + " = " + rowId
                , null
                , null);
        cursor.moveToFirst();
        TestUtilities.assertRowEquals(
                TestUtilities.createMadMaxMovieReviewValues()
                , cursor);
        cursor.close();
    }

    /**
     * Verifies that
     * {@link MovieProvider#update(Uri, ContentValues, String, String[])}
     * works properly. Case for a cached movie.
     */
    public void testUpdate_movie() {
        ContentValues values = TestUtilities.createMadMaxMovieValues();
        Uri locationUri = mContext.getContentResolver().insert(
                CachedMovieEntry.CONTENT_URI, values);
        long rowId = ContentUris.parseId(locationUri);
        Assert.assertTrue("Provider inserted the movie", rowId != -1);

        ContentValues updatedValues = new ContentValues(values);
        updatedValues.put(CachedMovieEntry._ID, rowId);
        updatedValues.put(CachedMovieEntry.COLUMN_OVERVIEW, "A happy story!");

        int count = mContext.getContentResolver().update(
                CachedMovieEntry.CONTENT_URI
                , updatedValues
                , CachedMovieEntry._ID + "= ?"
                , new String[]{Long.toString(rowId)});
        Assert.assertEquals("One entry must be updated", 1, count);

        // Verify the changes were applied
        Cursor cursor = mContext.getContentResolver().query(
                CachedMovieEntry.CONTENT_URI
                , null
                , CachedMovieEntry._ID + " = " + rowId
                , null
                , null);
        cursor.moveToFirst();
        TestUtilities.assertRowEquals(updatedValues, cursor);
        cursor.close();
    }

    /**
     * Verifies that
     * {@link MovieProvider#update(Uri, ContentValues, String, String[])}
     * works properly. Case for a cached movie video.
     */
    public void testUpdate_video() {
        ContentValues values = TestUtilities.createMadMaxMovieVideoValues();
        Uri locationUri = mContext.getContentResolver().insert(
                CachedMovieVideoEntry.CONTENT_URI, values);
        long rowId = ContentUris.parseId(locationUri);
        Assert.assertTrue("Provider inserted the movie video", rowId != -1);

        ContentValues updatedValues = new ContentValues(values);
        updatedValues.put(CachedMovieVideoEntry._ID, rowId);
        updatedValues.put(CachedMovieVideoEntry.COLUMN_NAME, "A happy video!");

        int count = mContext.getContentResolver().update(
                CachedMovieVideoEntry.CONTENT_URI
                , updatedValues
                , CachedMovieEntry._ID + "= ?"
                , new String[]{Long.toString(rowId)});
        Assert.assertEquals("One entry must be updated", 1, count);

        // Verify the changes were applied
        Cursor cursor = mContext.getContentResolver().query(
                CachedMovieVideoEntry.CONTENT_URI
                , null
                , CachedMovieVideoEntry._ID + " = " + rowId
                , null
                , null);
        cursor.moveToFirst();
        TestUtilities.assertRowEquals(updatedValues, cursor);
        cursor.close();
    }

    /**
     * Verifies that
     * {@link MovieProvider#update(Uri, ContentValues, String, String[])}
     * works properly. Case for a cached movie review.
     */
    public void testUpdate_review() {
        ContentValues values = TestUtilities.createMadMaxMovieReviewValues();
        Uri locationUri = mContext.getContentResolver().insert(
                CachedMovieReviewEntry.CONTENT_URI, values);
        long rowId = ContentUris.parseId(locationUri);
        Assert.assertTrue("Provider inserted the movie review", rowId != -1);

        ContentValues updatedValues = new ContentValues(values);
        updatedValues.put(CachedMovieReviewEntry._ID, rowId);
        updatedValues.put(CachedMovieReviewEntry.COLUMN_AUTHOR, "Some Guy");

        int count = mContext.getContentResolver().update(
                CachedMovieReviewEntry.CONTENT_URI
                , updatedValues
                , CachedMovieEntry._ID + "= ?"
                , new String[]{Long.toString(rowId)});
        Assert.assertEquals("One entry must be updated", 1, count);

        // Verify the changes were applied
        Cursor cursor = mContext.getContentResolver().query(
                CachedMovieReviewEntry.CONTENT_URI
                , null
                , CachedMovieReviewEntry._ID + " = " + rowId
                , null
                , null);
        cursor.moveToFirst();
        TestUtilities.assertRowEquals(updatedValues, cursor);
        cursor.close();
    }

    /**
     * Verifies that {@link MovieProvider#delete(Uri, String, String[])} works
     * properly. Case for cached movies.
     */
    public void testDelete_movie() {
        testInsert_movie();
        deleteAllMovies();
    }

    /**
     * Verifies that {@link MovieProvider#delete(Uri, String, String[])} works
     * properly. Case for cached movie videos.
     */
    public void testDelete_video() {
        testInsert_video();
        deleteAllMovieVideos();
    }

    /**
     * Verifies that {@link MovieProvider#delete(Uri, String, String[])} works
     * properly. Case for cached movie reviews.
     */
    public void testDelete_review() {
        testInsert_review();
        deleteAllMovieReviews();
    }

    /**
     * Returns a set of movie entries that may be inserted into the database.
     *
     * @return a set of movie entries that may be inserted into the database.
     */
    static ContentValues[] createBulkInsertMovieValues() {
        ContentValues[] returnContentValues =
                new ContentValues[BULK_INSERT_NUMBER_OF_RECORDS];
        for (int i = 1; i <= BULK_INSERT_NUMBER_OF_RECORDS; i++) {
            ContentValues movieValues = new ContentValues();
            movieValues.put(CachedMovieEntry._ID, i);
            movieValues.put(CachedMovieEntry.COLUMN_OVERVIEW, "A great story of " + i);
            movieValues.put(CachedMovieEntry.COLUMN_VOTE_AVERAGE, i);
            movieValues.put(CachedMovieEntry.COLUMN_POSTER_PATH, "/poster" + i + ".jpg");
            movieValues.put(CachedMovieEntry.COLUMN_POPULARITY, i);
            movieValues.put(CachedMovieEntry.COLUMN_BACKDROP_PATH, "/backdrop" + i + ".jpg");
            movieValues.put(CachedMovieEntry.COLUMN_ORIGINAL_TITLE, "The Amazing " + i);
            returnContentValues[i - 1] = movieValues;
        }
        return returnContentValues;
    }

    /**
     * Verifies that {@link MovieProvider#bulkInsert(Uri, ContentValues[])}
     * works properly. Case for cached movies.
     */
    public void testBulkInsert_movie() {
        ContentValues[] bulkInsertContentValues = createBulkInsertMovieValues();
        int insertCount =
                mContext.getContentResolver().bulkInsert(CachedMovieEntry.CONTENT_URI
                        , bulkInsertContentValues);
        Assert.assertEquals("The expected number of entries must be inserted"
                , BULK_INSERT_NUMBER_OF_RECORDS
                , insertCount);
        Cursor cursor = mContext.getContentResolver().query(
                CachedMovieEntry.CONTENT_URI
                , null
                , null
                , null
                , CachedMovieEntry._ID + " ASC");
        Assert.assertEquals("The expected number of entries must be retrieved"
                , BULK_INSERT_NUMBER_OF_RECORDS
                , cursor.getCount());

        // Verify the entries were correctly inserted.
        cursor.moveToFirst();
        for ( int i = 0; i < BULK_INSERT_NUMBER_OF_RECORDS; i++) {
            TestUtilities.assertRowEquals(bulkInsertContentValues[i], cursor);
            cursor.moveToNext();
        }
        cursor.close();
    }

    /**
     * Returns a set of movie video entries that may be inserted into the database.
     *
     * @return a set of movie video entries that may be inserted into the database.
     */
    static ContentValues[] createBulkInsertMovieVideoValues() {
        ContentValues[] returnContentValues =
                new ContentValues[BULK_INSERT_NUMBER_OF_RECORDS];
        for (int i = 1; i <= BULK_INSERT_NUMBER_OF_RECORDS; i++) {
            ContentValues videoValues = new ContentValues();
            videoValues.put(CachedMovieVideoEntry._ID, i);
            videoValues.put(CachedMovieVideoEntry.COLUMN_MOVIE_API_ID, 76341);
            videoValues.put(CachedMovieVideoEntry.COLUMN_API_ID, UUID.randomUUID().toString());
            videoValues.put(CachedMovieVideoEntry.COLUMN_LANGUAGE, "en");
            videoValues.put(CachedMovieVideoEntry.COLUMN_KEY, UUID.randomUUID().toString());
            videoValues.put(CachedMovieVideoEntry.COLUMN_NAME, "Trailer " + i);
            videoValues.put(CachedMovieVideoEntry.COLUMN_SITE, "YouTube");
            videoValues.put(CachedMovieVideoEntry.COLUMN_SIZE, 1080);
            videoValues.put(CachedMovieVideoEntry.COLUMN_TYPE, "Trailer");
            returnContentValues[i - 1] = videoValues;
        }
        return returnContentValues;
    }

    /**
     * Verifies that {@link MovieProvider#bulkInsert(Uri, ContentValues[])}
     * works properly. Case for cached movie videos.
     */
    public void testBulkInsert_video() {
        ContentValues[] bulkInsertContentValues = createBulkInsertMovieVideoValues();
        int insertCount =
                mContext.getContentResolver().bulkInsert(
                        CachedMovieVideoEntry.CONTENT_URI
                        , bulkInsertContentValues);
        Assert.assertEquals("The expected number of entries must be inserted"
                , BULK_INSERT_NUMBER_OF_RECORDS
                , insertCount);
        Cursor cursor = mContext.getContentResolver().query(
                CachedMovieVideoEntry.CONTENT_URI
                , null
                , null
                , null
                , CachedMovieVideoEntry._ID + " ASC");
        Assert.assertEquals("The expected number of entries must be retrieved"
                , BULK_INSERT_NUMBER_OF_RECORDS
                , cursor.getCount());

        // Verify the entries were correctly inserted.
        cursor.moveToFirst();
        for ( int i = 0; i < BULK_INSERT_NUMBER_OF_RECORDS; i++) {
            TestUtilities.assertRowEquals(bulkInsertContentValues[i], cursor);
            cursor.moveToNext();
        }
        cursor.close();
    }

    /**
     * Returns a set of movie review entries that may be inserted into the database.
     *
     * @return a set of movie review entries that may be inserted into the database.
     */
    static ContentValues[] createBulkInsertMovieReviewValues() {
        ContentValues[] returnContentValues =
                new ContentValues[BULK_INSERT_NUMBER_OF_RECORDS];
        for (int i = 1; i <= BULK_INSERT_NUMBER_OF_RECORDS; i++) {
            ContentValues reviewValues = new ContentValues();
            reviewValues.put(CachedMovieReviewEntry._ID, i);
            reviewValues.put(CachedMovieReviewEntry.COLUMN_MOVIE_API_ID, 76341);
            reviewValues.put(CachedMovieReviewEntry.COLUMN_API_ID, UUID.randomUUID().toString());
            reviewValues.put(CachedMovieReviewEntry.COLUMN_AUTHOR, "SomeGuy" + i);
            reviewValues.put(CachedMovieReviewEntry.COLUMN_CONTENT, "I would rate it " + i);
            reviewValues.put(CachedMovieReviewEntry.COLUMN_URL, "http://site/" + i);
            returnContentValues[i - 1] = reviewValues;
        }
        return returnContentValues;
    }

    /**
     * Verifies that {@link MovieProvider#bulkInsert(Uri, ContentValues[])}
     * works properly. Case for cached movie reviews.
     */
    public void testBulkInsert_review() {
        ContentValues[] bulkInsertContentValues = createBulkInsertMovieReviewValues();
        int insertCount =
                mContext.getContentResolver().bulkInsert(
                        CachedMovieReviewEntry.CONTENT_URI
                        , bulkInsertContentValues);
        Assert.assertEquals("The expected number of entries must be inserted"
                , BULK_INSERT_NUMBER_OF_RECORDS
                , insertCount);
        Cursor cursor = mContext.getContentResolver().query(
                CachedMovieReviewEntry.CONTENT_URI
                , null
                , null
                , null
                , CachedMovieReviewEntry._ID + " ASC");
        Assert.assertEquals("The expected number of entries must be retrieved"
                , BULK_INSERT_NUMBER_OF_RECORDS
                , cursor.getCount());

        // Verify the entries were correctly inserted.
        cursor.moveToFirst();
        for ( int i = 0; i < BULK_INSERT_NUMBER_OF_RECORDS; i++) {
            TestUtilities.assertRowEquals(bulkInsertContentValues[i], cursor);
            cursor.moveToNext();
        }
        cursor.close();
    }

}
