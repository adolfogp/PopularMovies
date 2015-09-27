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
 * Transfer object for a movie-related video (trailer, featurette, etc.) as
 * returned by <a href="https://www.themoviedb.org/">themoviedb.org</a>'s
 * RESTful API.
 *
 * @see <a href="http://docs.themoviedb.apiary.io/">docs.themoviedb.apiary.io</a>
 * @author Jesús Adolfo García Pasquel
 */
public class VideoJsonModel {

    /**
     * The video's identifier in <a href="">themoviedb.org</a>.
     */
    @SerializedName("id")
    @Expose
    private String mId;

    /**
     * The video's language in ISO 639.
     */
    @SerializedName("iso_639_1")
    @Expose
    private String mLanguage;

    /**
     * The mKey used to retrieve the video from the website it is posted at.
     * @see #mSite
     */
    @SerializedName("key")
    @Expose
    private String mKey;

    /**
     * The video's mName.
     */
    @SerializedName("name")
    @Expose
    private String mName;

    /**
     * The website where the video can be retrieved from.
     * @see #mKey
     */
    @SerializedName("site")
    @Expose
    private String mSite;

    /**
     * The video's resolution (e.g. {@code 1080}).
     */
    @SerializedName("size")
    @Expose
    private Integer mSize;

    /**
     * How the video is related to the movie (e.g. {@code Trailer}).
     */
    @SerializedName("type")
    @Expose
    private String mType;

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        this.mId = id;
    }

    public String getLanguage() {
        return mLanguage;
    }

    public void setLanguage(String language) {
        this.mLanguage = language;
    }

    public String getKey() {
        return mKey;
    }

    public void setKey(String key) {
        this.mKey = key;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getSite() {
        return mSite;
    }

    public void setSite(String site) {
        this.mSite = site;
    }

    public Integer getSize() {
        return mSize;
    }

    public void setSize(Integer size) {
        this.mSize = size;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        this.mType = type;
    }

    @Override
    public int hashCode() {
        final int initial = 11;
        final int multiplier = 83;
        return new HashCodeBuilder(initial, multiplier)
                .append(this.mId)
                .append(this.mKey)
                .append(this.mLanguage)
                .append(this.mName)
                .append(this.mSite)
                .append(this.mSize)
                .append(this.mType)
                .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof VideoJsonModel)) {
            return false;
        }
        VideoJsonModel that = ((VideoJsonModel) obj);
        return new EqualsBuilder()
                .append(this.mId, that.mId)
                .append(this.mKey, that.mKey)
                .append(this.mLanguage, that.mLanguage)
                .append(this.mName, that.mName)
                .append(this.mSite, that.mSite)
                .append(this.mSize, that.mSize)
                .append(this.mType, that.mType)
                .isEquals();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("mId", this.mId)
                .append("mKey", this.mKey)
                .append("mLanguage", this.mLanguage)
                .append("mName", this.mName)
                .append("mSite", this.mSite)
                .append("mSize", this.mSize)
                .append("mType", this.mType)
                .toString();
    }

}
