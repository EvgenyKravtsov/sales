package kgk.mobile;


import android.content.Context;

import kgk.mobile.domain.service.DatabaseService;
import kgk.mobile.domain.service.KgkService;
import kgk.mobile.domain.service.LocationService;
import kgk.mobile.domain.service.SettingsStorageService;
import kgk.mobile.external.greendao.GreenDaoSqlite;
import kgk.mobile.external.android.LocationServiceGoogleFusedApi;
import kgk.mobile.external.android.SharedPreferencesStorage;
import kgk.mobile.external.network.KgkApi;
import kgk.mobile.external.network.json.JsonProtocol;
import kgk.mobile.external.network.socket.SocketNio;
import kgk.mobile.external.network.socket.SocketService;
import kgk.mobile.presentation.model.SalesOutletStore;
import kgk.mobile.presentation.model.UserStore;
import kgk.mobile.presentation.model.async.SalesOutletStoreAsync;
import kgk.mobile.presentation.model.async.UserStoreAsync;
import kgk.mobile.presentation.view.mainscreen.MainContract;
import kgk.mobile.presentation.view.mainscreen.MainPresenter;
import kgk.mobile.presentation.view.mainscreen.managerboard.ManagerBoardContract;
import kgk.mobile.presentation.view.mainscreen.managerboard.ManagerBoardPresenter;
import kgk.mobile.threading.ThreadScheduler;
import kgk.mobile.threading.ThreadSchedulerThreadPool;

public final class DependencyInjection {

    private static ThreadScheduler threadScheduler;
    private static SalesOutletStore salesOutletStore;

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
        return new LocationServiceGoogleFusedApi(provideAppContext());
    }

    private static SettingsStorageService provideSettingsStorageService() {
        return new SharedPreferencesStorage(provideAppContext());
    }

    private static SocketService provideSocketService() {
        return new SocketNio();
    }

    private static KgkService provideKgkService() {
        return new KgkApi(new JsonProtocol(), provideSocketService());
    }

    private static DatabaseService provideDatabaseService() {
        return new GreenDaoSqlite(provideAppContext());
    }

    //// STORE

    private static UserStore provideUserStore() {
        return new UserStoreAsync(provideLocationService(), provideSettingsStorageService());
    }

    private static SalesOutletStore provideSalesOutletStore() {
        if (salesOutletStore == null) {
            salesOutletStore = new SalesOutletStoreAsync(
                    provideKgkService(), provideDatabaseService());
        }

        return salesOutletStore;
    }

    //// PRESENTER

    public static MainContract.Presenter provideMainContractPresenter() {
        return new MainPresenter(provideUserStore(), provideSalesOutletStore(), provideThreadScheduler());
    }

    public static ManagerBoardContract.Presenter provideManagerBoardPresenter() {
        return new ManagerBoardPresenter(provideSalesOutletStore());
    }
}
