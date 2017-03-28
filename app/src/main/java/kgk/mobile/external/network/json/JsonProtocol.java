package kgk.mobile.external.network.json;



import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import kgk.mobile.domain.SalesOutlet;

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

    public JsonAnswer parseAnswer(byte[] data) {
        String answer = new String(data).trim();

        try {
            JSONObject answerJson = new JSONObject(answer);
            JSONObject answerParameters = answerJson.getJSONObject("ANSWER");
            String answerType = answerParameters.getString("TYPE");

            if (answerType.equals("AUTH")) {
                return new JsonAnswer(JsonAnswerType.Authentication, answerJson);
            }
            else if (answerType.equals("POINTS")) {
                return new JsonAnswer(JsonAnswerType.SalesOutlets,  answerJson);
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

    ////

    private SalesOutlet parseSalesOutletJson(JSONObject salesOutletJson) {
        try {
            double latitude = salesOutletJson.getDouble("LAT");
            double longitude = salesOutletJson.getDouble("LNG");
            String code = salesOutletJson.getString("CODE");
            return new SalesOutlet(latitude, longitude, code);
        }
        catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "parseSalesOutletJson: Exception");
            return null;
        }
    }
}
