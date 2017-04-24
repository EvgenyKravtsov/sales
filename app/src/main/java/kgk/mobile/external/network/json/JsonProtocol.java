package kgk.mobile.external.network.json;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import kgk.mobile.domain.Mode;
import kgk.mobile.domain.SalesOutlet;
import kgk.mobile.domain.SalesOutletAttendance;
import kgk.mobile.domain.UserLocation;
import kgk.mobile.domain.UserOperation;
import kgk.mobile.domain.service.KgkService.LoginAnswerType;
import kgk.mobile.domain.service.SystemService;

public final class JsonProtocol {

    private static final String TAG = JsonProtocol.class.getSimpleName();

    private final SystemService systemService;

    ////

    public JsonProtocol(SystemService systemService) {
        this.systemService = systemService;
    }

    ////

    public JSONObject createAuthenticationMessage() {
        JSONObject authenticationMessage = new JSONObject();
        JSONObject messageParameters = new JSONObject();
        JSONObject authParameters = new JSONObject();

        try {
            authParameters.put("ID", systemService.getDeviceId());
            authParameters.put("VERSION", systemService.getAppVersion());
            messageParameters.put("ID", 0);
            messageParameters.put("TIME", Calendar.getInstance().getTimeInMillis() / 1000);
            messageParameters.put("TYPE", "AUTH");
            messageParameters.put("PARAMS", authParameters);
            authenticationMessage.put("REQUEST", messageParameters);
        }
        catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "createAuthenticationMessage: Exception");
        }

        return authenticationMessage;
    }

    public JSONObject createGetSalesOutletsMessage() {
        JSONObject getSalesOutletsMessage = new JSONObject();
        JSONObject messageParameters = new JSONObject();

        try {
            messageParameters.put("ID", 0);
            messageParameters.put("TIME", Calendar.getInstance().getTimeInMillis() / 1000);
            messageParameters.put("TYPE", "POINTS");
            getSalesOutletsMessage.put("REQUEST", messageParameters);
        }
        catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "createGetSalesOutletsMessage: Exception");
        }

        return getSalesOutletsMessage;
    }

    public JSONObject createGetUserOperationsMessage() {
        JSONObject getUserOperationsMessage = new JSONObject();
        JSONObject messageParameters = new JSONObject();

        try {
            messageParameters.put("ID", 0);
            messageParameters.put("TIME", Calendar.getInstance().getTimeInMillis() / 1000);
            messageParameters.put("TYPE", "TASKS");
            getUserOperationsMessage.put("REQUEST", messageParameters);
        }
        catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "createGetSalesOutletsMessage: Exception");
        }

        return getUserOperationsMessage;
    }

    public JSONObject createSalesOutletAttendanceMessage(SalesOutletAttendance attendance) {
        JSONObject salesOutletAttendanceMessage = new JSONObject();
        JSONObject messageParameters = new JSONObject();
        JSONObject additionalParameters = new JSONObject();
        JSONArray userOperations = new JSONArray();

        try {
            for (UserOperation userOperation : attendance.getSelectedUserOperations()) {
                JSONObject userOperationJson = new JSONObject();
                userOperationJson.put("ID", userOperation.getId());

                if (userOperation.getId() == 3) {
                    JSONObject userOperationParameters = new JSONObject();
                    userOperationParameters.put("CASH", attendance.getAddedValue());
                    userOperationJson.put("DATA", userOperationParameters);
                }

                userOperations.put(userOperationJson);
            }

            additionalParameters.put("POINT_ID", attendance.getAttendedSalesOutlet().getId());
            additionalParameters.put("TASKS", userOperations);
            additionalParameters.put("HISTORY", true);
            additionalParameters.put("ENTER_TIME", attendance.getBeginDateUnixSeconds());

            JSONObject modeJson = new JSONObject();
            if (attendance.getMode() == Mode.Telephone) modeJson.put("PHONE_FLAG", true);
            additionalParameters.put("DATA", modeJson);

            messageParameters.put("TIME", attendance.getEndDateUnixSeconds());
            messageParameters.put("TYPE", "POINT_EXIT");
            messageParameters.put("PARAMS", additionalParameters);

            salesOutletAttendanceMessage.put("EVENT", messageParameters);
        }
        catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "createSalesOutletAttendanceMessage: Exception");
        }

        return salesOutletAttendanceMessage;
    }

    public JSONObject createLocationMessage(UserLocation userLocation) {
        JSONObject locationMessage = new JSONObject();
        JSONObject messageParameters = new JSONObject();
        JSONObject locationParameters = new JSONObject();

        try {
            locationParameters.put("ID", 0);
            locationParameters.put("TIME", userLocation.getLocationTime());
            locationParameters.put("LAT", userLocation.getLatitude());
            locationParameters.put("LNG", userLocation.getLongitude());
            locationParameters.put("ALTITUDE", userLocation.getAltitude());
            locationParameters.put("AZIMUT", userLocation.getAzimut());
            locationParameters.put("SPEED", userLocation.getSpeed());
            locationParameters.put("HISTORY", systemService.getInternetConnectionStatus());
            locationParameters.put("GPS_ENABLED", systemService.getGpsModuleStatus());

            messageParameters.put("TIME", Calendar.getInstance().getTimeInMillis() / 1000);
            messageParameters.put("TYPE", "GPS");
            messageParameters.put("PARAMS", locationParameters);

            locationMessage.put("MSG", messageParameters);
        }
        catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "createLocationMessage: Exception");
        }

        return locationMessage;
    }

    public JsonAnswer parseAnswer(byte[] data) throws JSONException {
        String answer = new String(data).trim();

        JSONObject answerJson = new JSONObject(answer);

        if (answerJson.has("ANSWER")) {
            return parseStandardAnswer(answerJson);
        }
        else if (answerJson.has("CONFIRM")) {
            Log.d(TAG, "parseAnswer: Location Message Received By Server");
        }
        else if (answerJson.has("EVENT_ANSWER")) {
            return parseEventAnswer(answerJson);
        }

        return null;
    }

    public boolean parseAuthenticationAnswer(JSONObject answerJson) {
        try {
            JSONObject answerParameters = answerJson.getJSONObject("ANSWER");
            JSONObject authenticationParameters = answerParameters.getJSONObject("PARAMS");
            String authenticationResult = authenticationParameters.getString("ACCESS");
            return authenticationResult.equals("GRANTED");
        }
        catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "parseAuthenticationAnswer: Exception");
            return false;
        }
    }

    public List<SalesOutlet> parseSalesOutletsAnswer(JSONObject answerJson) {
        List<SalesOutlet> salesOutlets = new ArrayList<>();

        try {
            JSONObject answerParameters = answerJson.getJSONObject("ANSWER");
            JSONArray salesOutletJsonArray = answerParameters.getJSONArray("DATA");

            for (int i = 0; i < salesOutletJsonArray.length(); i++) {
                JSONObject salesOutletJson = salesOutletJsonArray.getJSONObject(i);
                SalesOutlet salesOutlet = parseSalesOutletJson(salesOutletJson);
                if (salesOutlet != null) salesOutlets.add(salesOutlet);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "parseSalesOutletsAnswer: Exception");
        }

        return salesOutlets;
    }

    public List<UserOperation> parseUserOperationsAnswer(JSONObject answerJson) {
        List<UserOperation> userOperations = new ArrayList<>();

        try {
            JSONObject answerParameters = answerJson.getJSONObject("ANSWER");
            JSONArray userOperationsJsonArray = answerParameters.getJSONArray("DATA");

            for (int i = 0; i < userOperationsJsonArray.length(); i++) {
                JSONObject userOperationJson = userOperationsJsonArray.getJSONObject(i);
                UserOperation userOperation = parseUserOperationJson(userOperationJson);
                if (userOperation != null) userOperations.add(userOperation);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "parseUserOperationsAnswer: Exception");
        }

        return userOperations;
    }

    public String parsePointExitAnswer(JSONObject answerJson) {
        // {"EVENT_ANSWER": {"TYPE": "POINT_EXIT", "EVENT_ID": '14918134493022606286'  }}
        String eventId = "";

        try {
            JSONObject answerParameters = answerJson.getJSONObject("EVENT_ANSWER");
            eventId = answerParameters.getString("EVENT_ID");
        }
        catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "parsePointExitAnswer: Exception");
        }

        return eventId;
    }

    public LoginAnswerType parseLoginAnswer(JSONObject responseJson) {
        try {
            boolean status = responseJson.getBoolean("status");
            if (status) return LoginAnswerType.Success;
            else {
                if (responseJson.getString("error").equals("Device is not allowed for that user")) return LoginAnswerType.DeviceNotAllowed;
                return LoginAnswerType.NoUserFound;
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "parseLoginResponse: Exception");
            return LoginAnswerType.Error;
        }
    }

    ////

    private SalesOutlet parseSalesOutletJson(JSONObject salesOutletJson) {
        try {
            int id = salesOutletJson.getInt("ID");
            double latitude = salesOutletJson.getDouble("LAT");
            double longitude = salesOutletJson.getDouble("LNG");
            String code = salesOutletJson.getString("CODE");
            String title = salesOutletJson.getString("NAME");
            return new SalesOutlet(id, latitude, longitude, code, title);
        }
        catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "parseSalesOutletJson: Exception");
            return null;
        }
    }

    private UserOperation parseUserOperationJson(JSONObject userOperationJson) {
        try {
            int id = userOperationJson.getInt("ID");
            String title = userOperationJson.getString("NAME");
            return new UserOperation(id, title);
        }
        catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "parseUserOperationJson: Exception");
            return null;
        }
    }

    private JsonAnswer parseStandardAnswer(JSONObject answerJson) throws JSONException {
        JSONObject answerParameters = answerJson.getJSONObject("ANSWER");
        String answerType = answerParameters.getString("TYPE");

        switch (answerType) {
            case "AUTH":
                return new JsonAnswer(JsonAnswerType.Authentication, answerJson);
            case "POINTS":
                return new JsonAnswer(JsonAnswerType.SalesOutlets, answerJson);
            case "TASKS":
                return new JsonAnswer(JsonAnswerType.UserOperations, answerJson);

        }

        return null;
    }

    private JsonAnswer parseEventAnswer(JSONObject answerJson) throws JSONException {
        return new JsonAnswer(JsonAnswerType.PointExit, answerJson);
    }
}
