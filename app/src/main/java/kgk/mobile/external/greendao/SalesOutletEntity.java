package kgk.mobile.external.greendao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;

@SuppressWarnings("WeakerAccess")
@Entity(
        generateConstructors = true
)
final class SalesOutletEntity {

    private int id;
    private double latitude;
    private double longitude;
    private String code;
    private String title;

    ////

    @Generated(hash = 1084584881)
    public SalesOutletEntity(int id, double latitude, double longitude, String code,
            String title) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.code = code;
        this.title = title;
    }

    @Generated(hash = 1720227137)
    public SalesOutletEntity() {
    }

    

    ////

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    double getLatitude() {
    return this.latitude;
}

    void setLatitude(double latitude) {
    this.latitude = latitude;
}

    double getLongitude() {
    return this.longitude;
}

    void setLongitude(double longitude) {
    this.longitude = longitude;
}

    String getCode() {
    return this.code;
}

    void setCode(String code) {
    this.code = code;
}

    String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
