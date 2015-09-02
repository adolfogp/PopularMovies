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

package mx.com.adolfogarcia.popularmovies.fragment;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;

import javax.inject.Inject;

import mx.com.adolfogarcia.popularmovies.Configuration;
import mx.com.adolfogarcia.popularmovies.PopularMoviesApplication;
import mx.com.adolfogarcia.popularmovies.PopularMoviesApplicationModule;
import mx.com.adolfogarcia.popularmovies.R;
import mx.com.adolfogarcia.popularmovies.model.transport.GeneralConfigurationJsonModel;
import mx.com.adolfogarcia.popularmovies.rest.TheMovieDbApi;
import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * A placeholder fragment containing a simple view.
 *
 * @author Jesús Adolfo García Pasquel
 */
public class MainActivityFragment extends Fragment {

    private static final String LOG_TAG = MainActivityFragment.class.getSimpleName();

    @Inject Configuration mConfiguration;

    /**
     * Creates a new instance of {@link MainActivityFragment}.
     */
    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((PopularMoviesApplication) getActivity().getApplication()).getComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        AsyncTask<Void, Void, Void> myTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                retrofitTest();
                return null;
            }
        };
        myTask.execute();
    }

    private void retrofitTest() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(TheMovieDbApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        TheMovieDbApi service = retrofit.create(TheMovieDbApi.class);
        // FIXME - Remove!
        Call<GeneralConfigurationJsonModel> configCall =
                service.getConfiguration(mConfiguration.getMovieApiKey());
        try {
            Response<GeneralConfigurationJsonModel> response = configCall.execute();
            if (response.isSuccess()) {
                Log.d(LOG_TAG, "SUCCESS! " + response.body().toString());
            } else {
                Log.d(LOG_TAG, "FAILURE! " + response.errorBody().string());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error getting config", e);
            return;
        }
    }
}
