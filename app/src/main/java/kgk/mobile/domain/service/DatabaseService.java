package kgk.mobile.domain.service;


import java.util.List;

import kgk.mobile.domain.SalesOutlet;

public interface DatabaseService {

    interface Listener {

        void onSalesOutletsReceivedFromLocalStorage(List<SalesOutlet> salesOutlets);
    }

    ////

    void addListener(Listener listener);

    void requestSalesOutlets();

    void updateSalesOutlets(List<SalesOutlet> salesOutlets);
}
