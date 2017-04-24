package kgk.mobile.presentation.view.mainscreen.technicalinformation;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import butterknife.BindColor;
import butterknife.BindDrawable;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kgk.mobile.DependencyInjection;
import kgk.mobile.R;
import kgk.mobile.domain.service.SettingsStorageService;


public final class TechnicalInformationFragment extends Fragment
        implements TechnicalInformationContract.View {

    private static final String TAG = TechnicalInformationFragment.class.getSimpleName();

    @BindString(R.string.technicalInformationFragment_lastLocationDateText)
    String lastLocationDateText;
    @BindString(R.string.technicalInformationFragment_loadingDataText)
    String loadingDataText;
    @BindString(R.string.technicalInformationFragment_lastCoordinatesText)
    String lastCoordinatesText;
    @BindString(R.string.technicalInformationFragment_speedText)
    String speedText;
    @BindString(R.string.technicalInformationFragment_lastSendingDateText)
    String lastSendingDateText;
    @BindString(R.string.technicalInformationFragment_salesOutletEntranceRadiusText)
    String salesOutletEntranceRadiusText;
    @BindString(R.string.technicalInformationFragment_deviceIdText)
    String deviceIdText;
    @BindString(R.string.technicalInformationFragment_appVersionText)
    String appVersionText;

    @BindColor(android.R.color.white)
    int whiteColor;
    @BindColor(android.R.color.black)
    int blackColor;

    @BindDrawable(R.drawable.black_border_all_sides_transparent_background)
    Drawable blackBorderAllSidesTransparentBackgroundDrawable;
    @BindDrawable(R.drawable.black_background)
    Drawable blackBackgroundDrawable;

    @BindView(R.id.technicalInformationFragment_lastLocationDateTextView)
    TextView lastLocationTextView;
    @BindView(R.id.technicalInformationFragment_lastCoordinatesTextView)
    TextView lastCoordinatesTextView;
    @BindView(R.id.technicalInformationFragment_speedTextView)
    TextView speedTextView;
    @BindView(R.id.technicalInformationFragment_lastSendingDateTextView)
    TextView lastSendingDateTextView;
    @BindView(R.id.technicalInformationFragment_salesOutletEntranceTextView)
    TextView salesOutletEntranceTextView;
    @BindView(R.id.technicalInformationFragment_salesOutletEntranceRadiusSeekBar)
    SeekBar salesOutletEntranceRadiusSeekBar;
    @BindView(R.id.technicalInformationFragment_deviceIdTextView)
    TextView deviceIdTextView;
    @BindView(R.id.technicalInformationFragment_appVersionTextView)
    TextView appVersionTextView;
    @BindView(R.id.technicalInformationFragment_gpsModeButton)
    Button gpsModeToggleButton;
    @BindView(R.id.technicalInformationFragment_telephoneModeButton)
    Button telephoneModeToggleButton;

    private TechnicalInformationContract.Presenter presenter;

    //// FRAGMENT

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = DependencyInjection.provideTechnicalInformationPresenter();
        presenter.attachView(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_technical_information, container, false);
        ButterKnife.bind(this, view);
        setupSalesOutletEntranceRadiusSeekBar();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.onViewReady();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }

    //// TECHNICAL INFORMATION VIEW

    @Override
    public void displayLastLocationDate(String text) {
        lastLocationTextView.setText(String.format(lastLocationDateText, text));
    }

    @Override
    public void displayInvalidLastLocationDate() {
        lastLocationTextView.setText(String.format(lastLocationDateText, loadingDataText));
    }

    @Override
    public void displayLastCoordinates(String text) {
        lastCoordinatesTextView.setText(String.format(lastCoordinatesText, text));
    }

    @Override
    public void displayInvalidLastCoordinates() {
        lastCoordinatesTextView.setText(String.format(lastCoordinatesText, loadingDataText));
    }

    @Override
    public void displaySpeed(String text) {
        speedTextView.setText(String.format(speedText, text));
    }

    @Override
    public void displayInvalidSpeed() {
        speedTextView.setText(String.format(speedText, loadingDataText));
    }

    @Override
    public void displayLastSendingDate(String text) {
        lastSendingDateTextView.setText(String.format(lastSendingDateText, text));
    }

    @Override
    public void displayInvalidLastSendingDate() {
        lastSendingDateTextView.setText(String.format(lastSendingDateText, loadingDataText));
    }

    @Override
    public void displaySalesOutletEntranceRadius(String text) {
        salesOutletEntranceTextView.setText(String.format(salesOutletEntranceRadiusText, text));
        salesOutletEntranceRadiusSeekBar.setProgress(Integer.valueOf(text));
    }

    @Override
    public void displayDeviceId(String text) {
        deviceIdTextView.setText(String.format(deviceIdText, text));
    }

    @Override
    public void displayAppVersion(String text) {
        appVersionTextView.setText(String.format(appVersionText, text));
    }

    @Override
    public void displayGpsModeOn() {
        gpsModeToggleButton.setBackgroundDrawable(blackBackgroundDrawable);
        gpsModeToggleButton.setTextColor(whiteColor);
    }

    @Override
    public void displayGpsModeOff() {
        gpsModeToggleButton.setBackgroundDrawable(blackBorderAllSidesTransparentBackgroundDrawable);
        gpsModeToggleButton.setTextColor(blackColor);
    }

    @Override
    public void displayTelephoneModeOn() {
        telephoneModeToggleButton.setBackgroundDrawable(blackBackgroundDrawable);
        telephoneModeToggleButton.setTextColor(whiteColor);
    }

    @Override
    public void displayTelephoneModeOff() {
        telephoneModeToggleButton.setBackgroundDrawable(blackBorderAllSidesTransparentBackgroundDrawable);
        telephoneModeToggleButton.setTextColor(blackColor);
    }

    //// CONTROL CALLBACKS

    @OnClick(R.id.technicalInformationFragment_gpsModeButton)
    public void onClickGpsModeToggleButton() {
        presenter.onClickGpsModeToggleButton();
    }

    @OnClick(R.id.technicalInformationFragment_telephoneModeButton)
    public void onClickTelephoneModeToggleButton() {
        presenter.onClickTelephoneModeToggleButton();
    }

    //// PRIVATE

    private void setupSalesOutletEntranceRadiusSeekBar() {
        salesOutletEntranceRadiusSeekBar.setMax(
                SettingsStorageService.SALES_OUTLET_ENTRANCE_RADIUS_MAX_METERS -
                        SettingsStorageService.SALES_OUTLET_ENTRANCE_RADIUS_MIN_METERS);
        salesOutletEntranceRadiusSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (!b) return;
                presenter.onSalesOutletEntranceRadiusChanged(
                        i + SettingsStorageService.SALES_OUTLET_ENTRANCE_RADIUS_MIN_METERS);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}






















