
package mx.com.adolfogarcia.popularmovies.model.transport;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Transfer object for the movie data returned by
 * <a href="https://www.themoviedb.org/">themoviedb.org</a>'s RESTful API.
 *
 * @see <a href="http://docs.themoviedb.apiary.io/">docs.themoviedb.apiary.io</a>
 * @author Jesús Adolfo García Pasquel
 */
public class MovieJsonModel {

    /**
     * Identifies if the movie is adult content.
     */
    @SerializedName("adult")
    @Expose
    private Boolean mAdult;

    /**
     * Relative path to the backdrop images.
     * @see ImageConfigurationJsonModel
     */
    @SerializedName("backdrop_path")
    @Expose
    private String mBackdropPath;

    /**
     * Identifiers of the genres the movie belongs to.
     */
    @SerializedName("genre_ids")
    @Expose
    private List<Integer> mGenreIds = new ArrayList<>();

    /**
     * The movie's identifier in <a href="">themoviedb.org</a>.
     */
    @SerializedName("id")
    @Expose
    private Integer mId;

    /**
     * ISO 639-2 code of the film's language.
     */
    @SerializedName("original_language")
    @Expose
    private String mOriginalLanguage;

    /**
     * The movie's original title.
     */
    @SerializedName("original_title")
    @Expose
    private String mOriginalTitle;

    /**
     * The plot synopsis.
     */
    @SerializedName("overview")
    @Expose
    private String mOverview;

    /**
     * Date the movie was released.
     */
    @SerializedName("release_date")
    @Expose
    private String mReleaseDate;

    /**
     * Relative path to the poster images.
     * @see ImageConfigurationJsonModel
     */
    @SerializedName("poster_path")
    @Expose
    private String mPosterPath;

    /**
     * The movie's popularity rating.
     */
    @SerializedName("popularity")
    @Expose
    private Double mPopularity;

    /**
     * The movie's title.
     */
    @SerializedName("title")
    @Expose
    private String mTitle;

    /**
     * Identifies a movie as a video.
     */
    @SerializedName("video")
    @Expose
    private Boolean mVideo;

    /**
     * The movie's user rating.
     */
    @SerializedName("vote_average")
    @Expose
    private Double mVoteAverage;

    /**
     * The number of votes the movie has received from users.
     */
    @SerializedName("vote_count")
    @Expose
    private Integer mVoteCount;

    public Boolean getAdult() {
        return mAdult;
    }

    public void setAdult(Boolean adult) {
        this.mAdult = adult;
    }

    public String getBackdropPath() {
        return mBackdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.mBackdropPath = backdropPath;
    }

    public List<Integer> getGenreIds() {
        return mGenreIds;
    }

    public void setGenreIds(List<Integer> genreIds) {
        this.mGenreIds = genreIds;
    }

    public Integer getId() {
        return mId;
    }

    public void setId(Integer id) {
        this.mId = id;
    }

    public String getOriginalLanguage() {
        return mOriginalLanguage;
    }

    public void setOriginalLanguage(String originalLanguage) {
        this.mOriginalLanguage = originalLanguage;
    }

    public String getOriginalTitle() {
        return mOriginalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.mOriginalTitle = originalTitle;
    }

    public String getOverview() {
        return mOverview;
    }

    public void setOverview(String overview) {
        this.mOverview = overview;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.mReleaseDate = releaseDate;
    }

    public String getPosterPath() {
        return mPosterPath;
    }

    public void setPosterPath(String posterPath) {
        this.mPosterPath = posterPath;
    }

    public Double getPopularity() {
        return mPopularity;
    }

    public void setPopularity(Double popularity) {
        this.mPopularity = popularity;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public Boolean getVideo() {
        return mVideo;
    }

    public void setVideo(Boolean video) {
        this.mVideo = video;
    }

    public Double getVoteAverage() {
        return mVoteAverage;
    }

    public void setVoteAverage(Double voteAverage) {
        this.mVoteAverage = voteAverage;
    }

    public Integer getVoteCount() {
        return mVoteCount;
    }

    public void setVoteCount(Integer voteCount) {
        this.mVoteCount = voteCount;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("mAdult", this.mAdult)
                .append("mBackdropPath", this.mBackdropPath)
                .append("mGenreIds", this.mGenreIds)
                .append("mId", this.mId)
                .append("mOriginalLanguage", this.mOriginalLanguage)
                .append("mOriginalTitle", this.mOriginalTitle)
                .append("mOverview", this.mOverview)
                .append("mPopularity", this.mPopularity)
                .append("mPosterPath", this.mPosterPath)
                .append("mReleaseDate", this.mReleaseDate)
                .append("mTitle", this.mTitle)
                .append("mVideo", this.mVideo)
                .append("mVoteAverage", this.mVoteAverage)
                .append("mVoteCount", this.mVoteCount)
                .toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(23, 131)
                .append(this.mAdult)
                .append(this.mBackdropPath)
                .append(this.mGenreIds)
                .append(this.mId)
                .append(this.mOriginalLanguage)
                .append(this.mOriginalTitle)
                .append(this.mOverview)
                .append(this.mReleaseDate)
                .append(this.mPosterPath)
                .append(this.mPopularity)
                .append(this.mTitle)
                .append(this.mVideo)
                .append(this.mVoteAverage)
                .append(this.mVoteCount)
                .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if ((obj instanceof MovieJsonModel) == false) {
            return false;
        }
        MovieJsonModel that = ((MovieJsonModel) obj);
        return new EqualsBuilder()
                .append(this.mAdult, that.mAdult)
                .append(this.mBackdropPath, that.mBackdropPath)
                .append(this.mGenreIds, that.mGenreIds)
                .append(this.mId, that.mId)
                .append(this.mOriginalLanguage, that.mOriginalLanguage)
                .append(this.mOriginalTitle, that.mOriginalTitle)
                .append(this.mOverview, that.mOverview)
                .append(this.mReleaseDate, that.mReleaseDate)
                .append(this.mPosterPath, that.mPosterPath)
                .append(this.mPopularity, that.mPopularity)
                .append(this.mTitle, that.mTitle)
                .append(this.mVideo, that.mVideo)
                .append(this.mVoteAverage, that.mVoteAverage)
                .append(this.mVoteCount, that.mVoteCount)
                .isEquals();
    }

}
