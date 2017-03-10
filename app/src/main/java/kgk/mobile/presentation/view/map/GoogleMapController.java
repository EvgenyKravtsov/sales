package kgk.mobile.presentation.view.map;


import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import kgk.mobile.external.ImageCreatorAndroid;

public final class GoogleMapController implements MapController {

    private static final float USER_MARKER_ANCHOR_HORIZONTAL = 0.5f;
    private static final float USER_MARKER_ANCHOR_VERTICAL = 0.5f;

    private final GoogleMap googleMap;

    private Marker userMarker;
    private boolean isCameraCenteredOnUser;
    private BitmapDescriptor userMarkerImage;

    ////

    public GoogleMapController(GoogleMap googleMap) {
        this.googleMap = googleMap;
        userMarkerImage = new ImageCreatorAndroid().createUserMarkerImage();
    }

    //// MAP CONTROLLER

    @Override
    public void displayZoom(float zoom, boolean isAnimated) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.zoomTo(zoom);
        moveCamera(cameraUpdate, isAnimated);
    }

    @Override
    public void displayUser(double latitude, double longitude) {
        if (userMarker != null) {
            removeUserMarker();
        }

        displayUserMarker(latitude, longitude);
    }

    @Override
    public void centerCameraOnUser(double latitude, double longitude, boolean isAnimated) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude));

        if (!isCameraCenteredOnUser) {
            moveCamera(cameraUpdate, isAnimated);
            isCameraCenteredOnUser = true;
        }
    }

    @Override
    public void displaySalesOutlet(double latitude, double longitude) {
        // TODO Implement
    }

    ////

    private void removeUserMarker() {
        userMarker.remove();
        userMarker = null;
    }

    private void displayUserMarker(double latitude, double longitude) {
        Marker marker = googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .anchor(USER_MARKER_ANCHOR_HORIZONTAL, USER_MARKER_ANCHOR_VERTICAL));
        if (userMarkerImage != null) marker.setIcon(userMarkerImage);
    }

    private void moveCamera(CameraUpdate cameraUpdate, boolean isAnimated) {
        if (isAnimated) googleMap.animateCamera(cameraUpdate);
        else googleMap.moveCamera(cameraUpdate);
    }
}
