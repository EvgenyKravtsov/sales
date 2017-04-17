package kgk.mobile.presentation.model.async;



import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.ArrayList;
import java.util.List;

import kgk.mobile.domain.SalesOutletAttendance;
import kgk.mobile.domain.service.DatabaseService;
import kgk.mobile.domain.service.KgkService;
import kgk.mobile.domain.service.SettingsStorageService;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public final class SalesOutletAttendanceStoreAsyncTest {

    @Mock
    private DatabaseService databaseServiceMock;
    @Mock
    private KgkService kgkService;
    @Mock
    private SettingsStorageService settingsStorageServiceMock;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private SalesOutletAttendanceStoreAsync salesOutletAttendanceStoreAsync;

    ////

    @Before
    public void setUp() {
        salesOutletAttendanceStoreAsync = new SalesOutletAttendanceStoreAsync(
                databaseServiceMock,
                kgkService,
                settingsStorageServiceMock);
    }

    ////

    @Test
    public void salesOutletAttendancePut_salesOutletAttendanceSavedInLocalStorage() {
        SalesOutletAttendance attendance = createDummySalesOutletAttendance();

        salesOutletAttendanceStoreAsync.put(attendance);

        verify(databaseServiceMock).insertSalesOutletAttendance(attendance);
    }

    @Test
    public void nonSynchronizedSalesOutletAttendancesReceivedFromLocalStorage_kgkServiceAvailable_nonSynchronizedSalesOutletAttendancesSentToRemoteStorage() {
        List<SalesOutletAttendance> attendances = new ArrayList<>();
        when(kgkService.isAvailable()).thenReturn(true);

        salesOutletAttendanceStoreAsync
                .onNonSynchronizedSalesOutletAttendancesReceivedFromLocalStorage(attendances);

        verify(kgkService).sendSalesOutletAttendances(attendances);

    }

    @Test
    public void nonSynchronizedSalesOutletAttendancesReceivedFromLocalStorage_kgkServiceNotAvailable_nonSynchronizedSalesOutletAttendancesNotSentToRemoteStorage() {
        List<SalesOutletAttendance> attendances = new ArrayList<>();
        when(kgkService.isAvailable()).thenReturn(false);

        salesOutletAttendanceStoreAsync
                .onNonSynchronizedSalesOutletAttendancesReceivedFromLocalStorage(attendances);

        verify(kgkService, never()).sendSalesOutletAttendances(attendances);
    }

    //// PRIVATE

    private SalesOutletAttendance createDummySalesOutletAttendance() {
        return new SalesOutletAttendance(0, 0, null, null, 0);
    }
}





















