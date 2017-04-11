package kgk.mobile.presentation.view.mainscreen.menu;


import kgk.mobile.domain.UserLocation;
import kgk.mobile.domain.service.LocationService;
import kgk.mobile.external.util.DateFormatter;
import kgk.mobile.presentation.view.base.BasePresenterImpl;

public final class MenuPresenter extends BasePresenterImpl<MenuContract.View>
        implements MenuContract.Presenter,
        LocationService.Listener {

    private final LocationService locationService;

    ////

    public MenuPresenter(LocationService locationService) {
        this.locationService = locationService;
        this.locationService.addListener(this);
    }

    //// BASE PRESENTER

    @Override
    public void detachView() {
        super.detachView();
        this.locationService.removeListener(this);
    }

    //// MENU PRESENTER

    @Override
    public void onCreateView() {
        UserLocation lastKnownUserLocation = locationService.getLastKnownUserLocation();

        if (lastKnownUserLocation != null) displayValidLastKnownUserLocation(lastKnownUserLocation);
        else view.displayInvalidLastLocationDate();
    }

    //// LOCATION SERVICE LISTENER

    @Override
    public void onLocationChanged(UserLocation userLocation) {
        displayValidLastKnownUserLocation(userLocation);
    }

    //// PRIVATE

    private void displayValidLastKnownUserLocation(UserLocation userLocation) {
        long lastLocationDateUnixSeconds = userLocation.getLocationTime();
        String formattedDateString = new DateFormatter()
                .unixSecondsToFormattedString(lastLocationDateUnixSeconds, "yyyy-MM-dd HH:mm:ss");
        view.displayLastLocationDate(formattedDateString);
    }
}
