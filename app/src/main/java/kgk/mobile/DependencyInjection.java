package kgk.mobile;


import android.content.Context;

import kgk.mobile.domain.service.DatabaseService;
import kgk.mobile.domain.service.KgkService;
import kgk.mobile.domain.service.LocationService;
import kgk.mobile.domain.service.SettingsStorageService;
import kgk.mobile.domain.service.SystemService;
import kgk.mobile.external.android.SystemServiceAndroid;
import kgk.mobile.external.greendao.GreenDaoSqlite;
import kgk.mobile.external.android.LocationServiceGoogleFusedApi;
import kgk.mobile.external.android.SharedPreferencesStorage;
import kgk.mobile.external.greendao.JsonSerializer;
import kgk.mobile.external.network.KgkApi;
import kgk.mobile.external.network.json.JsonProtocol;
import kgk.mobile.external.network.socket.SocketNio;
import kgk.mobile.external.network.socket.SocketService;
import kgk.mobile.presentation.model.SalesOutletAttendanceStore;
import kgk.mobile.presentation.model.SalesOutletStore;
import kgk.mobile.presentation.model.UserStore;
import kgk.mobile.presentation.model.async.SalesOutletAttendanceStoreAsync;
import kgk.mobile.presentation.model.async.SalesOutletStoreAsync;
import kgk.mobile.presentation.model.async.UserStoreAsync;
import kgk.mobile.presentation.view.loginscreen.LoginContract;
import kgk.mobile.presentation.view.loginscreen.LoginPresenter;
import kgk.mobile.presentation.view.mainscreen.MainContract;
import kgk.mobile.presentation.view.mainscreen.MainPresenter;
import kgk.mobile.presentation.model.MainStore;
import kgk.mobile.presentation.model.reactive.MainStoreReactive;
import kgk.mobile.presentation.view.mainscreen.lastactions.LastActionsContract;
import kgk.mobile.presentation.view.mainscreen.lastactions.LastActionsPresenter;
import kgk.mobile.presentation.view.mainscreen.technicalinformation.TechnicalInformationContract;
import kgk.mobile.presentation.view.mainscreen.technicalinformation.TechnicalInformationPresenter;
import kgk.mobile.external.threading.ThreadScheduler;
import kgk.mobile.external.threading.ThreadSchedulerThreadPool;

public final class DependencyInjection {

    private static ThreadScheduler threadScheduler;
    private static UserStore userStore;
    private static SalesOutletStore salesOutletStore;
    private static KgkService kgkService;
    private static DatabaseService databaseService;
    private static SalesOutletAttendanceStore salesOutletAttendanceStore;
    private static LocationService locationService;
    private static SystemService systemService;
    private static MainStore mainStore;

    ////

    public static Context provideAppContext() {
        return App.getAppContext();
    }

    private static ThreadScheduler provideThreadScheduler() {
        if (threadScheduler == null) {
            threadScheduler = new ThreadSchedulerThreadPool(provideAppContext());
        }

        return threadScheduler;
    }

    //// SERVICE

    private static LocationService provideLocationService() {
        if (locationService == null) {
            locationService = new LocationServiceGoogleFusedApi(provideAppContext());
        }

        return locationService;
    }

    public static SettingsStorageService provideSettingsStorageService() {
        return new SharedPreferencesStorage(provideAppContext());
    }

    public static SocketService provideSocketService() {
        return new SocketNio();
    }

    private static KgkService provideKgkService() {
        if (kgkService == null) {
            kgkService = new KgkApi(new JsonProtocol(provideSystemService()),
                                    provideSocketService(),
                                    provideLocationService());
        }

        return kgkService;
    }

    private static DatabaseService provideDatabaseService() {
        if (databaseService == null) {
            databaseService = new GreenDaoSqlite(provideAppContext(),
                                                 provideSystemService(),
                                                 new JsonSerializer());
        }

        return databaseService;
    }

    public static SystemService provideSystemService() {
        if (systemService == null) {
            systemService = new SystemServiceAndroid(provideAppContext());
        }

        return systemService;
    }

    //// STORE

    private static UserStore provideUserStore() {
        if (userStore == null) {
            userStore = new UserStoreAsync(
                    provideLocationService(),
                    provideSettingsStorageService(),
                    provideKgkService(),
                    provideDatabaseService(),
                    provideSystemService());
        }

        return userStore;
    }

    private static SalesOutletStore provideSalesOutletStore() {
        if (salesOutletStore == null) {
            salesOutletStore = new SalesOutletStoreAsync(
                    provideKgkService(), provideDatabaseService(), provideSettingsStorageService());
        }

        return salesOutletStore;
    }

    private static SalesOutletAttendanceStore provideSalesOutletAttendanceStore() {
        if (salesOutletAttendanceStore == null) {
            salesOutletAttendanceStore = new SalesOutletAttendanceStoreAsync(
                    provideDatabaseService(),
                    provideKgkService(),
                    provideSettingsStorageService());
        }

        return salesOutletAttendanceStore;
    }

    private static MainStore provideMainStore() {
        if (mainStore == null) {
            mainStore = new MainStoreReactive(
                    provideLocationService(),
                    provideThreadScheduler(),
                    provideKgkService(),
                    provideDatabaseService(),
                    provideSettingsStorageService()
            );
        }

        return mainStore;
    }

    //// PRESENTER

    public static MainContract.Presenter provideMainPresenter() {
        return new MainPresenter(provideMainStore());
    }

    public static TechnicalInformationContract.Presenter provideTechnicalInformationPresenter() {
        return new TechnicalInformationPresenter(
                provideLocationService(),
                provideKgkService(),
                provideThreadScheduler(),
                provideSettingsStorageService(),
                provideSystemService(),
                provideMainStore());
    }

    public static LastActionsContract.Presenter provideLastActionsPresenter() {
        return new LastActionsPresenter(provideDatabaseService(), provideThreadScheduler());
    }

    public static LoginContract.Presenter provideLoginPresenter() {
        return new LoginPresenter(provideUserStore(),
                                  provideThreadScheduler(),
                                  provideSettingsStorageService(),
                                  provideSystemService());
    }
}
