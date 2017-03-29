package kgk.mobile.domain.service;


import java.util.List;

import kgk.mobile.domain.SalesOutlet;
import kgk.mobile.domain.UserOperation;

public interface KgkService {

    interface Listener {

        void onSalesOutletsReceivedFromRemoteStorage(List<SalesOutlet> salesOutlets);

        void onUserOperationsReceivedFromRemoteStorage(List<UserOperation> userOperations);
    }

    ////

    boolean isAvailable();

    void addListener(Listener listener);

    void requestSalesOutlets();

    void requestUserOperations();
}
