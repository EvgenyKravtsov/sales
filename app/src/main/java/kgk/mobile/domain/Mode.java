package kgk.mobile.domain;


public enum Mode {

    Gps, Telephone;

    ////

    public static Mode intToMode(int modeAsInt) {
        switch (modeAsInt) {
            case 0: return Mode.Gps;
            case 1: return Mode.Telephone;
            default: return Mode.Gps;
        }
    }

    public static int modeToInt(Mode mode) {
        switch (mode) {
            case Gps: return 0;
            case Telephone: return 1;
            default: return 0;
        }
    }
}
