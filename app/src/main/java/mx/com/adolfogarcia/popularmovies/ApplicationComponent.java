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
import mx.com.adolfogarcia.popularmovies.view.fragment.MovieDetailFragment;

@Singleton
@Component(modules = PopularMoviesApplicationModule.class)
public interface ApplicationComponent {

    void inject(MovieCollectionFragment movieCollectionFragment);

    void inject(MovieCollectionViewModel movieCollectionViewModel);

    void inject(MovieDetailViewModel movieDetailViewModel);

}
