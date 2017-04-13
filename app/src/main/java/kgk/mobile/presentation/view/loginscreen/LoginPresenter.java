package kgk.mobile.presentation.view.loginscreen;


import kgk.mobile.domain.service.SettingsStorageService;
import kgk.mobile.domain.service.SystemService;
import kgk.mobile.external.threading.ThreadScheduler;
import kgk.mobile.presentation.model.UserStore;
import kgk.mobile.presentation.view.base.BasePresenterImpl;

public final class LoginPresenter extends BasePresenterImpl<LoginContract.View>
        implements LoginContract.Presenter, UserStore.UserLoginListener {

    private final UserStore userStore;
    private final ThreadScheduler threadScheduler;
    private final SettingsStorageService settingsStorageService;
    private final SystemService systemService;

    ////

    public LoginPresenter(UserStore userStore,
                          ThreadScheduler threadScheduler,
                          SettingsStorageService settingsStorageService,
                          SystemService systemService) {
        this.userStore = userStore;
        this.userStore.addUserLoginListener(this);
        this.threadScheduler = threadScheduler;
        this.settingsStorageService = settingsStorageService;
        this.systemService = systemService;
    }

    //// BASE PRESENTER

    @Override
    public void detachView() {
        super.detachView();
        this.userStore.removeUserLoginListener(this);
    }

    //// LOGIN PRESENTER

    @Override
    public void onClickLoginButton(final String login,
                                   final String password,
                                   boolean isUserRemembered) {

        if (!systemService.getInternetConnectionStatus()) {
            view.displayNoInternetConnectionAlert();
            return;
        }

        if ((login == null || login.equals("")) ||
                (password == null || password.equals(""))) {
            view.displayWrongCredentialsAlert();
            return;
        }

        view.displayLoadingAlert();

        if (isUserRemembered) {
            settingsStorageService.setUserRemembered(true);
            settingsStorageService.setLogin(login);
            settingsStorageService.setPassword(password);
        }
        else {
            settingsStorageService.setUserRemembered(false);
        }

        threadScheduler.executeBackgroundThread(new Runnable() {
            @Override
            public void run() {
                userStore.requestUserLogin(login, password);
            }
        });
    }

    @Override
    public void onCreateView() {
        view.displayAppVersion(systemService.getAppVersion());

        if (settingsStorageService.getUserRemembered())
            view.displayUserCredentials(settingsStorageService.getLogin(),
                                        settingsStorageService.getPassword());
    }

    //// USER LOGIN LISTENER

    @Override
    public void onLoginSuccess() {
        threadScheduler.executeMainThread(new Runnable() {
            @Override
            public void run() {
                view.hideLoadingAlert();
                view.navigateToMainScreen();
            }
        });
    }

    @Override
    public void onUserNotFound() {
        threadScheduler.executeMainThread(new Runnable() {
            @Override
            public void run() {
                view.hideLoadingAlert();
                view.displayUserNotFoundAlert();
            }
        });
    }

    @Override
    public void onDeviceNotAllowed() {
        threadScheduler.executeMainThread(new Runnable() {
            @Override
            public void run() {
                view.hideLoadingAlert();
                view.displayDeviceNotAllowedAlert();
            }
        });
    }

    @Override
    public void onLoginError() {
        threadScheduler.executeMainThread(new Runnable() {
            @Override
            public void run() {
                view.hideLoadingAlert();
                view.displayInternetErrorAlert();
            }
        });
    }
}
