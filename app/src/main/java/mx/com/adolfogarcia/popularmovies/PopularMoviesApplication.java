package mx.com.adolfogarcia.popularmovies;

import android.app.Application;

/**
 * Custom {@link Application} that provides access to the
 * {@link ApplicationComponent}, for dependency injection.
 *
 * @author Jesús Adolfo García Pasquel
 */
public class PopularMoviesApplication extends Application {

    /**
     * Identifies the messages written to the log by this class.
     */
    private static final String LOG_TAG = PopularMoviesApplication.class.getSimpleName();

    /**
     * Provides methods to inject dependencies.
     */
    private ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        applicationComponent = DaggerApplicationComponent.builder()
                .popularMoviesApplicationModule(new PopularMoviesApplicationModule(this))
                .build();
    }

    /**
     * Returns the {@link ApplicationComponent} that provides methods to
     * inject dependencies.
     *
     * @return the {@link ApplicationComponent} that provides methods to
     *     inject dependencies.
     */
    public ApplicationComponent getComponent() {
        return applicationComponent;
    }

}
