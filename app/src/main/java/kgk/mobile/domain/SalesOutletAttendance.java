package kgk.mobile.domain;


import java.util.List;

public final class SalesOutletAttendance {

    private final long beginDateUnixSeconds;
    private final long endDateUnixSeconds;
    private final SalesOutlet attendedSalesOutlet;
    private final List<UserOperation> selectedUserOperations;
    private final int addedValue;
    private final Mode mode;

    ////

    public SalesOutletAttendance(long beginDateUnixSeconds,
                                 long endDateUnixSeconds,
                                 SalesOutlet attendedSalesOutlet,
                                 List<UserOperation> selectedUserOperations,
                                 int addedValue,
                                 Mode mode) {

        this.beginDateUnixSeconds = beginDateUnixSeconds;
        this.endDateUnixSeconds = endDateUnixSeconds;
        this.attendedSalesOutlet = attendedSalesOutlet;
        this.selectedUserOperations = selectedUserOperations;
        this.addedValue = addedValue;
        this.mode = mode;
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

    public Mode getMode() {
        return mode;
    }
}
