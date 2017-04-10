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

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public final class SalesOutletAttendanceStoreAsyncTest {

    @Mock
    private DatabaseService databaseServiceMock;
    @Mock
    private KgkService kgkService;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private SalesOutletAttendanceStoreAsync salesOutletAttendanceStoreAsync;

    ////

    @Before
    public void setUp() {
        salesOutletAttendanceStoreAsync = new SalesOutletAttendanceStoreAsync(
                databaseServiceMock,
                kgkService);
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
        List<String> attendanceMessages = new ArrayList<>();
        when(kgkService.isAvailable()).thenReturn(true);

        salesOutletAttendanceStoreAsync
                .onNonSynchronizedSalesOutletAttendanceMessagesReceivedFromLocalStorage(attendanceMessages);

        verify(kgkService).sendSalesOutletAttendances(attendanceMessages);

    }

    @Test
    public void nonSynchronizedSalesOutletAttendancesReceivedFromLocalStorage_kgkServiceNotAvailable_nonSynchronizedSalesOutletAttendancesNotSentToRemoteStorage() {
        List<String> attendanceMessages = new ArrayList<>();
        when(kgkService.isAvailable()).thenReturn(false);

        salesOutletAttendanceStoreAsync
                .onNonSynchronizedSalesOutletAttendanceMessagesReceivedFromLocalStorage(attendanceMessages);

        verify(kgkService, never()).sendSalesOutletAttendances(attendanceMessages);
    }

    //// PRIVATE

    private SalesOutletAttendance createDummySalesOutletAttendance() {
        return new SalesOutletAttendance(0, 0, null, null, 0);
    }
}





















