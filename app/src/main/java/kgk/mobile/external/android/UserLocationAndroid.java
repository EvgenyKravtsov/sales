package kgk.mobile.external.android;


import android.location.Location;

import kgk.mobile.domain.UserLocation;

final class UserLocationAndroid implements UserLocation {

    private final Location location;

    ////

    UserLocationAndroid(Location location) {
        this.location = location;
    }

    //// USER LOCATION

    @Override
    public double getLatitude() {
        return location.getLatitude();
    }

    @Override
    public double getLongitude() {
        return location.getLongitude();
    }

    @Override
    public int distanceToInMeters(double latitude, double longitude) {
        Location destination = new Location("");
        destination.setLatitude(latitude);
        destination.setLongitude(longitude);
        return (int) location.distanceTo(destination);
    }

    @Override
    public long getLocationTime() {
        return location.getTime() / 1000;
    }

    @Override
    public int getAltitude() {
        return (int) location.getAltitude();
    }

    @Override
    public int getAzimut() {
        return (int) location.getBearing();
    }

    @Override
    public double getSpeed() {
        return location.getSpeed();
    }
}
