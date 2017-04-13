package kgk.mobile.domain.service;


public interface SettingsStorageService {

    int SALES_OUTLET_ENTRANCE_RADIUS_MIN_METERS = 25;
    int SALES_OUTLET_ENTRANCE_RADIUS_MAX_METERS = 500;

    ////

    float getPreferredMapZoom();

    void setPreferredMapZoom(float zoom);

    int getSalesOutletEntranceRadius();

    void setSalesOutletEntranceRadius(int radius);

    boolean getUserRemembered();

    void setUserRemembered(boolean isUserRemembered);

    String getLogin();

    void setLogin(String login);

    String getPassword();

    void setPassword(String password);
}
