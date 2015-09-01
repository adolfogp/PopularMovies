package mx.com.adolfogarcia.popularmovies;

import android.app.Application;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PopularMoviesApplication extends Application {

    private static final String LOG_TAG = PopularMoviesApplication.class.getSimpleName();

    // TODO: Determine if it is possible to avoid keeping this in the Application injecting with Dagger2 instead
    // TODO: Use a constant instead of a properties file?
    private static Properties configuration = null;

    @Override
    public void onCreate() {
        super.onCreate();
        initConfiguration();
    }

    private void initConfiguration() {
        if (configuration != null) {
            return;
        }
        configuration = new Properties();
        InputStream is = null; // FIXME - Use try-with-resources
        try {
            is = getResources().openRawResource(R.raw.configuration);
            configuration.loadFromXML(is);
        } catch (IOException ioe) {
            Log.e(LOG_TAG, "Failed to load configuration properties file.", ioe);
            configuration = null;
        } finally {
            try {
                is.close();
            } catch (IOException ioe) {
                Log.e(LOG_TAG, "Failed to close configuration properties file.", ioe);
            }
        }
    }

    public static String getTheMovieDbKey() {
        if (configuration == null) {
            throw new IllegalStateException("The configuration has not been initialized.");
        }
        return configuration.getProperty("apikey.themoviedb");
    }

}
