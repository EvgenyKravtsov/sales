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
import kgk.mobile.domain.UserOperation;
import kgk.mobile.domain.SalesOutlet;
import kgk.mobile.presentation.model.SalesOutletAttendanceStore;
import kgk.mobile.presentation.model.SalesOutletStore;
import kgk.mobile.presentation.model.UserStore;
import kgk.mobile.presentation.view.mainscreen.managerboard.UserBoardContract;
import kgk.mobile.presentation.view.mainscreen.managerboard.UserBoardPresenter;
import kgk.mobile.presentation.view.map.MapController;
import kgk.mobile.threading.ThreadScheduler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

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
    private SalesOutletAttendanceStore salesOutletAttendanceStoreMock;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private UserBoardPresenter presenter;
    private SalesOutlet dummySalesOutlet;

    ////

    @Before
    public void setUp() {
        SalesOutletStore salesOutletStoreMock = mock(SalesOutletStore.class);
        presenter = new UserBoardPresenter(salesOutletStoreMock,
                userStoreMock, threadSchedulerMock, salesOutletAttendanceStoreMock);
        presenter.setMapController(mapControllerMock);
        presenter.attachView(viewMock);
        dummySalesOutlet = createDummySalesOutlet();
    }

    ////

    @Test
    public void salesOutletsEnteredByUserReceived_enteredSalesOutletsDisplayed() {
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

    @Test
    public void salesOutletAttended_salesOutletAttendanceStored() {
        List<UserOperation> selectedUserOperations = new ArrayList<>();

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                salesOutletAttendanceStoreMock.put(any(SalesOutletAttendance.class));
                return null;
            }
        }).when(threadSchedulerMock).executeBackgroundThread(any(Runnable.class));

        presenter.salesOutletAttended(selectedUserOperations, any(Integer.class));

        verify(salesOutletAttendanceStoreMock).put(any(SalesOutletAttendance.class));
    }

    ////

    private SalesOutlet createDummySalesOutlet() {
        return new SalesOutlet(0, 0, "test", "test");
    }
}
