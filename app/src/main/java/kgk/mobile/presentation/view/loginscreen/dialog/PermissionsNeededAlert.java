package kgk.mobile.presentation.view.loginscreen.dialog;

import android.app.Activity;
import android.view.View;

import kgk.mobile.R;
import kgk.mobile.presentation.view.base.SimpleAlertDialog;
import kgk.mobile.presentation.view.base.SimpleAlertDialogWithAction;

public final class PermissionsNeededAlert extends SimpleAlertDialogWithAction {

    public PermissionsNeededAlert(Activity activity, View.OnClickListener listener) {
        super(activity, activity.getString(R.string.permissionsNeededAlert_messageText), listener);
    }
}
