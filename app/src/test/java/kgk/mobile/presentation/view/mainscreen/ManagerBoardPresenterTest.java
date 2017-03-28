package kgk.mobile.presentation.view.mainscreen;


import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import kgk.mobile.domain.SalesOutlet;
import kgk.mobile.presentation.view.mainscreen.managerboard.ManagerBoardContract;
import kgk.mobile.presentation.view.mainscreen.managerboard.ManagerBoardPresenter;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public final class ManagerBoardPresenterTest {

    @Test
    public void salesOutletsEnteredByUserReceived_enteredSalesOutletsDisplayed() {
        ManagerBoardPresenter presenter = new ManagerBoardPresenter();
        ManagerBoardContract.View viewMock = mock(ManagerBoardContract.View.class);
        presenter.attachView(viewMock);
        List<SalesOutlet> salesOutletsEntered = new ArrayList<>();

        presenter.salesOutletsEnteredByUser(salesOutletsEntered);

        verify(viewMock).displayEnteredSalesOutlets(salesOutletsEntered);
    }
}
