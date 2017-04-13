package kgk.mobile.presentation.view.mainscreen.dialog;


import android.app.Activity;
import kgk.mobile.R;
import kgk.mobile.presentation.view.base.SimpleAlertDialog;

final class NoValueEnteredAlert extends SimpleAlertDialog {

    NoValueEnteredAlert(Activity activity) {
        super(activity, activity.getString(R.string.noValueEnteredAlert_messageText));
    }
}
