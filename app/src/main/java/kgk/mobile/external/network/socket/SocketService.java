package kgk.mobile.external.network.socket;


import org.json.JSONException;

import java.util.List;

import kgk.mobile.domain.SalesOutlet;

public interface SocketService {

    interface Listener {

        void onConnected();

        void onDataReceived(byte[] data) throws Exception ;

        void onDisconnected();
    }

    ////

    String SERVER_ADDRESS = "rcv1.kgk-global.com";
    int SERVER_PORT = 8877;

    ////

    void addListener(Listener listener);

    void connect();

    boolean isConnected();

    void send(byte[] data);
}
