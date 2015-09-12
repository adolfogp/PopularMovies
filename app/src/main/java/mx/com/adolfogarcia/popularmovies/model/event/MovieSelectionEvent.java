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

package mx.com.adolfogarcia.popularmovies.model.event;

import mx.com.adolfogarcia.popularmovies.model.domain.Movie;

/**
 * Event that occurs when a movie is selected. For example, from a list of
 * movies, like the one handled by
 * {@link mx.com.adolfogarcia.popularmovies.model.view.MovieCollectionViewModel}.
 *
 * @author Jesús Adolfo García Pasquel
 */
public class MovieSelectionEvent {

    /**
     * The {@link Movie} that was selected.
     */
    private final Movie mMovie;

    /**
     * Creates a new {@link MovieSelectionEvent} for the {@link Movie} passed
     * as argument.
     *
     * @param movie the {@link Movie} that was selected.
     */
    public MovieSelectionEvent(Movie movie) {
        mMovie = movie;
    }

    /**
     * Returns the selected {@link Movie}.
     *
     * @return the selected {@link Movie}.
     */
    public Movie getSelectedMovie() {
        return mMovie;
    }

}
