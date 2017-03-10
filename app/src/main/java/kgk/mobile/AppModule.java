package kgk.mobile;


import android.content.Context;
import android.support.annotation.NonNull;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
final class AppModule {

    private Context appContext;

    ////

    AppModule(@NonNull Context context) {
        appContext = context;
    }

    ////

    @Provides
    @Singleton
    Context provideContext() {
        return appContext;
    }
}
