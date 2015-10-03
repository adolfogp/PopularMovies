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

package mx.com.adolfogarcia.popularmovies.model.view;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import org.parceler.Parcel;

import mx.com.adolfogarcia.popularmovies.model.domain.Trailer;

/**
 * View model for the movie trailers. Provides data and behaviour.
 *
 * @author Jesús Adolfo García Pasquel
 */
@Parcel(Parcel.Serialization.BEAN)
public class MovieTrailerListItemViewModel {

    /**
     * Identifies the messages written to the log by this class.
     */
    private static final String LOG_TAG =
            MovieTrailerListItemViewModel.class.getSimpleName();

    /**
     * The movie trailer to be displayed.
     */
    private Trailer mTrailer;

    public Trailer getTrailer() {
        return mTrailer;
    }

    public void setTrailer(Trailer trailer) {
        mTrailer = trailer;
    }

    /**
     * Returns the name or title of the movie trailer.
     *
     * @return the name or title of the movie trailer.
     */
    public String getName() {
        return mTrailer.getName();
    }

    /**
     * Requests for the movie trailer to be played back. If no app on the device
     * can open it, nothing is done.
     *
     * @param view the {@link View} that was clicked.
     */
    public void onClick(View view) {
        Context context = view.getContext();
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(mTrailer.getVideoUri());
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        } else {
            Log.w(LOG_TAG, "Unable to open trailer. No application on device can open it.");
        }
    }

}
