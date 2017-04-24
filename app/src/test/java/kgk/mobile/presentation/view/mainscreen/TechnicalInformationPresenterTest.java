package kgk.mobile.presentation.view.mainscreen;


import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.stubbing.Answer;

import kgk.mobile.domain.UserLocation;
import kgk.mobile.domain.service.KgkService;
import kgk.mobile.domain.service.LocationService;
import kgk.mobile.domain.service.SettingsStorageService;
import kgk.mobile.domain.service.SystemService;
import kgk.mobile.external.threading.ThreadScheduler;
import kgk.mobile.presentation.model.MainStore;
import kgk.mobile.presentation.view.mainscreen.technicalinformation.TechnicalInformationContract;
import kgk.mobile.presentation.view.mainscreen.technicalinformation.TechnicalInformationPresenter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public final class TechnicalInformationPresenterTest {

    @Mock
    private TechnicalInformationContract.View viewMock;
    @Mock
    private LocationService locationServiceMock;
    @Mock
    private UserLocation userLocationMock;
    @Mock
    private KgkService kgkServiceMock;
    @Mock
    private ThreadScheduler threadSchedulerMock;
    @Mock
    private SettingsStorageService settingsStorageServiceMock;
    @Mock
    private SystemService systemServiceMock;
    @Mock
    private MainStore mainStoreMock;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private TechnicalInformationPresenter presenter;
    private long validLastSendingDateUnixSeconds = 99999;

    ////

    @Before
    public void setUp() {
        presenter = new TechnicalInformationPresenter(locationServiceMock,
                                                     kgkServiceMock,
                                                     threadSchedulerMock,
                                                     settingsStorageServiceMock,
                                                     systemServiceMock,
                                                     mainStoreMock);
        presenter.attachView(viewMock);
    }

    ////

    @Test
    public void viewCreated_locationIsValid_lastLocationDateDisplayed() {
        when(locationServiceMock.getLastKnownUserLocation()).thenReturn(userLocationMock);
        presenter.onViewReady();
        verify(viewMock).displayLastLocationDate(anyString());
    }

    @Test
    public void viewCreated_locationIsInvalid_invalidLastLocationDateDisplayed() {
        when(locationServiceMock.getLastKnownUserLocation()).thenReturn(null);
        presenter.onViewReady();
        verify(viewMock).displayInvalidLastLocationDate();
    }

    @Test
    public void locationChanged_lastLocationDateDisplayed() {
        presenter.onLocationChanged(userLocationMock);
        verify(viewMock).displayLastLocationDate(anyString());
    }

    @Test
    public void viewCreated_locationIsValid_lastCoordinatesDisplayed() {
        when(locationServiceMock.getLastKnownUserLocation()).thenReturn(userLocationMock);
        presenter.onViewReady();
        verify(viewMock).displayLastCoordinates(anyString());
    }

    @Test
    public void viewCreated_locationIsInvalid_invalidLastCoordinatesDisplayed() {
        when(locationServiceMock.getLastKnownUserLocation()).thenReturn(null);
        presenter.onViewReady();
        verify(viewMock).displayInvalidLastCoordinates();
    }

    @Test
    public void locationChanged_lastCoordinatesDisplayed() {
        presenter.onLocationChanged(userLocationMock);
        verify(viewMock).displayLastCoordinates(anyString());
    }

    @Test
    public void viewCreated_locationIsValid_speedDisplayed() {
        when(locationServiceMock.getLastKnownUserLocation()).thenReturn(userLocationMock);
        presenter.onViewReady();
        verify(viewMock).displaySpeed(anyString());
    }

    @Test
    public void viewCreated_locationIsInvalid_invalidSpeedDisplayed() {
        when(locationServiceMock.getLastKnownUserLocation()).thenReturn(null);
        presenter.onViewReady();
        verify(viewMock).displayInvalidSpeed();
    }

    @Test
    public void locationChanged_speedDisplayed() {
        presenter.onLocationChanged(userLocationMock);
        verify(viewMock).displaySpeed(anyString());
    }

    @Test
    public void viewCreated_lastSendingDateIsValid_lastSendingDateDisplayed() {
        when(kgkServiceMock.getLastSendingDate()).thenReturn(validLastSendingDateUnixSeconds);
        presenter.onViewReady();
        verify(viewMock).displayLastSendingDate(anyString());
    }

    @Test
    public void viewCreated_lastSendingDateIsInvalid_invalidLastSendingDateDisplayed() {
        long dateUnixSeconds = 0;
        when(kgkServiceMock.getLastSendingDate()).thenReturn(dateUnixSeconds);

        presenter.onViewReady();

        verify(viewMock).displayInvalidLastSendingDate();
    }

    @Test
    public void lastSendingDateChanged_lastSendingDateDisplayed() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                viewMock.displayLastSendingDate(anyString());
                return null;
            }
        }).when(threadSchedulerMock).executeMainThread(any(Runnable.class));

        presenter.onLastSendingDateChanged(validLastSendingDateUnixSeconds);

        verify(viewMock).displayLastSendingDate(anyString());
    }

    @Test
    public void viewCreated_salesOutletEntranceRadiusDisplayed() {
        int radius = 10;
        when(settingsStorageServiceMock.getSalesOutletEntranceRadius()).thenReturn(radius);

        presenter.onViewReady();

        verify(viewMock).displaySalesOutletEntranceRadius(String.valueOf(radius));
    }

    @Test
    public void salesOutletEntranceRadiusChanged_radiusSavedToLocalStorage() {
        int radius = 10;
        presenter.onSalesOutletEntranceRadiusChanged(radius);
        verify(settingsStorageServiceMock).setSalesOutletEntranceRadius(radius);
    }

    @Test
    public void salesOutletEntranceRadiusChanged_salesOutletEntranceRadiusDisplayed() {
        int radius = 10;
        presenter.onSalesOutletEntranceRadiusChanged(radius);
        verify(viewMock).displaySalesOutletEntranceRadius(String.valueOf(radius));
    }
}
























