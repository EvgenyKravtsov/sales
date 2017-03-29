package kgk.mobile.presentation.model;


import java.util.List;

import kgk.mobile.domain.UserLocation;
import kgk.mobile.domain.UserOperation;

public interface UserStore {

    interface LocationListener {

        void onLocationReceived(UserLocation userLocation);
    }

    interface PreferredMapZoomListener {

        void onPreferredMapZoomReceived(float zoom);
    }

    interface UserOperationsListener {

        void onUserOperationsReceived(List<UserOperation> userOperations);
    }

    void subscribeForUserLocationUpdate(LocationListener listener);

    void requestPreferredMapZoom(PreferredMapZoomListener listener);

    void savePreferredMapZoom(float zoom);

    void requestUserOperations();

    void addUserOperationsListener(UserOperationsListener listener);
}
