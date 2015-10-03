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

import mx.com.adolfogarcia.popularmovies.model.domain.Review;

/**
 * View model for the movie reviews. Provides data and behaviour.
 *
 * @author Jesús Adolfo García Pasquel
 */
@Parcel(Parcel.Serialization.BEAN)
public class MovieReviewListItemViewModel {

    /**
     * Identifies the messages written to the log by this class.
     */
    private static final String LOG_TAG =
            MovieReviewListItemViewModel.class.getSimpleName();

    /**
     * The review data to be displayed.
     */
    private Review mReview;

    public Review getReview() {
        return mReview;
    }

    public void setReview(Review review) {
        mReview = review;
    }

    /**
     * Returns the name of the review's author.
     *
     * @return the name of the review's author.
     */
    public String getAuthor() {
        return mReview.getAuthor();
    }

    /**
     * Returns the review's text.
     *
     * @return the review's text.
     */
    public String getContent() {
        return  mReview.getContent();
    }

    /**
     * Requests for the web page containing the original review to be opened.
     * If no app on the device can open the page, nothing is done.
     *
     * @param view the {@link View} that was clicked.
     */
    public void onClick(View view) {
        Context context = view.getContext();
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(mReview.getSourceUri());
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        } else {
            Log.w(LOG_TAG, "Unable to open review. No application on device can open it.");
        }
    }

}
