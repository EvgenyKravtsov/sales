package kgk.mobile.presentation.view.mainactivitynew;


import java.util.List;

import kgk.mobile.domain.SalesOutlet;
import kgk.mobile.domain.UserLocation;
import kgk.mobile.domain.UserOperation;

interface MainContract_new {

    interface Presenter {

        void attachView(View view);

        void detachView();

        void onViewReady();

        void onMapZoomChanged(float zoom);

        void onClickNavigationMenuButton();

        void onClickNavigationMenuDropDownButton();

        void onClickTechnicalInformationButton();

        void onClickEnteredSalesOutlet(SalesOutlet selectedSalesOutlet);

        void onClickUserOperationsConfirmButton(List<UserOperation> selectedUserOperations,
                                                int attendanceAddedValue);
    }

    ////

    interface View {

        void displayUserLocation(UserLocation userLocation);

        void displayUserLocationFetchingAlert();

        void hideUserLocationFetchingAlert();

        void displaySalesOutlets(List<SalesOutlet> salesOutlets);

        void displayLoadingSalesOutletsAlert();

        void hidLoadingSalesOutletsAlert();

        void displayEnteredSalesOutlets(List<SalesOutlet> enteredSalesOutlet);

        void displayMapZoom(float zoom);

        void displayNavigationMenu();

        void hideNavigationMenu();

        void navigateToTechnicalInformation();

        void displaySelectedSalesOutlet(SalesOutlet selectedSalesOutlet,
                                        long salesOutletAttendanceBeginDateUnixSeconds);

        void displayUserOperations(List<UserOperation> userOperations);

        void hideSelectedSalesOutlet();
    }
}





















