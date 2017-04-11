package kgk.mobile.domain.service;


import kgk.mobile.domain.UserLocation;

public interface LocationService {

    interface Listener {

        void onLocationChanged(UserLocation userLocation);
    }

    ////

    void startLocationUpdate();

    void addListener(Listener listener);

    void removeListener(Listener listener);

    UserLocation getLastKnownUserLocation();
}
