package kgk.mobile.external.network.socket;


import android.util.Log;
import android.util.StringBuilderPrinter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

public final class SocketNio implements Runnable, SocketService {

    private static final String TAG = SocketNio.class.getSimpleName();
    private static final int SELECTOR_TIMEOUT_MILLISECONDS = 1000;
    private static final int SOCKET_THREAD_SLEEP_TIME_MILLISECONDS = 1000;
    private static final int READ_BUFFER_SIZE_BYTES = 2048;
    private static final int TIME_TO_RECONNECTION_SECONDS = 30;

    private int timeToReconnectionAttemptSeconds = TIME_TO_RECONNECTION_SECONDS;
    private Selector selector;
    private Queue<byte[]> writeQueue = new ConcurrentLinkedQueue<>();
    private List<Listener> listeners = new ArrayList<>();
    private boolean isConnected;
    private StringBuilder readString = new StringBuilder();

    //// SOCKET SERVICE

    @Override
    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    @Override
    public void connect() {
        new Thread(this).start();
    }

    @Override
    public boolean isConnected() {
        return isConnected;
    }

    @Override
    public void send(byte[] data) {
        writeQueue.add(data);
    }

    //// RUNNABLE

    @Override
    public void run() {
        try {
            readString.append("");
            selector = Selector.open();
            engageConnection();

            while(!Thread.currentThread().isInterrupted()) {
                selector.select(SELECTOR_TIMEOUT_MILLISECONDS);
                Iterator<SelectionKey> keys = selector.selectedKeys().iterator();

                while (keys.hasNext()) {
                    SelectionKey selectionKey = keys.next();

                    if (!selectionKey.isValid()) continue;
                    if (selectionKey.isConnectable()) setupConnection(selectionKey);

                    if (selectionKey.isWritable()) {
                        byte[] data = writeQueue.poll();
                        if (data != null) write(selectionKey, data);
                    }

                    if (selectionKey.isReadable()) read(selectionKey);

                    keys.remove();

                    timeToReconnectionAttemptSeconds -= 1;

                    if (timeToReconnectionAttemptSeconds == 0) {
                        for (Listener listener : listeners) listener.onDisconnected();
                        Thread.currentThread().interrupt();
                    }

                    TimeUnit.MILLISECONDS.sleep(SOCKET_THREAD_SLEEP_TIME_MILLISECONDS);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            close();
        }
    }

    //// PRIVATE

    private void close() {
        try {
            selector.close();
            isConnected = false;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void engageConnection() {
        try {
            SocketChannel socketChannel;
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_CONNECT);
            socketChannel.connect(new InetSocketAddress(
                    SocketService.SERVER_ADDRESS,
                    SocketService.SERVER_PORT
            ));
        }
        catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "engageConnection: Exception");
        }
    }

    private void setupConnection(SelectionKey selectionKey) throws IOException {
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();

        if (socketChannel.isConnectionPending()) {
            socketChannel.finishConnect();
            isConnected = true;
            for (Listener listener : listeners) listener.onConnected();
        }

        socketChannel.configureBlocking(false);
        int interestedSet = SelectionKey.OP_READ | SelectionKey.OP_WRITE;
        socketChannel.register(selector, interestedSet);
    }

    private void write(SelectionKey selectionKey, byte[] data) throws IOException {
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        socketChannel.write(ByteBuffer.wrap(data));
        Log.d(TAG, "write: " + new String(data));
    }

    private void read(SelectionKey selectionKey) {
        try {
            SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
            ByteBuffer readBuffer = ByteBuffer.allocate(READ_BUFFER_SIZE_BYTES);
            readBuffer.clear();
            int length;

            try {
                length = socketChannel.read(readBuffer);
            } catch (IOException e) {
                Log.d(TAG, "read: Exception During Socket Reading");
                selectionKey.cancel();
                socketChannel.close();
                return;
            }

            if (length == -1) {
                Log.d(TAG, "read: Nothing Was Read From Socket");
                selectionKey.cancel();
                socketChannel.close();
                return;
            }

            readBuffer.flip();
            byte[] buffer = new byte[READ_BUFFER_SIZE_BYTES];
            readBuffer.get(buffer, 0, length);
            readString.append(new String(buffer).trim());
            Log.d(TAG, "read: Read String: " + readString.toString());
            for (Listener listener : listeners) listener.onDataReceived(readString.toString().getBytes());
            readString.setLength(0);

            timeToReconnectionAttemptSeconds = TIME_TO_RECONNECTION_SECONDS;
        }
        catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "read: Exception");
        }
    }
}





























