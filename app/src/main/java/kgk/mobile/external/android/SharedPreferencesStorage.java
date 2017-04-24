package kgk.mobile.external.android;


import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import kgk.mobile.domain.Mode;
import kgk.mobile.domain.SalesOutlet;
import kgk.mobile.domain.service.SettingsStorageService;
import kgk.mobile.external.greendao.JsonSerializer;

import static kgk.mobile.domain.Mode.intToMode;
import static kgk.mobile.domain.Mode.modeToInt;

public final class SharedPreferencesStorage implements SettingsStorageService {

    private static final String TAG = SharedPreferencesStorage.class.getSimpleName();

    private static final String STORAGE_KEY = "kgk_sales_shared_preferences";
    private static final String PREFERRED_MAP_ZOOM_KEY = "preferred_map_zoom_key";
    private static final float PREFERRED_MAP_ZOOM_DEFAULT = 10;
    private static final String SALES_OUTLET_ENTRANCE_RADIUS_KEY = "sales_outlet_entrance_radius_key";
    private static final int SALES_OUTLET_ENTRANCE_RADIUS_DEFAULT = 100;
    private static final String USER_REMEMBERED_KEY = "user_remembered_key";
    private static final String LOGIN_KEY = "login_key";
    private static final String PASSWORD_KEY = "password_key";
    private static final String SELECTED_SALES_OUTLET_KEY = "selected_sales_outlet_key";
    private static final String SALES_OUTLET_ATTENDANCE_BEGIN_DATE_UNIX_SECONDS_KEY = "sales_outlet_attendance_begin_date_unix_seconds_key";
    private static final String MODE_KEY = "mode_key";

    private final SharedPreferences sharedPreferences;

    ////

    public SharedPreferencesStorage(Context context) {
        sharedPreferences = context.getSharedPreferences(STORAGE_KEY, Context.MODE_PRIVATE);
    }

    //// SETTINGS STORAGE

    @Override
    public float getPreferredMapZoom() {
        return sharedPreferences.getFloat(PREFERRED_MAP_ZOOM_KEY, PREFERRED_MAP_ZOOM_DEFAULT);
    }

    @Override
    public void setPreferredMapZoom(float zoom) {
        sharedPreferences.edit()
                .putFloat(PREFERRED_MAP_ZOOM_KEY, zoom)
                .apply();
    }

    @Override
    public int getSalesOutletEntranceRadius() {
        return sharedPreferences.getInt(SALES_OUTLET_ENTRANCE_RADIUS_KEY,
                                        SALES_OUTLET_ENTRANCE_RADIUS_DEFAULT);
    }

    @Override
    public void setSalesOutletEntranceRadius(int radius) {
        sharedPreferences.edit()
                .putInt(SALES_OUTLET_ENTRANCE_RADIUS_KEY, radius)
                .apply();
    }

    @Override
    public boolean getUserRemembered() {
        return sharedPreferences.getBoolean(USER_REMEMBERED_KEY, false);
    }

    @Override
    public void setUserRemembered(boolean isUserRemembered) {
        sharedPreferences.edit()
                .putBoolean(USER_REMEMBERED_KEY, isUserRemembered)
                .apply();
    }

    @Override
    public String getLogin() {
        return sharedPreferences.getString(LOGIN_KEY, "");
    }

    @Override
    public void setLogin(String login) {
        sharedPreferences.edit()
                .putString(LOGIN_KEY, login)
                .apply();
    }

    @Override
    public String getPassword() {
        return sharedPreferences.getString(PASSWORD_KEY, "");
    }

    @Override
    public void setPassword(String password) {
        sharedPreferences.edit()
                .putString(PASSWORD_KEY, password)
                .apply();
    }

    @Override
    public SalesOutlet getSelectedSalesOutlet() {
        String salesOutletAsString = sharedPreferences.getString(SELECTED_SALES_OUTLET_KEY, "");
        if (salesOutletAsString.equals("")) return null;

        try { return new JsonSerializer().deserializeSalesOutlet(new JSONObject(salesOutletAsString)); }
        catch (JSONException e) { return null; }
    }

    @Override
    public void setSelectedSalesOutlet(SalesOutlet salesOutlet) {
        String salesOutletAsString = "";

        if (salesOutlet != null) {
            try { salesOutletAsString = new JsonSerializer().serializeSalesOutlet(salesOutlet).toString(); }
            catch (JSONException e) { e.printStackTrace(); }
        }

        sharedPreferences.edit()
                .putString(SELECTED_SALES_OUTLET_KEY, salesOutletAsString)
                .apply();
    }

    @Override
    public long getSalesOutletAttendanceBeginDateUnixSeconds() {
        return sharedPreferences.getLong(SALES_OUTLET_ATTENDANCE_BEGIN_DATE_UNIX_SECONDS_KEY, 0);
    }

    @Override
    public void setSalesOutletAttendanceBeginDateUnixSeconds(long salesOutletAttendanceBeginDateUnixSeconds) {
        sharedPreferences.edit()
                .putLong(
                        SALES_OUTLET_ATTENDANCE_BEGIN_DATE_UNIX_SECONDS_KEY,
                        salesOutletAttendanceBeginDateUnixSeconds)
                .apply();
    }

    @Override
    public Mode getMode() {
        return intToMode(sharedPreferences.getInt(MODE_KEY, 0));
    }

    @Override
    public void setMode(Mode mode) {
        sharedPreferences.edit()
                .putInt(MODE_KEY, modeToInt(mode))
                .apply();
    }
}
