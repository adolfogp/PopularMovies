package mx.com.adolfogarcia.popularmovies;

import android.app.Application;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.inject.Singleton;

import dagger.Component;

public class PopularMoviesApplication extends Application {

    private static final String LOG_TAG = PopularMoviesApplication.class.getSimpleName();

    private ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        applicationComponent = DaggerApplicationComponent.builder()
                .popularMoviesApplicationModule(new PopularMoviesApplicationModule(this))
                .build();
    }

    public ApplicationComponent getComponent() {
        return applicationComponent;
    }

}
