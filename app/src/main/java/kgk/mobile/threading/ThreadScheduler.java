package kgk.mobile.threading;


public interface ThreadScheduler {

    void executeBackgroundThread(Runnable runnable);

    void executeMainThread(Runnable runnable);
}
