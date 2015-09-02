
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
public class GeneralConfigurationJsonModel {

    @Expose
    private ImageConfigurationJsonModel images;

    @SerializedName("change_keys")
    @Expose
    private List<String> changeKeys = new ArrayList<>();

    public ImageConfigurationJsonModel getImages() {
        return images;
    }

    public void setImages(ImageConfigurationJsonModel images) {
        this.images = images;
    }

    public List<String> getChangeKeys() {
        return changeKeys;
    }

    public void setChangeKeys(List<String> changeKeys) {
        this.changeKeys = changeKeys;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(11, 109)
                .append(this.changeKeys)
                .append(this.images)
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
                .append(this.changeKeys, that.changeKeys)
                .append(this.images, that.images)
                .isEquals();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("changeKeys", this.changeKeys)
                .append("images", this.images)
                .toString();
    }

}
