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
import kgk.mobile.domain.service.DatabaseService;
import kgk.mobile.external.threading.ThreadScheduler;
import kgk.mobile.presentation.view.mainscreen.lastactions.LastActionsContract;
import kgk.mobile.presentation.view.mainscreen.lastactions.LastActionsPresenter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public final class LastActionsPresenterTest {

    @Mock
    private LastActionsContract.View viewMock;
    @Mock
    private ThreadScheduler threadSchedulerMock;
    @Mock
    private DatabaseService databaseServiceMock;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private LastActionsPresenter presenter;

    ////

    @Before
    public void setUp() {
        presenter = new LastActionsPresenter(databaseServiceMock, threadSchedulerMock);
        presenter.attachView(viewMock);
    }

    ////

    @Test
    public void viewCreated_salesOutletAttendancesQueriedFromLocalStorage() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                databaseServiceMock.requestSalesOutletAttendances();
                return null;
            }
        }).when(threadSchedulerMock).executeBackgroundThread(any(Runnable.class));

        presenter.onCreateView();

        verify(databaseServiceMock).requestSalesOutletAttendances();
    }

    @Test
    public void salesOutletAttendancesReceivedFromLocalStorage_salesOutletAttendancesDisplayed() {
        final List<SalesOutletAttendance> attendances = new ArrayList<>();
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                viewMock.displaySalesOutletAttendances(attendances);
                return null;
            }
        }).when(threadSchedulerMock).executeMainThread(any(Runnable.class));

        presenter.onSalesOutletAttendancesReceivedFromLocalStorage(attendances);

        verify(viewMock).displaySalesOutletAttendances(attendances);
    }
}
