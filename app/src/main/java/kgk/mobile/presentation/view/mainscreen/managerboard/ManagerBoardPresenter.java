package kgk.mobile.presentation.view.mainscreen.managerboard;


import java.util.List;

import kgk.mobile.domain.SalesOutlet;
import kgk.mobile.presentation.model.SalesOutletStore;
import kgk.mobile.presentation.view.base.BasePresenterImpl;

public final class ManagerBoardPresenter extends BasePresenterImpl<ManagerBoardContract.View>
        implements ManagerBoardContract.Presenter,
        SalesOutletStore.Listener {

    ////

    public ManagerBoardPresenter(SalesOutletStore salesOutletStore) {
        salesOutletStore.addListener(this);
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
}
