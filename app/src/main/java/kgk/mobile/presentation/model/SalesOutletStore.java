package kgk.mobile.presentation.model;


import java.util.List;

import kgk.mobile.domain.SalesOutlet;

public interface SalesOutletStore {

    interface Listener {

        void onSalesOutletsReceived(List<SalesOutlet> outlets);
    }

    void requestSalesOutlets(Listener listener);
}
