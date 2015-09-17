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

import javax.inject.Singleton;

import dagger.Component;
import mx.com.adolfogarcia.popularmovies.model.view.MovieCollectionViewModel;
import mx.com.adolfogarcia.popularmovies.model.view.MovieDetailViewModel;
import mx.com.adolfogarcia.popularmovies.view.fragment.MovieCollectionFragment;

/**
 * Provides methods that inject dependencies into the object passed
 * as argument.
 *
 * @author Jesús Adolfo García Pasquel
 */
@Singleton
@Component(modules = PopularMoviesApplicationModule.class)
public interface ApplicationComponent {

    /**
     * Injects the dependencies required by the {@link MovieCollectionFragment},
     * like the RESTful API's configuration.
     *
     * @param movieCollectionFragment the object to inject dependencies into.
     */
    void inject(MovieCollectionFragment movieCollectionFragment);

    /**
     * Injects the dependencies required by the {@link MovieCollectionViewModel},
     * like the available sort orders.
     *
     * @param movieCollectionViewModel the object to inject dependencies into.
     */
    void inject(MovieCollectionViewModel movieCollectionViewModel);

    /**
     * Injects the dependencies required by the {@link MovieDetailViewModel},
     * like the RESTful API's configuration.
     *
     * @param movieDetailViewModel the object to inject dependencies into.
     */
    void inject(MovieDetailViewModel movieDetailViewModel);

}
