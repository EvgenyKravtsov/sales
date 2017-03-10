package kgk.mobile.presentation.model;


import kgk.mobile.domain.UserLocation;

public interface UserStore {

    interface LocationListener {

        void onLocationReceived(UserLocation userLocation);
    }

    interface PreferredMapZoomListener {

        void onPreferredMapZoomReceived(float zoom);
    }

    void subscribeForUserLocationUpdate(LocationListener listener);

    void requestPreferredMapZoom(PreferredMapZoomListener listener);
}
