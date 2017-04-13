package kgk.mobile.presentation.view.loginscreen.dialog;

import android.app.Activity;

import kgk.mobile.R;
import kgk.mobile.presentation.view.base.SimpleAlertDialog;


public final class UserNotFoundAlert extends SimpleAlertDialog {

    public UserNotFoundAlert(Activity activity) {
        super(activity, activity.getString(R.string.userNotFoundAlert_messageText));
    }
}
