
package mx.com.adolfogarcia.popularmovies.model.transport;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Transfer object for the configuration related to images, of
 * <a href="https://www.themoviedb.org/">themoviedb.org</a>'s RESTful API.
 *
 * @see <a href="http://docs.themoviedb.apiary.io/">docs.themoviedb.apiary.io</a>
 * @author Jesús Adolfo García Pasquel
 */
public class ImageConfigurationJsonModel {

    /**
     * Base URL used to access image files.
     */
    @SerializedName("base_url")
    @Expose
    private String mBaseUrl;

    /**
     * Base URL used to access image files over SSL.
     */
    @SerializedName("secure_base_url")
    @Expose
    private String mSecureBaseUrl;

    /**
     * Widths (in pixels) at which the backdrop images are available.
     */
    @SerializedName("backdrop_sizes")
    @Expose
    private List<String> mBackdropSizes = new ArrayList<>();

    /**
     * Widths (in pixels) at which the logo is available.
     */
    @SerializedName("logo_sizes")
    @Expose
    private List<String> mLogoSizes = new ArrayList<>();

    /**
     * Widths (in pixels) at which the image files of the posters are available.
     */
    @SerializedName("poster_sizes")
    @Expose
    private List<String> mPosterSizes = new ArrayList<>();

    /**
     * Widths and heights (in pixels) at which the profile pictures are available.
     */
    @SerializedName("profile_sizes")
    @Expose
    private List<String> mProfileSizes = new ArrayList<>();

    /**
     * Widths (in pixels) at which the still images are available.
     */
    @SerializedName("still_sizes")
    @Expose
    private List<String> mStillSizes = new ArrayList<>();

    public String getBaseUrl() {
        return mBaseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.mBaseUrl = baseUrl;
    }

    public String getSecureBaseUrl() {
        return mSecureBaseUrl;
    }

    public void setSecureBaseUrl(String secureBaseUrl) {
        this.mSecureBaseUrl = secureBaseUrl;
    }

    public List<String> getBackdropSizes() {
        return mBackdropSizes;
    }

    public void setBackdropSizes(List<String> backdropSizes) {
        this.mBackdropSizes = backdropSizes;
    }

    public List<String> getLogoSizes() {
        return mLogoSizes;
    }

    public void setLogoSizes(List<String> logoSizes) {
        this.mLogoSizes = logoSizes;
    }

    public List<String> getPosterSizes() {
        return mPosterSizes;
    }

    public void setPosterSizes(List<String> posterSizes) {
        this.mPosterSizes = posterSizes;
    }

    public List<String> getProfileSizes() {
        return mProfileSizes;
    }

    public void setProfileSizes(List<String> profileSizes) {
        this.mProfileSizes = profileSizes;
    }

    public List<String> getStillSizes() {
        return mStillSizes;
    }

    public void setStillSizes(List<String> stillSizes) {
        this.mStillSizes = stillSizes;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 59)
                .append(this.mBackdropSizes)
                .append(this.mBaseUrl)
                .append(this.mLogoSizes)
                .append(this.mPosterSizes)
                .append(this.mProfileSizes)
                .append(this.mSecureBaseUrl)
                .append(this.mStillSizes)
                .hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if ((obj instanceof ImageConfigurationJsonModel) == false) {
            return false;
        }
        ImageConfigurationJsonModel that = (ImageConfigurationJsonModel) obj;
        return new EqualsBuilder()
                .append(this.mBackdropSizes, that.mBackdropSizes)
                .append(this.mBaseUrl, that.mBaseUrl)
                .append(this.mLogoSizes, that.mLogoSizes)
                .append(this.mPosterSizes, that.mPosterSizes)
                .append(this.mProfileSizes, that.mProfileSizes)
                .append(this.mSecureBaseUrl, that.mSecureBaseUrl)
                .append(this.mStillSizes, that.mStillSizes)
                .isEquals();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("mBackdropSizes", this.mBackdropSizes)
                .append("mBaseUrl", this.mBaseUrl)
                .append("mLogoSizes", this.mLogoSizes)
                .append("mPosterSizes", this.mPosterSizes)
                .append("mProfileSizes", this.mProfileSizes)
                .append("mSecureBaseUrl", this.mSecureBaseUrl)
                .append("mStillSizes", this.mStillSizes)
                .toString();
    }

}
