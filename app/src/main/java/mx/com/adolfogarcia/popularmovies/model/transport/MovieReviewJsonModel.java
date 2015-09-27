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

package mx.com.adolfogarcia.popularmovies.model.transport;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Transfer object for the review of a movie returned by
 * <a href="https://www.themoviedb.org/">themoviedb.org</a>'s RESTful API.
 *
 * @see <a href="http://docs.themoviedb.apiary.io/">docs.themoviedb.apiary.io</a>
 * @author Jesús Adolfo García Pasquel
 */
public class MovieReviewJsonModel {

    /**
     * The movie review's identifier in <a href="">themoviedb.org</a>.
     */
    @SerializedName("id")
    @Expose
    private String mId;

    /**
     * The name of the review's mAuthor.
     */
    @SerializedName("author")
    @Expose
    private String mAuthor;

    /**
     * The review's text.
     */
    @SerializedName("content")
    @Expose
    private String mContent;

    /**
     * The URL for the full review.
     */
    @SerializedName("url")
    @Expose
    private String mUrl;

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        this.mId = id;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public void setAuthor(String author) {
        this.mAuthor = author;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        this.mContent = content;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        this.mUrl = url;
    }

    @Override
    public int hashCode() {
        final int initial = 23;
        final int multiplier = 131;
        return new HashCodeBuilder(initial, multiplier)
                .append(this.mAuthor)
                .append(this.mContent)
                .append(this.mId)
                .append(this.mUrl)
                .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof MovieReviewJsonModel)) {
            return false;
        }
        MovieReviewJsonModel that = ((MovieReviewJsonModel) obj);
        return new EqualsBuilder()
                .append(this.mAuthor, that.mAuthor)
                .append(this.mContent, that.mContent)
                .append(this.mId, that.mId)
                .append(this.mUrl, that.mUrl)
                .isEquals();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("mAuthor", this.mAuthor)
                .append("mContent", this.mContent)
                .append("mId", this.mId)
                .append("mUrl", this.mUrl)
                .toString();
    }

}
