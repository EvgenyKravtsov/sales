package kgk.mobile;


import javax.inject.Singleton;

import dagger.Component;
import kgk.mobile.external.ImageCreatorAndroid;
import kgk.mobile.external.LocationServiceGoogleFusedApi;
import kgk.mobile.external.SharedPreferencesStorage;
import kgk.mobile.domain.ServiceModule;
import kgk.mobile.presentation.model.StoreModule;
import kgk.mobile.presentation.model.rx.SalesOutletStoreRx;
import kgk.mobile.presentation.model.rx.UserStoreRx;
import kgk.mobile.presentation.view.mainscreen.MainPresenter;

@SuppressWarnings("WeakerAccess")
@Component(modules = {AppModule.class, StoreModule.class, ServiceModule.class})
@Singleton
public interface AppComponent {

    void inject(LocationServiceGoogleFusedApi locationServiceGoogleFusedApi);

    void inject(SharedPreferencesStorage sharedPreferencesStorage);

    void inject(ImageCreatorAndroid imageCreatorAndroid);

    MainPresenter mainPresenter();

    UserStoreRx userStoreRx();

    SalesOutletStoreRx salesOutletStoreRx();
}
