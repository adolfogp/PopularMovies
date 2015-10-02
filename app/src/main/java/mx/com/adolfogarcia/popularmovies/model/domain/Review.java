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

/**
 * A an assessment or critique of a feature film.
 *
 * @author Jesús Adolfo García Pasquel
 */
@Parcel(Parcel.Serialization.BEAN)
public class Review {

    /**
     * The review's local identifier.
     */
    private long mId;

    /**
     * The review's identifier in
     * <a href="https://www.themoviedb.org/">themoviedb.org</a>'s RESTful API.
     */
    private String mApiId;

    /**
     * The name of the review's mAuthor.
     */
    private String mAuthor;

    /**
     * The review's text.
     */
    private String mContent;

    /**
     * Address where the review can be accessed.
     */
    private Uri mSourceUri;

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public String getApiId() {
        return mApiId;
    }

    public void setApiId(String apiId) {
        mApiId = apiId;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public void setAuthor(String author) {
        mAuthor = author;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }

    public Uri getSourceUri() {
        return mSourceUri;
    }

    public void setSourceUri(Uri sourceUri) {
        this.mSourceUri = sourceUri;
    }

    @Override
    public int hashCode() {
        final int initial = 53;
        final int multiplier = 151;
        return new HashCodeBuilder(initial, multiplier)
                .append(this.mId)
                .append(this.mApiId)
                .append(this.mAuthor)
                .append(this.mContent)
                .append(this.mSourceUri)
                .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Review)) {
            return false;
        }
        Review that = ((Review) obj);
        return new EqualsBuilder()
                .append(this.mId, that.mId)
                .append(this.mApiId, that.mApiId)
                .append(this.mAuthor, that.mAuthor)
                .append(this.mContent, that.mContent)
                .append(this.mSourceUri, that.mSourceUri)
                .isEquals();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("mId", this.mId)
                .append("mApiId", this.mApiId)
                .append("mAuthor", this.mAuthor)
                .append("mContent", this.mContent)
                .append("mSourceUri", this.mSourceUri)
                .toString();
    }

}
