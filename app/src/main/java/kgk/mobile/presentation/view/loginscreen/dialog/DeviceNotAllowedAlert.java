package kgk.mobile.presentation.view.loginscreen.dialog;

import android.app.Activity;

import kgk.mobile.R;
import kgk.mobile.presentation.view.base.SimpleAlertDialog;


public final class DeviceNotAllowedAlert extends SimpleAlertDialog {

    public DeviceNotAllowedAlert(Activity activity) {
        super(activity, activity.getString(R.string.deviceNotAllowedAlert_messageText));
    }
}
