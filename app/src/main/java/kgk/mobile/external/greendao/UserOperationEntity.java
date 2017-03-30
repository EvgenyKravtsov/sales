package kgk.mobile.external.greendao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;

@SuppressWarnings("WeakerAccess")
@Entity(
        generateConstructors = true
)
final class UserOperationEntity {

    private int id;
    private String title;

    ////

    @Generated(hash = 879045542)
    public UserOperationEntity(int id, String title) {
        this.id = id;
        this.title = title;
    }

    @Generated(hash = 5394749)
    public UserOperationEntity() {
    }

    ////

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
