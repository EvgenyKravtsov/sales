package kgk.mobile.presentation.model;


import dagger.Module;
import dagger.Provides;
import kgk.mobile.App;
import kgk.mobile.presentation.model.rx.SalesOutletStoreRx;

@Module
public final class StoreModule {

    @Provides
    UserStore provideUserStore() {
        return App.getComponent().userStoreRx();
    }

    @Provides
    SalesOutletStore providePointStore() {
        return App.getComponent().salesOutletStoreRx();
    }
}
