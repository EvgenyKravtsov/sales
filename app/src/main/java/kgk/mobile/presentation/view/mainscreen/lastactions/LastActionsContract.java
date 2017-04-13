package kgk.mobile.presentation.view.mainscreen.lastactions;


import java.util.List;

import kgk.mobile.domain.SalesOutletAttendance;
import kgk.mobile.presentation.view.base.BasePresenter;
import kgk.mobile.presentation.view.base.BaseView;

public interface LastActionsContract {

    interface Presenter extends BasePresenter<View> {

        void onCreateView();
    }

    interface View extends BaseView {

        void displaySalesOutletAttendances(List<SalesOutletAttendance> attendances);
    }
}
