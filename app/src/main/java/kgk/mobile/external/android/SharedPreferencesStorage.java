package kgk.mobile.external.android;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import kgk.mobile.domain.service.SettingsStorageService;

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

    private final SharedPreferences sharedPreferences;

    ////

    public SharedPreferencesStorage(Context context) {
        sharedPreferences = context.getSharedPreferences(STORAGE_KEY, Context.MODE_PRIVATE);
    }

    //// SETTINGS STORAGE
    // TODO Make User Dependent Options

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
}
