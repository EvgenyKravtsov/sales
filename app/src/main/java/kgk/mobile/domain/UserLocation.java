package kgk.mobile.domain;


public interface UserLocation {

    double getLatitude();

    double getLongitude();

    int distanceToInMeters(double latitude, double longitude);

    long getLocationTime();

    int getAltitude();

    int getAzimut();

    double getSpeed();
}
