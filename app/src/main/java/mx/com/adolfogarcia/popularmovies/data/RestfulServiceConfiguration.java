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
import android.support.v4.util.SparseArrayCompat;
import android.util.Log;
import android.util.TypedValue;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import mx.com.adolfogarcia.popularmovies.R;
import mx.com.adolfogarcia.popularmovies.model.transport.ImageConfigurationJsonModel;

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
     * Key used to retrieve the hash required to access the RESTful API, from
     * {@link #mConfigurationProperties}.
     */
    private static final String PROPERTIES_KEY_API_ACCESS = "api_key";

    /**
     * Key used to access the number of the page last retrieved from the RESTful
     * API, as stored in the {@link SharedPreferences}.
     */
    private static final String PREFERENCES_KEY_LAST_MOVIE_PAGE_RETRIEVED =
            "last_movie_page_retrieved";

    /**
     * Key used to access the total number of movie pages available in the RESTful
     * API, as stored in the {@link SharedPreferences}.
     */
    private static final String PREFERENCES_KEY_TOTAL_MOVIE_PAGES_AVAILABLE =
            "total_movie_pages_available";

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
     * Used to cache image size code names that best match sizes measured in
     * <i>Device Independent Pixels</i>. The code names are those used by
     * <a href="https://www.themoviedb.org/">themoviedb.org</a>'s RESTful API.
     */
    private SparseArrayCompat<String> mBestImageSizeNameCache =
            new SparseArrayCompat<>(2);

    /**
     * Creates a new instance of {@link RestfulServiceConfiguration}
     * @param context the {@link Context} used to access the application's
     *                {@link SharedPreferences}.
     */
    public RestfulServiceConfiguration(Context context) {
        this.mContext = context;
        initProperties();
    }

    private void initProperties() {
        InputStream is = null;
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
                , mConfigurationProperties.getProperty(PROPERTIES_KEY_DEFAULT_IMAGE_URL));
    }

    /**
     * Sets the base URL used to retrieve images from the API.
     *
     * @param url the base URL to be used for image retrieval.
     */
    public void setImageBaseUrl(String url) {
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PREFERENCES_KEY_IMAGE_URL, url);
        editor.putLong(PREFERENCES_KEY_LAST_UPDATE, System.currentTimeMillis());
        editor.apply();
    }

    /**
     * Returns the set of available image sizes for the movie posters. The
     * sizes are specified by the code names used in
     * <a href="https://www.themoviedb.org/">themoviedb.org</a>'s RESTful API
     * (usually the width in pixels, prefixed by the character 'w').
     *
     * @return the set of available image sizes for the movie posters as
     *     previously saved, or the default values.
     */
    public Set<String> getPosterSizes() {
        SharedPreferences settings =
                PreferenceManager.getDefaultSharedPreferences(mContext);
        return settings.getStringSet(PREFERENCES_KEY_POSTER_SIZES
                , getDefaultSetValue(PROPERTIES_KEY_DEFAULT_POSTER_SIZES));
    }

    /**
     * Sets the collection of available image sizes for the movie posters. The
     * sizes must be specified by the code names used in
     * <a href="https://www.themoviedb.org/">themoviedb.org</a>'s RESTful API
     * (usually the width in pixels, prefixed by the character 'w').
     *
     * @param posterSizes the collection of available image sizes for the movie
     *                    posters.
     */
    public void setPosterSizes(Set<String> posterSizes) {
        SharedPreferences settings =
                PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = settings.edit();
        editor.putStringSet(PREFERENCES_KEY_POSTER_SIZES, posterSizes);
        editor.putLong(PREFERENCES_KEY_LAST_UPDATE, System.currentTimeMillis());
        editor.apply();
    }

    /**
     * Returns the set of available image sizes for the movie backdrops. The
     * sizes are specified by the code names used in
     * <a href="https://www.themoviedb.org/">themoviedb.org</a>'s RESTful API
     * (usually the width in pixels, prefixed by the character 'w').
     *
     * @return the set of available image sizes for the movie backdrops as
     *     previously saved, or the default values.
     */
    public Set<String> getBackdropSizes() {
        SharedPreferences settings =
                PreferenceManager.getDefaultSharedPreferences(mContext);
        return settings.getStringSet(PREFERENCES_KEY_BACKDROP_SIZES
                , getDefaultSetValue(PROPERTIES_KEY_DEFAULT_BACKDROP_SIZES));
    }

    /**
     * Sets the collection of available image sizes for the movie backdrops. The
     * sizes must be specified by the code names used in
     * <a href="https://www.themoviedb.org/">themoviedb.org</a>'s RESTful API
     * (usually the width in pixels, prefixed by the character 'w').
     *
     * @param backdropSizes the collection of available image sizes for the movie
     *                    backdrops.
     */
    public void setBackdropSizes(Set<String> backdropSizes) {
        SharedPreferences settings =
                PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = settings.edit();
        editor.putStringSet(PREFERENCES_KEY_BACKDROP_SIZES, backdropSizes);
        editor.putLong(PREFERENCES_KEY_LAST_UPDATE, System.currentTimeMillis());
        editor.apply();
    }

    /**
     * <p>
     *   Sets all the image related configuration. That is, the default base URL
     *   for retrieving images, as well as the sizes available for movie poster
     *   and backdrop images. The following attributes must be present in the
     *   object passed:
     * </p>
     * <ul>
     *   <li>{@link ImageConfigurationJsonModel#getSecureBaseUrl()}</li>
     *   <li>{@link ImageConfigurationJsonModel#getPosterSizes()}</li>
     *   <li>{@link ImageConfigurationJsonModel#getBackdropSizes()}</li>
     * </ul>
     *
     * @param configuration object containing the default base URL for
     *     retrieving images, as well as the sizes available for movie poster
     *     and backdrop images.
     * @see #setImageBaseUrl(String)
     * @see #setPosterSizes(Set)
     * @see #setBackdropSizes(Set)
     */
    public void setImageConfiguration(ImageConfigurationJsonModel configuration) {
        SharedPreferences settings =
                PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(PREFERENCES_KEY_IMAGE_URL
                , configuration.getSecureBaseUrl());
        editor.putStringSet(PREFERENCES_KEY_POSTER_SIZES
                , new HashSet<>(configuration.getPosterSizes()));
        editor.putStringSet(PREFERENCES_KEY_BACKDROP_SIZES
                , new HashSet<>(configuration.getBackdropSizes()));
        editor.putLong(PREFERENCES_KEY_LAST_UPDATE, System.currentTimeMillis());
        editor.apply();
    }

    /**
     * Returns the name of the image size that is closest to the one requested.
     * The set must contain size the size code names provided by
     * <a href="https://www.themoviedb.org/">themoviedb.org/</a>'s API, and the
     * requested size must be provided in device independent pixels.
     *
     * @param imageSizes the size code names to choose from, as provided by the
     *     <a href="https://www.themoviedb.org/">themoviedb.org/</a>'s API.
     * @param dipWidth the desired width of the poster in <i>Device Independent
     *                 Pixels</i>.
     * @return the name of the size that fits the device best or or {@code null}
     *     if none is found.
     */
    private String getBestImageSizeName(Set<String> imageSizes, int dipWidth) {
        String cachedResult = mBestImageSizeNameCache.get(dipWidth);
        if (cachedResult != null && imageSizes.contains(cachedResult)) {
            return cachedResult;
        }

        int optimalWidthPixels = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP
                , dipWidth
                , mContext.getResources().getDisplayMetrics());

        int minDiff = Integer.MAX_VALUE;
        String bestSizeName = null;
        for (String sizeName : imageSizes) {
            try {
                int size = Integer.parseInt(sizeName.substring(1));
                int diff = Math.abs(optimalWidthPixels - size);
                if (minDiff > diff) {
                    minDiff = diff;
                    bestSizeName = sizeName;
                }
            } catch (NumberFormatException nfe) {
                Log.w(LOG_TAG, "Ignoring image size: " + sizeName);
            }
        }
        mBestImageSizeNameCache.put(dipWidth, bestSizeName);
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
     * @return the URL ofthe image that fits the device best.
     *
     */
    public String getBestFittingPosterUrl(String relativePath, int dipWidth) {
        return getImageBaseUrl()
                + getBestImageSizeName(getPosterSizes(), dipWidth)
                + relativePath;
    }

    /**
     * Returns the URL for the movie backdrop image provided by
     * <a href="https://www.themoviedb.org/">themoviedb.org/</a> that is
     * closest in size to the one requested.
     *
     * @param relativePath relative path to the desired image in
     *     <a href="https://www.themoviedb.org/">themoviedb.org/</a>.
     * @param dipWidth the desired width of the poster in <i>Device Independent
     *     Pixels</i>.
     * @return the URL ofthe image that fits the device best.
     */
    public String getBestFittingBackdropUrl(String relativePath, int dipWidth) {
        return getImageBaseUrl()
                + getBestImageSizeName(getBackdropSizes(), dipWidth)
                + relativePath;
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

    /**
     * Returns the epoch time at which the configuration was last updated or
     * zero if it has never been set. Note that this only applies to the
     * properties (those that appear in {@link ImageConfigurationJsonModel}),
     * if other attributes (e.g. the last movie page retrieved) are changed,
     * the time is not updated.
     *
     * @return the epoch time at which the configuration was last updated or
     *     zero if it has never been set.
     */
    public Long getLastUpdateTime() {
        SharedPreferences settings =
                PreferenceManager.getDefaultSharedPreferences(mContext);
        return settings.getLong(PREFERENCES_KEY_LAST_UPDATE, 0);
    }

    /**
     * Returns the number of the page last retrieved from the RESTful
     * API, as stored in the {@link SharedPreferences}, or zero if none have
     * been retrieved.
     *
     * @return the number of the page last retrieved from the RESTful
     * API, as stored in the {@link SharedPreferences}, or zero if none have
     * been retrieved.
     */
    public int getLastMoviePageRetrieved() {
        SharedPreferences settings =
                PreferenceManager.getDefaultSharedPreferences(mContext);
        return settings.getInt(PREFERENCES_KEY_LAST_MOVIE_PAGE_RETRIEVED, 0);
    }

    /**
     * Sets the number of the page last retrieved from the RESTful
     * API. That is, stores the value in the application's
     * {@link SharedPreferences}.
     *
     * @param page the value to store.
     */
    public void setLastMoviePageRetrieved(int page) {
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(PREFERENCES_KEY_LAST_MOVIE_PAGE_RETRIEVED, page);
        editor.apply();
    }

    /**
     * Returns the total number of movie pages available in the RESTful
     * API, as stored in the {@link SharedPreferences}, or
     * {@link Integer#MAX_VALUE} if the value has not been set.
     *
     * @return the total number of movie pages available in the RESTful
     *     API, as stored in the {@link SharedPreferences}, or
     *     {@link Integer#MAX_VALUE} if the value has not been set.
     */
    public int getTotalMoviePagesAvailable() {
        SharedPreferences settings =
                PreferenceManager.getDefaultSharedPreferences(mContext);
        return settings.getInt(PREFERENCES_KEY_TOTAL_MOVIE_PAGES_AVAILABLE
                , Integer.MAX_VALUE);
    }

    /**
     * Sets the number of the page last retrieved from the RESTful
     * API. That is, stores the value in the application's
     * {@link SharedPreferences}.
     *
     * @param total the value to store.
     */
    public void setTotalMoviePagesAvailable(int total) {
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(PREFERENCES_KEY_TOTAL_MOVIE_PAGES_AVAILABLE, total);
        editor.apply();
    }

}