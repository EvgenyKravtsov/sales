package kgk.mobile.presentation.view.loginscreen.dialog;

import android.app.Activity;

import kgk.mobile.R;
import kgk.mobile.presentation.view.base.SimpleAlertDialog;


public final class NoInternetConnectionAlert extends SimpleAlertDialog {

    public NoInternetConnectionAlert(Activity activity) {
        super(activity, activity.getString(R.string.noInternetConnectionAlert_messageText));
    }
}
