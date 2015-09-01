
package mx.com.adolfogarcia.popularmovies.model.transport;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * TODO: Document.
 * Created using <a href="http://www.jsonschema2pojo.org/">jsonschema2pojo</a>.
 *
 * @author Jesús Adolfo García Pasquel
 */
public class ImageConfigurationJsonModel {

    @SerializedName("base_url")
    @Expose
    private String baseUrl;

    @SerializedName("secure_base_url")
    @Expose
    private String secureBaseUrl;

    @SerializedName("backdrop_sizes")
    @Expose
    private List<String> backdropSizes = new ArrayList<>();

    @SerializedName("logo_sizes")
    @Expose
    private List<String> logoSizes = new ArrayList<>();

    @SerializedName("poster_sizes")
    @Expose
    private List<String> posterSizes = new ArrayList<>();

    @SerializedName("profile_sizes")
    @Expose
    private List<String> profileSizes = new ArrayList<>();

    @SerializedName("still_sizes")
    @Expose
    private List<String> stillSizes = new ArrayList<>();

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getSecureBaseUrl() {
        return secureBaseUrl;
    }

    public void setSecureBaseUrl(String secureBaseUrl) {
        this.secureBaseUrl = secureBaseUrl;
    }

    public List<String> getBackdropSizes() {
        return backdropSizes;
    }

    public void setBackdropSizes(List<String> backdropSizes) {
        this.backdropSizes = backdropSizes;
    }

    public List<String> getLogoSizes() {
        return logoSizes;
    }

    public void setLogoSizes(List<String> logoSizes) {
        this.logoSizes = logoSizes;
    }

    public List<String> getPosterSizes() {
        return posterSizes;
    }

    public void setPosterSizes(List<String> posterSizes) {
        this.posterSizes = posterSizes;
    }

    public List<String> getProfileSizes() {
        return profileSizes;
    }

    public void setProfileSizes(List<String> profileSizes) {
        this.profileSizes = profileSizes;
    }

    public List<String> getStillSizes() {
        return stillSizes;
    }

    public void setStillSizes(List<String> stillSizes) {
        this.stillSizes = stillSizes;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 59)
                .append(this.getBackdropSizes())
                .append(this.getBaseUrl())
                .append(this.getLogoSizes())
                .append(this.getPosterSizes())
                .append(this.getProfileSizes())
                .append(this.getSecureBaseUrl())
                .append(this.getStillSizes())
                .hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ImageConfigurationJsonModel)) {
            return false;
        }
        ImageConfigurationJsonModel that = (ImageConfigurationJsonModel) obj;
        return new EqualsBuilder()
                .append(this.getBackdropSizes(), that.getBackdropSizes())
                .append(this.getBaseUrl(), that.getBaseUrl())
                .append(this.getLogoSizes(), that.getLogoSizes())
                .append(this.getPosterSizes(), that.getPosterSizes())
                .append(this.getProfileSizes(), that.getProfileSizes())
                .append(this.getSecureBaseUrl(), that.getSecureBaseUrl())
                .append(this.getStillSizes(), that.getStillSizes())
                .isEquals();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("backdropSizes", this.getBackdropSizes())
                .append("baseUrl", this.getBaseUrl())
                .append("logoSizes", this.getLogoSizes())
                .append("posterSizes", this.getPosterSizes())
                .append("profileSizes", this.getProfileSizes())
                .append("secureBaseUrl", this.getSecureBaseUrl())
                .append("stillSizes", this.getStillSizes())
                .toString();
    }

}
