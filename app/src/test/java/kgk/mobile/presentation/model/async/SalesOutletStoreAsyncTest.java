package kgk.mobile.presentation.model.async;


import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.ArrayList;
import java.util.List;

import kgk.mobile.domain.SalesOutlet;
import kgk.mobile.domain.UserLocation;
import kgk.mobile.domain.service.DatabaseService;
import kgk.mobile.domain.service.KgkService;
import kgk.mobile.presentation.model.SalesOutletStore;
import kgk.mobile.presentation.model.async.SalesOutletStoreAsync;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public final class SalesOutletStoreAsyncTest {

    @Mock
    private KgkService kgkServiceMock;
    @Mock
    private DatabaseService databaseServiceMock;
    @Mock
    private SalesOutletStore.Listener salesOutletStoreListenerMock;
    @Mock
    private UserLocation userLocationMock;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private SalesOutletStoreAsync salesOutletStoreAsync;

    ////

    @Before
    public void setUp() {
        salesOutletStoreAsync = new SalesOutletStoreAsync(kgkServiceMock, databaseServiceMock);
    }

    ////

    @Test
    public void salesOutletsRequested_kgkServiceAvailable_remoteStorageQueried() {
        when(kgkServiceMock.isAvailable()).thenReturn(true);
        salesOutletStoreAsync.requestSalesOutlets();
        verify(kgkServiceMock).requestSalesOutlets();
    }

    @Test
    public void salesOutletsRequested_kgkServiceNotAvailable_localStorageQueried() {
        when(kgkServiceMock.isAvailable()).thenReturn(false);
        salesOutletStoreAsync.requestSalesOutlets();
        verify(databaseServiceMock).requestSalesOutlets();
    }

    @Test
    public void salesOutletsReceivedFromRemoteStorage_listenersNotified() {
        List<SalesOutlet> salesOutlets = new ArrayList<>();
        salesOutletStoreAsync.addListener(salesOutletStoreListenerMock);

        salesOutletStoreAsync.onSalesOutletsReceivedFromRemoteStorage(salesOutlets);

        verify(salesOutletStoreListenerMock).onSalesOutletsReceived(salesOutlets);
    }

    @Test
    public void salesOutletsReceivedFromRemoteStorage_salesOutletLocalStorageUpdated() {
        List<SalesOutlet> salesOutlets = new ArrayList<>();
        salesOutletStoreAsync.onSalesOutletsReceivedFromRemoteStorage(salesOutlets);
        verify(databaseServiceMock).updateSalesOutlets(salesOutlets);
    }

    @Test
    public void ifUserInSalesOutletZoneChecked_listenersNotified() {
        List<SalesOutlet> salesOutletsAffected = new ArrayList<>();
        salesOutletStoreAsync.addListener(salesOutletStoreListenerMock);

        salesOutletStoreAsync.isUserInSalesOutletZone(userLocationMock);

        verify(salesOutletStoreListenerMock).salesOutletsEnteredByUser(salesOutletsAffected);
    }

    @Test
    public void salesOutletsReceivedFromLocalStorage_listenersNotified() {
        List<SalesOutlet> salesOutlets = new ArrayList<>();
        salesOutletStoreAsync.addListener(salesOutletStoreListenerMock);

        salesOutletStoreAsync.onSalesOutletsReceivedFromLocalStorage(salesOutlets);

        verify(salesOutletStoreListenerMock).onSalesOutletsReceived(salesOutlets);
    }
}






































