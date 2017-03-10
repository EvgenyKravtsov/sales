package kgk.mobile.presentation.view.mainscreen;


import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

import kgk.mobile.domain.SalesOutlet;
import kgk.mobile.domain.UserLocation;
import kgk.mobile.presentation.model.SalesOutletStore;
import kgk.mobile.presentation.model.UserStore;
import kgk.mobile.presentation.view.map.MapController;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public final class MainPresenterTest {

    private static final float PREFERRED_ZOOM = 10.0f;

    private UserStore userStoreMock;
    private MainContract.View viewMock;
    private MapController mapControllerMock;
    private SalesOutletStore salesOutletStoreMock;
    private MainPresenter mainPresenter;

    ////

    @Before
    public void setUp() {
        userStoreMock = Mockito.mock(UserStore.class);
        viewMock = Mockito.mock(MainContract.View.class);
        mapControllerMock = Mockito.mock(MapController.class);
        salesOutletStoreMock = Mockito.mock(SalesOutletStore.class);
        mainPresenter = new MainPresenter(userStoreMock, salesOutletStoreMock);
        mainPresenter.attachView(viewMock);
    }

    ////

    @Test
    public void mapDisplayed_preferredMapZoomRequested() {
        mainPresenter.onMapDisplayed(null);
        verify(userStoreMock).requestPreferredMapZoom(mainPresenter);
    }

    @Test
    public void mapDisplayed_salesOutletsRequested() {
        mainPresenter.onMapDisplayed(null);
        verify(salesOutletStoreMock).requestSalesOutlets(mainPresenter);
    }

    @Test
    public void mapPreferredZoomReceived_preferredZoomDisplayed() {
        mainPresenter.onMapDisplayed(mapControllerMock);
        mainPresenter.onPreferredMapZoomReceived(PREFERRED_ZOOM);

        verify(mapControllerMock).displayZoom(PREFERRED_ZOOM, true);
    }

    @Test
    public void onPreferredZoomReceived_locationPermissionRequested() {
        mainPresenter.onMapDisplayed(mapControllerMock);
        mainPresenter.onPreferredMapZoomReceived(PREFERRED_ZOOM);

        verify(viewMock).requestLocationPermission();
    }

    @Test
    public void locationPermissionGranted_subscribedForUserLocationUpdate() {
        mainPresenter.onLocationPermissionGranted();
        verify(userStoreMock).subscribeForUserLocationUpdate(mainPresenter);
    }

    @Test
    public void userLocationReceived_userDisplayedOnMap() {
        mainPresenter.onMapDisplayed(mapControllerMock);
        mainPresenter.onLocationReceived(Mockito.mock(UserLocation.class));

        verify(mapControllerMock).displayUser(anyDouble(), anyDouble());
    }

    @Test
    public void userLocationReceived_mapCameraCenteredOnUser() {
        mainPresenter.onMapDisplayed(mapControllerMock);
        mainPresenter.onLocationReceived(Mockito.mock(UserLocation.class));

        verify(mapControllerMock).centerCameraOnUser(anyDouble(), anyDouble(), anyBoolean());
    }

    @Test
    public void salesOutletsReceived_salesOutletsDisplayed() {
        List<SalesOutlet> outlets = Arrays.asList(new SalesOutlet(), new SalesOutlet(),
                new SalesOutlet(), new SalesOutlet());

        mainPresenter.onMapDisplayed(mapControllerMock);
        mainPresenter.onSalesOutletsReceived(outlets);

        verify(mapControllerMock, times(outlets.size()))
                .displaySalesOutlet(anyDouble(), anyDouble());
    }
}



















