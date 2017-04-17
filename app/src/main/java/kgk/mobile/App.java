package kgk.mobile;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;


public final class App extends Application {

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