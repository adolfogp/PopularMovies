
package mx.com.adolfogarcia.popularmovies.model.transport;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.List;

public class Result {

    @Expose
    private Boolean adult;

    @SerializedName("backdrop_path")
    @Expose
    private String backdropPath;

    @SerializedName("genre_ids")
    @Expose
    private List<Integer> genreIds = new ArrayList<>();

    @Expose
    private Integer id;

    @SerializedName("original_language")
    @Expose
    private String originalLanguage;

    @SerializedName("original_title")
    @Expose
    private String originalTitle;

    @Expose
    private String overview;

    @SerializedName("release_date")
    @Expose
    private String releaseDate;

    @SerializedName("poster_path")
    @Expose
    private String posterPath;

    @Expose
    private Double popularity;

    @Expose
    private String title;

    @Expose
    private Boolean video;

    @SerializedName("vote_average")
    @Expose
    private Double voteAverage;

    @SerializedName("vote_count")
    @Expose
    private Integer voteCount;

    public Boolean getAdult() {
        return adult;
    }

    public void setAdult(Boolean adult) {
        this.adult = adult;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public List<Integer> getGenreIds() {
        return genreIds;
    }

    public void setGenreIds(List<Integer> genreIds) {
        this.genreIds = genreIds;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public void setOriginalLanguage(String originalLanguage) {
        this.originalLanguage = originalLanguage;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public Double getPopularity() {
        return popularity;
    }

    public void setPopularity(Double popularity) {
        this.popularity = popularity;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getVideo() {
        return video;
    }

    public void setVideo(Boolean video) {
        this.video = video;
    }

    public Double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(Double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public Integer getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(Integer voteCount) {
        this.voteCount = voteCount;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("adult", this.adult)
                .append("backdropPath", this.backdropPath)
                .append("genreIds", this.genreIds)
                .append("id", this.id)
                .append("originalLanguage", this.originalLanguage)
                .append("originalTitle", this.originalTitle)
                .append("overview", this.overview)
                .append("popularity", this.popularity)
                .append("posterPath", this.posterPath)
                .append("releaseDate", this.releaseDate)
                .append("title", this.title)
                .append("video", this.video)
                .append("voteAverage", this.voteAverage)
                .append("voteCount", this.voteCount)
                .toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(23, 131)
                .append(this.adult)
                .append(this.backdropPath)
                .append(this.genreIds)
                .append(this.id)
                .append(this.originalLanguage)
                .append(this.originalTitle)
                .append(this.overview)
                .append(this.releaseDate)
                .append(this.posterPath)
                .append(this.popularity)
                .append(this.title)
                .append(this.video)
                .append(this.voteAverage)
                .append(this.voteCount)
                .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if ((obj instanceof Result) == false) {
            return false;
        }
        Result that = ((Result) obj);
        return new EqualsBuilder()
                .append(this.adult, that.adult)
                .append(this.backdropPath, that.backdropPath)
                .append(this.genreIds, that.genreIds)
                .append(this.id, that.id)
                .append(this.originalLanguage, that.originalLanguage)
                .append(this.originalTitle, that.originalTitle)
                .append(this.overview, that.overview)
                .append(this.releaseDate, that.releaseDate)
                .append(this.posterPath, that.posterPath)
                .append(this.popularity, that.popularity)
                .append(this.title, that.title)
                .append(this.video, that.video)
                .append(this.voteAverage, that.voteAverage)
                .append(this.voteCount, that.voteCount)
                .isEquals();
    }

}
