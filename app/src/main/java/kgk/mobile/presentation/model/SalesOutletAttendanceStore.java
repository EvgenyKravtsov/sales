package kgk.mobile.presentation.model;


import kgk.mobile.domain.SalesOutletAttendance;

public interface SalesOutletAttendanceStore {

    void put(SalesOutletAttendance attendance);

    SalesOutletAttendanceBegin getSalesOutletAttendanceBegin();

    void setSalesOutletAttendanceBegin(SalesOutletAttendanceBegin salesOutletAttendanceBegin);
}
