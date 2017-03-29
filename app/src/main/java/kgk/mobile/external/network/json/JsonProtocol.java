package kgk.mobile.external.network.json;



import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import kgk.mobile.domain.SalesOutlet;
import kgk.mobile.domain.UserOperation;

public final class JsonProtocol {

    private static final String TAG = JsonProtocol.class.getSimpleName();

    ////

    public JSONObject createAuthenticationMessage() {
        JSONObject authenticationMessage = new JSONObject();
        JSONObject messageParameters = new JSONObject();
        JSONObject authParameters = new JSONObject();

        try {
            authParameters.put("ID", "3022606286");
            authParameters.put("VERSION", "0.0.0.1");
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

    public JsonAnswer parseAnswer(byte[] data) {
        String answer = new String(data).trim();

        try {
            JSONObject answerJson = new JSONObject(answer);
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
        catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "parseAnswer: Exception");
            return null;
        }
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

    ////

    private SalesOutlet parseSalesOutletJson(JSONObject salesOutletJson) {
        try {
            double latitude = salesOutletJson.getDouble("LAT");
            double longitude = salesOutletJson.getDouble("LNG");
            String code = salesOutletJson.getString("CODE");
            String title = salesOutletJson.getString("NAME");
            return new SalesOutlet(latitude, longitude, code, title);
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
}
