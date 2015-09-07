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

package mx.com.adolfogarcia.popularmovies.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;

import mx.com.adolfogarcia.popularmovies.R;

/**
 * Handles storage of <a href="https://www.themoviedb.org/">themoviedb.org</a>'s
 * RESTful API configuration and provides utility methods related to it. The
 * data is stored in the application's {@link SharedPreferences}, using the
 * keys defined as constants. Utility methods are provided for storage and
 * retrieval.
 *
 * @author  Jesús Adolfo García Pasquel
 */
public class RestfulServiceConfiguration {

    /**
     * Identifies the messages written to the log by this class.
     */
    private static final String LOG_TAG = "";

    /**
     * Key used to access the time when the configuration was last updated.
     * @see #PREFERENCES_DEFAULT_LAST_UPDATE
     */
    public static final String PREFERENCES_KEY_LAST_UPDATE = "date_last_update";

    /**
     * Default value for the time when the configuration was last updated.
     * @see #PREFERENCES_KEY_LAST_UPDATE
     */
    public static final long PREFERENCES_DEFAULT_LAST_UPDATE = 0;

    /**
     * <p>
     *   Key used to access the base URL used to retrieve images from the API.
     *   To build an image URL, 3 elements are required:
     * </p>
     * <ul>
     *   <li>Base URL.</li>
     *   <li>Image size</li>
     *   <li>File path</li>
     * </ul>
     * <p>
     *   The first two are defined in the API's configuration, and the file path
     *   on the element the image belongs to.
     * </p>
     * @see #PREFERENCES_DEFAULT_LAST_UPDATE
     * @see #PREFERENCES_KEY_POSTER_SIZES
     * @see #PREFERENCES_DEFAULT_POSTER_SIZES
     */
    public static final String PREFERENCES_KEY_IMAGE_URL = "image_secure_base_url";

    /**
     * <p>
     *   Default value for the base URL used to retrieve images from the API.
     *   To build an image URL, 3 elements are required:
     * </p>
     * <ul>
     *   <li>Base URL.</li>
     *   <li>Image size</li>
     *   <li>File path</li>
     * </ul>
     * <p>
     *   The first two are defined in the API's configuration, and the file path
     *   on the element the image belongs to.
     * </p>
     * @see #PREFERENCES_DEFAULT_LAST_UPDATE
     * @see #PREFERENCES_KEY_POSTER_SIZES
     * @see #PREFERENCES_DEFAULT_POSTER_SIZES
     */
    public static final String PREFERENCES_DEFAULT_IMAGE_URL = "https://image.tmdb.org/t/p/";

    /**
     * <p>
     *   Key used to access the set of available image sizes for the movie posters.
     *   To build an image URL,3 elements are required:
     * </p>
     * <ul>
     *   <li>Base URL.</li>
     *   <li>Image size</li>
     *   <li>File path</li>
     * </ul>
     * <p>
     *   The first two are defined in the API's configuration and the file path on
     *   the element the image belongs to.
     * </p>
     * @see #PREFERENCES_DEFAULT_POSTER_SIZES
     * @see #PREFERENCES_KEY_IMAGE_URL
     * @see #PREFERENCES_DEFAULT_IMAGE_URL
     */
    public static final String PREFERENCES_KEY_POSTER_SIZES = "poster_sizes";

    /**
     * <p>
     *   Default value for the set of available image sizes for the movie posters.
     *   To build an image URL,3 elements are required:
     * </p>
     * <ul>
     *   <li>Base URL.</li>
     *   <li>Image size</li>
     *   <li>File path</li>
     * </ul>
     * <p>
     *   The first two are defined in the API's configuration and the file path
     *   on the element the image belongs to.
     * </p>
     * @see #PREFERENCES_KEY_POSTER_SIZES
     * @see #PREFERENCES_KEY_IMAGE_URL
     * @see #PREFERENCES_DEFAULT_IMAGE_URL
     */
    public static final Set<String> PREFERENCES_DEFAULT_POSTER_SIZES = null;

    /**
     * <p>
     *   Key used to access the set of available image sizes for the movie
     *   backdrops. To build an image URL, 3 elements are required:
     * </p>
     * <ul>
     *   <li>Base URL.</li>
     *   <li>Image size</li>
     *   <li>File path</li>
     * </ul>
     * <p>
     *   The first two are defined in the API's configuration and the file
     *   path on the element the image belongs to.
     * </p>
     * @see #PREFERENCES_DEFAULT_BACKDROP_SIZES
     * @see #PREFERENCES_KEY_IMAGE_URL
     * @see #PREFERENCES_DEFAULT_IMAGE_URL
     */
    public static final String PREFERENCES_KEY_BACKDROP_SIZES = "backdrop_sizes";

    /**
     * <p>
     *   Default value for the set of available image sizes for the movie
     *   backdrops. To build an image URL,3 elements are required:
     * </p>
     * <ul>
     *   <li>Base URL.</li>
     *   <li>Image size</li>
     *   <li>File path</li>
     * </ul>
     * <p>
     *   The first two are defined in the API's configuration and the file
     *   path on the element the image belongs to.
     * </p>
     * @see #PREFERENCES_KEY_BACKDROP_SIZES
     * @see #PREFERENCES_KEY_IMAGE_URL
     * @see #PREFERENCES_DEFAULT_IMAGE_URL
     */
    public static final Set<String> PREFERENCES_DEFAULT_BACKDROP_SIZES = null;

    /**
     * The {@link Context} used to retrieve and store the configuration values.
     */
    private final Context mContext;

    /**
     * Holds default configuration values and the key required to access the
     * RESTful API.
     */
    private static final Properties configurationProperties = new Properties();

    /**
     * Creates a new instance of {@link RestfulServiceConfiguration}
     * @param context
     */
    public RestfulServiceConfiguration(Context context) {
        this.mContext = context;
        initProperties();
    }

    private void initProperties() {
        InputStream is = null; // TODO: Use try-with-resources (Android Studio shows errors in spite of Retrolambda)
        try {
            is = mContext.getResources().openRawResource(R.raw.configuration);
            configurationProperties.loadFromXML(is);
        } catch (IOException ioe) {
            Log.e(LOG_TAG, "Failed to load configuration file.", ioe);
        } finally {
            try {
                is.close();
            } catch (IOException ioe) {
                Log.e(LOG_TAG, "Failed to close configuration file.", ioe);
            }
        }
    }

    /**
     * Returns the base URL used to retrieve images from the API or a default
     * value if one has not been previously stored.
     *
     * @return the base URL used to retrieve images from the API or a default
     *     value if one has not been previously stored.
     */
    public String getImageBaseUrl(Context context) {
        SharedPreferences settings =
                PreferenceManager.getDefaultSharedPreferences(context);
        return settings.getString(PREFERENCES_KEY_IMAGE_URL
                , PREFERENCES_DEFAULT_IMAGE_URL);
    }

    /**
     * Returns the name of the poster image size provided by
     * <a href="https://www.themoviedb.org/">themoviedb.org/</a>'s API that is
     * closest in size to the one passed as argument. Returns {@code null} if
     * none is found.
     *
     * @param dipWidth the desired width of the poster in <i>Device Independent
     *                 Pixels</i>.
     * @return the name of the size that fits the device best or or {@code null}
     *     if none is found.
     */
    public String getBestPosterSizeName(int dipWidth) {
        // TODO: Calculate best and set it in an instance variable to avoid recalculation
        int optimalWidthPixels = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP
                , dipWidth
                , mContext.getResources().getDisplayMetrics());
        SharedPreferences settings =
                PreferenceManager.getDefaultSharedPreferences(mContext);
        Set<String> apiPosterSizes = settings.getStringSet(PREFERENCES_KEY_POSTER_SIZES
                , PREFERENCES_DEFAULT_POSTER_SIZES);
        if (apiPosterSizes == null) {
            Log.e(LOG_TAG, "No poster sizes found");
            return null;
        }

        int minDiff = Integer.MAX_VALUE;
        String bestSizeName = null;
        for (String sizeName : apiPosterSizes) {
            try {
                int size = Integer.parseInt(sizeName.substring(1));
                int diff = Math.abs(optimalWidthPixels - size);
                if (minDiff > diff) {
                    minDiff = diff;
                    bestSizeName = sizeName;
                }
            } catch (NumberFormatException nfe) {
                Log.w(LOG_TAG, "Ignoring poster size: " + sizeName);
            }
        }
        return bestSizeName;
    }

    /**
     * Returns the URL for the movie poster image provided by
     * <a href="https://www.themoviedb.org/">themoviedb.org/</a> that is
     * closest in size to the one requested.
     *
     * @param relativePath relative path to the desired image in
     *     <a href="https://www.themoviedb.org/">themoviedb.org/</a>.
     * @param dipWidth the desired width of the poster in <i>Device Independent
     *     Pixels</i>.
     * @return the name of the size that fits the device best or or {@code null}
     *     if none is found.
     * @throws IllegalStateException if there is no configuration data available,
     *     and the URL cannot be built.
     *
     */
    public String getBestFittingPosterUrl(String relativePath, int dipWidth) {
        // TODO: Calculate best and cache results to avoid recalculation
        int optimalWidthPixels = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP
                , dipWidth
                , mContext.getResources().getDisplayMetrics());
        SharedPreferences settings =
                PreferenceManager.getDefaultSharedPreferences(mContext);
        Set<String> apiPosterSizes = settings.getStringSet(PREFERENCES_KEY_POSTER_SIZES
                , PREFERENCES_DEFAULT_POSTER_SIZES);
        if (apiPosterSizes == null) {
            Log.e(LOG_TAG, "No poster sizes found");
            return null;
        }

        int minDiff = Integer.MAX_VALUE;
        String bestSizeName = null;
        for (String sizeName : apiPosterSizes) {
            try {
                int size = Integer.parseInt(sizeName.substring(1));
                int diff = Math.abs(optimalWidthPixels - size);
                if (minDiff > diff) {
                    minDiff = diff;
                    bestSizeName = sizeName;
                }
            } catch (NumberFormatException nfe) {
                Log.w(LOG_TAG, "Ignoring poster size: " + sizeName);
            }
        }
        return bestSizeName;
    }

    /**
     * Returns the key used to access the RESTful API provided by
     * <a href="http://www.themoviedb.org">themoviedb.org</a>.
     *
     * @return the key used to access the RESTful API provided by
     * <a href="http://www.themoviedb.org">themoviedb.org</a>.
     */
    public String getMovieApiKey() {
        return configurationProperties.getProperty("apikey.themoviedb"); // TODO: Move key to constant and load defaults from properties.
    }

}
