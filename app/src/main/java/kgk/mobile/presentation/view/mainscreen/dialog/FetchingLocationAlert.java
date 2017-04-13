package kgk.mobile.presentation.view.mainscreen.dialog;

import android.app.Activity;

import kgk.mobile.R;
import kgk.mobile.presentation.view.base.ProgressDialog;


public final class FetchingLocationAlert extends ProgressDialog {

    public FetchingLocationAlert(Activity activity) {
        super(activity, activity.getString(R.string.fetchingLocationAlert_messageText));
    }
}
