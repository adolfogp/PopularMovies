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

package mx.com.adolfogarcia.popularmovies.model.domain;

import android.net.Uri;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.parceler.Parcel;

import java.util.List;

import static org.parceler.Parcel.Serialization;

/**
 * A feature film.
 *
 * @author Jesús Adolfo García Pasquel
 */
@Parcel(Serialization.BEAN)
public class Movie {

    /**
     * The movie's local identifier.
     */
    private long mId;

    /**
     * The movie's identifier in
     * <a href="https://www.themoviedb.org/">themoviedb.org</a>'s RESTful API.
     */
    private long mApiId;

    /**
     * The movie's original title.
     */
    private  String mOriginalTitle;

    /**
     * Date the movie was released in Epoch time.
     */
    private long mReleaseDate;

    /**
     * The plot synopsis.
     */
    private String mOverview;

    /**
     * Uri of the movie's poster image.
     */
    private Uri mPosterUri;

    /**
     * Uri of the movies backdrop image.
     */
    private Uri mBackdropUri;

    /**
     * The movie's user rating.
     */
    private double mVoteAverage;

    /**
     * Collection of trailers available for the feature film.
     */
    private List<Trailer> mTrailers;

    /**
     * Collection of reviews available for the feature film.
     */
    private List<Review> mReviews;

    /**
     * Indicates if the movie is one of the user's favorites.
     */
    private boolean mUserFavorite;

    /**
     * Creates a new instance of {@link Movie} with the default values for
     * all its attributes.
     */
    public Movie() {
        // Empty bean constructor.
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public long getApiId() {
        return mApiId;
    }

    public void setApiId(long apiId) {
        mApiId = apiId;
    }

    public String getOriginalTitle() {
        return mOriginalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        mOriginalTitle = originalTitle;
    }

    public long getReleaseDate() {
        return mReleaseDate;
    }

    public void setReleaseDate(long releaseDate) {
        mReleaseDate = releaseDate;
    }

    public String getOverview() {
        return mOverview;
    }

    public void setOverview(String overview) {
        mOverview = overview;
    }

    public Uri getPosterUri() {
        return mPosterUri;
    }

    public void setPosterUri(Uri posterUri) {
        mPosterUri = posterUri;
    }

    public Uri getBackdropUri() {
        return mBackdropUri;
    }

    public void setBackdropUri(Uri backdropUri) {
        mBackdropUri = backdropUri;
    }

    public double getVoteAverage() {
        return mVoteAverage;
    }

    public void setVoteAverage(double voteAverage) {
        mVoteAverage = voteAverage;
    }

    public List<Trailer> getTrailers() {
        return mTrailers;
    }

    public void setTrailers(List<Trailer> trailers) {
        mTrailers = trailers;
    }

    public List<Review> getReviews() {
        return mReviews;
    }

    public void setReviews(List<Review> reviews) {
        mReviews = reviews;
    }

    public boolean isUserFavorite() {
        return mUserFavorite;
    }

    public void setUserFavorite(boolean userFavorite) {
        this.mUserFavorite = userFavorite;
    }

    @Override
    public int hashCode() {
        final int initial = 19;
        final int multiplier = 137;
        return new HashCodeBuilder(initial, multiplier)
                .append(this.mId)
                .append(this.mOriginalTitle)
                .append(this.mReleaseDate)
                .append(this.mOverview)
                .append(this.mPosterUri)
                .append(this.mBackdropUri)
                .append(this.mVoteAverage)
                .append(this.mTrailers)
                .append(this.mReviews)
                .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Movie)) {
            return false;
        }
        Movie that = ((Movie) obj);
        return new EqualsBuilder()
                .append(this.mId, that.mId)
                .append(this.mOriginalTitle, that.mOriginalTitle)
                .append(this.mReleaseDate, that.mReleaseDate)
                .append(this.mOverview, that.mOverview)
                .append(this.mPosterUri, that.mPosterUri)
                .append(this.mBackdropUri, that.mBackdropUri)
                .append(this.mVoteAverage, that.mVoteAverage)
                .append(this.mTrailers, that.mTrailers)
                .append(this.mReviews, that.mReviews)
                .isEquals();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("mId", this.mId)
                .append("mOriginalTitle", this.mOriginalTitle)
                .append("mReleaseDate", this.mReleaseDate)
                .append("mOverview", this.mOverview)
                .append("mPosterUri", this.mPosterUri)
                .append("mBackdropUri", this.mBackdropUri)
                .append("mVoteAverage", this.mVoteAverage)
                .append("mTrailers", this.mTrailers)
                .append("mReviews", this.mReviews)
                .toString();
    }

}
