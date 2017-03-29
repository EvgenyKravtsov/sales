package kgk.mobile.presentation.model.async;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import kgk.mobile.domain.SalesOutlet;
import kgk.mobile.domain.UserLocation;
import kgk.mobile.domain.UserOperation;
import kgk.mobile.domain.service.DatabaseService;
import kgk.mobile.domain.service.KgkService;
import kgk.mobile.presentation.model.SalesOutletStore;


public final class SalesOutletStoreAsync
        implements SalesOutletStore,
        KgkService.Listener,
        DatabaseService.Listener {

    private static final String TAG = SalesOutletStoreAsync.class.getSimpleName();
    private static final int REMOTE_STORAGE_SYNCHRONIZATION_INTERVAL_NORMAL_MINUTES = 15;
    private static final int REMOTE_STORAGE_SYNCHRONIZATION_INTERVAL_EMERGENCY_SECONDS = 5;

    private boolean isSalesOutletsSynchronizedWithRemoteStorage;

    private final KgkService kgkService;
    private final DatabaseService databaseService;
    private final List<Listener> listeners = new ArrayList<>();
    private final List<SalesOutlet> salesOutlets = new ArrayList<>();

    ////

    public SalesOutletStoreAsync(KgkService kgkService, DatabaseService databaseService) {
        this.kgkService = kgkService;
        this.kgkService.addListener(this);
        this.databaseService = databaseService;
        this.databaseService.addListener(this);
        startPeriodicSynchronizationWithRemoteStorage();
    }

    //// SALES OUTLET STORE

    @Override
    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    @Override
    public void requestSalesOutlets() {
        if (kgkService.isAvailable()) requestSalesOutletFromRemoteStorage();
        else requestSalesOutletFromLocalStorage();
    }

    @Override
    public void isUserInSalesOutletZone(UserLocation userLocation) {
        List<SalesOutlet> salesOutletsEntered = new ArrayList<>();

        for (SalesOutlet salesOutlet : salesOutlets) {
            if (salesOutlet.isUserInZone(userLocation)) salesOutletsEntered.add(salesOutlet);
        }

        for (Listener listener : listeners) listener.salesOutletsEnteredByUser(salesOutletsEntered);
    }

    //// KGK SERVICE LISTENER

    @Override
    public void onSalesOutletsReceivedFromRemoteStorage(List<SalesOutlet> salesOutlets) {
        for (Listener listener : listeners) listener.onSalesOutletsReceived(salesOutlets);
        databaseService.updateSalesOutlets(salesOutlets);
        updateSalesOutlets(salesOutlets);
    }

    @Override
    public void onUserOperationsReceivedFromRemoteStorage(List<UserOperation> userOperations) {
        // Not Used
    }

    //// DATABASE SERVICE LISTENER

    @Override
    public void onSalesOutletsReceivedFromLocalStorage(List<SalesOutlet> salesOutlets) {
        for (Listener listener : listeners) listener.onSalesOutletsReceived(salesOutlets);
        updateSalesOutlets(salesOutlets);
    }

    //// PRIVATE

    private void requestSalesOutletFromRemoteStorage() {
        kgkService.requestSalesOutlets();
    }

    private void requestSalesOutletFromLocalStorage() {
        databaseService.requestSalesOutlets();
    }

    private void startPeriodicSynchronizationWithRemoteStorage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (isSalesOutletsSynchronizedWithRemoteStorage) {
                        TimeUnit.MINUTES.sleep(REMOTE_STORAGE_SYNCHRONIZATION_INTERVAL_NORMAL_MINUTES);
                        isSalesOutletsSynchronizedWithRemoteStorage = false;
                        requestSalesOutletFromRemoteStorage();
                    }
                    else {
                        TimeUnit.SECONDS.sleep(REMOTE_STORAGE_SYNCHRONIZATION_INTERVAL_EMERGENCY_SECONDS);
                        requestSalesOutletFromRemoteStorage();
                    }
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                    startPeriodicSynchronizationWithRemoteStorage();
                }
            }
        }).start();
    }

    private void updateSalesOutlets(List<SalesOutlet> salesOutlets) {
        this.salesOutlets.clear();
        this.salesOutlets.addAll(salesOutlets);
    }
}


















