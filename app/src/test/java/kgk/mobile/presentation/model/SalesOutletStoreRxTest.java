package kgk.mobile.presentation.model;


import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import kgk.mobile.domain.DatabaseService;
import kgk.mobile.domain.KgkService;
import kgk.mobile.presentation.model.rx.SalesOutletStoreRx;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SalesOutletStoreRxTest {

    private KgkService kgkServiceMock;
    private DatabaseService databaseServiceMock;
    private SalesOutletStoreRx salesOutletStoreRx;

    @Before
    public void setUp() {
        kgkServiceMock = Mockito.mock(KgkService.class);
        databaseServiceMock = Mockito.mock(DatabaseService.class);
        salesOutletStoreRx = new SalesOutletStoreRx(kgkServiceMock, databaseServiceMock);
    }

    ////


}
