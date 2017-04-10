package kgk.mobile.presentation.model.async;

import java.util.List;
import java.util.concurrent.TimeUnit;

import kgk.mobile.domain.SalesOutlet;
import kgk.mobile.domain.SalesOutletAttendance;
import kgk.mobile.domain.UserOperation;
import kgk.mobile.domain.service.DatabaseService;
import kgk.mobile.domain.service.KgkService;
import kgk.mobile.presentation.model.SalesOutletAttendanceStore;


public final class SalesOutletAttendanceStoreAsync
        implements SalesOutletAttendanceStore,
        DatabaseService.Listener,
        KgkService.Listener {

    private static final int REMOTE_STORAGE_SYNCHRONIZATION_INTERVAL_SECONDS = 10;

    private boolean isSynchronizationThreadActive;

    private final DatabaseService databaseService;
    private final KgkService kgkService;

    ////

    public SalesOutletAttendanceStoreAsync(DatabaseService databaseService,
                                           KgkService kgkService) {
        this.databaseService = databaseService;
        this.databaseService.addListener(this);

        this.kgkService = kgkService;
        this.kgkService.addListener(this);

        isSynchronizationThreadActive = true;
        startPeriodicSynchronizationWithRemoteStorage();
    }

    //// SALES OUTLET ATTENDANCE STORE

    @Override
    public void put(SalesOutletAttendance attendance) {
        databaseService.insertSalesOutletAttendance(attendance);
    }

    //// DATABASE SERVICE LISTENER

    @Override
    public void onSalesOutletsReceivedFromLocalStorage(List<SalesOutlet> salesOutlets) {
        // Not Used
    }

    @Override
    public void onUserOperationsReceivedFromLocalStorage(List<UserOperation> userOperations) {
        // Not Used
    }

    @Override
    public void onNonSynchronizedSalesOutletAttendanceMessagesReceivedFromLocalStorage(
            List<String> attendanceMessages) {
        if (kgkService.isAvailable()) {
            kgkService.sendSalesOutletAttendances(attendanceMessages);
        }
    }

    //// KGK SERVICE LISTENER

    @Override
    public void onSalesOutletsReceivedFromRemoteStorage(List<SalesOutlet> salesOutlets) {
        // Not Used
    }

    @Override
    public void onUserOperationsReceivedFromRemoteStorage(List<UserOperation> userOperations) {
        // Not Used
    }

    @Override
    public void onPointExitIdReceivedFromRemoteStorage(String eventId) {
        databaseService.confirmSalesOutletAttendance(eventId);
    }

    //// PRIVATE

    private void startPeriodicSynchronizationWithRemoteStorage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isSynchronizationThreadActive) {
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
        }).start();
    }
}
