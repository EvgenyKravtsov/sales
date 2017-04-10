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
import kgk.mobile.presentation.view.mainscreen.MainContract;
import kgk.mobile.presentation.view.mainscreen.MainPresenter;
import kgk.mobile.presentation.view.mainscreen.userboard.UserBoardContract;
import kgk.mobile.presentation.view.mainscreen.userboard.UserBoardPresenter;
import kgk.mobile.presentation.view.map.MapController;
import kgk.mobile.threading.ThreadScheduler;
import kgk.mobile.threading.ThreadSchedulerThreadPool;

public final class DependencyInjection {

    private static ThreadScheduler threadScheduler;
    private static UserStore userStore;
    private static SalesOutletStore salesOutletStore;
    private static MapController mapController;
    private static KgkService kgkService;
    private static DatabaseService databaseService;
    private static SalesOutletAttendanceStore salesOutletAttendanceStore;
    private static LocationService locationService;
    private static SystemService systemService;

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

    private static SettingsStorageService provideSettingsStorageService() {
        return new SharedPreferencesStorage(provideAppContext());
    }

    private static SocketService provideSocketService() {
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
            databaseService = new GreenDaoSqlite(provideAppContext(), provideSystemService());
        }

        return databaseService;
    }

    public static void setMapController(MapController mapController) {
        DependencyInjection.mapController = mapController;
    }

    public static MapController provideMapController() {
        return mapController;
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
                    provideDatabaseService());
        }

        return userStore;
    }

    private static SalesOutletStore provideSalesOutletStore() {
        if (salesOutletStore == null) {
            salesOutletStore = new SalesOutletStoreAsync(
                    provideKgkService(), provideDatabaseService());
        }

        return salesOutletStore;
    }

    private static SalesOutletAttendanceStore provideSalesOutletAttendanceStore() {
        if (salesOutletAttendanceStore == null) {
            salesOutletAttendanceStore = new SalesOutletAttendanceStoreAsync(
                    provideDatabaseService(),
                    provideKgkService());
        }

        return salesOutletAttendanceStore;
    }

    //// PRESENTER

    public static MainContract.Presenter provideMainContractPresenter() {
        return new MainPresenter(provideUserStore(), provideSalesOutletStore(), provideThreadScheduler());
    }

    public static UserBoardContract.Presenter provideManagerBoardPresenter() {
        return new UserBoardPresenter(
                provideSalesOutletStore(),
                provideUserStore(),
                provideThreadScheduler(),
                provideSalesOutletAttendanceStore());
    }
}
