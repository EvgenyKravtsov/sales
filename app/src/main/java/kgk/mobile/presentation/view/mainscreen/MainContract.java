package kgk.mobile.presentation.view.mainscreen;

import kgk.mobile.presentation.view.base.BasePresenter;
import kgk.mobile.presentation.view.base.BaseView;
import kgk.mobile.presentation.view.map.MapController;

public interface MainContract {

    interface Presenter extends BasePresenter<View> {

        void onMapDisplayed(MapController mapController);

        void onLocationPermissionGranted();

        void onMapZoomChanged(float zoom);

        void onPhoneStatePermissionNotGranted();

        void onMenuButtonClicked();
    }

    interface View extends BaseView {

        void requestLocationPermission();

        void requestPhoneStatePermission();

        void exit();

        void navigateToMenu();
    }
}
