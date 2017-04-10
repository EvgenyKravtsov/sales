package kgk.mobile.domain.service;


public interface SystemService {

    interface Listener {

        void onInternetConnectionStatusChanged(boolean status);
    }

    void addListener(Listener listener);

    boolean getInternetConnectionStatus();

    boolean getGpsModuleStatus();

    String getDeviceId();

    void internetConnectionStatusChanged(boolean status);
}
