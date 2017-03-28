package kgk.mobile.external.android;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

import kgk.mobile.domain.service.LocationService;
import kgk.mobile.domain.UserLocation;
import kgk.mobile.external.android.UserLocationAndroid;

public final class LocationServiceGoogleFusedApi implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        LocationService {

    private static final String TAG = LocationServiceGoogleFusedApi.class.getSimpleName();
    private static final int LOCATION_UPDATE_INTERVAL_MILLISECONDS = 5000;

    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private boolean isLocationUpdateRequested;
    private List<Listener> listeners = new ArrayList<>();

    private final Context context;

    ////

    public LocationServiceGoogleFusedApi(Context context) {
        this.context = context;

        googleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        locationRequest = new LocationRequest();
        locationRequest.setInterval(LOCATION_UPDATE_INTERVAL_MILLISECONDS);
        locationRequest.setFastestInterval(LOCATION_UPDATE_INTERVAL_MILLISECONDS);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    //// CONNECTION CALLBACKS

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected: ");
        requestLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended: ");
    }

    //// ON CONNECTION FAILED LISTENER

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed: ");
    }

    //// LOCATION LISTENER

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged: ");
        UserLocation userLocation = new UserLocationAndroid(location); // TODO Inject Object Instead Of Creation
        for (Listener listener : listeners) listener.onLocationChanged(userLocation);
    }

    //// LOCATION SERVICE

    @Override
    public void startLocationUpdate() {
        if (!googleApiClient.isConnected()) googleApiClient.connect();
        else requestLocationUpdates();
    }

    @Override
    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    //// PRIVATE

    private void requestLocationUpdates() {
        Log.d(TAG, "requestLocationUpdates: ");
        if (!googleApiClient.isConnected()) return;
        if (isLocationUpdateRequested) return;
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,
                    locationRequest,
                    this);
            isLocationUpdateRequested = true;
        }
        else {
            // TODO Handle Permission Deny
        }
    }
}























