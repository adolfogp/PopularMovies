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
import static org.parceler.Parcel.Serialization;

/**
 * A feature film.
 *
 * @author Jesús Adolfo García Pasquel
 */
@Parcel(Serialization.BEAN)
public class Movie {

    /**
     * The movie's identifier.
     */
    private long mId;

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
    private Uri mPoster;

    /**
     * Uri of the movies backdrop image.
     */
    private Uri mBackdrop;

    /**
     * The movie's user rating.
     */
    private double mVoteAverage;

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

    public Uri getPoster() {
        return mPoster;
    }

    public void setPoster(Uri poster) {
        mPoster = poster;
    }

    public Uri getBackdrop() {
        return mBackdrop;
    }

    public void setBackdrop(Uri backdrop) {
        mBackdrop = backdrop;
    }

    public double getVoteAverage() {
        return mVoteAverage;
    }

    public void setVoteAverage(double voteAverage) {
        mVoteAverage = voteAverage;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(19, 137)
                .append(this.mId)
                .append(this.mOriginalTitle)
                .append(this.mReleaseDate)
                .append(this.mOverview)
                .append(this.mPoster)
                .append(this.mBackdrop)
                .append(this.mVoteAverage)
                .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if ((obj instanceof Movie) == false) {
            return false;
        }
        Movie that = ((Movie) obj);
        return new EqualsBuilder()
                .append(this.mId, that.mId)
                .append(this.mOriginalTitle, that.mOriginalTitle)
                .append(this.mReleaseDate, that.mReleaseDate)
                .append(this.mOverview, that.mOverview)
                .append(this.mPoster, that.mPoster)
                .append(this.mBackdrop, that.mBackdrop)
                .append(this.mVoteAverage, that.mVoteAverage)
                .isEquals();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("mId", this.mId)
                .append("mOriginalTitle", this.mOriginalTitle)
                .append("mReleaseDate", this.mReleaseDate)
                .append("mOverview", this.mOverview)
                .append("mPoster", this.mPoster)
                .append("mBackdrop", this.mBackdrop)
                .append("mVoteAverage", this.mVoteAverage)
                .toString();
    }

}
