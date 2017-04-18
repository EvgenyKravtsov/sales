package kgk.mobile.presentation.view.mainactivitynew;


import java.util.Calendar;
import java.util.List;

import kgk.mobile.domain.SalesOutlet;
import kgk.mobile.domain.UserLocation;
import kgk.mobile.domain.UserOperation;
import kgk.mobile.presentation.model.SalesOutletAttendanceBegin;

final class MainPresenter_new implements MainContract_new.Presenter, MainStore.Listener {

    private final MainStore store;

    private MainContract_new.View view;

    ////

    MainPresenter_new(MainStore store) {
        this.store = store;
    }

    //// MAIN PRESENTER

    @Override
    public void attachView(MainContract_new.View view) {
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

        // Displaying User Location
        UserLocation userLocation = store.getUserLocation();
        if (userLocation != null) view.displayUserLocation(userLocation);
        else view.displayUserLocationFetchingAlert();

        // Displaying Sales Outlets
        List<SalesOutlet> salesOutlets = store.getSalesOutlets();
        if (salesOutlets.size() == 0) view.displayLoadingSalesOutletsAlert();
        view.displaySalesOutlets(store.getSalesOutlets());

        view.displayMapZoom(store.getMapZoom());
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
    public void onClickEnteredSalesOutlet(SalesOutlet selectedSalesOutlet) {
        store.setSelectedSalesOutlet(selectedSalesOutlet);
    }

    @Override
    public void onClickUserOperationsConfirmButton(List<UserOperation> selectedUserOperations,
                                                   int attendanceAddedValue) {
        store.salesOutletAttended(selectedUserOperations, attendanceAddedValue);
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
}



































