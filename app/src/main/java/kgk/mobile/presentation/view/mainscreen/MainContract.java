package kgk.mobile.presentation.view.mainscreen;


import kgk.mobile.domain.UserLocation;
import kgk.mobile.presentation.view.base.BasePresenter;
import kgk.mobile.presentation.view.base.BaseView;
import kgk.mobile.presentation.view.map.MapController;

interface MainContract {

    interface Presenter extends BasePresenter<View> {

        void onMapDisplayed(MapController mapController);

        void onLocationPermissionGranted();
    }

    interface View extends BaseView {

        void requestLocationPermission();
    }
}
