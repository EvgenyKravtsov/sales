package kgk.mobile.presentation.view.mainscreen.technicalinformation;


import android.util.Log;

import java.util.List;
import java.util.Locale;

import kgk.mobile.domain.SalesOutlet;
import kgk.mobile.domain.UserLocation;
import kgk.mobile.domain.UserOperation;
import kgk.mobile.domain.service.KgkService;
import kgk.mobile.domain.service.LocationService;
import kgk.mobile.domain.service.SettingsStorageService;
import kgk.mobile.external.threading.ThreadScheduler;
import kgk.mobile.external.util.DateFormatter;
import kgk.mobile.presentation.view.base.BasePresenterImpl;

public final class TechnicalInformationPresenter extends BasePresenterImpl<TechnicalInformationContract.View>
        implements TechnicalInformationContract.Presenter,
        LocationService.Listener,
        KgkService.Listener {

    // TODO Design Flaw - Presenter Communicates With Api Directly

    private static final String TAG = TechnicalInformationPresenter.class.getSimpleName();

    private final LocationService locationService;
    private final KgkService kgkService;
    private final ThreadScheduler threadScheduler;
    private final SettingsStorageService settingsStorageService;

    ////

    public TechnicalInformationPresenter(LocationService locationService,
                                         KgkService kgkService,
                                         ThreadScheduler threadScheduler,
                                         SettingsStorageService settingsStorageService) {
        this.locationService = locationService;
        this.locationService.addListener(this);
        this.kgkService = kgkService;
        this.kgkService.addListener(this);
        this.threadScheduler = threadScheduler;
        this.settingsStorageService = settingsStorageService;
    }

    //// BASE PRESENTER

    @Override
    public void detachView() {
        super.detachView();
        this.locationService.removeListener(this);
        this.kgkService.removeListener(this);
    }

    //// MENU PRESENTER

    @Override
    public void onCreateView() {
        UserLocation lastKnownUserLocation = locationService.getLastKnownUserLocation();

        if (lastKnownUserLocation != null) {
            view.displayLastLocationDate(formatValidLastKnownUserLocation(lastKnownUserLocation));
            view.displayLastCoordinates(formatValidLastCoordinates(lastKnownUserLocation));
            view.displaySpeed(formatSpeed(lastKnownUserLocation));
        }
        else {
            view.displayInvalidLastLocationDate();
            view.displayInvalidLastCoordinates();
            view.displayInvalidSpeed();
        }

        long lastSendingDateUnixSeconds = kgkService.getLastSendingDate();

        if (lastSendingDateUnixSeconds != 0) {
            view.displayLastSendingDate(formatLastSendingDate(kgkService.getLastSendingDate()));
        }
        else {
            view.displayInvalidLastSendingDate();
        }

        int salesOutletEntranceRadius = settingsStorageService.getSalesOutletEntranceRadius();
        view.displaySalesOutletEntranceRadius(String.valueOf(salesOutletEntranceRadius));
    }

    @Override
    public void onSalesOutletEntranceRadiusChanged(int radius) {
        settingsStorageService.setSalesOutletEntranceRadius(radius);
        view.displaySalesOutletEntranceRadius(String.valueOf(radius));
    }

    //// LOCATION SERVICE LISTENER

    @Override
    public void onLocationChanged(UserLocation userLocation) {
        view.displayLastLocationDate(formatValidLastKnownUserLocation(userLocation));
        view.displayLastCoordinates(formatValidLastCoordinates(userLocation));
        view.displaySpeed(formatSpeed(userLocation));
    }

    //// KGK SERVICE LISTENER

    @Override
    public void onSalesOutletsReceivedFromRemoteStorage(List<SalesOutlet> salesOutlets) {
        // Not Used
    }

    @Override
    public void onUserOperationsReceivedFromRemoteStorage(List<UserOperation> userOperations) {
        // Not Used
    }

    @Override
    public void onPointExitIdReceivedFromRemoteStorage(String eventId) {
        // Not Used
    }

    @Override
    public void onLastSendingDateChanged(final long lastSendingDateUnixSeconds) {
        threadScheduler.executeMainThread(new Runnable() {
            @Override
            public void run() {
                view.displayLastSendingDate(formatLastSendingDate(lastSendingDateUnixSeconds));
            }
        });
    }

    @Override
    public void onLoginAnswerReceived(KgkService.LoginAnswerType answerType) {
        // Not Used
    }

    //// PRIVATE

    private String formatValidLastKnownUserLocation(UserLocation userLocation) {
        long lastLocationDateUnixSeconds = userLocation.getLocationTime();
        return new DateFormatter()
                .unixSecondsToFormattedString(lastLocationDateUnixSeconds,
                                              "yyyy-MM-dd HH:mm:ss");
    }

    private String formatValidLastCoordinates(UserLocation userLocation) {
        return String.format(Locale.ROOT,
                             "LAT: %f, LNG: %f",
                             userLocation.getLatitude(),
                             userLocation.getLongitude());
    }

    private String formatSpeed(UserLocation userLocation) {
        return String.format(Locale.ROOT, "%.2f км/ч", userLocation.getSpeed());
    }

    private String formatLastSendingDate(long dateUnixSeconds) {
        return new DateFormatter().unixSecondsToFormattedString(dateUnixSeconds,
                                                                "yyyy-MM-dd HH:mm:ss");
    }
}
