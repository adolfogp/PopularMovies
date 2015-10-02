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
 * A video preview of a feature film, that can be watched on
 * <a href="https://www.youtube.com/">YouTube</a>.
 *
 * @author Jesús Adolfo García Pasquel
 */
@Parcel(Parcel.Serialization.BEAN)
public class Trailer {

    /**
     * The trailer's local identifier.
     */
    private long mId;

    /**
     * The trailer's identifier in
     * <a href="https://www.themoviedb.org/">themoviedb.org</a>'s RESTful API.
     */
    private long mApiId;

    /**
     * The trailer's name or title.
     */
    private String mName;

    /**
     * Address where the movie trailer video can be accessed.
     */
    private Uri mVideoUri;

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

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public Uri getVideoUri() {
        return mVideoUri;
    }

    public void setVideoUri(Uri videoUri) {
        mVideoUri = videoUri;
    }

    @Override
    public int hashCode() {
        final int initial = 157;
        final int multiplier = 23;
        return new HashCodeBuilder(initial, multiplier)
                .append(this.mId)
                .append(this.mApiId)
                .append(this.mName)
                .append(this.mVideoUri)
                .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Trailer)) {
            return false;
        }
        Trailer that = ((Trailer) obj);
        return new EqualsBuilder()
                .append(this.mId, that.mId)
                .append(this.mApiId, that.mApiId)
                .append(this.mName, that.mName)
                .append(this.mVideoUri, that.mVideoUri)
                .isEquals();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("mId", this.mId)
                .append("mApiId", this.mApiId)
                .append("mName", this.mName)
                .append("mVideoUri", this.mVideoUri)
                .toString();
    }

}
