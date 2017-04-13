package kgk.mobile.presentation.view.map;


import java.util.List;

import kgk.mobile.domain.SalesOutlet;

public interface MapController {

    interface Listener {

        void onMapZoomChanged(float zoom);
    }

    ////

    void addListener(Listener listener);

    void displayZoom(float zoom, boolean isAnimated);

    void displayUser(double latitude, double longitude);

    void centerCameraOnUser(double latitude, double longitude, boolean isAnimated);

    void displaySalesOutlets(List<SalesOutlet> salesOutlets);

    void displayEnteredSalesOutlets(List<SalesOutlet> salesOutletsEntered);

    void displaySelectedSalesOutlet(SalesOutlet selectedSalesOutlet);

    void redrawMapObjects();
}
