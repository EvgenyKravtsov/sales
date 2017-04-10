package kgk.mobile.domain.service;


import java.util.List;

import kgk.mobile.domain.SalesOutlet;
import kgk.mobile.domain.SalesOutletAttendance;
import kgk.mobile.domain.UserLocation;
import kgk.mobile.domain.UserOperation;

public interface DatabaseService {

    interface Listener {

        void onSalesOutletsReceivedFromLocalStorage(List<SalesOutlet> salesOutlets);

        void onUserOperationsReceivedFromLocalStorage(List<UserOperation> userOperations);

        void onNonSynchronizedSalesOutletAttendanceMessagesReceivedFromLocalStorage(
                List<String> attendanceMessages);
    }

    ////

    void addListener(Listener listener);

    void requestSalesOutlets();

    void updateSalesOutlets(List<SalesOutlet> salesOutlets);

    void requestUserOperations();

    void updateUserOperations(List<UserOperation> userOperations);

    void insertSalesOutletAttendance(SalesOutletAttendance attendance);

    void requestNonSynchronizedSalesOutletAttendances();

    void confirmSalesOutletAttendance(String eventId);
}
