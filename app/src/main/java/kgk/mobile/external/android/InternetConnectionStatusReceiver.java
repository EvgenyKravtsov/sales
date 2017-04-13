package kgk.mobile.external.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import kgk.mobile.DependencyInjection;
import kgk.mobile.domain.service.SystemService;

public final class InternetConnectionStatusReceiver extends BroadcastReceiver {

    private static final String TAG = InternetConnectionStatusReceiver.class.getSimpleName();

    private final SystemService systemService;

    ////

    public InternetConnectionStatusReceiver() {
        systemService = DependencyInjection.provideSystemService();
    }

    //// BROADCAST RECEIVER

    @Override
    public void onReceive(final Context context, final Intent intent) {
        ConnectivityManager connectivity = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        boolean internetConnectionStatus = false;

        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();

            if (info != null) {

                for (NetworkInfo anInfo : info) {

                    if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                        internetConnectionStatus = true;
                    }
                }
            }
        }

        systemService.internetConnectionStatusChanged(internetConnectionStatus);
    }
}
