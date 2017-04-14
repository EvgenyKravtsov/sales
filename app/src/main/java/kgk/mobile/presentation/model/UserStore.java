package kgk.mobile.presentation.model;


import java.util.List;

import kgk.mobile.domain.UserLocation;
import kgk.mobile.domain.UserOperation;

public interface UserStore {

    interface LocationListener {

        void onLocationReceived(UserLocation userLocation);
    }

    ////

    interface PreferredMapZoomListener {

        void onPreferredMapZoomReceived(float zoom);
    }

    ////

    interface UserOperationsListener {

        void onUserOperationsReceived(List<UserOperation> userOperations);
    }

    ////

    interface UserLoginListener {

        void onLoginSuccess();

        void onUserNotFound();

        void onDeviceNotAllowed();

        void onLoginError();
    }

    ////

    void subscribeForUserLocationUpdate(LocationListener listener);

    void unsubscribeForUserLocationUpdate(LocationListener listener);

    void requestPreferredMapZoom(PreferredMapZoomListener listener);

    void savePreferredMapZoom(float zoom);

    void requestUserOperations();

    void addUserOperationsListener(UserOperationsListener listener);

    void removeUserOperationsListener(UserOperationsListener listener);

    void addUserLoginListener(UserLoginListener listener);

    void removeUserLoginListener(UserLoginListener listener);

    void requestUserLogin(String login, String password);
}
