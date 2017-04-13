package kgk.mobile.presentation.view.mainscreen.dialog;


import android.app.Activity;

import kgk.mobile.R;
import kgk.mobile.presentation.view.base.SimpleAlertDialog;

public final class NoOperationSelectedAlert extends SimpleAlertDialog {

    public NoOperationSelectedAlert(Activity activity) {
        super(activity, activity.getString(R.string.noOperationSelectedAlert_messageText));
    }
}
