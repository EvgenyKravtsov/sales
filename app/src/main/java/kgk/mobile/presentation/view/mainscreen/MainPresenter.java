package kgk.mobile.presentation.view.mainscreen;

import java.util.List;

import javax.inject.Inject;

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
        SalesOutletStore.Listener {

    private static final String TAG = MainPresenter.class.getSimpleName();

    private final UserStore userStore;
    private final SalesOutletStore salesOutletStore;

    private MapController mapController;

    ////

    @Inject MainPresenter(UserStore userStore, SalesOutletStore salesOutletStore) {
        this.userStore = userStore;
        this.salesOutletStore = salesOutletStore;
    }

    //// MAIN CONTRACT

    @Override
    public void onMapDisplayed(MapController mapController) {
        this.mapController = mapController;
        userStore.requestPreferredMapZoom(this);
        salesOutletStore.requestSalesOutlets(this);
    }

    @Override
    public void onLocationPermissionGranted() {
        userStore.subscribeForUserLocationUpdate(this);
    }

    //// USER LOCATION LISTENER

    @Override
    public void onLocationReceived(UserLocation userLocation) {
        double latitude = userLocation.getLatitude();
        double longitude = userLocation.getLongitude();
        mapController.displayUser(latitude, longitude);
        mapController.centerCameraOnUser(latitude, longitude, true);
    }

    //// USER PREFERRED MAP ZOOM LISTENER

    @Override
    public void onPreferredMapZoomReceived(float zoom) {
        mapController.displayZoom(zoom, true);
        view.requestLocationPermission();
    }

    //// SALES OUTLET STORE LISTENER

    @Override
    public void onSalesOutletsReceived(List<SalesOutlet> outlets) {
        for (SalesOutlet outlet : outlets) {
            mapController.displaySalesOutlet(outlet.getLatitude(), outlet.getLongitude());
        }
    }
}
