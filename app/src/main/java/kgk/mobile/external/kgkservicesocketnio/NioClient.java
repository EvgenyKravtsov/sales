package kgk.mobile.external.kgkservicesocketnio;


import android.util.Log;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

final class NioClient implements Runnable {

    private static final String TAG = NioClient.class.getSimpleName();
    private static final String KGK_SERVER_ADDRESS = "rcv1.kgk-global.com";
    private static final int KGK_SERVER_PORT = 8877;

    private final List<ChangeRequest> pendingChanges = new ArrayList<>();
    private final Map<SocketChannel, List<ByteBuffer>> pendingData = new HashMap<>();

    private boolean keepConnection = true;
    private Selector selector;
    private SocketChannel socketChannel;
    private Map<SocketChannel, ResponseHandler> responseHandlers =
            Collections.synchronizedMap(new HashMap<SocketChannel, ResponseHandler>());

    ////

    NioClient() throws IOException {
        selector = SelectorProvider.provider().openSelector();
        socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        socketChannel.connect(new InetSocketAddress(KGK_SERVER_ADDRESS, KGK_SERVER_PORT));

        synchronized (pendingChanges) {
            pendingChanges.add(new ChangeRequest(socketChannel,
                    ChangeRequest.REGISTER, SelectionKey.OP_CONNECT));
        }
    }

    //// RUNNABLE

    @Override
    public void run() {
        while (keepConnection) {
            try {
                synchronized (pendingChanges) {

                    for (ChangeRequest change : pendingChanges) {
                        switch (change.type) {
                            case ChangeRequest.CHANGEOPS:
                                SelectionKey key = change.socketChannel.keyFor(selector);
                                key.interestOps(change.ops);
                                break;
                            case ChangeRequest.REGISTER:
                                change.socketChannel.register(selector, change.ops);
                                break;
                        }
                    }

                    pendingChanges.clear();
                }

                selector.select();

                Iterator<SelectionKey> selectedKeys = selector.selectedKeys().iterator();

                while (selectedKeys.hasNext()) {
                    SelectionKey key = selectedKeys.next();
                    selectedKeys.remove();

                    if (!key.isValid()) continue;
                }
            }
            catch (IOException e) {
                e.printStackTrace();
                // TODO Handle
            }
        }
    }

    //// PRIVATE

    private void send(byte[] data, ResponseHandler responseHandler) throws IOException {
        responseHandlers.put(socketChannel, responseHandler);

        synchronized (pendingData) {
            List<ByteBuffer> queue = pendingData.get(socketChannel);

            if (queue == null) {
                queue = new ArrayList<>();
                pendingData.put(socketChannel, queue);
            }

            queue.add(ByteBuffer.wrap(data));
        }

        selector.wakeup();
    }
}






















