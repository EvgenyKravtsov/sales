package kgk.mobile.presentation.model;


import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import kgk.mobile.domain.LocationService;
import kgk.mobile.domain.SettingsStorageService;
import kgk.mobile.domain.UserLocation;
import kgk.mobile.presentation.model.rx.UserStoreRx;

import static org.mockito.Mockito.verify;

public final class UserStoreRxTest {

    private LocationService locationServiceMock;
    private UserStoreRx userStoreRx;

    ////

    @Before
    public void setUp() {
        locationServiceMock = Mockito.mock(LocationService.class);
        userStoreRx = new UserStoreRx(locationServiceMock,
                Mockito.mock(SettingsStorageService.class));
    }

    ////

    @Test
    public void listenerAskedForUserLocationUpdateSubscription_locationUpdateStarted() {
        userStoreRx.subscribeForUserLocationUpdate(null);
        verify(locationServiceMock).startLocationUpdate();
    }

    @Test
    public void userLocationChanged_listenersNotified() {
        UserStore.LocationListener listenerMock1 = Mockito.mock(UserStore.LocationListener.class);
        UserStore.LocationListener listenerMock2 = Mockito.mock(UserStore.LocationListener.class);

        UserLocation receivedUserLocation = Mockito.mock(UserLocation.class);

        userStoreRx.subscribeForUserLocationUpdate(listenerMock1);
        userStoreRx.subscribeForUserLocationUpdate(listenerMock2);

        userStoreRx.onLocationChanged(receivedUserLocation);

        verify(listenerMock1).onLocationReceived(receivedUserLocation);
        verify(listenerMock2).onLocationReceived(receivedUserLocation);
    }
}
