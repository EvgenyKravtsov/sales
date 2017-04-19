package kgk.mobile.presentation.view.mainscreen.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.Window;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kgk.mobile.DependencyInjection;
import kgk.mobile.R;
import kgk.mobile.domain.service.SystemService;


public final class AuthorizationDeniedAlert extends Dialog {

    @BindView(R.id.simpleAlertDialog_messageTextView)
    TextView messageTextView;

    private final String message;

    ////

    public AuthorizationDeniedAlert(Activity activity) {
        super(activity);
        message = setupMessage(activity);
    }

    //// DIALOG

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_simple_alert);
        ButterKnife.bind(this);
        messageTextView.setAutoLinkMask(Linkify.WEB_URLS);
        messageTextView.setText(message);
    }

    //// CONTROL CALLBACKS

    @OnClick(R.id.simpleAlertDialog_confirmButton)
    void onClickConfirmButton() {
        dismiss();
    }

    //// PRIVATE

    private String setupMessage(Activity activity) {
        SystemService systemService = DependencyInjection.provideSystemService();
        String deviceId = systemService.getDeviceId();
        String message = activity.getString(R.string.authorizationDeniedAlert_messageText);
        return String.format(message, deviceId);
    }
}
