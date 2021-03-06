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

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import junit.framework.Assert;

import java.util.HashSet;
import java.util.Set;
import static mx.com.adolfogarcia.popularmovies.data.MovieContract.CachedMovieEntry;
import static mx.com.adolfogarcia.popularmovies.data.MovieContract.CachedMovieVideoEntry;
import static mx.com.adolfogarcia.popularmovies.data.MovieContract.CachedMovieReviewEntry;

/**
 * Test cases that verify the movie database is properly created with a
 * {@link MovieDbHelper} and the {@link MovieContract}. The code is based on
 * {@code TestDb} from the sample application <i>Sunshine</i>.
 *
 * @author Jesús Adolfo García Pasquel
 */
public class MovieDbTest extends AndroidTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mContext.deleteDatabase(MovieDbHelper.DATABASE_NAME);
    }

    /**
     * Verifies the database schema is created correctly.
     */
    public void testCreateDb() {
        // Set of tables in the schema
        final Set<String> tableNameSet = new HashSet<>();
        tableNameSet.add(CachedMovieEntry.TABLE_NAME);
        tableNameSet.add(CachedMovieVideoEntry.TABLE_NAME);
        tableNameSet.add(CachedMovieReviewEntry.TABLE_NAME);

        SQLiteDatabase db = new MovieDbHelper(this.mContext).getWritableDatabase();
        Assert.assertTrue("Database should be open.", db.isOpen());

        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        Assert.assertTrue("There must be at least one table", c.moveToFirst());

        // verify that all tables in the schema were created
        do {
            tableNameSet.remove(c.getString(0));
        } while( c.moveToNext() );

        Assert.assertTrue("All tables in the schema must be in the database"
                , tableNameSet.isEmpty());
        c.close();

        // Verify the tables contain the required columns
        // Cached Movies' table
        c = db.rawQuery("PRAGMA table_info(" + CachedMovieEntry.TABLE_NAME + ")", null);
        assertTrue("The table must contain columns.", c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        Set<String> columnNameSet = new HashSet<>();
        columnNameSet.add(CachedMovieEntry._ID);
        columnNameSet.add(CachedMovieEntry.COLUMN_API_ID);
        columnNameSet.add(CachedMovieEntry.COLUMN_BACKDROP_PATH);
        columnNameSet.add(CachedMovieEntry.COLUMN_RELEASE_DATE);
        columnNameSet.add(CachedMovieEntry.COLUMN_ORIGINAL_TITLE);
        columnNameSet.add(CachedMovieEntry.COLUMN_OVERVIEW);
        columnNameSet.add(CachedMovieEntry.COLUMN_POPULARITY);
        columnNameSet.add(CachedMovieEntry.COLUMN_POSTER_PATH);
        columnNameSet.add(CachedMovieEntry.COLUMN_VOTE_AVERAGE);
        columnNameSet.add(CachedMovieEntry.COLUMN_MOST_POPULAR);
        columnNameSet.add(CachedMovieEntry.COLUMN_HIGHEST_RATED);
        columnNameSet.add(CachedMovieEntry.COLUMN_USER_FAVORITE);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            Assert.assertTrue("No unexpected colums in " + CachedMovieEntry.TABLE_NAME
                    , columnNameSet.remove(columnName));
        } while(c.moveToNext());

        Assert.assertTrue("The table " + CachedMovieEntry.TABLE_NAME
                        + " must contain the required columns"
                , columnNameSet.isEmpty());
        c.close();

        // Cached Movie Videos' table
        c = db.rawQuery("PRAGMA table_info(" + CachedMovieVideoEntry.TABLE_NAME + ")", null);
        assertTrue("The table must contain columns.", c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        columnNameSet = new HashSet<>();
        columnNameSet.add(CachedMovieVideoEntry._ID);
        columnNameSet.add(CachedMovieVideoEntry.COLUMN_MOVIE_API_ID);
        columnNameSet.add(CachedMovieVideoEntry.COLUMN_API_ID);
        columnNameSet.add(CachedMovieVideoEntry.COLUMN_LANGUAGE);
        columnNameSet.add(CachedMovieVideoEntry.COLUMN_KEY);
        columnNameSet.add(CachedMovieVideoEntry.COLUMN_NAME);
        columnNameSet.add(CachedMovieVideoEntry.COLUMN_SITE);
        columnNameSet.add(CachedMovieVideoEntry.COLUMN_SIZE);
        columnNameSet.add(CachedMovieVideoEntry.COLUMN_TYPE);

        columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            Assert.assertTrue("No unexpected colums in " + CachedMovieVideoEntry.TABLE_NAME
                    , columnNameSet.remove(columnName));
        } while(c.moveToNext());

        Assert.assertTrue("The table " + CachedMovieVideoEntry.TABLE_NAME
                + " must contain the required columns"
                , columnNameSet.isEmpty());
        c.close();

        // Chaced Movie Reviews' table
        c = db.rawQuery("PRAGMA table_info(" + CachedMovieReviewEntry.TABLE_NAME + ")", null);
        assertTrue("The table must contain columns.", c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        columnNameSet = new HashSet<>();
        columnNameSet.add(CachedMovieReviewEntry._ID);
        columnNameSet.add(CachedMovieReviewEntry.COLUMN_MOVIE_API_ID);
        columnNameSet.add(CachedMovieReviewEntry.COLUMN_API_ID);
        columnNameSet.add(CachedMovieReviewEntry.COLUMN_AUTHOR);
        columnNameSet.add(CachedMovieReviewEntry.COLUMN_CONTENT);
        columnNameSet.add(CachedMovieReviewEntry.COLUMN_URL);

        columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            Assert.assertTrue("No unexpected colums in " + CachedMovieReviewEntry.TABLE_NAME
                    , columnNameSet.remove(columnName));
        } while(c.moveToNext());

        Assert.assertTrue("The table " + CachedMovieReviewEntry.TABLE_NAME
                + " must contain the required columns"
                , columnNameSet.isEmpty());
        c.close();

        db.close();
    }

    /**
     * Verifies insertion and query operations work correctly on the movie table.
     */
    public void testMovieTable() {
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        TestUtilities.insertMadMaxMovieValues(mContext);
        // Verify the data was inserted
        Cursor cursor = db.query(
                CachedMovieEntry.TABLE_NAME
                , null // all columns
                , null // all rows
                , null // no values for conditions
                , null // no group by
                , null // no having
                , null // no ordering
        );
        Assert.assertTrue("The table must have at least one row", cursor.moveToFirst());
        TestUtilities.assertRowEquals(TestUtilities.createMadMaxMovieValues(), cursor);
        Assert.assertFalse("There must be only one record in the database"
                , cursor.moveToNext());
        cursor.close();
        db.close();
    }

    /**
     * Verifies insertion and query operations work correctly on the movie
     * video table.
     */
    public void testMovieVideoTable() {
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        TestUtilities.insertMadMaxMovieVideoValues(mContext);
        // Verify the data was inserted
        Cursor cursor = db.query(
                CachedMovieVideoEntry.TABLE_NAME
                , null // all columns
                , null // all rows
                , null // no values for conditions
                , null // no group by
                , null // no having
                , null // no ordering
        );
        Assert.assertTrue("The table must have at least one row", cursor.moveToFirst());
        TestUtilities.assertRowEquals(TestUtilities.createMadMaxMovieVideoValues(), cursor);
        Assert.assertFalse("There must be only one record in the database"
                , cursor.moveToNext());
        cursor.close();
        db.close();
    }

    /**
     * Verifies insertion and query operations work correctly on the movie
     * review table.
     */
    public void testMovieReviewTable() {
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        TestUtilities.insertMadMaxMovieReviewValues(mContext);
        // Verify the data was inserted
        Cursor cursor = db.query(
                CachedMovieReviewEntry.TABLE_NAME
                , null // all columns
                , null // all rows
                , null // no values for conditions
                , null // no group by
                , null // no having
                , null // no ordering
        );
        Assert.assertTrue("The table must have at least one row", cursor.moveToFirst());
        TestUtilities.assertRowEquals(TestUtilities.createMadMaxMovieReviewValues(), cursor);
        Assert.assertFalse("There must be only one record in the database"
                , cursor.moveToNext());
        cursor.close();
        db.close();
    }

}
