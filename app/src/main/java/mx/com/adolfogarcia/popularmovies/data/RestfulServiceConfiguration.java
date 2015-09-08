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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import mx.com.adolfogarcia.popularmovies.R;

/**
 * Handles access to storage of
 * <a href="https://www.themoviedb.org/">themoviedb.org</a>'s RESTful API
 * configuration and provides utility methods related to it. The data is stored
 * in the application's {@link SharedPreferences}, using the keys defined as
 * constants. Utility methods are provided for storage and retrieval.
 *
 * @author  Jesús Adolfo García Pasquel
 */
public class RestfulServiceConfiguration {

    /**
     * Identifies the messages written to the log by this class.
     */
    private static final String LOG_TAG =
            RestfulServiceConfiguration.class.getSimpleName();

    /**
     * Key used to access the default value for the time when the configuration
     * was last updated, stored in the application's {@link SharedPreferences}.
     */
    private static final String PREFERENCES_KEY_LAST_UPDATE = "date_last_update";

    /**
     * Key used to access the default value for the time when the configuration
     * was last updated, from {@link #mConfigurationProperties}.
     */
    private static final String PROPERTIES_KEY_DEFAULT_LAST_UPDATE = "date_last_update";

    /**
     * <p>
     *   Key used to access the base URL used to retrieve images from the API,
     *   stored in the application's {@link SharedPreferences}. To build an
     *   image URL, 3 elements are required:
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
     */
    private static final String PREFERENCES_KEY_IMAGE_URL = "image_secure_base_url";

    /**
     * <p>
     *   Key used to access the default value for the base URL used to retrieve
     *   images from the API, stored in {@link #mConfigurationProperties}. To
     *   build an image URL, 3 elements are required:
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
     */
    private static final String PROPERTIES_KEY_DEFAULT_IMAGE_URL =
            "image_secure_base_url";

    /**
     * <p>
     *   Key used to access the set of available image sizes for the movie
     *   posters, on the application's {@link SharedPreferences}. To build an
     *   image URL, 3 elements are required:
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
     */
    private static final String PREFERENCES_KEY_POSTER_SIZES = "poster_sizes";

    /**
     * <p>
     *   Key used to access the default value for the set of available image
     *   sizes for the movie  posters, in {@link #mConfigurationProperties}. To
     *   build an image URL, 3 elements are required:
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
     */
    private static final String PROPERTIES_KEY_DEFAULT_POSTER_SIZES =
            "poster_sizes";

    /**
     * Character used to separate entry values in the properties that
     * contain default sets of values.
     */
    private static final String SET_ENTRY_SEPARATOR = ",";

    /**
     * <p>
     *   Key used to access the set of available image sizes for the movie
     *   backdrops, on the application's {@link SharedPreferences}. To build an
     *   image URL, 3 elements are required:
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
     */
    private static final String PREFERENCES_KEY_BACKDROP_SIZES = "backdrop_sizes";

    /**
     * <p>
     *   Key used to access the default value for the set of available image
     *   sizes for the movie  backdrops, in {@link #mConfigurationProperties}.
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
     */
    private static final String PROPERTIES_KEY_DEFAULT_BACKDROP_SIZES =
            "backdrop_sizes";

    /**
     *
     */
    private static final String PROPERTIES_KEY_API_ACCESS = "api_key";

    /**
     * The {@link Context} used to retrieve and store the configuration values.
     */
    private final Context mContext;

    /**
     * Holds default configuration values and the key required to access the
     * RESTful API.
     */
    private static final Properties mConfigurationProperties = new Properties();

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
            mConfigurationProperties.loadFromXML(is);
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
    public String getImageBaseUrl() {
        SharedPreferences settings =
                PreferenceManager.getDefaultSharedPreferences(mContext);
        return settings.getString(PREFERENCES_KEY_IMAGE_URL
                , PROPERTIES_KEY_DEFAULT_IMAGE_URL);
    }

    // FIXME: Add setters to store values in SharedPreferences and use in FetchConfigurationTask.

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
    private String getBestPosterSizeName(int dipWidth) {
        // TODO: Calculate best and set it in an instance variable to avoid recalculation
        int optimalWidthPixels = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP
                , dipWidth
                , mContext.getResources().getDisplayMetrics());
        SharedPreferences settings =
                PreferenceManager.getDefaultSharedPreferences(mContext);
        Set<String> apiPosterSizes =
                settings.getStringSet(PREFERENCES_KEY_POSTER_SIZES
                        , getDefaultSetValue(PROPERTIES_KEY_DEFAULT_POSTER_SIZES));

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
     * Returns the values stored in the specified property as a set,
     * considering item are separated by {@link #SET_ENTRY_SEPARATOR}.
     *
     * @param key the key to access the desired value from
     *     {@link #mConfigurationProperties}.
     * @return the values stored in the specified property as a set,
     *     considering item are separated by {@link #SET_ENTRY_SEPARATOR}.
     */
    private Set<String> getDefaultSetValue(String key) {
        String defaultSizesProperty = mConfigurationProperties.getProperty(key);
        return new HashSet<>(Arrays.asList(
                defaultSizesProperty.split(SET_ENTRY_SEPARATOR)));
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
        return getImageBaseUrl() + getBestPosterSizeName(dipWidth) + relativePath;
    }

    /**
     * Returns the key used to access the RESTful API provided by
     * <a href="http://www.themoviedb.org">themoviedb.org</a>.
     *
     * @return the key used to access the RESTful API provided by
     * <a href="http://www.themoviedb.org">themoviedb.org</a>.
     */
    public String getMovieApiKey() {
        return mConfigurationProperties.getProperty(PROPERTIES_KEY_API_ACCESS);
    }

}
