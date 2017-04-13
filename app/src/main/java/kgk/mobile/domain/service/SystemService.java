package kgk.mobile.domain.service;


public interface SystemService {

    interface Listener {

        void onInternetConnectionStatusChanged(boolean status);
    }

    void addListener(Listener listener);

    void removeListener(Listener listener);

    boolean getInternetConnectionStatus();

    boolean getGpsModuleStatus();

    String getDeviceId();

    void internetConnectionStatusChanged(boolean status);

    String getAppVersion();
}
