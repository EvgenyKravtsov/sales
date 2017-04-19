package kgk.mobile.presentation.view.mainscreen;


import java.util.List;

import kgk.mobile.domain.SalesOutlet;
import kgk.mobile.domain.UserLocation;
import kgk.mobile.domain.UserOperation;

public interface MainContract {

    interface Presenter {

        void attachView(View view);

        void detachView();

        void onViewReady();

        void onMapZoomChanged(float zoom);

        void onClickNavigationMenuButton();

        void onClickNavigationMenuDropDownButton();

        void onClickTechnicalInformationButton();

        void onClickLastActionsButton();

        void onClickHelpButton();

        void onClickEnteredSalesOutlet(SalesOutlet selectedSalesOutlet);

        void onClickUserOperationsConfirmButton(List<UserOperation> selectedUserOperations,
                                                int attendanceAddedValue);
        void onPermissionsDenied();

        void onPermissionGranted();
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

        void navigateToLastActions();

        void navigateToHelp();

        void displaySelectedSalesOutlet(SalesOutlet selectedSalesOutlet,
                                        long salesOutletAttendanceBeginDateUnixSeconds);
        void displayUserOperations(List<UserOperation> userOperations);

        void hideSelectedSalesOutlet();

        void displayLoadingAuthorizationAlert();

        void hideLoadingAuthorizationAlert();

        void displayAuthorizationDeniedAlert();

        void displayAuthorizationDenied();

        void hideAuthorizationDenied();

        void requestPermissions();

        void displayPermissionsNeededAlert();
    }
}