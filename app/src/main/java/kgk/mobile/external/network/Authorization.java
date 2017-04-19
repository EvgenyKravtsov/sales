package kgk.mobile.external.network;


public final class Authorization {

    private final boolean authorized;

    ////

    public Authorization(boolean authorized) {
        this.authorized = authorized;
    }

    ////

    public boolean isAuthorized() {
        return authorized;
    }
}
