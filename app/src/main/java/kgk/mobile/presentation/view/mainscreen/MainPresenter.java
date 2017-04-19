package kgk.mobile.presentation.view.mainscreen;


import android.util.Log;

import java.util.List;

import kgk.mobile.domain.SalesOutlet;
import kgk.mobile.domain.UserLocation;
import kgk.mobile.domain.UserOperation;
import kgk.mobile.external.network.Authorization;
import kgk.mobile.presentation.model.MainStore;

public final class MainPresenter implements MainContract.Presenter, MainStore.Listener {

    private final static String TAG = MainPresenter.class.getSimpleName();

    private final MainStore store;

    private MainContract.View view;

    ////

    public MainPresenter(MainStore store) {
        this.store = store;
    }

    //// MAIN PRESENTER

    @Override
    public void attachView(MainContract.View view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        this.view = null;
        this.store.removeListener(this);
    }

    @Override
    public void onViewReady() {
        store.addListener(this);
        view.requestPermissions();
        view.displayMapZoom(store.getMapZoom());

        // Authorization
        Authorization authorization = store.getAuthorization();
        if (authorization == null) view.displayLoadingAuthorizationAlert();
        else setupAuthorization(authorization);

        // Displaying User Location
        UserLocation userLocation = store.getUserLocation();
        if (userLocation != null) view.displayUserLocation(userLocation);
        else view.displayUserLocationFetchingAlert();
    }

    @Override
    public void onMapZoomChanged(float zoom) {
        store.setMapZoom(zoom);
    }

    @Override
    public void onClickNavigationMenuButton() {
        view.displayNavigationMenu();
    }

    @Override
    public void onClickNavigationMenuDropDownButton() {
        view.hideNavigationMenu();
    }

    @Override
    public void onClickTechnicalInformationButton() {
        view.hideNavigationMenu();
        view.navigateToTechnicalInformation();
    }

    @Override
    public void onClickLastActionsButton() {
        view.hideNavigationMenu();
        view.navigateToLastActions();
    }

    @Override
    public void onClickHelpButton() {
        view.navigateToHelp();
    }

    @Override
    public void onClickEnteredSalesOutlet(SalesOutlet selectedSalesOutlet) {
        store.setSelectedSalesOutlet(selectedSalesOutlet);
    }

    @Override
    public void onClickUserOperationsConfirmButton(List<UserOperation> selectedUserOperations,
                                                   int attendanceAddedValue) {
        store.salesOutletAttended(selectedUserOperations, attendanceAddedValue);
    }
    @Override
    public void onPermissionsDenied() {
        view.displayPermissionsNeededAlert();
    }

    @Override
    public void onPermissionGranted() {
        Log.d(TAG, "onPermissionGranted: ");
        store.setup();
    }

    //// MAIN STORE LISTENER

    @Override
    public void onUserLocationChanged() {
        view.hideUserLocationFetchingAlert();
        view.displayUserLocation(store.getUserLocation());
    }

    @Override
    public void onSalesOutletChanged() {
        view.hidLoadingSalesOutletsAlert();
        view.displaySalesOutlets(store.getSalesOutlets());
    }

    @Override
    public void onEnteredSalesOutletChanged() {
        view.displayEnteredSalesOutlets(store.getEnteredSalesOutlets());
    }

    @Override
    public void onSelectedSalesOutletChanged() {
        SalesOutlet selectedSalesOutlet = store.getSelectedSalesOutlet();

        if (selectedSalesOutlet != null) {
            view.displaySelectedSalesOutlet(
                    store.getSelectedSalesOutlet(),
                    store.getSalesOutletAttendanceBeginDateUnixSeconds());
            view.displayUserOperations(store.getUserOperations());
        }
        else {
            view.hideSelectedSalesOutlet();
        }
    }

    @Override
    public void onUserOperationsChanged() {
        view.displayUserOperations(store.getUserOperations());
    }

    @Override
    public void onAuthorizationChanged() {
        setupAuthorization(store.getAuthorization());
    }

    //// PRIVATE

    private void setupAuthorization(Authorization authorization) {
        view.hideLoadingAuthorizationAlert();

        if (authorization.isAuthorized()) {
            List<SalesOutlet> salesOutlets = store.getSalesOutlets();
            if (salesOutlets.size() == 0) view.displayLoadingSalesOutletsAlert();
            view.displaySalesOutlets(store.getSalesOutlets());
            view.hideAuthorizationDenied();
        }
        else {
            view.displayAuthorizationDeniedAlert();
            view.displayAuthorizationDenied();
        }
    }
}



































