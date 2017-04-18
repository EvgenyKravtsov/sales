package kgk.mobile.presentation.view.common.dialog;

import android.app.Activity;

import kgk.mobile.R;
import kgk.mobile.presentation.view.base.ProgressDialog;


public final class LoadingAlert extends ProgressDialog {

    public LoadingAlert(Activity activity) {
        super(activity, activity.getString(R.string.technicalInformationFragment_loadingDataText));
    }
}
