package kgk.mobile.presentation.view.mainscreen;


import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import kgk.mobile.external.threading.ThreadScheduler;
import kgk.mobile.domain.SalesOutlet;
import kgk.mobile.domain.UserLocation;
import kgk.mobile.presentation.model.SalesOutletStore;
import kgk.mobile.presentation.model.UserStore;
import kgk.mobile.presentation.view.map.MapController;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

public final class MainPresenterTest {

    private static final float PREFERRED_ZOOM = 10.0f;

    @Mock
    private UserStore userStoreMock;
    @Mock
    private MapController mapControllerMock;
    @Mock
    private MainContract.View viewMock;
    @Mock
    private ThreadScheduler threadSchedulerMock;
    @Mock
    private SalesOutletStore salesOutletStoreMock;
    @Mock
    private UserLocation userLocationMock;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private MainPresenter mainPresenter;

    ////

    @Before
    public void setUp() {
        mainPresenter = new MainPresenter(userStoreMock, salesOutletStoreMock, threadSchedulerMock);
        mainPresenter.attachView(viewMock);
    }

    ////

    @Test
    public void mapDisplayed_preferredMapZoomRequested() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                userStoreMock.requestPreferredMapZoom(mainPresenter);
                return null;
            }
        }).when(threadSchedulerMock).executeBackgroundThread(any(Runnable.class));

        mainPresenter.onMapDisplayed(mapControllerMock);

        verify(userStoreMock).requestPreferredMapZoom(mainPresenter);
    }

    @Test
    public void mapDisplayed_salesOutletsRequested() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                salesOutletStoreMock.requestSalesOutlets();
                return null;
            }
        }).when(threadSchedulerMock).executeBackgroundThread(any(Runnable.class));

        mainPresenter.onMapDisplayed(mapControllerMock);

        verify(salesOutletStoreMock).requestSalesOutlets();
    }

    @Test
    public void mapDisplayed_startedListenToMapEvents() {
        mainPresenter.onMapDisplayed(mapControllerMock);
        verify(mapControllerMock).addListener(mainPresenter);
    }

    @Test
    public void mapDisplayed_fetchingLocationAlertDisplayed() {
        mainPresenter.onMapDisplayed(mapControllerMock);
        verify(viewMock).displayFetchingLocationAlert();
    }

    @Test
    public void mapPreferredZoomReceived_preferredZoomDisplayed() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                mapControllerMock.displayZoom(PREFERRED_ZOOM, true);
                return null;
            }
        }).when(threadSchedulerMock).executeMainThread(any(Runnable.class));

        mainPresenter.onMapDisplayed(mapControllerMock);
        mainPresenter.onPreferredMapZoomReceived(PREFERRED_ZOOM);

        verify(mapControllerMock).displayZoom(PREFERRED_ZOOM, true);
    }

    @Test
    public void onPreferredZoomReceived_locationPermissionRequested() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                viewMock.requestLocationPermission();
                return null;
            }
        }).when(threadSchedulerMock).executeMainThread(any(Runnable.class));

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
    public void userLocationReceived_fetchingLocationAlertHidden() {
        mainPresenter.onMapDisplayed(mapControllerMock);
        mainPresenter.onLocationReceived(Mockito.mock(UserLocation.class));

        verify(viewMock).hideFetchingLocationAlert();
    }

    @Test
    public void userLocationReceived_mapCameraCenteredOnUser() {
        mainPresenter.onMapDisplayed(mapControllerMock);
        mainPresenter.onLocationReceived(userLocationMock);

        verify(mapControllerMock).centerCameraOnUser(anyDouble(), anyDouble(), anyBoolean());
    }

    @Test
    public void userLocationReceived_isUserInSalesOutletZoneChecked() {
        mainPresenter.onMapDisplayed(mapControllerMock);
        mainPresenter.onLocationReceived(userLocationMock);

        verify(salesOutletStoreMock).isUserInSalesOutletZone(userLocationMock);
    }

    @Test
    public void salesOutletsReceived_salesOutletsDisplayedOnMap() {
        final List<SalesOutlet> outlets = Arrays.asList(createDummySalesOutlet(), createDummySalesOutlet());

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                mapControllerMock.displaySalesOutlets(outlets);
                return null;
            }
        }).when(threadSchedulerMock).executeMainThread(any(Runnable.class));

        mainPresenter.onMapDisplayed(mapControllerMock);
        mainPresenter.onSalesOutletsReceived(outlets);

        verify(mapControllerMock).displaySalesOutlets(outlets);
    }

    @Test
    public void salesOutletsEnteredByUserReceived_enteredSalesOutletsDisplayedOnMap() {
        List<SalesOutlet> salesOutletsEntered = new ArrayList<>();

        mainPresenter.onMapDisplayed(mapControllerMock);
        mainPresenter.salesOutletsEnteredByUser(salesOutletsEntered);

        verify(mapControllerMock).displayEnteredSalesOutlets(salesOutletsEntered);
    }

    @Test
    public void mapCameraZoomChanged_mapZoomSavedInLocalStorage() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                userStoreMock.savePreferredMapZoom(PREFERRED_ZOOM);
                return null;
            }
        }).when(threadSchedulerMock).executeBackgroundThread(any(Runnable.class));

        mainPresenter.onMapZoomChanged(PREFERRED_ZOOM);

        verify(userStoreMock).savePreferredMapZoom(PREFERRED_ZOOM);
    }

    @Test
    public void menuButtonClicked_navigationMenuDisplayed() {
        mainPresenter.onMenuButtonClicked();
        verify(viewMock).displayNavigationMenu();
    }

    @Test
    public void navigateToTechnicalInformationButtonClicked_navigatedToTechnicalInformation() {
        mainPresenter.onNavigateToTechnicalInformationButtonClicked();
        verify(viewMock).navigateToTechnicalInformation();
    }

    @Test
    public void navigateToLastActionsButtonClicked_navigatedToLastActions() {
        mainPresenter.onNavigateToLastActionsButtonClicked();
        verify(viewMock).navigateToLastActions();
    }

    @Test
    public void backHardwareButtonPressed_mapObjectsRedrawn() {
        mainPresenter.onMapDisplayed(mapControllerMock);
        mainPresenter.onClickHardwareBack();

        verify(mapControllerMock).redrawMapObjects();
    }

    ////

    private SalesOutlet createDummySalesOutlet() {
        return new SalesOutlet(0, 0, "test", "test");
    }
}



















