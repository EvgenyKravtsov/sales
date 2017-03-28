package kgk.mobile.domain.service;


import java.util.List;

import kgk.mobile.domain.SalesOutlet;

public interface KgkService {

    interface Listener {

        void onSalesOutletsReceivedFromRemoteStorage(List<SalesOutlet> salesOutlets);
    }

    ////

    boolean isAvailable();

    void addListener(Listener listener);

    void requestSalesOutlets();

    List<SalesOutlet> getSalesOutlets();
}
