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

import java.util.ArrayList;
import java.util.List;

/**
 * Transfer object for the videos available for a movie (trailers, featurettes,
 * etc.) as returned by <a href="https://www.themoviedb.org/">themoviedb.org</a>'s
 * RESTful API.
 *
 * @see <a href="http://docs.themoviedb.apiary.io/">docs.themoviedb.apiary.io</a>
 * @author Jesús Adolfo García Pasquel
 */
public class MovieVideosJsonModel {

    /**
     * The movie's identifier in <a href="">themoviedb.org</a>.
     */
    @SerializedName("id")
    @Expose
    private Long mMovieId;

    /**
     * The videos available for a movie (trailers, featurettes, etc.).
     */
    @SerializedName("results")
    @Expose
    private List<VideoJsonModel> mVideos = new ArrayList<>();

    public Long getMovieId() {
        return mMovieId;
    }

    public void setMovieId(Long id) {
        this.mMovieId = id;
    }

    public List<VideoJsonModel> getVideos() {
        return mVideos;
    }

    public void setVideos(List<VideoJsonModel> results) {
        this.mVideos = results;
    }

    @Override
    public int hashCode() {
        final int initial = 109;
        final int multiplier = 19;
        return new HashCodeBuilder(initial, multiplier)
                .append(this.mMovieId)
                .append(this.mVideos)
                .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof MovieVideosJsonModel)) {
            return false;
        }
        MovieVideosJsonModel that = ((MovieVideosJsonModel) obj);
        return new EqualsBuilder()
                .append(this.mMovieId, that.mMovieId)
                .append(this.mVideos, that.mVideos)
                .isEquals();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("mMovieId", this.mMovieId)
                .append("mVideos", this.mVideos)
                .toString();
    }

}
