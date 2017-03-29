package kgk.mobile.presentation.model.async;


import java.util.ArrayList;
import java.util.List;

import kgk.mobile.domain.SalesOutlet;
import kgk.mobile.domain.UserLocation;
import kgk.mobile.domain.UserOperation;
import kgk.mobile.domain.service.KgkService;
import kgk.mobile.domain.service.LocationService;
import kgk.mobile.domain.service.SettingsStorageService;
import kgk.mobile.presentation.model.UserStore;

public final class UserStoreAsync
        implements UserStore, LocationService.Listener, KgkService.Listener {

    private static final String TAG = UserStoreAsync.class.getSimpleName();

    private final LocationService locationService;
    private final SettingsStorageService settingsStorageService;
    private final KgkService kgkService;
    private final List<LocationListener> locationListeners = new ArrayList<>();
    private final List<UserStore.UserOperationsListener> userOperationsListeners = new ArrayList<>();

    ////

    public UserStoreAsync(LocationService locationService,
                          SettingsStorageService settingsStorageService,
                          KgkService kgkService) {
        this.locationService = locationService;
        this.locationService.addListener(this);
        this.settingsStorageService = settingsStorageService;
        this.kgkService = kgkService;
        this.kgkService.addListener(this);
    }

    //// USER STORE

    @Override
    public void subscribeForUserLocationUpdate(LocationListener listener) {
        locationService.startLocationUpdate();
        locationListeners.add(listener);
    }

    @Override
    public void requestPreferredMapZoom(final PreferredMapZoomListener listener) {
        float preferredZoom = settingsStorageService.getPreferredMapZoom();
        listener.onPreferredMapZoomReceived(preferredZoom);
    }

    @Override
    public void savePreferredMapZoom(float zoom) {
        settingsStorageService.setPreferredMapZoom(zoom);
    }

    @Override
    public void requestUserOperations() {
        kgkService.requestUserOperations();
    }

    @Override
    public void addUserOperationsListener(UserOperationsListener listener) {
        userOperationsListeners.add(listener);
    }

    //// LOCATION SERVICE LISTENER

    @Override
    public void onLocationChanged(UserLocation userLocation) {
        for (LocationListener listener : locationListeners) listener.onLocationReceived(userLocation);
    }

    //// KGK SERVICE LISTENER

    @Override
    public void onSalesOutletsReceivedFromRemoteStorage(List<SalesOutlet> salesOutlets) {
        // Not Used
    }

    @Override
    public void onUserOperationsReceivedFromRemoteStorage(List<UserOperation> userOperations) {
        for (UserOperationsListener listener : userOperationsListeners)
            listener.onUserOperationsReceived(userOperations);
    }
}
