package kgk.mobile.presentation.view.mainscreen.menu;

import kgk.mobile.presentation.view.base.BasePresenter;
import kgk.mobile.presentation.view.base.BaseView;

public interface MenuContract {

    interface Presenter extends BasePresenter<View> {

        void onCreateView();
    }

    interface View extends BaseView {

        void displayLastLocationDate(String text);

        void displayInvalidLastLocationDate();
    }
}
