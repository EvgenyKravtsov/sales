package kgk.mobile.presentation.view.map.google;


import com.google.android.gms.maps.model.Marker;

final class SalesOutletMarker {

    private final Marker marker;
    private final String salesOutletCode;

    ////

    public SalesOutletMarker(Marker marker, String salesOutletCode) {
        this.marker = marker;
        this.salesOutletCode = salesOutletCode;
    }

    ////

    public Marker getMarker() {
        return marker;
    }

    public String getSalesOutletCode() {
        return salesOutletCode;
    }
}
