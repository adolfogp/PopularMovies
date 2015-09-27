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
 * Transfer object for the pages of reviews of a movie returned by
 * <a href="https://www.themoviedb.org/">themoviedb.org</a>'s RESTful API.
 *
 * @see <a href="http://docs.themoviedb.apiary.io/">docs.themoviedb.apiary.io</a>
 * @author Jesús Adolfo García Pasquel
 */
public class MovieReviewPageJsonModel {

    /**
     * The movie's identifier in <a href="">themoviedb.org</a>.
     */
    @SerializedName("id")
    @Expose
    private Integer mMovieId;

    /**
     * The page's index.
     */
    @SerializedName("page")
    @Expose
    private Integer mPageNumber;

    /**
     * Reviews included in the page.
     */
    @SerializedName("results")
    @Expose
    private List<MovieReviewJsonModel> mReviews = new ArrayList<>();

    /**
     * The total number of pages of reviews available for the movie.
     */
    @SerializedName("total_pages")
    @Expose
    private Integer mTotalPages;

    /**
     * The total number of reviews available for the movie.
     */
    @SerializedName("total_results")
    @Expose
    private Integer mTotalResults;

    public Integer getMovieId() {
        return mMovieId;
    }

    public void setMovieId(Integer id) {
        this.mMovieId = id;
    }

    public Integer getPageNumber() {
        return mPageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.mPageNumber = pageNumber;
    }

    public List<MovieReviewJsonModel> getReviews() {
        return mReviews;
    }

    public void setReviews(List<MovieReviewJsonModel> reviews) {
        this.mReviews = reviews;
    }

    public Integer getTotalPages() {
        return mTotalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.mTotalPages = totalPages;
    }

    public Integer getTotalResults() {
        return mTotalResults;
    }

    public void setTotalResults(Integer totalResults) {
        this.mTotalResults = totalResults;
    }

    @Override
    public int hashCode() {
        final int initial = 139;
        final int multiplier = 7;
        return new HashCodeBuilder(initial, multiplier)
                .append(this.mMovieId)
                .append(this.mPageNumber)
                .append(this.mReviews)
                .append(this.mTotalPages)
                .append(this.mTotalResults)
                .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof MovieReviewPageJsonModel)) {
            return false;
        }
        MovieReviewPageJsonModel that = ((MovieReviewPageJsonModel) obj);
        return new EqualsBuilder()
                .append(this.mMovieId, that.mMovieId)
                .append(this.mPageNumber, that.mPageNumber)
                .append(this.mReviews, that.mReviews)
                .append(this.mTotalPages, that.mTotalPages)
                .append(this.mTotalResults, that.mTotalResults)
                .isEquals();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("mMovieId", this.mMovieId)
                .append("mPageNumber", this.mPageNumber)
                .append("mReviews", this.mReviews)
                .append("mTotalPages", this.mTotalPages)
                .append("mTotalResults", this.mTotalResults)
                .toString();
    }

}
