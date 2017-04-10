package kgk.mobile.presentation.view.mainscreen.userboard.dialog;


import android.app.Activity;

import kgk.mobile.R;
import kgk.mobile.presentation.view.base.SimpleAlertDialog;

public final class AttendanceSuccessfulAlert extends SimpleAlertDialog {

    public AttendanceSuccessfulAlert(Activity activity) {
        super(activity, activity.getString(R.string.attendanceSuccessfulAlert_messageText));
    }
}
