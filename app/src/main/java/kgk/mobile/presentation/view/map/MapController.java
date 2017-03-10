package kgk.mobile.presentation.view.map;


public interface MapController {

    void displayZoom(float zoom, boolean isAnimated);

    void displayUser(double latitude, double longitude);

    void centerCameraOnUser(double latitude, double longitude, boolean isAnimated);

    void displaySalesOutlet(double latitude, double longitude);
}
