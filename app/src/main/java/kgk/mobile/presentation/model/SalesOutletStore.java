package kgk.mobile.presentation.model;


import java.util.List;

import kgk.mobile.domain.SalesOutlet;
import kgk.mobile.domain.UserLocation;

public interface SalesOutletStore {

    interface Listener {

        void onSalesOutletsReceived(List<SalesOutlet> outlets);

        void salesOutletsEnteredByUser(List<SalesOutlet> salesOutletsEntered);
    }

    void addListener(Listener listener);

    void requestSalesOutlets();

    void isUserInSalesOutletZone(UserLocation userLocation);
}
