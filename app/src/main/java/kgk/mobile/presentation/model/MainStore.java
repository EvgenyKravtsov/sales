package kgk.mobile.presentation.model;


import java.util.List;

import kgk.mobile.domain.SalesOutlet;
import kgk.mobile.domain.UserLocation;
import kgk.mobile.domain.UserOperation;
import kgk.mobile.external.network.Authorization;

public interface MainStore {

    interface Listener {

        void onUserLocationChanged();

        void onSalesOutletChanged();

        void onEnteredSalesOutletChanged();

        void onSelectedSalesOutletChanged();

        void onUserOperationsChanged();

        void onAuthorizationChanged();
    }

    ////

    void setup();

    void addListener(Listener listener);

    void removeListener(Listener listener);

    UserLocation getUserLocation();

    List<SalesOutlet> getSalesOutlets();

    List<SalesOutlet> getEnteredSalesOutlets();

    float getMapZoom();

    void setMapZoom(float zoom);

    SalesOutlet getSelectedSalesOutlet();

    void setSelectedSalesOutlet(SalesOutlet selectedSalesOutlet);

    List<UserOperation> getUserOperations();

    long getSalesOutletAttendanceBeginDateUnixSeconds();

    void salesOutletAttended(List<UserOperation> selectedUserOperations, int attendanceAddedValue);

    Authorization getAuthorization();
}
