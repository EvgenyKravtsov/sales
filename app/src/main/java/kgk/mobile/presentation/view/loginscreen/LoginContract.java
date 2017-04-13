package kgk.mobile.presentation.view.loginscreen;


import kgk.mobile.presentation.view.base.BasePresenter;
import kgk.mobile.presentation.view.base.BaseView;

public interface LoginContract {

    interface Presenter extends BasePresenter<View> {

        void onClickLoginButton(String login, String password, boolean isUserRemembered);

        void onCreateView();
    }

    interface View extends BaseView {

        void displayWrongCredentialsAlert();

        void displayUserCredentials(String login, String password);

        void displayAppVersion(String text);

        void navigateToMainScreen();

        void displayLoadingAlert();

        void hideLoadingAlert();

        void displayUserNotFoundAlert();

        void displayDeviceNotAllowedAlert();

        void displayInternetErrorAlert();

        void displayNoInternetConnectionAlert();
    }
}
