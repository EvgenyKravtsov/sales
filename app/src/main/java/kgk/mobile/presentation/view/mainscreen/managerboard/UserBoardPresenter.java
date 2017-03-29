package kgk.mobile.presentation.view.mainscreen.managerboard;


import java.util.List;

import kgk.mobile.domain.SalesOutlet;
import kgk.mobile.domain.UserOperation;
import kgk.mobile.presentation.model.SalesOutletStore;
import kgk.mobile.presentation.model.UserStore;
import kgk.mobile.presentation.view.base.BasePresenterImpl;
import kgk.mobile.presentation.view.map.MapController;
import kgk.mobile.threading.ThreadScheduler;

public final class UserBoardPresenter extends BasePresenterImpl<UserBoardContract.View>
        implements UserBoardContract.Presenter,
        SalesOutletStore.Listener,
        UserStore.UserOperationsListener {

    private MapController mapController;

    private final UserStore userStore;
    private final ThreadScheduler threadScheduler;

    ////

    public UserBoardPresenter(
            SalesOutletStore salesOutletStore,
            UserStore userStore,
            ThreadScheduler threadScheduler) {
        this.userStore = userStore;
        this.userStore.addUserOperationsListener(this);
        salesOutletStore.addListener(this);
        this.threadScheduler = threadScheduler;
    }

    //// MANAGER BOARD PRESENTER

    @Override
    public void salesOutletSelectedByUser(SalesOutlet selectedSalesOutlet) {
        mapController.displaySelectedSalesOutlet(selectedSalesOutlet);

        threadScheduler.executeBackgroundThread(new Runnable() {
            @Override
            public void run() {
                userStore.requestUserOperations();
            }
        });
    }

    @Override
    public void setMapController(MapController mapController) {
        this.mapController = mapController;
    }

    //// SALES OUTLET STORE LISTENER

    @Override
    public void onSalesOutletsReceived(List<SalesOutlet> outlets) {
        // Not Used
    }

    @Override
    public void salesOutletsEnteredByUser(List<SalesOutlet> salesOutletsEntered) {
        view.displayEnteredSalesOutlets(salesOutletsEntered);
    }

    //// USER OPERATIONS LISTENER

    @Override
    public void onUserOperationsReceived(final List<UserOperation> userOperations) {
        threadScheduler.executeMainThread(new Runnable() {
            @Override
            public void run() {
                view.displayUserOperations(userOperations);
            }
        });
    }
}
