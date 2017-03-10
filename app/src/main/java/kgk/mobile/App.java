package kgk.mobile;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import kgk.mobile.domain.ServiceModule;
import kgk.mobile.external.kgkservicesocketnio.KgkServiceSocketNio;
import kgk.mobile.external.kgkservicesocketnio.SocketService;
import kgk.mobile.presentation.model.StoreModule;


public final class App extends Application {

    private static AppComponent component;

    ////

    public static AppComponent getComponent() {
        return component;
    }

    //// APPLICATION

    @Override
    public void onCreate() {
        super.onCreate();

        Intent socketServiceIntent = new Intent(this, SocketService.class);
        startService(socketServiceIntent);

        component = buildComponent();
    }

    //// PRIVATE

    protected AppComponent buildComponent() {
        return DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .storeModule(new StoreModule())
                .serviceModule(new ServiceModule())
                .build();
    }
}
