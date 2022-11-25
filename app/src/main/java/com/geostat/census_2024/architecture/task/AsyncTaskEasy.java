package com.geostat.census_2024.architecture.task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsyncTaskEasy {

    private final ExecutorService executorService;

    public AsyncTaskEasy() {
        this.executorService = Executors.newFixedThreadPool(1);
    }

    private void startService(Runnable runnable) {
        executorService.execute(runnable);
    }

    public void execute(Runnable runnable) {
        startService(runnable);
    }
}
