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

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;

import javax.inject.Inject;

import mx.com.adolfogarcia.popularmovies.data.RestfulServiceConfiguration;
import mx.com.adolfogarcia.popularmovies.model.transport.GeneralConfigurationJsonModel;
import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class FetchConfigurationTask extends AsyncTask<Void, Void, Void> {

    private static final String LOG_TAG = FetchMovieTask.class.getSimpleName();

    // TODO: Inject with Dagger 2 and get instances of FetchMovieTask with Dagger 2.
    @Inject
    RestfulServiceConfiguration mConfiguration;

    public FetchConfigurationTask(RestfulServiceConfiguration configuration) {
        mConfiguration = configuration;
    }

    // FIXME: Use setter methods in configuration.
    @Override
    protected Void doInBackground(Void... params) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(TheMovieDbApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        TheMovieDbApi service = retrofit.create(TheMovieDbApi.class);
        Call<GeneralConfigurationJsonModel> configCall =
                service.getConfiguration(mConfiguration.getMovieApiKey());
        try {
            Response<GeneralConfigurationJsonModel> response = configCall.execute();
            if (response.isSuccess()) {
                mConfiguration.setImageConfiguration(
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
