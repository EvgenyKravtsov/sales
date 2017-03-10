package kgk.mobile.domain;


public interface LocationService {

    interface Listener {

        void onLocationChanged(UserLocation userLocation);
    }

    ////

    void startLocationUpdate();

    void addListener(Listener listener);
}
