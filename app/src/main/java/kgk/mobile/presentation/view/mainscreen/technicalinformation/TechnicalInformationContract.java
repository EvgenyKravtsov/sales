package kgk.mobile.presentation.view.mainscreen.technicalinformation;

import kgk.mobile.presentation.view.base.BasePresenter;
import kgk.mobile.presentation.view.base.BaseView;

public interface TechnicalInformationContract {

    interface Presenter extends BasePresenter<View> {

        void onCreateView();

        void onSalesOutletEntranceRadiusChanged(int radius);
    }

    interface View extends BaseView {

        void displayLastLocationDate(String text);

        void displayInvalidLastLocationDate();

        void displayLastCoordinates(String text);

        void displayInvalidLastCoordinates();

        void displaySpeed(String text);

        void displayInvalidSpeed();

        void displayLastSendingDate(String text);

        void displayInvalidLastSendingDate();

        void displaySalesOutletEntranceRadius(String text);

        void displayDeviceId(String text);

        void displayAppVersion(String text);
    }
}
