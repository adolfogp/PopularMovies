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

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.lang.ref.WeakReference;

import javax.inject.Inject;

import mx.com.adolfogarcia.popularmovies.data.RestfulServiceConfiguration;
import mx.com.adolfogarcia.popularmovies.model.transport.GeneralConfigurationJsonModel;
import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Task that downloads and stores the configuration for
 * <a href="https://www.themoviedb.org/">themoviedb.org</a>'s RESTful API.
 *
 * @author Jesús Adolfo García Pasquel
 */
public class FetchConfigurationTask extends AsyncTask<Void, Void, Void> {

    /**
     * Identifies the messages written to the log by this class.
     */
    private static final String LOG_TAG = FetchMoviePageTask.class.getSimpleName();

    /**
     * Reference to the object that stores the RESTful API's configuration.
     */
    @Inject WeakReference<RestfulServiceConfiguration> mWeakConfiguration;

    /**
     * Creates a new instance of {@link FetchConfigurationTask} that will
     * store the retrieved information in the object passed as argument.
     *
     * @param configuration the object where the retrieved information will
     *                      be stored.
     */
    public FetchConfigurationTask(RestfulServiceConfiguration configuration) {
        mWeakConfiguration = new WeakReference<>(configuration);
    }

    @Override
    protected Void doInBackground(Void... params) {
        RestfulServiceConfiguration configuration = mWeakConfiguration.get();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(TheMovieDbApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        TheMovieDbApi service = retrofit.create(TheMovieDbApi.class);
        Call<GeneralConfigurationJsonModel> configCall =
                service.getConfiguration(configuration.getMovieApiKey());
        try {
            Response<GeneralConfigurationJsonModel> response = configCall.execute();
            if (response.isSuccess()) {
                configuration.setImageConfiguration(
                        response.body().getImageConfiguration());
            } else {
                Log.d(LOG_TAG, "FAILURE! " + response.errorBody().string());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error getting RESTful API configuration.", e);
        }
        return null;
    }

}
