package kgk.mobile;

import android.app.Application;
import android.content.Context;


public final class App extends Application {

    // TODO Establish Background Service To Persist App

    private static Context appContext;

    ////

    public static Context getAppContext() {
        return appContext;
    }

    //// APPLICATION

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();
    }
}