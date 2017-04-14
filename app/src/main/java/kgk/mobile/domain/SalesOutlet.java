package kgk.mobile.domain;


import kgk.mobile.DependencyInjection;
import kgk.mobile.domain.service.SettingsStorageService;

public final class SalesOutlet {

    private final int id;
    private final double latitude;
    private final double longitude;
    private final String code;
    private final String title;

    ////

    public SalesOutlet(int id, double latitude, double longitude, String code, String title) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.code = code;
        this.title = title;
    }

    ////

    public int getId() {
        return id;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getCode() {
        return code;
    }

    public String getTitle() {
        return title;
    }

    public boolean isUserInZone(UserLocation userLocation, SettingsStorageService settingsStorageService) {
        return userLocation.distanceToInMeters(latitude, longitude) <=
                settingsStorageService.getSalesOutletEntranceRadius();
    }

    //// OBJECT

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SalesOutlet)) return false;
        SalesOutlet salesOutlet = (SalesOutlet) obj;
        return code.equals(salesOutlet.code);
    }
}
