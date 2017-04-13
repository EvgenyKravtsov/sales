package kgk.mobile.presentation.view.mainscreen.lastactions;


import java.util.List;

import kgk.mobile.domain.SalesOutlet;
import kgk.mobile.domain.SalesOutletAttendance;
import kgk.mobile.domain.UserOperation;
import kgk.mobile.domain.service.DatabaseService;
import kgk.mobile.external.threading.ThreadScheduler;
import kgk.mobile.presentation.view.base.BasePresenterImpl;

public final class LastActionsPresenter extends BasePresenterImpl<LastActionsContract.View>
        implements  LastActionsContract.Presenter, DatabaseService.Listener {

    private final DatabaseService databaseService;
    private final ThreadScheduler threadScheduler;

    ////

    public LastActionsPresenter(DatabaseService databaseService, ThreadScheduler threadScheduler) {
        this.databaseService = databaseService;
        this.databaseService.addListener(this);
        this.threadScheduler = threadScheduler;
    }

    //// BASE PRESENTER

    @Override
    public void detachView() {
        super.detachView();
        this.databaseService.removeListener(this);
    }

    //// LAST ACTIONS PRESENTER

    @Override
    public void onCreateView() {
        threadScheduler.executeBackgroundThread(new Runnable() {
            @Override
            public void run() {
                databaseService.requestSalesOutletAttendances();
            }
        });
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
    public void onNonSynchronizedSalesOutletAttendancesReceivedFromLocalStorage(List<SalesOutletAttendance> attendances) {
        // Not Used
    }

    @Override
    public void onSalesOutletAttendancesReceivedFromLocalStorage(final List<SalesOutletAttendance> attendances) {
        threadScheduler.executeMainThread(new Runnable() {
            @Override
            public void run() {
                view.displaySalesOutletAttendances(attendances);
            }
        });
    }
}
