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

package mx.com.adolfogarcia.popularmovies.net;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import mx.com.adolfogarcia.popularmovies.data.RestfulServiceConfiguration;
import mx.com.adolfogarcia.popularmovies.model.transport.MoviePageJsonModel;

import static mx.com.adolfogarcia.popularmovies.data.MovieContract.CachedMovieEntry;
import mx.com.adolfogarcia.popularmovies.model.transport.MovieJsonModel;
import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

// TODO: Rename to FetchMoviePageTask
/*
 Receives the page number to load.
 */
public class FetchMovieTask extends AsyncTask<Integer, Void, Void> {

    private static final String LOG_TAG = FetchMovieTask.class.getSimpleName();

    // TODO: Inject with Dagger 2 and get instances of FetchMovieTask with Dagger 2.
    @Inject
    RestfulServiceConfiguration mConfiguration;
    @Inject Context mContext;

    public FetchMovieTask(Context context, RestfulServiceConfiguration configuration) {
        mContext = context;
        mConfiguration = configuration;
    }

    @Override
    protected Void doInBackground(Integer... params) {
        Log.d(LOG_TAG, "Starting download of movie page: " + params[0]);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(TheMovieDbApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        TheMovieDbApi service = retrofit.create(TheMovieDbApi.class);
        Call<MoviePageJsonModel> configCall = service.getMoviePage(
                mConfiguration.getMovieApiKey()
                , TheMovieDbApi.SORT_BY_POPULARITY
                , params[0]
        );
        try {
            Response<MoviePageJsonModel> response = configCall.execute();
            if (response.isSuccess()) {
                Log.i(LOG_TAG, "Successfully downloaded movie page " + params[0]);
                insertMoviesInProvider(response.body());
            } else {
                Log.w(LOG_TAG, "Failed to download movie page " + params[0]);
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error getting movie page " + params[0], e);
        }
        return null;
    }

    private void insertMoviesInProvider(MoviePageJsonModel response) {
        // TODO: Use an array to begin with
        List<ContentValues> cvList = new ArrayList<>(response.getMovies().size());
        for (MovieJsonModel result : response.getMovies()) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(CachedMovieEntry._ID
                    , result.getId());
            contentValues.put(CachedMovieEntry.COLUMN_ORIGINAL_TITLE
                    , result.getOriginalTitle());
            contentValues.put(CachedMovieEntry.COLUMN_RELEASE_DATE
                    , result.getReleaseDateEpochTimeUtc());
            contentValues.put(CachedMovieEntry.COLUMN_OVERVIEW
                    , result.getOverview());
            contentValues.put(CachedMovieEntry.COLUMN_BACKDROP_PATH
                    , result.getBackdropPath());
            contentValues.put(CachedMovieEntry.COLUMN_POPULARITY
                    , result.getPopularity());
            contentValues.put(CachedMovieEntry.COLUMN_VOTE_AVERAGE
                    , result.getVoteAverage());
            contentValues.put(CachedMovieEntry.COLUMN_POSTER_PATH
                    , result.getPosterPath());
            cvList.add(contentValues);
        }
        if (cvList.size() > 0 ) {
            mContext.getContentResolver().bulkInsert(CachedMovieEntry.CONTENT_URI
                    , cvList.toArray(new ContentValues[cvList.size()]));
            mConfiguration.setTotalMoviePagesAvailable(response.getTotalPages());
            mConfiguration.setLastMoviePageRetrieved(response.getPageNumber());
        }
    }


}
