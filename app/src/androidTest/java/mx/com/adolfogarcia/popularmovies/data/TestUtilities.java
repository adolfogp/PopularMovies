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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import junit.framework.Assert;

import java.util.Map;
import java.util.Set;

import static mx.com.adolfogarcia.popularmovies.data.MovieContract.MovieEntry;

/**
 * Utility methods to help test the movie database. The code is based on
 * {@code TestUtilities} from the sample application <i>Sunshine</i>.
 *
 * @author Jesús Adolfo García Pasquel
 */
public final class TestUtilities {

    /**
     * Returns the values of a row that may be inserted into the movie database.
     *
     * @return the values of a row that may be inserted into the movie database.
     */
    static ContentValues createMadMaxMovieValues() {
        ContentValues testValues = new ContentValues();
        testValues.put(MovieEntry._ID, 76341);
        testValues.put(MovieEntry.COLUMN_BACKDROP_PATH, "/tbhdm8UJAb4ViCTsulYFL3lxMCd.jpg");
        testValues.put(MovieEntry.COLUMN_ORIGINAL_TITLE, "Mad Max: Fury Road");
        testValues.put(MovieEntry.COLUMN_OVERVIEW, "An apocalyptic story...");
        testValues.put(MovieEntry.COLUMN_POPULARITY, 55.32);
        testValues.put(MovieEntry.COLUMN_POSTER_PATH, "/kqjL17yufvn9OVLyXYpvtyrFfak.jpg");
        testValues.put(MovieEntry.COLUMN_VOTE_AVERAGE, 7.7);
        return testValues;
    }

    /**
     * Inserts the values returned by {@link #createMadMaxMovieValues()} into
     * the movie database.
     *
     * @param context the {@link Context} used to access the database.
     * @return the row id of the insertion.
     */
    static long insertMadMaxMovieValues(Context context) {
        MovieDbHelper dbHelper = new MovieDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestUtilities.createMadMaxMovieValues();

        long rowId;
        rowId = db.insert(MovieEntry.TABLE_NAME, null, testValues);
        Assert.assertTrue("Row was successfully inserted", rowId != -1);
        return rowId;
    }

    /**
     * Verifies the row the {@link Cursor} is pointing at, contains the same
     * information as that in the {@link ContentValues}.
     *
     * @param expectedValues values expected to be in the row.
     * @param valueCursor data to verify.
     */
    static void assertRowEquals(ContentValues expectedValues, Cursor valueCursor) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int columnIdx = valueCursor.getColumnIndex(columnName);
            Assert.assertTrue("Column '" + columnName + "' should exist."
                    , columnIdx != -1);
            String expectedValue = entry.getValue().toString();
            Assert.assertEquals("Value must be equal to'" + expectedValue + "'."
                    , expectedValue
                    , valueCursor.getString(columnIdx));
        }
    }

}
