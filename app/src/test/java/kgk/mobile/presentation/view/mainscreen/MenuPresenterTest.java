package kgk.mobile.presentation.view.mainscreen;


import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import kgk.mobile.domain.UserLocation;
import kgk.mobile.domain.service.LocationService;
import kgk.mobile.presentation.view.mainscreen.menu.MenuContract;
import kgk.mobile.presentation.view.mainscreen.menu.MenuPresenter;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public final class MenuPresenterTest {

    @Mock
    private MenuContract.View viewMock;
    @Mock
    private LocationService locationServiceMock;
    @Mock
    private UserLocation userLocationMock;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private MenuPresenter presenter;

    ////

    @Before
    public void setUp() {
        presenter = new MenuPresenter(locationServiceMock);
        presenter.attachView(viewMock);
    }

    ////

    @Test
    public void viewCreated_locationIsValid_lastLocationDateDisplayed() {
        when(locationServiceMock.getLastKnownUserLocation()).thenReturn(userLocationMock);
        presenter.onCreateView();
        verify(viewMock).displayLastLocationDate(anyString());
    }

    @Test
    public void viewCreated_locationIsInvalid_invalidLastLocationDateDisplayed() {
        when(locationServiceMock.getLastKnownUserLocation()).thenReturn(null);
        presenter.onCreateView();
        verify(viewMock).displayInvalidLastLocationDate();
    }

    @Test
    public void locationChanged_lastLocationDateDisplayed() {
        presenter.onLocationChanged(userLocationMock);
        verify(viewMock).displayLastLocationDate(anyString());
    }
}
