package kgk.mobile.presentation.view.mainscreen.userboard;


import java.util.List;

import kgk.mobile.domain.UserOperation;
import kgk.mobile.domain.SalesOutlet;
import kgk.mobile.presentation.view.base.BasePresenter;
import kgk.mobile.presentation.view.base.BaseView;
import kgk.mobile.presentation.view.map.MapController;

public interface UserBoardContract {

    interface Presenter extends BasePresenter<View> {

        void salesOutletSelectedByUser(SalesOutlet selectedSalesOutlet);

        void setMapController(MapController mapController);

        void salesOutletAttended(List<UserOperation> selectedUserOperations,
                                 int addedValue,
                                 long salesOutletAttendanceEndDateUnixSeconds);
    }

    interface View extends BaseView {

        void displayEnteredSalesOutlets(List<SalesOutlet> salesOutletsEntered);

        void displayUserOperations(List<UserOperation> userOperations);

        void hideUserOperations();

        void displaySelectedSalesOutlet(SalesOutlet selectedSalesOutlet);
    }
}
