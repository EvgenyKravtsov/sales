package kgk.mobile.domain;


public final class SalesOutlet {

    private static final int USER_IN_ZONE_DETECTION_DISTANCE_METERS = 250;

    private final double latitude;
    private final double longitude;
    private final String code;

    ////

    public SalesOutlet(double latitude, double longitude, String code) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.code = code;
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

    public boolean isUserInZone(UserLocation userLocation) {
        return userLocation.distanceToInMeters(latitude, longitude) <=
                USER_IN_ZONE_DETECTION_DISTANCE_METERS;
    }
}
