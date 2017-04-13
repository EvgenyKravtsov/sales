package kgk.mobile.external.network;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import kgk.mobile.DependencyInjection;
import kgk.mobile.domain.SalesOutletAttendance;
import kgk.mobile.domain.UserLocation;
import kgk.mobile.domain.UserOperation;
import kgk.mobile.domain.service.KgkService;
import kgk.mobile.domain.SalesOutlet;
import kgk.mobile.domain.service.LocationService;
import kgk.mobile.external.network.json.JsonAnswer;
import kgk.mobile.external.network.json.JsonProtocol;
import kgk.mobile.external.network.socket.SocketService;

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;

public final class KgkApi implements KgkService, SocketService.Listener, LocationService.Listener {

    private static final String TAG = KgkApi.class.getSimpleName();

    private final JsonProtocol jsonProtocol;
    private final SocketService socketService;
    private final List<Listener> listeners = new ArrayList<>();

    private boolean isAuthenticated;
    private long lastSendingDate;

    ////

    public KgkApi(JsonProtocol jsonProtocol,
                  SocketService socketService,
                  LocationService locationService) {

        this.jsonProtocol = jsonProtocol;
        this.socketService = socketService;
        this.socketService.addListener(this);
        this.socketService.connect();

        locationService.addListener(this);
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
    public void removeListener(Listener listener) {
        listeners.remove(listener);
    }

    @Override
    public void requestSalesOutlets() {
        socketService.send(jsonProtocol.createGetSalesOutletsMessage().toString().getBytes());
        updateLastSendingDate();
    }

    @Override
    public void requestUserOperations() {
        socketService.send(jsonProtocol.createGetUserOperationsMessage().toString().getBytes());
        updateLastSendingDate();
    }

    @Override
    public void sendSalesOutletAttendances(List<SalesOutletAttendance> attendances) {
        for (SalesOutletAttendance attendance : attendances) {
            socketService.send(jsonProtocol.createSalesOutletAttendanceMessage(attendance).toString().getBytes());
            updateLastSendingDate();
        }
    }

    @Override
    public long getLastSendingDate() {
        return lastSendingDate;
    }

    @Override
    public void requestUserLogin(String login, String password, String deviceId) {
        Log.d(TAG, "requestUserLogin: " + deviceId);

        String url = String.format(
                "http://api.trezub.ru/api2/mobile/authorize?login=%s&password=%s&device_id=%s",
                login, password, deviceId);

        RequestQueue queue = Volley.newRequestQueue(DependencyInjection.provideAppContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "onResponse: " + response);

                        try {
                            LoginAnswerType answerType = jsonProtocol
                                    .parseLoginAnswer(new JSONObject(response));

                            for (Listener listener : listeners) {
                                listener.onLoginAnswerReceived(answerType);
                            }
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                            Log.d(TAG, "onResponse: Exception");

                            for (Listener listener : listeners) {
                                listener.onLoginAnswerReceived(LoginAnswerType.Error);
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onErrorResponse: Error - " + error.getLocalizedMessage());

                        for (Listener listener : listeners) {
                            listener.onLoginAnswerReceived(LoginAnswerType.Error);
                        }
                    }
        });

        queue.add(stringRequest);
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
            case PointExit:
                String eventId = jsonProtocol.parsePointExitAnswer(jsonAnswer.getMessage());
                for (Listener listener : listeners)
                    listener.onPointExitIdReceivedFromRemoteStorage(eventId);
                break;
        }
    }

    //// LOCATION SERVICE LISTENER

    @Override
    public void onLocationChanged(UserLocation userLocation) {
        if (socketService.isConnected() && isAuthenticated) {
            socketService.send(jsonProtocol.createLocationMessage(userLocation).toString().getBytes());
            updateLastSendingDate();
        }
    }

    private void updateLastSendingDate() {
        lastSendingDate = Calendar.getInstance().getTimeInMillis() / 1000;

        for (Listener listener : listeners)
            listener.onLastSendingDateChanged(lastSendingDate);
    }
}
