package kgk.mobile.presentation.view.base;


import android.app.Activity;
import android.app.Dialog;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import kgk.mobile.R;

public abstract class ProgressDialog extends Dialog {

    @BindView(R.id.progressDialog_messageTextView)
    TextView messageTextView;
    @BindView(R.id.progressDialog_progressBar)
    ProgressBar progressBar;

    private final String message;

    ////

    public ProgressDialog(Activity activity, String message) {
        super(activity);
        this.message = message;
    }

    //// DIALOG

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_progress);
        ButterKnife.bind(this);

        messageTextView.setText(message);
        progressBar.getIndeterminateDrawable().setColorFilter(0xFF000000, PorterDuff.Mode.MULTIPLY);
    }
}
