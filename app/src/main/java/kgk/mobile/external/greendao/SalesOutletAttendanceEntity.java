package kgk.mobile.external.greendao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@SuppressWarnings("WeakerAccess")
@Entity(
        generateConstructors = true
)
final class SalesOutletAttendanceEntity {

    private String attendanceJson;
    private boolean isSynchronized;

    ////

    @Generated(hash = 2130562439)
    public SalesOutletAttendanceEntity(String attendanceJson,
            boolean isSynchronized) {
        this.attendanceJson = attendanceJson;
        this.isSynchronized = isSynchronized;
    }

    @Generated(hash = 489969259)
    public SalesOutletAttendanceEntity() {
    }

    ////

    public String getAttendanceJson() {
        return this.attendanceJson;
    }

    public void setAttendanceJson(String attendanceJson) {
        this.attendanceJson = attendanceJson;
    }

    public boolean getIsSynchronized() {
        return this.isSynchronized;
    }

    public void setIsSynchronized(boolean isSynchronized) {
        this.isSynchronized = isSynchronized;
    }
}
