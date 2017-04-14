package kgk.mobile.presentation.model.async;


import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import kgk.mobile.domain.SalesOutlet;
import kgk.mobile.domain.SalesOutletAttendance;
import kgk.mobile.domain.UserLocation;
import kgk.mobile.domain.UserOperation;
import kgk.mobile.domain.service.DatabaseService;
import kgk.mobile.domain.service.KgkService;
import kgk.mobile.domain.service.LocationService;
import kgk.mobile.domain.service.SettingsStorageService;
import kgk.mobile.domain.service.SystemService;
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
    private final SystemService systemService;
    private final List<LocationListener> locationListeners = new ArrayList<>();
    private final List<UserLoginListener> userLoginListeners = new ArrayList<>();
    private final List<UserStore.UserOperationsListener> userOperationsListeners = new ArrayList<>();

    private boolean userOperationsProvidedFromLocalStorage;

    ////

    public UserStoreAsync(LocationService locationService,
                          SettingsStorageService settingsStorageService,
                          KgkService kgkService,
                          DatabaseService databaseService,
                          SystemService systemService) {
        this.locationService = locationService;
        this.locationService.addListener(this);
        this.settingsStorageService = settingsStorageService;
        this.kgkService = kgkService;
        this.kgkService.addListener(this);
        this.databaseService = databaseService;
        this.systemService = systemService;
        databaseService.addListener(this);
    }

    //// USER STORE

    @Override
    public void subscribeForUserLocationUpdate(LocationListener listener) {
        locationService.startLocationUpdate();
        locationListeners.add(listener);
    }

    @Override
    public void unsubscribeForUserLocationUpdate(LocationListener listener) {
        locationListeners.remove(listener);
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

    @Override
    public void removeUserOperationsListener(UserOperationsListener listener) {
        userOperationsListeners.remove(listener);
    }

    @Override
    public void requestUserLogin(String login, String password) {
        kgkService.requestUserLogin(login, password, systemService.getDeviceId());
    }

    @Override
    public void addUserLoginListener(UserLoginListener listener) {
        userLoginListeners.add(listener);
    }

    @Override
    public void removeUserLoginListener(UserLoginListener listener) {
        userLoginListeners.remove(listener);
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

    @Override
    public void onPointExitIdReceivedFromRemoteStorage(String eventId) {

    }

    @Override
    public void onLastSendingDateChanged(long lastSendingDateUnixSeconds) {
        // Not Used
    }

    @Override
    public void onLoginAnswerReceived(KgkService.LoginAnswerType answerType) {
        switch (answerType) {
            case Success:
                for (UserLoginListener listener : userLoginListeners)
                    listener.onLoginSuccess();
                return;
            case NoUserFound:
                for (UserLoginListener listener : userLoginListeners)
                    listener.onUserNotFound();
                return;
            case DeviceNotAllowed:
                for (UserLoginListener listener : userLoginListeners)
                    listener.onDeviceNotAllowed();
                return;
            case Error:
                for (UserLoginListener listener : userLoginListeners)
                    listener.onLoginError();
        }
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

    @Override
    public void onNonSynchronizedSalesOutletAttendancesReceivedFromLocalStorage(
            List<SalesOutletAttendance> attendances) {
        // Not Used
    }

    @Override
    public void onSalesOutletAttendancesReceivedFromLocalStorage(List<SalesOutletAttendance> attendances) {
        // Not Used
    }
}
