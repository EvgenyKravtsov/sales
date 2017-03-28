package kgk.mobile.domain;


import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.when;

public final class SalesOutletTest {

    @Mock
    private UserLocation userLocationMock;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private SalesOutlet salesOutlet;

    ////

    @Before
    public void setUp() {
        salesOutlet = new SalesOutlet(55.646138, 37.461989, "test");
    }

    ////

    @Test
    public void isUserInZoneChecked_userIsNotInZone_falseReturned() {
        when(userLocationMock.distanceToInMeters(anyDouble(), anyDouble())).thenReturn(350);
        assertFalse(salesOutlet.isUserInZone(userLocationMock));
    }

    @Test
    public void isUserInZoneChecked_userIsInZone_trueReturned() {
        when(userLocationMock.distanceToInMeters(anyDouble(), anyDouble())).thenReturn(150);
        assertTrue(salesOutlet.isUserInZone(userLocationMock));
    }
}
