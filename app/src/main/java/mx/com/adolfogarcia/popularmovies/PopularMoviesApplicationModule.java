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

package mx.com.adolfogarcia.popularmovies;

import android.app.Application;

import java.lang.ref.WeakReference;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import mx.com.adolfogarcia.popularmovies.data.RestfulServiceConfiguration;
import mx.com.adolfogarcia.popularmovies.net.FetchMoviePageTaskFactory;
import mx.com.adolfogarcia.popularmovies.net.FetchPopularityMoviePageTaskFactory;
import mx.com.adolfogarcia.popularmovies.net.FetchRatingMoviePageTaskFactory;
import mx.com.adolfogarcia.popularmovies.view.adapter.LabeledItem;

/**
 * Module used by <a href="">Dagger 2</a> to provide dependencies in the
 * Popular Movies application.
 *
 * @author Jesús Adolfo García Pasquel
 */
@Module
public class PopularMoviesApplicationModule {

    /**
     * The application's context.
     */
    private final PopularMoviesApplication mApplication;

    private final RestfulServiceConfiguration mConfiguration;

    /**
     * Creates a new instance of {@link PopularMoviesApplicationModule}, given the
     * application's context.
     *
     * @param application the application's context.
     */
    public PopularMoviesApplicationModule(PopularMoviesApplication application) {
        mApplication = application;
        mConfiguration = new RestfulServiceConfiguration(application);
    }

    /**
     * Provides the application's context for injection.
     *
     * @return the application's context.
     */
    @Provides @Singleton PopularMoviesApplication provideApplicationContext() {
        return mApplication;
    }

    // TODO: Change to use the @Inject annotation on RestfulServiceConfiguration
    /**
     * Provides the application's system wide configuration data.
     *
     * @return the application's system wide configuration data.
     */
    @Provides @Singleton RestfulServiceConfiguration provideConfiguration() {
        return mConfiguration;
    }

    /**
     * Provides the application's context for injection.
     *
     * @return the application's context.
     */
    @Provides WeakReference<PopularMoviesApplication> provideWeakApplicationContext(
            PopularMoviesApplication application) {
        return new WeakReference<>(application);
    }

    /**
     * Provides the application's system wide configuration data.
     *
     * @return the application's system wide configuration data.
     */
    @Provides WeakReference<RestfulServiceConfiguration> provideWeakConfiguration(
            RestfulServiceConfiguration configuration) {
        return new WeakReference<>(configuration);
    }

    /**
     * Provides the sorting options used by
     * {@link mx.com.adolfogarcia.popularmovies.model.view.MovieDetailViewModel}.
     *
     * @return the application's system wide configuration data.
     */
//    @Provides LabeledItem<FetchMoviePageTaskFactory>[] provideSortOrderOptions() {
//        return new LabeledItem[] {
//                new LabeledItem(mApplication.getString(R.string.label_sort_order_popular)
//                        , new FetchPopularityMoviePageTaskFactory(mConfiguration, mApplication))
//                , new LabeledItem(mApplication.getString(R.string.label_sort_order_vote_average)
//                        , new FetchRatingMoviePageTaskFactory(mConfiguration, mApplication))
//        };
//    }

}
