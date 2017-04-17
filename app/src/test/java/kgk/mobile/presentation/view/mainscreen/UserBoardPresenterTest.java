package kgk.mobile.presentation.view.mainscreen;


import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;

import kgk.mobile.domain.SalesOutletAttendance;
import kgk.mobile.domain.UserLocation;
import kgk.mobile.domain.UserOperation;
import kgk.mobile.domain.SalesOutlet;
import kgk.mobile.domain.service.LocationService;
import kgk.mobile.domain.service.SettingsStorageService;
import kgk.mobile.presentation.model.SalesOutletAttendanceBegin;
import kgk.mobile.presentation.model.SalesOutletAttendanceStore;
import kgk.mobile.presentation.model.SalesOutletStore;
import kgk.mobile.presentation.model.UserStore;
import kgk.mobile.presentation.view.mainscreen.userboard.UserBoardContract;
import kgk.mobile.presentation.view.mainscreen.userboard.UserBoardPresenter;
import kgk.mobile.presentation.view.map.MapController;
import kgk.mobile.external.threading.ThreadScheduler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public final class UserBoardPresenterTest {

    @Mock
    private MapController mapControllerMock;
    @Mock
    private UserBoardContract.View viewMock;
    @Mock
    private UserStore userStoreMock;
    @Mock
    private ThreadScheduler threadSchedulerMock;
    @Mock
    private SalesOutletStore salesOutletStoreMock;
    @Mock
    LocationService locationServiceMock;
    @Mock
    SettingsStorageService settingsStorageServiceMock;
    @Mock
    private SalesOutletAttendanceStore salesOutletAttendanceStoreMock;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private UserBoardPresenter presenter;
    private SalesOutlet dummySalesOutlet;

    ////

    @Before
    public void setUp() {
        presenter = new UserBoardPresenter(salesOutletStoreMock,
                                           userStoreMock,
                                           threadSchedulerMock,
                                           salesOutletAttendanceStoreMock,
                                           locationServiceMock,
                                           settingsStorageServiceMock);
        presenter.setMapController(mapControllerMock);
        presenter.attachView(viewMock);
        dummySalesOutlet = createDummySalesOutlet();
    }

    ////

    @Test
    public void salesOutletsEnteredByUserReceived_enteredSalesOutletsDisplayed() {
        when(salesOutletAttendanceStoreMock.getSalesOutletAttendanceBegin())
                .thenReturn(new SalesOutletAttendanceBegin(null, 0));
        List<SalesOutlet> salesOutletsEntered = new ArrayList<>();

        presenter.salesOutletsEnteredByUser(salesOutletsEntered);

        verify(viewMock).displayEnteredSalesOutlets(salesOutletsEntered);
    }

    @Test
    public void salesOutletSelected_selectedSalesOutletDisplayedOnMap() {
        presenter.salesOutletSelectedByUser(dummySalesOutlet);
        verify(mapControllerMock).displaySelectedSalesOutlet(dummySalesOutlet);
    }

    @Test
    public void salesOutletSelected_selectedSalesOutletDisplayedOnBoard() {
        presenter.salesOutletSelectedByUser(dummySalesOutlet);
        verify(viewMock).displaySelectedSalesOutlet(dummySalesOutlet);
    }

    @Test
    public void salesOutletSelected_userOperationsRequested() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                userStoreMock.requestUserOperations();
                return null;
            }
        }).when(threadSchedulerMock).executeBackgroundThread(any(Runnable.class));

        presenter.salesOutletSelectedByUser(dummySalesOutlet);

        verify(userStoreMock).requestUserOperations();
    }

    @Test
    public void userOperationsReceived_userOperationsDisplayed() {
        final List<UserOperation> userOperations = new ArrayList<>();

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                viewMock.displayUserOperations(userOperations);
                return null;
            }
        }).when(threadSchedulerMock).executeMainThread(any(Runnable.class));

        presenter.onUserOperationsReceived(userOperations);

        verify(viewMock).displayUserOperations(userOperations);
    }

    //// PRIVATE

    private SalesOutlet createDummySalesOutlet() {
        return new SalesOutlet(0, 0, 0, "test", "test");
    }

    private SalesOutletAttendance createDummySalesOutletAttendance() {
        return new SalesOutletAttendance(0, 0, null, null, 0);
    }
}
