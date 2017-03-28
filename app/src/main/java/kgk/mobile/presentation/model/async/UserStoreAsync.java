package kgk.mobile.presentation.model.async;


import java.util.ArrayList;
import java.util.List;

import kgk.mobile.domain.UserLocation;
import kgk.mobile.domain.service.LocationService;
import kgk.mobile.domain.service.SettingsStorageService;
import kgk.mobile.presentation.model.UserStore;

public final class UserStoreAsync implements UserStore, LocationService.Listener {

    private static final String TAG = UserStoreAsync.class.getSimpleName();

    private final LocationService locationService;
    private final SettingsStorageService settingsStorageService;
    private final List<LocationListener> locationListeners = new ArrayList<>();

    ////

    public UserStoreAsync(LocationService locationService,
                          SettingsStorageService settingsStorageService) {
        this.locationService = locationService;
        this.locationService.addListener(this);
        this.settingsStorageService = settingsStorageService;
    }

    //// USER STORE

    @Override
    public void subscribeForUserLocationUpdate(LocationListener listener) {
        locationService.startLocationUpdate();
        locationListeners.add(listener);
    }

    @Override
    public void requestPreferredMapZoom(final PreferredMapZoomListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                float preferredZoom = settingsStorageService.getPreferredMapZoom();
                listener.onPreferredMapZoomReceived(preferredZoom);
            }
        }).start();
    }

    @Override
    public void savePreferredMapZoom(float zoom) {
        settingsStorageService.setPreferredMapZoom(zoom);
    }
    //// LOCATION SERVICE LISTENER

    @Override
    public void onLocationChanged(UserLocation userLocation) {
        for (LocationListener listener : locationListeners) listener.onLocationReceived(userLocation);
    }
}
