package kgk.mobile.domain;


import java.util.List;

public final class SalesOutletAttendance {

    private final long beginDateUnixSeconds;
    private final long endDateUnixSeconds;
    private final SalesOutlet attendedSalesOutlet;
    private final List<UserOperation> selectedUserOperations;
    private final int addedValue;

    ////

    public SalesOutletAttendance(long beginDateUnixSeconds,
                                 long endDateUnixSeconds,
                                 SalesOutlet attendedSalesOutlet,
                                 List<UserOperation> selectedUserOperations,
                                 int addedValue) {

        this.beginDateUnixSeconds = beginDateUnixSeconds;
        this.endDateUnixSeconds = endDateUnixSeconds;
        this.attendedSalesOutlet = attendedSalesOutlet;
        this.selectedUserOperations = selectedUserOperations;
        this.addedValue = addedValue;
    }


    ////

    public long getBeginDateUnixSeconds() {
        return beginDateUnixSeconds;
    }

    public long getEndDateUnixSeconds() {
        return endDateUnixSeconds;
    }

    public SalesOutlet getAttendedSalesOutlet() {
        return attendedSalesOutlet;
    }

    public List<UserOperation> getSelectedUserOperations() {
        return selectedUserOperations;
    }

    public int getAddedValue() {
        return addedValue;
    }
}
