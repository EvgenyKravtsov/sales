package kgk.mobile.presentation.model.async;


import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.ArrayList;
import java.util.List;

import kgk.mobile.domain.UserOperation;
import kgk.mobile.domain.service.DatabaseService;
import kgk.mobile.domain.service.KgkService;
import kgk.mobile.domain.service.LocationService;
import kgk.mobile.domain.service.SettingsStorageService;
import kgk.mobile.domain.UserLocation;
import kgk.mobile.domain.service.SystemService;
import kgk.mobile.presentation.model.UserStore;
import kgk.mobile.presentation.model.async.UserStoreAsync;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public final class UserStoreAsyncTest {

    @Mock
    private LocationService locationServiceMock;
    @Mock
    private SettingsStorageService settingsStorageServiceMock;
    @Mock
    private KgkService kgkServiceMock;
    @Mock
    SystemService systemServiceMock;
    @Mock
    private DatabaseService databaseServiceMock;
    @Mock
    private UserStore.UserOperationsListener userOperationsListenerMock;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private UserStoreAsync userStoreAsync;

    ////

    @Before
    public void setUp() {
        userStoreAsync = new UserStoreAsync(
                locationServiceMock,
                settingsStorageServiceMock,
                kgkServiceMock,
                databaseServiceMock,
                systemServiceMock);
    }

    ////

    @Test
    public void listenerAskedForUserLocationUpdateSubscription_locationUpdateStarted() {
        userStoreAsync.subscribeForUserLocationUpdate(null);
        verify(locationServiceMock).startLocationUpdate();
    }

    @Test
    public void userLocationChanged_listenersNotified() {
        UserStore.LocationListener listenerMock1 = Mockito.mock(UserStore.LocationListener.class);
        UserStore.LocationListener listenerMock2 = Mockito.mock(UserStore.LocationListener.class);

        UserLocation receivedUserLocation = Mockito.mock(UserLocation.class);

        userStoreAsync.subscribeForUserLocationUpdate(listenerMock1);
        userStoreAsync.subscribeForUserLocationUpdate(listenerMock2);

        userStoreAsync.onLocationChanged(receivedUserLocation);

        verify(listenerMock1).onLocationReceived(receivedUserLocation);
        verify(listenerMock2).onLocationReceived(receivedUserLocation);
    }

    @Test
    public void preferredMapZoomSavedToSettingsStorage() {
        final float zoom = 10.0f;
        userStoreAsync.savePreferredMapZoom(zoom);
        verify(settingsStorageServiceMock).setPreferredMapZoom(zoom);
    }

    @Test
    public void userOperationsRequested_localStorageQueried() {
        userStoreAsync.requestUserOperations();
        verify(databaseServiceMock).requestUserOperations();
    }

    @Test
    public void userOperationsRequested_remoteStorageAvailable_remoteStorageQueried() {
        when(kgkServiceMock.isAvailable()).thenReturn(true);
        userStoreAsync.requestUserOperations();
        verify(kgkServiceMock).requestUserOperations();
    }

    @Test
    public void userOperationsRequested_remoteStorageNotAvailable_remoteStorageNotQueried() {
        when(kgkServiceMock.isAvailable()).thenReturn(false);
        userStoreAsync.requestUserOperations();
        verify(kgkServiceMock, never()).requestUserOperations();
    }

    @Test
    public void userOperationsReceivedFromLocalStorage_listNotEmpty_listenersNotified() {
        userStoreAsync.addUserOperationsListener(userOperationsListenerMock);
        List<UserOperation> userOperations = new ArrayList<>();
        userOperations.add(new UserOperation(0, "test"));

        userStoreAsync.onUserOperationsReceivedFromLocalStorage(userOperations);

        verify(userOperationsListenerMock).onUserOperationsReceived(userOperations);
    }

    @Test
    public void userOperationsReceivedFromLocalStorage_listEmpty_listenerDidNotNotified() {
        userStoreAsync.addUserOperationsListener(userOperationsListenerMock);
        List<UserOperation> userOperations = new ArrayList<>();

        userStoreAsync.onUserOperationsReceivedFromLocalStorage(userOperations);

        verify(userOperationsListenerMock, never()).onUserOperationsReceived(userOperations);
    }

    @Test
    public void userOperationsReceivedFromRemoteStorage_localStorageUpdated() {
        List<UserOperation> userOperations = new ArrayList<>();

        userStoreAsync.onUserOperationsReceivedFromRemoteStorage(userOperations);

        verify(databaseServiceMock).updateUserOperations(userOperations);
    }
}



















