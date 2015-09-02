
package mx.com.adolfogarcia.popularmovies.model.transport;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class DiscoverMoviePage {

    @Expose
    private Integer page;

    @Expose
    private List<Result> results = new ArrayList<>();

    @SerializedName("total_pages")
    @Expose
    private Integer totalPages;

    @SerializedName("total_results")
    @Expose
    private Integer totalResults;

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public Integer getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(Integer totalResults) {
        this.totalResults = totalResults;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("page", this.page)
                .append("results", this.results)
                .append("totalPages", this.totalPages)
                .append("totalResults", this.totalResults)
                .toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(23, 61)
                .append(this.page)
                .append(this.results)
                .append(this.totalPages)
                .append(this.totalResults)
                .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if ((obj instanceof DiscoverMoviePage) == false) {
            return false;
        }
        DiscoverMoviePage that = ((DiscoverMoviePage) obj);
        return new EqualsBuilder()
                .append(this.page, that.page)
                .append(this.results, that.results)
                .append(this.totalPages, that.totalPages)
                .append(this.totalResults, that.totalResults)
                .isEquals();
    }

}
