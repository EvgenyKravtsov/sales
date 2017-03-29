package kgk.mobile.domain;


public final class UserOperation {

    private final int id;
    private final String title;

    ////

    public UserOperation(int id, String title) {
        this.id = id;
        this.title = title;
    }

    ////

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }
}
