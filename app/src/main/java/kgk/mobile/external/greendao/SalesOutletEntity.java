package kgk.mobile.external.greendao;


import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;

@Entity(
        generateConstructors = true
)
final class SalesOutletEntity {

    private double latitude;
    private double longitude;
    private String code;

    ////

    @Generated(hash = 1983341784)
    public SalesOutletEntity(double latitude, double longitude, String code) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.code = code;
    }
    @Generated(hash = 1720227137)
    public SalesOutletEntity() {
    }

    ////

    public double getLatitude() {
    return this.latitude;
}

    public void setLatitude(double latitude) {
    this.latitude = latitude;
}

    public double getLongitude() {
    return this.longitude;
}

    public void setLongitude(double longitude) {
    this.longitude = longitude;
}

    public String getCode() {
    return this.code;
}

    public void setCode(String code) {
    this.code = code;
}
}
