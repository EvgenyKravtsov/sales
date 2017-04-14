package kgk.mobile.presentation.view.loginscreen;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kgk.mobile.DependencyInjection;
import kgk.mobile.R;
import kgk.mobile.presentation.view.loginscreen.dialog.DeviceNotAllowedAlert;
import kgk.mobile.presentation.view.loginscreen.dialog.InternetErrorAlert;
import kgk.mobile.presentation.view.loginscreen.dialog.LoadingAlert;
import kgk.mobile.presentation.view.loginscreen.dialog.NoInternetConnectionAlert;
import kgk.mobile.presentation.view.loginscreen.dialog.PermissionsNeededAlert;
import kgk.mobile.presentation.view.loginscreen.dialog.UserNotFoundAlert;
import kgk.mobile.presentation.view.loginscreen.dialog.WrongCredentialsAlert;
import kgk.mobile.presentation.view.mainscreen.MainActivity;

public final class LoginActivity extends AppCompatActivity implements LoginContract.View {

    @BindView(R.id.loginActivity_loginEditText)
    EditText loginEditText;
    @BindView(R.id.loginActivity_passwordEditText)
    EditText passwordEditText;
    @BindView(R.id.loginActivity_rememberMeCheckBox)
    CheckBox rememberMeCheckBox;
    @BindView(R.id.loginActivity_appVersionTextView)
    TextView appVersionTextView;

    private static final int REQUEST_PERMISSIONS_ID = 10;
    private static final String[] PERMISSIONS = { Manifest.permission.READ_PHONE_STATE,
                                                  Manifest.permission.ACCESS_FINE_LOCATION };

    private LoginContract.Presenter presenter;
    private LoadingAlert loadingAlert;

    //// ACTIVITY

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        presenter = DependencyInjection.provideLoginPresenter();
        presenter.attachView(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.onCreateView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        View v = getCurrentFocus();
        boolean ret = super.dispatchTouchEvent(event);

        if (v instanceof EditText) {
            View w = getCurrentFocus();
            int scrCoordinates[] = new int[2];
            w.getLocationOnScreen(scrCoordinates);
            float x = event.getRawX() + w.getLeft() - scrCoordinates[0];
            float y = event.getRawY() + w.getTop() - scrCoordinates[1];

            if (event.getAction() == MotionEvent.ACTION_UP && (x < w.getLeft() || x >= w.getRight() || y < w.getTop() || y > w.getBottom()) ) {
                InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
            }
        }
        return ret;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        if (!hasPermissions()) presenter.onPermissionsDenied();
    }

    //// LOGIN VIEW

    @Override
    public void requestPermissions() {
        if (!hasPermissions()) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_PERMISSIONS_ID);
        }
    }

    @Override
    public void displayPermissionsNeededAlert() {
        new PermissionsNeededAlert(this, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        }).show();
    }

    @Override
    public void displayWrongCredentialsAlert() {
        new WrongCredentialsAlert(this).show();
    }

    @Override
    public void displayUserCredentials(String login, String password) {
        rememberMeCheckBox.setChecked(true);
        loginEditText.setText(login);
        passwordEditText.setText(password);
    }

    @Override
    public void displayAppVersion(String text) {
        appVersionTextView.setText(text);
    }

    @Override
    public void navigateToMainScreen() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void displayLoadingAlert() {
        loadingAlert = new LoadingAlert(this);
        loadingAlert.show();
    }

    @Override
    public void hideLoadingAlert() {
        if (loadingAlert != null) {
            loadingAlert.dismiss();
            loadingAlert = null;
        }
    }

    @Override
    public void displayUserNotFoundAlert() {
        new UserNotFoundAlert(this).show();
    }

    @Override
    public void displayDeviceNotAllowedAlert() {
        new DeviceNotAllowedAlert(this).show();
    }

    @Override
    public void displayInternetErrorAlert() {
        new InternetErrorAlert(this).show();
    }

    @Override
    public void displayNoInternetConnectionAlert() {
        new NoInternetConnectionAlert(this).show();
    }

    //// CONTROL CALLBACKS

    @OnClick(R.id.loginActivity_loginButton)
    public void onClickLoginButton() {
        presenter.onClickLoginButton(loginEditText.getText().toString(),
                                     passwordEditText.getText().toString(),
                                     rememberMeCheckBox.isChecked());
    }

    //// PRIVATE

    private boolean hasPermissions() {
        for (String permission : PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }

        return true;
    }
}
