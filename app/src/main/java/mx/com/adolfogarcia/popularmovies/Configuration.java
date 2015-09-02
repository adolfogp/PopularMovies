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

package mx.com.adolfogarcia.popularmovies;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Provides access to system wide configuration data for the Popular Movies
 * application. For example, access to the key required to access
 * the RESTful API provided by
 * <a href="http://www.themoviedb.org">themoviedb.org</a>.
 *
 * @author Jesús Adolfo García Pasquel
 */
public class Configuration {

    /**
     * Identifies messages written to the log by this class.
     */
    private static final String LOG_TAG = Configuration.class.getSimpleName();

    /**
     * Holds the system wide configuration data.
     */
    private static final Properties configurationProperties = new Properties();

    /**
     * Creates a new instance of {@link Configuration}.
     *
     * @param context The application's {@link Context}. Used to read the
     *                properties file that contains the configuration data.
     */

    public Configuration(Context context) {
        InputStream is = null; // TODO: Use try-with-resources (Android Studio shows errors in spite of Retrolambda)
        try {
            is = context.getResources().openRawResource(R.raw.configuration);
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
     * Returns the key used to access the RESTful API provided by
     * <a href="http://www.themoviedb.org">themoviedb.org</a>.
     *
     * @return the key used to access the RESTful API provided by
     * <a href="http://www.themoviedb.org">themoviedb.org</a>.
     */
    public String getMovieApiKey() {
        return configurationProperties.getProperty("apikey.themoviedb");
    }

}
