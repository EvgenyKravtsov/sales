package kgk.mobile.presentation.view.map.google;


import android.graphics.Color;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import kgk.mobile.DependencyInjection;
import kgk.mobile.domain.SalesOutlet;
import kgk.mobile.external.android.ImageCreator;
import kgk.mobile.presentation.view.mainscreen.MainContract;
import kgk.mobile.presentation.view.map.MapController;

public final class GoogleMapController implements MapController, GoogleMap.OnCameraIdleListener {

    private static final String TAG = GoogleMapController.class.getSimpleName();
    private static final float USER_MARKER_ANCHOR_HORIZONTAL = 0.5f;
    private static final float USER_MARKER_ANCHOR_VERTICAL = 0.5f;
    private static final float SALES_OUTLET_MARKER_ANCHOR_HORIZONTAL = 0.5f;
    private static final float SALES_OUTLET_MARKER_ANCHOR_VERTICAL = 0.5f;

    private final GoogleMap googleMap;
    private final List<MapController.Listener> listeners = new ArrayList<>();
    private final List<SalesOutletMarker> salesOutletMarkers = new ArrayList<>();
    private final List<Circle> salesOutletZones = new ArrayList<>();

    private Marker userMarker;
    private boolean isCameraCenteredOnUser;
    private BitmapDescriptor userMarkerImage;
    private BitmapDescriptor salesOutletMarkerImage;
    private BitmapDescriptor salesOutletEnteredMarkerImage;

    ////

    public GoogleMapController(GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.googleMap.setOnCameraIdleListener(this);
        prepareImages();
    }

    //// MAP CONTROLLER

    @Override
    public void addListener(Listener listener) {
        listeners.add(listener);
    }

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
    public void displaySalesOutlets(List<SalesOutlet> salesOutlets) {
        removeSalesOutlets();

        for (SalesOutlet salesOutlet : salesOutlets) {
            double latitude = salesOutlet.getLatitude();
            double longitude = salesOutlet.getLongitude();

            Marker marker = googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(latitude, longitude))
                    .anchor(SALES_OUTLET_MARKER_ANCHOR_HORIZONTAL,
                            SALES_OUTLET_MARKER_ANCHOR_VERTICAL));
            if (salesOutletMarkerImage != null) marker.setIcon(salesOutletMarkerImage);
            salesOutletMarkers.add(new SalesOutletMarker(marker, salesOutlet.getCode()));

            Circle circle = googleMap.addCircle(new CircleOptions()
                    .center(new LatLng(latitude, longitude))
                    .radius(250).strokeColor(Color.BLACK)
                    .strokeWidth(ImageCreator.dpToPx(1)));
            salesOutletZones.add(circle);
        }
    }

    @Override
    public void displayEnteredSalesOutlets(List<SalesOutlet> salesOutletsEntered) {
        for (SalesOutlet salesOutlet : salesOutletsEntered)
            Log.d(TAG, "displayEnteredSalesOutlets: " + salesOutlet.getCode());

        for (SalesOutletMarker salesOutletMarker : salesOutletMarkers) {
            for (SalesOutlet salesOutletEntered: salesOutletsEntered) {
                if (salesOutletEntered.getCode().equals(salesOutletMarker.getSalesOutletCode())) {
                    Log.d(TAG, "displayEnteredSalesOutlets: ");
                    salesOutletMarker.getMarker().setIcon(salesOutletEnteredMarkerImage);
                }
            }
        }
    }

    //// ON CAMERA ZOOM CHANGED LISTENER

    @Override
    public void onCameraIdle() {
        float currentZoom = googleMap.getCameraPosition().zoom;
        for (Listener listener : listeners) listener.onMapZoomChanged(currentZoom);
    }

    ////

    private void prepareImages() {
        ImageCreator imageCreator = new ImageCreator(DependencyInjection.provideAppContext());
        userMarkerImage = imageCreator.createUserMarkerImage();
        salesOutletMarkerImage = imageCreator.createSalesOutletMarkerImage();
        salesOutletEnteredMarkerImage = imageCreator.createSalesOutletEnteredMarkerImage();
    }

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

    private void removeSalesOutlets() {
        Iterator<SalesOutletMarker> salesOutletMarkerIterator = salesOutletMarkers.iterator();
        Iterator<Circle> salesOutletZoneIterator = salesOutletZones.iterator();

        while(salesOutletMarkerIterator.hasNext()) {
            SalesOutletMarker salesOutletMarker = salesOutletMarkerIterator.next();
            salesOutletMarker.getMarker().remove();
            salesOutletMarkerIterator.remove();
        }

        while (salesOutletZoneIterator.hasNext()) {
            Circle salesOutletZone = salesOutletZoneIterator.next();
            salesOutletZone.remove();
            salesOutletZoneIterator.remove();
        }
    }
}
