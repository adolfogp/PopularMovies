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
import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

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

    private final Configuration mConfiguration;

    /**
     * Creates a new instance of {@link PopularMoviesApplicationModule}, given the
     * application's context.
     *
     * @param application the application's context.
     */
    public PopularMoviesApplicationModule(PopularMoviesApplication application) {
        mApplication = application;
        mConfiguration = new Configuration(application);
    }

    /**
     * Provides the application's context for injection.
     *
     * @return the application's context.
     */
    @Provides @Singleton Application provideApplicationContext() {
        return mApplication;
    }

    // TODO: Change to use the @Inject annotation on Configuration
    /**
     * Provides the application's system wide configuration data.
     *
     * @return the application's system wide configuration data.
     */
    @Provides @Singleton Configuration provideConfiguration() {
        return mConfiguration;
    }

}
