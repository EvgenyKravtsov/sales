package kgk.mobile.presentation.view.loginscreen.dialog;

import android.app.Activity;

import kgk.mobile.R;
import kgk.mobile.presentation.view.base.SimpleAlertDialog;


public final class InternetErrorAlert extends SimpleAlertDialog {

    public InternetErrorAlert(Activity activity) {
        super(activity, activity.getString(R.string.internetErrorAlert_messageText));
    }
}
