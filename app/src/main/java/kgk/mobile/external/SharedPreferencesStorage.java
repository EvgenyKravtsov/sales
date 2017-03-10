package kgk.mobile.external;

import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Inject;

import kgk.mobile.App;
import kgk.mobile.domain.SettingsStorageService;


public final class SharedPreferencesStorage implements SettingsStorageService {

    private static final String STORAGE_KEY = "kgk_sales_shared_preferences";
    private static final String PREFERRED_MAP_ZOOM_KEY = "preferred_map_zoom_key";
    private static final float PREFERRED_MAP_ZOOM_DEFAULT = 10;

    private final SharedPreferences sharedPreferences;

    @Inject Context context;

    ////

    public SharedPreferencesStorage() {
        App.getComponent().inject(this);
        sharedPreferences = context.getSharedPreferences(STORAGE_KEY, Context.MODE_PRIVATE);
    }

    //// SETTINGS STORAGE
    // TODO Make User Dependent Options

    @Override
    public float getPreferredMapZoom() {
        return sharedPreferences.getFloat(PREFERRED_MAP_ZOOM_KEY, PREFERRED_MAP_ZOOM_DEFAULT);
    }
}
