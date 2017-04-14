package kgk.mobile.presentation.view.loginscreen;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.stubbing.Answer;

import kgk.mobile.domain.service.SettingsStorageService;
import kgk.mobile.domain.service.SystemService;
import kgk.mobile.external.threading.ThreadScheduler;
import kgk.mobile.presentation.model.UserStore;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LoginPresenterTest {

    @Mock
    private ThreadScheduler threadSchedulerMock;
    @Mock
    private UserStore userStoreMock;
    @Mock
    private LoginContract.View viewMock;
    @Mock
    private SettingsStorageService settingsStorageServiceMock;
    @Mock
    private SystemService systemServiceMock;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private LoginPresenter presenter;

    private final String login = "login";
    private final String password = "password";

    ////

    @Before
    public void setUp() {
        presenter = new LoginPresenter(userStoreMock,
                                       threadSchedulerMock,
                                       settingsStorageServiceMock,
                                       systemServiceMock);
        presenter.attachView(viewMock);
    }

    ////

    @Test
    public void viewCreated_permissionsRequested() {
        presenter.onCreateView();
        verify(viewMock).requestPermissions();
    }

    @Test
    public void permissionsDenied_permissionsNeededAlertDisplayed() {
        presenter.onPermissionsDenied();
        verify(viewMock).displayPermissionsNeededAlert();
    }

    @Test
    public void viewCreated_userRemembered_userCredentialsDisplayed() {
        when(settingsStorageServiceMock.getUserRemembered()).thenReturn(true);
        when(settingsStorageServiceMock.getLogin()).thenReturn(login);
        when(settingsStorageServiceMock.getPassword()).thenReturn(password);

        presenter.onCreateView();

        verify(viewMock).displayUserCredentials(login, password);
    }

    @Test
    public void viewCreated_userNotRemembered_userCredentialsNotDisplayed() {
        when(settingsStorageServiceMock.getUserRemembered()).thenReturn(false);
        presenter.onCreateView();
        verify(viewMock, never()).displayUserCredentials(login, password);
    }

    @Test
    public void viewCreated_appVersionDisplayed() {
        presenter.onCreateView();
        verify(viewMock).displayAppVersion(systemServiceMock.getAppVersion());
    }

    @Test
    public void loginButtonClicked_noInternetConnection_noInternetConnectionAlertDisplayed() {
        when(systemServiceMock.getInternetConnectionStatus()).thenReturn(false);
        presenter.onClickLoginButton(login, password, false);
        verify(viewMock).displayNoInternetConnectionAlert();
    }

    @Test
    public void loginButtonClicked_validCredentials_loadingAlertDisplayed() {
        when(systemServiceMock.getInternetConnectionStatus()).thenReturn(true);
        presenter.onClickLoginButton(login, password, false);
        verify(viewMock).displayLoadingAlert();
    }

    @Test
    public void loginButtonClicked_validCredentials_userLoginRequested() {
        when(systemServiceMock.getInternetConnectionStatus()).thenReturn(true);

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                userStoreMock.requestUserLogin(login, password);
                return null;
            }
        }).when(threadSchedulerMock).executeBackgroundThread(any(Runnable.class));

        presenter.onClickLoginButton(login, password, false);

        verify(userStoreMock).requestUserLogin(login, password);
    }

    @Test
    public void loginButtonClicked_validCredentials_userRemembered_credentialsSavedToLocalStorage() {
        when(systemServiceMock.getInternetConnectionStatus()).thenReturn(true);
        presenter.onClickLoginButton(login, password, true);
        verify(settingsStorageServiceMock).setUserRemembered(true);
        verify(settingsStorageServiceMock).setLogin(login);
        verify(settingsStorageServiceMock).setPassword(password);
    }

    @Test
    public void loginButtonClicked_validCredentials_userNotRemembered_credentialsNotSavedToLocalStorage() {
        when(systemServiceMock.getInternetConnectionStatus()).thenReturn(true);
        presenter.onClickLoginButton(login, password, false);
        verify(settingsStorageServiceMock).setUserRemembered(false);
        verify(settingsStorageServiceMock, never()).setLogin(login);
        verify(settingsStorageServiceMock, never()).setPassword(password);
    }

    @Test
    public void loginButtonClicked_invalidCredentials_alertDisplayed() {
        when(systemServiceMock.getInternetConnectionStatus()).thenReturn(true);
        presenter.onClickLoginButton("", null, false);
        verify(viewMock).displayWrongCredentialsAlert();
    }

    @Test
    public void loginSuccess_loadingAlertHidden() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                viewMock.hideLoadingAlert();
                return null;
            }
        }).when(threadSchedulerMock).executeMainThread(any(Runnable.class));

        presenter.onLoginSuccess();

        verify(viewMock).hideLoadingAlert();
    }

    @Test
    public void loginSuccess_navigatedToMainScreen() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                viewMock.navigateToMainScreen();
                return null;
            }
        }).when(threadSchedulerMock).executeMainThread(any(Runnable.class));

        presenter.onLoginSuccess();

        verify(viewMock).navigateToMainScreen();
    }

    @Test
    public void userNotFound_loadingAlertHidden() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                viewMock.hideLoadingAlert();
                return null;
            }
        }).when(threadSchedulerMock).executeMainThread(any(Runnable.class));

        presenter.onUserNotFound();

        verify(viewMock).hideLoadingAlert();
    }

    @Test
    public void userNotFound_userNotFoundAlertDisplayed() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                viewMock.displayUserNotFoundAlert();
                return null;
            }
        }).when(threadSchedulerMock).executeMainThread(any(Runnable.class));

        presenter.onUserNotFound();

        verify(viewMock).displayUserNotFoundAlert();
    }

    @Test
    public void deviceNotAllowed_loadingAlertHidden() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                viewMock.hideLoadingAlert();
                return null;
            }
        }).when(threadSchedulerMock).executeMainThread(any(Runnable.class));

        presenter.onDeviceNotAllowed();

        verify(viewMock).hideLoadingAlert();
    }

    @Test
    public void deviceNotAllowed_deviceNotAllowedAlertDisplayed() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                viewMock.displayDeviceNotAllowedAlert();
                return null;
            }
        }).when(threadSchedulerMock).executeMainThread(any(Runnable.class));

        presenter.onDeviceNotAllowed();

        verify(viewMock).displayDeviceNotAllowedAlert();
    }

    @Test
    public void loginError_loadingAlertHidden() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                viewMock.hideLoadingAlert();
                return null;
            }
        }).when(threadSchedulerMock).executeMainThread(any(Runnable.class));

        presenter.onLoginError();

        verify(viewMock).hideLoadingAlert();
    }

    @Test
    public void loginError_deviceNotAllowedAlertDisplayed() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                viewMock.displayInternetErrorAlert();
                return null;
            }
        }).when(threadSchedulerMock).executeMainThread(any(Runnable.class));

        presenter.onLoginError();

        verify(viewMock).displayInternetErrorAlert();
    }
}










































