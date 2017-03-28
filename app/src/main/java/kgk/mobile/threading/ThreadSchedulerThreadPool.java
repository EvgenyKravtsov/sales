package kgk.mobile.threading;


import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public final class ThreadSchedulerThreadPool implements ThreadScheduler {

    private static final String TAG = ThreadSchedulerThreadPool.class.getSimpleName();

    private Handler mainThreadHandler;
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    ////

    public ThreadSchedulerThreadPool(Context context) {
        this.mainThreadHandler = new Handler(context.getMainLooper());
    }

    //// THREAD SCHEDULER

    @Override
    public void executeBackgroundThread(Runnable runnable) {
        executorService.submit(runnable);
    }

    @Override
    public void executeMainThread(Runnable runnable) {
        mainThreadHandler.post(runnable);
    }
}
