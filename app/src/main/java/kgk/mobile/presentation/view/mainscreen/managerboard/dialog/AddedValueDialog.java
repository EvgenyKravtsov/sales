package kgk.mobile.presentation.view.mainscreen.managerboard.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kgk.mobile.R;

public final class AddedValueDialog extends Dialog {

    @BindView(R.id.addedValueDialog_valueEditText)
    EditText valueEditText;
    @BindView(R.id.addedValueDialog_confirmButton)
    Button confirmButton;
    @BindView(R.id.addedValueDialog_cancelButton)
    Button cancelButton;

    private final Activity activity;
    private final View.OnClickListener confirmButtonListener;
    private final View.OnClickListener cancelButtonListener;

    ////

    public AddedValueDialog(Activity activity,
                     View.OnClickListener confirmButtonListener,
                     View.OnClickListener cancelButtonListener) {
        super(activity);
        setCanceledOnTouchOutside(false);
        this.activity = activity;
        this.confirmButtonListener = confirmButtonListener;
        this.cancelButtonListener = cancelButtonListener;
    }

    //// DIALOG

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_added_value);
        ButterKnife.bind(this);
    }

    //// CONTROL CALLBACKS

    @OnClick(R.id.addedValueDialog_confirmButton)
    void onClickConfirmButton(View view) {
        if (valueEditText.getText().toString().isEmpty()) {
            displayNoValueEnteredAlert();
            return;
        }

        confirmButtonListener.onClick(valueEditText);
        dismiss();
    }

    @OnClick(R.id.addedValueDialog_cancelButton)
    void onClickCancelButton(View view) {
        cancelButtonListener.onClick(view);
        dismiss();
    }

    //// PRIVATE

    private void displayNoValueEnteredAlert() {
        NoValueEnteredAlert alert = new NoValueEnteredAlert(activity);
        alert.show();
    }
}
