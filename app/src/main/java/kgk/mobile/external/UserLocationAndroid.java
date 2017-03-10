package kgk.mobile.external;


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
}
