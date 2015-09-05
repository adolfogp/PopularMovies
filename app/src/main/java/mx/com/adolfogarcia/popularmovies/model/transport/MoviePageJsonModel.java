
package mx.com.adolfogarcia.popularmovies.model.transport;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Transfer object for the pages of movie data returned by
 * <a href="https://www.themoviedb.org/">themoviedb.org</a>'s RESTful API.
 *
 * @see <a href="http://docs.themoviedb.apiary.io/">docs.themoviedb.apiary.io</a>
 * @author Jesús Adolfo García Pasquel
 */
public class MoviePageJsonModel {

    /**
     * The page's index.
     */
    @SerializedName("page")
    @Expose
    private Integer mPageNumber;

    /**
     * The mMovies included in the page of movie data.
     */
    @SerializedName("results")
    @Expose
    private List<MovieJsonModel> mMovies = new ArrayList<>();

    /**
     * The total number of pages available from
     * <a href="https://www.themoviedb.org/">themoviedb.org</a>.
     */
    @SerializedName("total_pages")
    @Expose
    private Integer mTotalPages;

    /**
     * The total number of movies available from
     * <a href="https://www.themoviedb.org/">themoviedb.org</a>.
     */
    @SerializedName("total_results")
    @Expose
    private Integer mTotalMovies;

    public Integer getPageNumber() {
        return mPageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.mPageNumber = pageNumber;
    }

    public List<MovieJsonModel> getMovies() {
        return mMovies;
    }

    public void setMovies(List<MovieJsonModel> movies) {
        this.mMovies = movies;
    }

    public Integer getTotalPages() {
        return mTotalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.mTotalPages = totalPages;
    }

    public Integer getTotalMovies() {
        return mTotalMovies;
    }

    public void setTotalMovies(Integer totalMovies) {
        this.mTotalMovies = totalMovies;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("mPageNumber", this.mPageNumber)
                .append("mMovies", this.mMovies)
                .append("mTotalPages", this.mTotalPages)
                .append("mTotalMovies", this.mTotalMovies)
                .toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(23, 61)
                .append(this.mPageNumber)
                .append(this.mMovies)
                .append(this.mTotalPages)
                .append(this.mTotalMovies)
                .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if ((obj instanceof MoviePageJsonModel) == false) {
            return false;
        }
        MoviePageJsonModel that = ((MoviePageJsonModel) obj);
        return new EqualsBuilder()
                .append(this.mPageNumber, that.mPageNumber)
                .append(this.mMovies, that.mMovies)
                .append(this.mTotalPages, that.mTotalPages)
                .append(this.mTotalMovies, that.mTotalMovies)
                .isEquals();
    }

}
