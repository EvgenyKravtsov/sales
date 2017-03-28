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
}
