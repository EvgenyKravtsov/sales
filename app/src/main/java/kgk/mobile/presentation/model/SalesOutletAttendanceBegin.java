package kgk.mobile.presentation.model;


import kgk.mobile.domain.SalesOutlet;

public final class SalesOutletAttendanceBegin {

    private final SalesOutlet selectedSalesOutlet;
    private final long salesOutletAttendanceBeginDateUnixSeconds;

    ////

    public SalesOutletAttendanceBegin(SalesOutlet selectedSalesOutlet,
                                      long salesOutletAttendanceBeginDateUnixSeconds) {
        this.selectedSalesOutlet = selectedSalesOutlet;
        this.salesOutletAttendanceBeginDateUnixSeconds = salesOutletAttendanceBeginDateUnixSeconds;
    }

    ////


    public SalesOutlet getSelectedSalesOutlet() {
        return selectedSalesOutlet;
    }

    public long getSalesOutletAttendanceBeginDateUnixSeconds() {
        return salesOutletAttendanceBeginDateUnixSeconds;
    }
}
