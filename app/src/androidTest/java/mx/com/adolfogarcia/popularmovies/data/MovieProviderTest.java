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
import android.util.Log;

import static mx.com.adolfogarcia.popularmovies.data.MovieContract.MovieEntry;

import junit.framework.Assert;

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
        deleteAllMovies();
    }

    /**
     * Deletes all movie entries in the {@link MovieProvider}, and verifies
     * none remain.
     */
    public void deleteAllMovies() {
        mContext.getContentResolver().delete(
                MovieEntry.CONTENT_URI
                , null
                , null);

        Cursor cursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI
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
                , testMatcher.match(MovieEntry.CONTENT_URI)
                , MovieProvider.MOVIE);
    }

    /**
     * Verifies that {@link MovieProvider#buildUriMatcher()} creates an
     * {@link android.content.UriMatcher} that maps correctly the URIs to
     * the expected constants. Case for the URI that identifies a single movie.
     */
    public void testBuildUriMatcher_singleMovie() {
        UriMatcher testMatcher = MovieProvider.buildUriMatcher();
        Assert.assertEquals("Single movie URI must match expected constant."
                , testMatcher.match(MovieEntry.buildMovieUri(1))
                , MovieProvider.MOVIE_ID);
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
        String type = mContext.getContentResolver().getType(MovieEntry.CONTENT_URI);
        Assert.assertEquals(
                "The content type for the 'all movies' URI must be of type directory"
                , MovieEntry.CONTENT_TYPE, type);
    }

    /**
     * Verifies that {@link MovieProvider#getType(Uri)} works properly.
     * Case for the URI that identifies a single movie.
     */
    public void testGetType_singleMovies() {
        String type = mContext.getContentResolver().getType(MovieEntry.buildMovieUri(1));
        Assert.assertEquals(
                "The content type for the URI of a single movie must be of type entry"
                , MovieEntry.CONTENT_ITEM_TYPE, type);
    }

    /*
        This test uses the database directly to insert and then uses the ContentProvider to
        read out the data.  Uncomment this test to see if the basic weather query functionality
        given in the ContentProvider is working correctly.
     */

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
                MovieEntry.CONTENT_URI
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
                MovieEntry.buildMovieUri(rowId)
                , null
                , null
                , null
                , null);
        cursor.moveToFirst();
        TestUtilities.assertRowEquals(TestUtilities.createMadMaxMovieValues()
                , cursor);
    }

    /**
     * Verifies that {@link MovieProvider#insert(Uri, ContentValues)} works
     * properly.
     */
    public void testInsert() {
        ContentValues values = TestUtilities.createMadMaxMovieValues();
        Uri locationUri = mContext.getContentResolver().insert(
                MovieEntry.CONTENT_URI, values);
        long rowId = ContentUris.parseId(locationUri);
        Assert.assertTrue("Provider inserted the movie", rowId != -1);

        // Verify the entry was stored properly
        Cursor cursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI
                , null
                , MovieEntry._ID + " = " + rowId
                , null
                , null);
        cursor.moveToFirst();
        TestUtilities.assertRowEquals(
                TestUtilities.createMadMaxMovieValues()
                , cursor);
        cursor.close();
    }

    /**
     * Verifies that
     * {@link MovieProvider#update(Uri, ContentValues, String, String[])}
     * works properly.
     */
    public void testUpdate() {
        ContentValues values = TestUtilities.createMadMaxMovieValues();
        Uri locationUri = mContext.getContentResolver().insert(
                MovieEntry.CONTENT_URI, values);
        long rowId = ContentUris.parseId(locationUri);
        Assert.assertTrue("Provider inserted the movie", rowId != -1);

        ContentValues updatedValues = new ContentValues(values);
        updatedValues.put(MovieEntry._ID, rowId);
        updatedValues.put(MovieEntry.COLUMN_OVERVIEW, "A happy story!");

        int count = mContext.getContentResolver().update(
                MovieEntry.CONTENT_URI
                , updatedValues
                , MovieEntry._ID + "= ?"
                , new String[]{Long.toString(rowId)});
        Assert.assertEquals("One entry must be updated", 1, count);

        // Verify the changes were applied
        Cursor cursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI
                , null
                , MovieEntry._ID + " = " + rowId
                , null
                , null);
        cursor.moveToFirst();
        TestUtilities.assertRowEquals(updatedValues, cursor);
        cursor.close();
    }

    /**
     * Verifies that {@link MovieProvider#delete(Uri, String, String[])} works
     * properly.
     */
    public void testDelete() {
        testInsert();
        deleteAllMovies();
    }

    /**
     * Returns a set of movie entries that may be insert into the database.
     *
     * @return a set of movie entries that may be insert into the database.
     */
    static ContentValues[] createBulkInsertMovieValues() {
        ContentValues[] returnContentValues =
                new ContentValues[BULK_INSERT_NUMBER_OF_RECORDS];
        for (int i = 1; i <= BULK_INSERT_NUMBER_OF_RECORDS; i++) {
            ContentValues weatherValues = new ContentValues();
            weatherValues.put(MovieEntry._ID, i);
            weatherValues.put(MovieEntry.COLUMN_OVERVIEW, "A great story of " + i);
            weatherValues.put(MovieEntry.COLUMN_VOTE_AVERAGE, i);
            weatherValues.put(MovieEntry.COLUMN_POSTER_PATH, "/poster" + i + ".jpg");
            weatherValues.put(MovieEntry.COLUMN_POPULARITY, i);
            weatherValues.put(MovieEntry.COLUMN_BACKDROP_PATH, "/backdrop" + i + ".jpg");
            weatherValues.put(MovieEntry.COLUMN_ORIGINAL_TITLE, "The Amazing " + i);
            returnContentValues[i - 1] = weatherValues;
        }
        return returnContentValues;
    }

    // Student: Uncomment this test after you have completed writing the BulkInsert functionality
    // in your provider.  Note that this test will work with the built-in (default) provider
    // implementation, which just inserts records one-at-a-time, so really do implement the
    // BulkInsert ContentProvider function.

    /**
     * Verifies that {@link MovieProvider#bulkInsert(Uri, ContentValues[])}
     * works properly.
     */
    public void testBulkInsert() {
        ContentValues[] bulkInsertContentValues = createBulkInsertMovieValues();
        int insertCount =
                mContext.getContentResolver().bulkInsert(MovieEntry.CONTENT_URI
                        , bulkInsertContentValues);
        Assert.assertEquals("The expected number of entries must be inserted"
                , BULK_INSERT_NUMBER_OF_RECORDS
                , insertCount);
        Cursor cursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI
                , null
                , null
                , null
                , MovieEntry._ID + " ASC");
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
