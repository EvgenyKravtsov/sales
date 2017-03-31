package kgk.mobile.presentation.model.async;


import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import kgk.mobile.domain.SalesOutlet;
import kgk.mobile.domain.UserLocation;
import kgk.mobile.domain.UserOperation;
import kgk.mobile.domain.service.DatabaseService;
import kgk.mobile.domain.service.KgkService;
import kgk.mobile.domain.service.LocationService;
import kgk.mobile.domain.service.SettingsStorageService;
import kgk.mobile.presentation.model.UserStore;

public final class UserStoreAsync
        implements UserStore,
        LocationService.Listener,
        KgkService.Listener,
        DatabaseService.Listener {

    private static final String TAG = UserStoreAsync.class.getSimpleName();

    private final LocationService locationService;
    private final SettingsStorageService settingsStorageService;
    private final KgkService kgkService;
    private final DatabaseService databaseService;
    private final List<LocationListener> locationListeners = new ArrayList<>();
    private final List<UserStore.UserOperationsListener> userOperationsListeners = new ArrayList<>();

    private boolean userOperationsProvidedFromLocalStorage;

    ////

    public UserStoreAsync(LocationService locationService,
                          SettingsStorageService settingsStorageService,
                          KgkService kgkService,
                          DatabaseService databaseService) {
        this.locationService = locationService;
        this.locationService.addListener(this);
        this.settingsStorageService = settingsStorageService;
        this.kgkService = kgkService;
        this.kgkService.addListener(this);
        this.databaseService = databaseService;
        databaseService.addListener(this);
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
        databaseService.requestUserOperations();
        if (kgkService.isAvailable()) kgkService.requestUserOperations();
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
        databaseService.updateUserOperations(userOperations);

        if (userOperationsProvidedFromLocalStorage) return;

        for (UserOperationsListener listener : userOperationsListeners)
            listener.onUserOperationsReceived(userOperations);
    }

    //// DATABASE SERVICE LISTENER

    @Override
    public void onSalesOutletsReceivedFromLocalStorage(List<SalesOutlet> salesOutlets) {
        // Not Used
    }

    @Override
    public void onUserOperationsReceivedFromLocalStorage(List<UserOperation> userOperations) {
        if (userOperations.size() == 0) {
            userOperationsProvidedFromLocalStorage = false;
            return;
        }

        for (UserOperationsListener listener : userOperationsListeners)
            listener.onUserOperationsReceived(userOperations);

        userOperationsProvidedFromLocalStorage = true;
    }
}
