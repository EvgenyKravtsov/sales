package kgk.mobile.domain;


import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import kgk.mobile.domain.service.SettingsStorageService;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.when;

public final class SalesOutletTest {

    @Mock
    private UserLocation userLocationMock;
    @Mock
    private SettingsStorageService settingsStorageServiceMock;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private SalesOutlet salesOutlet;

    ////

    @Before
    public void setUp() {
        salesOutlet = new SalesOutlet(55.646138, 37.461989, "test", "test");
    }

    ////

    @Test
    public void isUserInZoneChecked_userIsNotInZone_falseReturned() {
        when(settingsStorageServiceMock.getSalesOutletEntranceRadius()).thenReturn(250);
        when(userLocationMock.distanceToInMeters(anyDouble(), anyDouble())).thenReturn(350);
        assertFalse(salesOutlet.isUserInZone(userLocationMock, settingsStorageServiceMock));
    }

    @Test
    public void isUserInZoneChecked_userIsInZone_trueReturned() {
        when(settingsStorageServiceMock.getSalesOutletEntranceRadius()).thenReturn(250);
        when(userLocationMock.distanceToInMeters(anyDouble(), anyDouble())).thenReturn(150);
        assertTrue(salesOutlet.isUserInZone(userLocationMock, settingsStorageServiceMock));
    }
}
