package kgk.mobile.external.kgkservicesocketnio;


import java.nio.channels.SocketChannel;

final class ChangeRequest {

    static final int REGISTER = 1;
    static final int CHANGEOPS = 2;

    SocketChannel socketChannel;
    int type;
    int ops;

    ////

    ChangeRequest(SocketChannel socketChannel, int type, int ops) {
        this.socketChannel = socketChannel;
        this.type = type;
        this.ops = ops;
    }
}
