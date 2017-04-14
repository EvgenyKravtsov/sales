package kgk.mobile.presentation.view.mainscreen;

import java.util.List;


import kgk.mobile.domain.service.SystemService;
import kgk.mobile.external.threading.ThreadScheduler;
import kgk.mobile.domain.SalesOutlet;
import kgk.mobile.domain.UserLocation;
import kgk.mobile.presentation.model.SalesOutletStore;
import kgk.mobile.presentation.model.UserStore;
import kgk.mobile.presentation.view.base.BasePresenterImpl;
import kgk.mobile.presentation.view.map.MapController;


public final class MainPresenter extends BasePresenterImpl<MainContract.View>
        implements MainContract.Presenter,
        UserStore.LocationListener,
        UserStore.PreferredMapZoomListener,
        SalesOutletStore.Listener,
        MapController.Listener,
        SystemService.Listener {

    private static final String TAG = MainPresenter.class.getSimpleName();

    private final UserStore userStore;
    private final SalesOutletStore salesOutletStore;
    private final ThreadScheduler threadScheduler;
    private final SystemService systemService;

    private MapController mapController;

    ////

    public MainPresenter(UserStore userStore,
                         SalesOutletStore salesOutletStore,
                         ThreadScheduler threadScheduler,
                         SystemService systemService) {

        this.userStore = userStore;
        this.salesOutletStore = salesOutletStore;
        this.threadScheduler = threadScheduler;
        this.systemService = systemService;
        this.systemService.addListener(this);
    }

    //// BASE PRESENTER

    @Override
    public void detachView() {
        super.detachView();
        this.userStore.unsubscribeForUserLocationUpdate(this);
        this.systemService.removeListener(this);
    }

    //// MAIN PRESENTER

    @Override
    public void onMapDisplayed(MapController mapController) {
        this.mapController = mapController;
        this.mapController.addListener(this);
        this.salesOutletStore.addListener(this);
        this.userStore.subscribeForUserLocationUpdate(this);

        threadScheduler.executeBackgroundThread(new Runnable() {
            @Override
            public void run() {
                userStore.requestPreferredMapZoom(MainPresenter.this);
                salesOutletStore.requestSalesOutlets();
            }
        });

        view.displayFetchingLocationAlert();
    }

    @Override
    public void onMenuButtonClicked() {
        view.displayNavigationMenu();
    }

    @Override
    public void onNavigateToTechnicalInformationButtonClicked() {
        view.navigateToTechnicalInformation();
    }

    @Override
    public void onNavigateToLastActionsButtonClicked() {
        view.navigateToLastActions();
    }

    @Override
    public void onClickHardwareBack() {
        mapController.redrawMapObjects();
    }

    //// USER LOCATION LISTENER

    @Override
    public void onLocationReceived(UserLocation userLocation) {
        double latitude = userLocation.getLatitude();
        double longitude = userLocation.getLongitude();
        mapController.displayUser(latitude, longitude);
        mapController.centerCamera(latitude, longitude, false);
        salesOutletStore.isUserInSalesOutletZone(userLocation);
        if (view != null) view.hideFetchingLocationAlert();
    }

    //// USER PREFERRED MAP ZOOM LISTENER

    @Override
    public void onPreferredMapZoomReceived(final float zoom) {
        threadScheduler.executeMainThread(new Runnable() {
            @Override
            public void run() {
                mapController.displayZoom(zoom, true);
            }
        });
    }

    //// SALES OUTLET STORE LISTENER

    @Override
    public void onSalesOutletsReceived(final List<SalesOutlet> outlets) {
        threadScheduler.executeMainThread(new Runnable() {
            @Override
            public void run() {
                mapController.displaySalesOutlets(outlets);
            }
        });
    }

    @Override
    public void salesOutletsEnteredByUser(List<SalesOutlet> salesOutletsEntered) {
        mapController.displayEnteredSalesOutlets(salesOutletsEntered);
    }

    //// MAP CONTROLLER LISTENER

    @Override
    public void onMapZoomChanged(final float zoom) {
        threadScheduler.executeBackgroundThread(new Runnable() {
            @Override
            public void run() {
                userStore.savePreferredMapZoom(zoom);
            }
        });
    }

    //// SYSTEM SERVICE LISTENER // TODO Write Test For This Functions

    @Override
    public void onInternetConnectionStatusChanged(final boolean status) {
        threadScheduler.executeMainThread(new Runnable() {
            @Override
            public void run() {
                if (status) {
                    view.hideKgkServiceOfflineAlert();
                    view.hideInternetServiceOfflineAlert();
                }
                else {
                    view.displayKgkServiceOfflineAlert();
                    view.displayInternetServiceOfflineAlert();
                }
            }
        });
    }
}
