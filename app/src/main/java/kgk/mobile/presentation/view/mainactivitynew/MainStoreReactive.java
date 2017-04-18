package kgk.mobile.presentation.view.mainactivitynew;


import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
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
import kgk.mobile.external.threading.ThreadScheduler;

class MainStoreReactive implements MainStore, LocationService.Listener,
        KgkService.Listener, DatabaseService.Listener {

    // TODO Disable Logging

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

    private UserLocation userLocation;
    private SalesOutlet selectedSalesOutlet;
    private long salesOutletAttendanceBeginDateUnixSeconds;
    private boolean previouslySelectedSalesOutletUpdated;

    ////

    MainStoreReactive(LocationService locationService,
                      ThreadScheduler threadScheduler,
                      KgkService kgkService,
                      DatabaseService databaseService,
                      SettingsStorageService settingsStorageService) {
        locationService.addListener(this);
        locationService.startLocationUpdate();

        this.threadScheduler = threadScheduler;
        this.kgkService = kgkService;
        this.kgkService.addListener(this);
        this.databaseService = databaseService;
        this.databaseService.addListener(this);
        this.settingsStorageService = settingsStorageService;

        setupInitialState();
        startPeriodicSynchronizationWithRemoteStorage();
    }

    //// MAIN STORE

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
                attendanceAddedValue);

        threadScheduler.executeBackgroundThread(new Runnable() {
            @Override
            public void run() {
                databaseService.insertSalesOutletAttendance(attendance);
            }
        });
    }

    //// LOCATION SERVICE LISTENER

    @Override
    public void onLocationChanged(UserLocation userLocation) {
        // Method Called On Main Thread

        this.userLocation = userLocation;
        for (Listener listener : listeners) listener.onUserLocationChanged();
        updateEnteredSalesOutlets(userLocation);
    }

    //// KGK SERVICE LISTENER

    @Override
    public void onSalesOutletsReceivedFromRemoteStorage(List<SalesOutlet> salesOutlets) {
        // Method Called On Background Thread

        if (salesOutlets.size() != 0) {
            databaseService.updateSalesOutlets(salesOutlets);
            updateSalesOutlets(salesOutlets);
        }

        Log.d(TAG, "onSalesOutletsReceivedFromRemoteStorage: " + salesOutlets.size());
    }

    @Override
    public void onUserOperationsReceivedFromRemoteStorage(List<UserOperation> userOperations) {
        // Method Called On Background Thread

        if (salesOutlets.size() != 0) {
            databaseService.updateUserOperations(userOperations);
            updateUserOperations(userOperations);
        }

        Log.d(TAG, "onUserOperationsReceivedFromRemoteStorage: " + userOperations.size());
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
        // Not Used
    }

    //// DATABASE SERVICE LISTENER

    @Override
    public void onSalesOutletsReceivedFromLocalStorage(List<SalesOutlet> salesOutlets) {
        // Method Called On Background Thread

        if (salesOutlets.size() != 0) updateSalesOutlets(salesOutlets);

        Log.d(TAG, "onSalesOutletsReceivedFromLocalStorage: " + salesOutlets.size());
    }

    @Override
    public void onUserOperationsReceivedFromLocalStorage(List<UserOperation> userOperations) {
        // Method Called On Background Thread

        if (userOperations.size() != 0) updateUserOperations(userOperations);

        Log.d(TAG, "onUserOperationsReceivedFromLocalStorage: " + userOperations.size());
    }

    @Override
    public void onNonSynchronizedSalesOutletAttendancesReceivedFromLocalStorage(List<SalesOutletAttendance> attendances) {
        // Method Called On Background Thread

        Log.d(TAG, "onNonSynchronizedSalesOutletAttendancesReceivedFromLocalStorage: " + attendances.size());

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
                    Log.d(TAG, "updateSalesOutlets: SalesOutletsUpdated");
                }
            }
        });
    }

    private void updateEnteredSalesOutlets(UserLocation userLocation) {
        List<SalesOutlet> salesOutletsInRadius = new ArrayList<>();

        for (SalesOutlet salesOutlet : salesOutlets) {
            if (salesOutlet.isUserInZone(userLocation, settingsStorageService)) {
                salesOutletsInRadius.add(salesOutlet);
            }
        }

        if (salesOutletsInRadius.size() == 0) return;

        if (enteredSalesOutlets.size() == 0) {
            enteredSalesOutlets.addAll(salesOutletsInRadius);
            for (Listener listener : listeners) listener.onEnteredSalesOutletChanged();
            if (!previouslySelectedSalesOutletUpdated) updatePreviouslySelectedSalesOutlet();
            Log.d(TAG, "updateEnteredSalesOutlets: ");
            return;
        }

        if (isThereDifferenceBetweenSalesOutlets(enteredSalesOutlets, salesOutletsInRadius)) {
            enteredSalesOutlets.clear();
            enteredSalesOutlets.addAll(salesOutletsInRadius);
            for (Listener listener : listeners) listener.onEnteredSalesOutletChanged();
            if (!previouslySelectedSalesOutletUpdated) updatePreviouslySelectedSalesOutlet();
            Log.d(TAG, "updateEnteredSalesOutlets: ");
        }
    }

    private boolean isThereDifferenceBetweenSalesOutlets(List<SalesOutlet> salesOutletsL, List<SalesOutlet> salesOutletsR) {
        if (salesOutletsL.size() != salesOutletsR.size()) return true;

        List<SalesOutlet> salesOutletsState = new ArrayList<>();
        salesOutletsState.addAll(salesOutletsL);
        salesOutletsState.removeAll(salesOutletsR);
        if (salesOutletsState.size() != 0) return true;

        Log.d(TAG, "isThereDifferenceBetweenSalesOutlets: No Difference");

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
                    Log.d(TAG, "updateUserOperations: UserOperationsUpdated");
                }
            }
        });
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
}






















