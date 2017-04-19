package kgk.mobile.domain.service;


import java.util.List;

import kgk.mobile.domain.SalesOutlet;
import kgk.mobile.domain.SalesOutletAttendance;
import kgk.mobile.domain.UserOperation;

public interface KgkService {

    enum LoginAnswerType {
        Success, NoUserFound, DeviceNotAllowed, Error
    }

    interface Listener {

        void onSalesOutletsReceivedFromRemoteStorage(List<SalesOutlet> salesOutlets);

        void onUserOperationsReceivedFromRemoteStorage(List<UserOperation> userOperations);

        void onPointExitIdReceivedFromRemoteStorage(String eventId);

        void onLastSendingDateChanged(long lastSendingDateUnixSeconds);

        void onLoginAnswerReceived(LoginAnswerType answerType);
    }

    ////

    void connect();

    boolean isAvailable();

    void addListener(Listener listener);

    void removeListener(Listener listener);

    void requestSalesOutlets();

    void requestUserOperations();

    void sendSalesOutletAttendances(List<SalesOutletAttendance> attendances);

    long getLastSendingDate();

    void requestUserLogin(String login, String password, String deviceId);
}
