package kgk.mobile.domain;


import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import kgk.mobile.external.DatabaseServiceSqlite;
import kgk.mobile.external.kgkservicesocketnio.KgkServiceSocketNio;
import kgk.mobile.external.LocationServiceGoogleFusedApi;
import kgk.mobile.external.SharedPreferencesStorage;

@Module
public final class ServiceModule {

    @Provides
    @Singleton
    LocationService provideLocationService() {
        return new LocationServiceGoogleFusedApi();
    }

    @Provides
    @Singleton
    SettingsStorageService provideSettingsStorageService() {
        return new SharedPreferencesStorage();
    }

    @Provides
    @Singleton
    KgkService provideKgkService() {
        return new KgkServiceSocketNio();
    }

    @Provides
    @Singleton
    DatabaseService provideDatabaseService() {
        return new DatabaseServiceSqlite();
    }
}
