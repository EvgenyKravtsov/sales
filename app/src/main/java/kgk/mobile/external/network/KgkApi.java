package kgk.mobile.external.network;


import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import kgk.mobile.domain.UserOperation;
import kgk.mobile.domain.service.KgkService;
import kgk.mobile.domain.SalesOutlet;
import kgk.mobile.external.network.json.JsonAnswer;
import kgk.mobile.external.network.json.JsonProtocol;
import kgk.mobile.external.network.socket.SocketService;

public final class KgkApi implements KgkService, SocketService.Listener {

    private static final String TAG = KgkApi.class.getSimpleName();

    private final JsonProtocol jsonProtocol;
    private final SocketService socketService;
    private final List<Listener> listeners = new ArrayList<>();

    private boolean isAuthenticated;

    ////

    public KgkApi(JsonProtocol jsonProtocol, SocketService socketService) {
        this.jsonProtocol = jsonProtocol;
        this.socketService = socketService;
        this.socketService.addListener(this);
        this.socketService.connect();
    }

    //// KGK SERVICE

    @Override
    public boolean isAvailable() {
        return isAuthenticated && socketService.isConnected();
    }

    @Override
    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    @Override
    public void requestSalesOutlets() {
        socketService.send(jsonProtocol.createGetSalesOutletsMessage().toString().getBytes());
    }

    @Override
    public void requestUserOperations() {
        socketService.send(jsonProtocol.createGetUserOperationsMessage().toString().getBytes());
    }

    //// SOCKET SERVICE LISTENER

    @Override
    public void onConnected() {
        socketService.send(jsonProtocol.createAuthenticationMessage().toString().getBytes());
    }

    @Override
    public void onDataReceived(byte[] data) {
        JsonAnswer jsonAnswer = jsonProtocol.parseAnswer(data);
        if (jsonAnswer == null) return;

        switch (jsonAnswer.getType()) {
            case Authentication:
                isAuthenticated = jsonProtocol
                        .parseAuthenticationAnswer(jsonAnswer.getMessage());
                break;
            case SalesOutlets:
                List<SalesOutlet> salesOutlets = jsonProtocol
                        .parseSalesOutletsAnswer(jsonAnswer.getMessage());
                for (Listener listener : listeners)
                    listener.onSalesOutletsReceivedFromRemoteStorage(salesOutlets);
                break;
            case UserOperations:
                List<UserOperation> userOperations = jsonProtocol
                        .parseUserOperationsAnswer(jsonAnswer.getMessage());
                for (Listener listener : listeners)
                    listener.onUserOperationsReceivedFromRemoteStorage(userOperations);
                break;
        }
    }
}
