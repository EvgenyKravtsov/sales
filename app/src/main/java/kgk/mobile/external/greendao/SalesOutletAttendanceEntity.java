package kgk.mobile.external.greendao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@SuppressWarnings("WeakerAccess")
@Entity(
        generateConstructors = true
)
final class SalesOutletAttendanceEntity {

    @Id(autoincrement = true) private Long id;

    private String attendanceJson;
    private boolean isSynchronized;

    ////

    @Generated(hash = 1646053523)
    public SalesOutletAttendanceEntity(Long id, String attendanceJson,
            boolean isSynchronized) {
        this.id = id;
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

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
