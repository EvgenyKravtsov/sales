package kgk.mobile.presentation.model.reactive;

import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import kgk.mobile.domain.Mode;
import kgk.mobile.domain.SalesOutlet;
import kgk.mobile.domain.SalesOutletAttendance;
import kgk.mobile.domain.UserLocation;
import kgk.mobile.domain.UserOperation;
import kgk.mobile.domain.service.DatabaseService;
import kgk.mobile.domain.service.KgkService;
import kgk.mobile.domain.service.LocationService;
import kgk.mobile.domain.service.SettingsStorageService;
import kgk.mobile.external.network.Authorization;
import kgk.mobile.external.threading.ThreadScheduler;
import kgk.mobile.presentation.model.MainStore;

public class MainStoreReactive implements MainStore, LocationService.Listener,
        KgkService.Listener, DatabaseService.Listener {

    private static final String TAG = MainStoreReactive.class.getSimpleName();
    private static final int REMOTE_STORAGE_SYNCHRONIZATION_INTERVAL_SECONDS = 10;

    private final List<Listener> listeners = new ArrayList<>();
    private final List<SalesOutlet> salesOutlets = new ArrayList<>();
    private final List<SalesOutlet> enteredSalesOutlets = new ArrayList<>();
    private final List<UserOperation> userOperations = new ArrayList<>();
    private final ThreadScheduler threadScheduler;
    private final KgkService kgkService;
    private final DatabaseService databaseService;
    private final SettingsStorageService settingsStorageService;
    private final LocationService locationService;

    private UserLocation userLocation;
    private SalesOutlet selectedSalesOutlet;
    private long salesOutletAttendanceBeginDateUnixSeconds;
    private boolean previouslySelectedSalesOutletUpdated;
    private Authorization authorization;
    private Mode mode;

    ////

    public MainStoreReactive(LocationService locationService,
                      ThreadScheduler threadScheduler,
                      KgkService kgkService,
                      DatabaseService databaseService,
                      SettingsStorageService settingsStorageService) {
        this.locationService = locationService;
        this.locationService.addListener(this);
        this.threadScheduler = threadScheduler;
        this.kgkService = kgkService;
        this.kgkService.addListener(this);
        this.databaseService = databaseService;
        this.databaseService.addListener(this);
        this.settingsStorageService = settingsStorageService;
    }

    //// MAIN STORE

    @Override
    public void setup() {
        mode = settingsStorageService.getMode();
        locationService.startLocationUpdate();
        threadScheduler.executeBackgroundThread(new Runnable() {
            @Override
            public void run() { kgkService.connect(); }
        });
    }

    @Override
    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(Listener listener) {
        listeners.remove(listener);
    }

    @Override
    public UserLocation getUserLocation() {
        return userLocation;
    }

    @Override
    public List<SalesOutlet> getSalesOutlets() {
        return salesOutlets;
    }

    @Override
    public List<SalesOutlet> getEnteredSalesOutlets() {
        return enteredSalesOutlets;
    }

    @Override
    public float getMapZoom() {
        return settingsStorageService.getPreferredMapZoom();
    }

    @Override
    public void setMapZoom(float zoom) {
        settingsStorageService.setPreferredMapZoom(zoom);
    }

    @Override
    public List<UserOperation> getUserOperations() {
        return userOperations;
    }

    @Override
    public SalesOutlet getSelectedSalesOutlet() {
        return selectedSalesOutlet;
    }

    @Override
    public void setSelectedSalesOutlet(SalesOutlet selectedSalesOutlet) {
        this.selectedSalesOutlet = selectedSalesOutlet;

        if (selectedSalesOutlet != null) {
            this.settingsStorageService.setSelectedSalesOutlet(selectedSalesOutlet);
            this.salesOutletAttendanceBeginDateUnixSeconds = Calendar.getInstance().getTimeInMillis() / 1000;
            this.settingsStorageService.setSalesOutletAttendanceBeginDateUnixSeconds(Calendar.getInstance().getTimeInMillis() / 1000);
        }
        else {
            this.settingsStorageService.setSelectedSalesOutlet(null);
            this.salesOutletAttendanceBeginDateUnixSeconds = 0;
            this.settingsStorageService.setSalesOutletAttendanceBeginDateUnixSeconds(0);
        }

        for (Listener listener : listeners) listener.onSelectedSalesOutletChanged();
    }

    @Override
    public long getSalesOutletAttendanceBeginDateUnixSeconds() {
        return salesOutletAttendanceBeginDateUnixSeconds;
    }

    @Override
    public void salesOutletAttended(List<UserOperation> selectedUserOperations,
                                    int attendanceAddedValue) {

        final SalesOutletAttendance attendance = new SalesOutletAttendance(
                salesOutletAttendanceBeginDateUnixSeconds,
                Calendar.getInstance().getTimeInMillis() / 1000,
                selectedSalesOutlet,
                selectedUserOperations,
                attendanceAddedValue,
                mode);

        threadScheduler.executeBackgroundThread(new Runnable() {
            @Override
            public void run() {
                databaseService.insertSalesOutletAttendance(attendance);
            }
        });
    }
    @Override
    public Authorization getAuthorization() {
        return authorization;
    }

    @Override
    public Mode getMode() {
        return mode;
    }

    @Override
    public void setMode(Mode mode) {
        this.mode = mode;
        this.settingsStorageService.setMode(mode);
        updateEnteredSalesOutlets(locationService.getLastKnownUserLocation());
        updateSelectedSalesOutlet();
    }

    //// LOCATION SERVICE LISTENER

    @Override
    public void onLocationChanged(UserLocation userLocation) {
        // Method Called On Main Thread

        this.userLocation = userLocation;
        for (Listener listener : listeners) listener.onUserLocationChanged();

        updateEnteredSalesOutlets(userLocation);
        updateSelectedSalesOutlet();
    }

    //// KGK SERVICE LISTENER

    @Override
    public void onSalesOutletsReceivedFromRemoteStorage(List<SalesOutlet> salesOutlets) {
        // Method Called On Background Thread

        if (salesOutlets.size() != 0) {
            databaseService.updateSalesOutlets(salesOutlets);
            updateSalesOutlets(salesOutlets);
        }
    }

    @Override
    public void onUserOperationsReceivedFromRemoteStorage(List<UserOperation> userOperations) {
        // Method Called On Background Thread

        if (salesOutlets.size() != 0) {
            databaseService.updateUserOperations(userOperations);
            updateUserOperations(userOperations);
        }
    }

    @Override
    public void onPointExitIdReceivedFromRemoteStorage(String eventId) {
        // Method Called On Background Thread

        databaseService.confirmSalesOutletAttendance(eventId);
    }

    @Override
    public void onLastSendingDateChanged(long lastSendingDateUnixSeconds) {
        // Not Used
    }

    @Override
    public void onLoginAnswerReceived(KgkService.LoginAnswerType answerType) {
        // Method Called On Background Thread

        Authorization authorization = null;

        switch (answerType) {
            case Success:
                authorization = new Authorization(true);
                setupInitialState();
                startPeriodicSynchronizationWithRemoteStorage();
                break;
            case DeviceNotAllowed:
                authorization = new Authorization(false);
                break;
        }

        if (authorization == null) return;

        if (this.authorization == null) {
            this.authorization = authorization;

            threadScheduler.executeMainThread(new Runnable() {
                @Override
                public void run() {
                    for (Listener listener : listeners) listener.onAuthorizationChanged();
                }
            });

            return;
        }

        if (this.authorization.isAuthorized() != authorization.isAuthorized()) {
            this.authorization = authorization;

            threadScheduler.executeMainThread(new Runnable() {
                @Override
                public void run() {
                    for (Listener listener : listeners) listener.onAuthorizationChanged();
                }
            });
        }
    }

    //// DATABASE SERVICE LISTENER

    @Override
    public void onSalesOutletsReceivedFromLocalStorage(List<SalesOutlet> salesOutlets) {
        // Method Called On Background Thread

        if (salesOutlets.size() != 0) updateSalesOutlets(salesOutlets);
    }

    @Override
    public void onUserOperationsReceivedFromLocalStorage(List<UserOperation> userOperations) {
        // Method Called On Background Thread

        if (userOperations.size() != 0) updateUserOperations(userOperations);
    }

    @Override
    public void onNonSynchronizedSalesOutletAttendancesReceivedFromLocalStorage(List<SalesOutletAttendance> attendances) {
        // Method Called On Background Thread

        if (kgkService.isAvailable()) {
            kgkService.sendSalesOutletAttendances(attendances);
        }
    }

    @Override
    public void onSalesOutletAttendancesReceivedFromLocalStorage(List<SalesOutletAttendance> attendances) {
        // Not Used
    }

    //// PRIVATE

    private void setupInitialState() {
        threadScheduler.executeBackgroundThread(new Runnable() {
            @Override
            public void run() {
                databaseService.requestSalesOutlets();
                kgkService.requestSalesOutlets();
                databaseService.requestUserOperations();
                kgkService.requestUserOperations();
            }
        });
    }

    private void updateSalesOutlets(final List<SalesOutlet> salesOutlets) {
        final boolean needToUpdate = this.salesOutlets.size() == 0 ||
                isThereDifferenceBetweenSalesOutlets(this.salesOutlets, salesOutlets);

        threadScheduler.executeMainThread(new Runnable() {
            @Override
            public void run() {
                if (needToUpdate) {
                    MainStoreReactive.this.salesOutlets.clear();
                    MainStoreReactive.this.salesOutlets.addAll(salesOutlets);
                    for (Listener listener : listeners) listener.onSalesOutletChanged();
                }
            }
        });
    }

    private void updateEnteredSalesOutlets(UserLocation userLocation) {
        final List<SalesOutlet> salesOutletsInRadius = setupSalesOutletInRadius(userLocation);

        threadScheduler.executeMainThread(new Runnable() {
            @Override
            public void run() {
                if (enteredSalesOutlets.size() == 0 && salesOutletsInRadius.size() != 0) {
                    enteredSalesOutlets.addAll(salesOutletsInRadius);
                    for (Listener listener : listeners) listener.onEnteredSalesOutletChanged();
                    if (!previouslySelectedSalesOutletUpdated) updatePreviouslySelectedSalesOutlet();
                    return;
                }

                if (isThereDifferenceBetweenSalesOutlets(enteredSalesOutlets, salesOutletsInRadius)) {
                    enteredSalesOutlets.clear();
                    enteredSalesOutlets.addAll(salesOutletsInRadius);
                    for (Listener listener : listeners) listener.onEnteredSalesOutletChanged();
                    if (!previouslySelectedSalesOutletUpdated) updatePreviouslySelectedSalesOutlet();
                }
            }
        });
    }

    private boolean isThereDifferenceBetweenSalesOutlets(List<SalesOutlet> salesOutletsL, List<SalesOutlet> salesOutletsR) {
        if (salesOutletsL.size() != salesOutletsR.size()) return true;

        List<SalesOutlet> salesOutletsState = new ArrayList<>();
        salesOutletsState.addAll(salesOutletsL);
        salesOutletsState.removeAll(salesOutletsR);
        if (salesOutletsState.size() != 0) return true;

        return false;
    }

    private void updateUserOperations(final List<UserOperation> userOperations) {
        final boolean needToUpdate = this.userOperations.size() == 0;

        threadScheduler.executeMainThread(new Runnable() {
            @Override
            public void run() {
                if (needToUpdate) {
                    MainStoreReactive.this.userOperations.clear();
                    MainStoreReactive.this.userOperations.addAll(userOperations);
                    for (Listener listener : listeners) listener.onUserOperationsChanged();
                }
            }
        });
    }

    private void updateSelectedSalesOutlet() {
        if (!enteredSalesOutlets.contains(selectedSalesOutlet)) {
            selectedSalesOutlet = null;
            salesOutletAttendanceBeginDateUnixSeconds = 0;
            for (Listener listener : listeners) listener.onSelectedSalesOutletChanged();
        }
    }

    private void updatePreviouslySelectedSalesOutlet() {
        SalesOutlet previouslySelectedSalesOutlet = settingsStorageService.getSelectedSalesOutlet();

        if (enteredSalesOutlets.contains(previouslySelectedSalesOutlet)) {
            selectedSalesOutlet = previouslySelectedSalesOutlet;
            salesOutletAttendanceBeginDateUnixSeconds = settingsStorageService.getSalesOutletAttendanceBeginDateUnixSeconds();
            for (Listener listener : listeners) listener.onSelectedSalesOutletChanged();
        }
        else {
            settingsStorageService.setSelectedSalesOutlet(null);
            settingsStorageService.setSalesOutletAttendanceBeginDateUnixSeconds(0);
        }

        previouslySelectedSalesOutletUpdated = true;
    }

    private void startPeriodicSynchronizationWithRemoteStorage() {
        threadScheduler.executeBackgroundThread(new Runnable() {
            @Override
            public void run() {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        if (kgkService.isAvailable()) {
                            databaseService.requestNonSynchronizedSalesOutletAttendances();
                        }
                        TimeUnit.SECONDS.sleep(REMOTE_STORAGE_SYNCHRONIZATION_INTERVAL_SECONDS);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        startPeriodicSynchronizationWithRemoteStorage();
                    }
                }
            }
        });
    }

    private List<SalesOutlet> setupSalesOutletInRadius(UserLocation userLocation) {
        List<SalesOutlet> salesOutletsInRadius = new ArrayList<>();

        switch (mode) {
            case Gps:
                for (SalesOutlet salesOutlet : salesOutlets)
                    if (salesOutlet.isUserInZone(userLocation, settingsStorageService))
                        salesOutletsInRadius.add(salesOutlet);
                break;
            case Telephone:
                salesOutletsInRadius.addAll(salesOutlets);
                break;
        }

        return salesOutletsInRadius;
    }
}






















