package kgk.mobile.presentation.view.map.google;


import com.google.android.gms.maps.model.Marker;

final class SalesOutletMarker {

    private final Marker marker;
    private final String salesOutletCode;

    ////

    SalesOutletMarker(Marker marker, String salesOutletCode) {
        this.marker = marker;
        this.salesOutletCode = salesOutletCode;
    }

    ////

    Marker getMarker() {
        return marker;
    }

    String getSalesOutletCode() {
        return salesOutletCode;
    }
}
