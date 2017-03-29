package kgk.mobile.domain;


public final class SalesOutlet {

    private static final int USER_IN_ZONE_DETECTION_DISTANCE_METERS = 250;

    private final double latitude;
    private final double longitude;
    private final String code;
    private final String title;

    ////

    public SalesOutlet(double latitude, double longitude, String code, String title) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.code = code;
        this.title = title;
    }

    ////

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

    public boolean isUserInZone(UserLocation userLocation) {
        return userLocation.distanceToInMeters(latitude, longitude) <=
                USER_IN_ZONE_DETECTION_DISTANCE_METERS;
    }
}
