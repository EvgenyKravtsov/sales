package kgk.mobile.presentation.view.base;


import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kgk.mobile.R;

public abstract class SimpleAlertDialog extends Dialog {

    @BindView(R.id.simpleAlertDialog_messageTextView)
    TextView messageTextView;

    private final String message;

    ////

    public SimpleAlertDialog(Activity activity, String message) {
        super(activity);
        this.message = message;
    }

    //// DIALOG

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_simple_alert);
        ButterKnife.bind(this);
        messageTextView.setText(message);
    }

    //// CONTROL CALLBACKS

    @OnClick(R.id.simpleAlertDialog_confirmButton)
    void onClickConfirmButton() {
        dismiss();
    }
}
