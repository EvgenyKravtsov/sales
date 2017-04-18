package kgk.mobile.presentation.view.mainactivitynew;

import android.app.Activity;

import kgk.mobile.R;
import kgk.mobile.presentation.view.base.ProgressDialog;
import kgk.mobile.presentation.view.base.SimpleAlertDialog;


final class LoadingSalesOutletsAlert extends ProgressDialog {

    public LoadingSalesOutletsAlert(Activity activity) {
        super(activity, activity.getString(R.string.loadingSalesOutletsAlert_messageText));
    }
}
