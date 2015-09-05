
package mx.com.adolfogarcia.popularmovies.model.transport;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Transfer object for the general configuration of
 * <a href="https://www.themoviedb.org/">themoviedb.org</a>'s RESTful API.
 *
 * @see <a href="http://docs.themoviedb.apiary.io/">docs.themoviedb.apiary.io</a>
 * @author Jesús Adolfo García Pasquel
 */
public class GeneralConfigurationJsonModel {

    /**
     * Configuration data related to imageConfiguration.
     */
    @SerializedName("images")
    @Expose
    private ImageConfigurationJsonModel mImageConfiguration;

    /**
     * Ignored.
     */
    @SerializedName("change_keys")
    @Expose
    private List<String> mChangeKeys = new ArrayList<>();

    public ImageConfigurationJsonModel getImageConfiguration() {
        return mImageConfiguration;
    }

    public void setImageConfiguration(ImageConfigurationJsonModel imageConfiguration) {
        this.mImageConfiguration = imageConfiguration;
    }

    public List<String> getChangeKeys() {
        return mChangeKeys;
    }

    public void setChangeKeys(List<String> changeKeys) {
        this.mChangeKeys = changeKeys;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(11, 109)
                .append(this.mChangeKeys)
                .append(this.mImageConfiguration)
                .hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if ((obj instanceof GeneralConfigurationJsonModel) == false) {
            return false;
        }
        GeneralConfigurationJsonModel that = (GeneralConfigurationJsonModel) obj;
        return new EqualsBuilder()
                .append(this.mChangeKeys, that.mChangeKeys)
                .append(this.mImageConfiguration, that.mImageConfiguration)
                .isEquals();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("mChangeKeys", this.mChangeKeys)
                .append("mImageConfiguration", this.mImageConfiguration)
                .toString();
    }

}
