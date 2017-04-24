package kgk.mobile.external.greendao;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import kgk.mobile.domain.SalesOutlet;
import kgk.mobile.domain.SalesOutletAttendance;
import kgk.mobile.domain.UserOperation;

import static kgk.mobile.domain.Mode.intToMode;
import static kgk.mobile.domain.Mode.modeToInt;

public final class JsonSerializer {

    private static final String BEGIN_DATE_UNIX_SECONDS_KEY = "beginDateUnixSeconds";
    private static final String END_DATE_UNIX_SECONDS_KEY = "endDateUnixSeconds";
    private static final String ATTENDED_SALES_OUTLET_KEY = "attendedSalesOutlet";
    private static final String SELECTED_USER_OPERATIONS_KEY = "selectedUserOperationsKey";
    private static final String ADDED_VALUE_KEY = "addedValue";
    private static final String MODE_KEY = "modeKey";

    private static final String SALES_OUTLET_ID_KEY = "salesOutletId";
    private static final String SALES_OUTLET_LATITUDE_KEY = "salesOutletLatitude";
    private static final String SALES_OUTLET_LONGITUDE_KEY = "salesOutletLongitude";
    private static final String SALES_OUTLET_CODE_KEY = "salesOutletCode";
    private static final String SALES_OUTLET_TITLE_KEY = "salesOutletTitle";

    private static final String USER_OPERATION_ID_KEY = "userOperationId";
    private static final String USER_OPERATION_TITLE_KEY = "userOperationTitleKey";

    ////

    JSONObject serializeSaleOutletAttendance(SalesOutletAttendance attendance) throws JSONException {
        JSONObject attendanceJson = new JSONObject();
        attendanceJson.put(BEGIN_DATE_UNIX_SECONDS_KEY, attendance.getBeginDateUnixSeconds());
        attendanceJson.put(END_DATE_UNIX_SECONDS_KEY, attendance.getEndDateUnixSeconds());

        SalesOutlet salesOutlet = attendance.getAttendedSalesOutlet();
        JSONObject attendedSalesOutletJson = new JSONObject();
        attendedSalesOutletJson.put(SALES_OUTLET_ID_KEY, salesOutlet.getId());
        attendedSalesOutletJson.put(SALES_OUTLET_LATITUDE_KEY, salesOutlet.getLatitude());
        attendedSalesOutletJson.put(SALES_OUTLET_LONGITUDE_KEY, salesOutlet.getLongitude());
        attendedSalesOutletJson.put(SALES_OUTLET_CODE_KEY, salesOutlet.getCode());
        attendedSalesOutletJson.put(SALES_OUTLET_TITLE_KEY, salesOutlet.getTitle());

        attendanceJson.put(ATTENDED_SALES_OUTLET_KEY, attendedSalesOutletJson);

        List<UserOperation> userOperations = attendance.getSelectedUserOperations();
        JSONArray userOperationsJson = new JSONArray();

        for (UserOperation userOperation : userOperations) {
            JSONObject userOperationJson = new JSONObject();
            userOperationJson.put(USER_OPERATION_ID_KEY, userOperation.getId());
            userOperationJson.put(USER_OPERATION_TITLE_KEY, userOperation.getTitle());
            userOperationsJson.put(userOperationJson);
        }

        attendanceJson.put(SELECTED_USER_OPERATIONS_KEY, userOperationsJson);
        attendanceJson.put(ADDED_VALUE_KEY, attendance.getAddedValue());
        attendanceJson.put(MODE_KEY, modeToInt(attendance.getMode()));

        return attendanceJson;
    }

    SalesOutletAttendance deserializeSalesOutletAttendance(JSONObject attendanceJson) throws JSONException {
        JSONObject attendedSalesOutletJson = attendanceJson.getJSONObject(ATTENDED_SALES_OUTLET_KEY);
        JSONArray selectedUserOperationsJson = attendanceJson.getJSONArray(SELECTED_USER_OPERATIONS_KEY);

        SalesOutlet attendedSalesOutlet = new SalesOutlet(
                attendedSalesOutletJson.getInt(SALES_OUTLET_ID_KEY),
                attendedSalesOutletJson.getDouble(SALES_OUTLET_LATITUDE_KEY),
                attendedSalesOutletJson.getDouble(SALES_OUTLET_LONGITUDE_KEY),
                attendedSalesOutletJson.getString(SALES_OUTLET_CODE_KEY),
                attendedSalesOutletJson.getString(SALES_OUTLET_TITLE_KEY));

        List<UserOperation> selectedUserOperations = new ArrayList<>();
        for (int i = 0; i < selectedUserOperationsJson.length(); i++) {
            JSONObject userOperationJson = selectedUserOperationsJson.getJSONObject(i);
            UserOperation userOperation = new UserOperation(
                    userOperationJson.getInt(USER_OPERATION_ID_KEY),
                    userOperationJson.getString(USER_OPERATION_TITLE_KEY));
            selectedUserOperations.add(userOperation);
        }

        return new SalesOutletAttendance(
                attendanceJson.getLong(BEGIN_DATE_UNIX_SECONDS_KEY),
                attendanceJson.getLong(END_DATE_UNIX_SECONDS_KEY),
                attendedSalesOutlet,
                selectedUserOperations,
                attendanceJson.getInt(ADDED_VALUE_KEY),
                intToMode(attendanceJson.getInt(MODE_KEY)));
    }

    public JSONObject serializeSalesOutlet(SalesOutlet salesOutlet) throws JSONException {
        if (salesOutlet != null) {
            JSONObject salesOutletJson = new JSONObject();
            salesOutletJson.put(SALES_OUTLET_ID_KEY, salesOutlet.getId());
            salesOutletJson.put(SALES_OUTLET_LATITUDE_KEY, salesOutlet.getLatitude());
            salesOutletJson.put(SALES_OUTLET_LONGITUDE_KEY, salesOutlet.getLongitude());
            salesOutletJson.put(SALES_OUTLET_CODE_KEY, salesOutlet.getCode());
            salesOutletJson.put(SALES_OUTLET_TITLE_KEY, salesOutlet.getTitle());
            return salesOutletJson;
        }

        return null;
    }

    public SalesOutlet deserializeSalesOutlet(JSONObject salesOutletJson) throws JSONException {
        return new SalesOutlet(
                salesOutletJson.getInt(SALES_OUTLET_ID_KEY),
                salesOutletJson.getDouble(SALES_OUTLET_LATITUDE_KEY),
                salesOutletJson.getDouble(SALES_OUTLET_LONGITUDE_KEY),
                salesOutletJson.getString(SALES_OUTLET_CODE_KEY),
                salesOutletJson.getString(SALES_OUTLET_TITLE_KEY));
    }
}
