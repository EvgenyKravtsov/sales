package kgk.mobile.presentation.view.mainscreen.dialog;

import android.app.Activity;

import kgk.mobile.R;
import kgk.mobile.presentation.view.base.SimpleAlertDialog;


public final class SalesOutletNotInRadiusAlert extends SimpleAlertDialog {

    public SalesOutletNotInRadiusAlert(Activity activity) {
        super(activity, activity.getString(R.string.salesOutletNotInRadiusAlert_messageText));
    }
}
