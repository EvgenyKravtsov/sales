package kgk.mobile.presentation.view.mainscreen;

import android.util.Log;

import java.util.List;


import kgk.mobile.threading.ThreadScheduler;
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
        MapController.Listener {

    private static final String TAG = MainPresenter.class.getSimpleName();

    private final UserStore userStore;
    private final SalesOutletStore salesOutletStore;
    private final ThreadScheduler threadScheduler;

    private MapController mapController;

    ////

    public MainPresenter(UserStore userStore,
                         SalesOutletStore salesOutletStore,
                         ThreadScheduler threadScheduler) {

        this.userStore = userStore;
        this.salesOutletStore = salesOutletStore;
        this.threadScheduler = threadScheduler;
    }

    //// MAIN CONTRACT

    @Override
    public void onMapDisplayed(MapController mapController) {
        this.mapController = mapController;
        this.mapController.addListener(this);
        this.salesOutletStore.addListener(this);

        threadScheduler.executeBackgroundThread(new Runnable() {
            @Override
            public void run() {
                userStore.requestPreferredMapZoom(MainPresenter.this);
                salesOutletStore.requestSalesOutlets();
            }
        });
    }

    @Override
    public void onLocationPermissionGranted() {
        userStore.subscribeForUserLocationUpdate(this);
    }

    @Override
    public void onPhoneStatePermissionNotGranted() {
        view.exit();
    }

    //// USER LOCATION LISTENER

    @Override
    public void onLocationReceived(UserLocation userLocation) {
        double latitude = userLocation.getLatitude();
        double longitude = userLocation.getLongitude();
        mapController.displayUser(latitude, longitude);
        mapController.centerCameraOnUser(latitude, longitude, true);
        salesOutletStore.isUserInSalesOutletZone(userLocation);
    }

    //// USER PREFERRED MAP ZOOM LISTENER

    @Override
    public void onPreferredMapZoomReceived(final float zoom) {
        threadScheduler.executeMainThread(new Runnable() {
            @Override
            public void run() {
                mapController.displayZoom(zoom, true);
                view.requestLocationPermission();
                view.requestPhoneStatePermission();
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
}
