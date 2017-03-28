package kgk.mobile.presentation.view.mainscreen.managerboard;


import java.util.List;

import kgk.mobile.domain.SalesOutlet;
import kgk.mobile.presentation.view.base.BasePresenter;
import kgk.mobile.presentation.view.base.BaseView;

public interface ManagerBoardContract {

    interface Presenter extends BasePresenter<View> {

    }

    interface View extends BaseView {

        void displayEnteredSalesOutlets(List<SalesOutlet> salesOutletsEntered);
    }
}
