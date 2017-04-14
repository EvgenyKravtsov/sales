package kgk.mobile.external.android;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import kgk.mobile.domain.service.SystemService;

import static android.content.Context.LOCATION_SERVICE;


public final class SystemServiceAndroid implements SystemService {

    private static final String TAG = SystemServiceAndroid.class.getSimpleName();

    private final Context context;
    private final List<Listener> listeners = new ArrayList<>();

    ////

    public SystemServiceAndroid(Context context) {
        this.context = context;
    }

    //// SYSTEM SERVICE

    @Override
    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(Listener listener) {
        listeners.remove(listener);
    }

    @Override
    public boolean getInternetConnectionStatus() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        if (activeNetwork == null) return false;
        return activeNetwork.isConnectedOrConnecting();
    }

    @Override
    public boolean getGpsModuleStatus() {
        LocationManager locationManager = (LocationManager)
                context.getSystemService(LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    @SuppressLint("HardwareIds")
    @Override
    public String getDeviceId() {
        TelephonyManager telephonyManager = (TelephonyManager)
                context.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId().substring(5);
        //return "3022606286";
    }

    @Override
    public void internetConnectionStatusChanged(boolean status) {
        for (Listener listener : listeners)
            listener.onInternetConnectionStatusChanged(status);
    }

    @Override
    public String getAppVersion() {
        try {
            PackageInfo pInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return pInfo.versionName;
        }
        catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }
}
