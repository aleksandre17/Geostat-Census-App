package com.geostat.census_2024.architecture.task;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class AsyncTask<T extends AppCompatActivity> {

    private final ExecutorService executorService;

    public AsyncTask() {
        this.executorService = Executors.newFixedThreadPool(2);
    }

    private void startService(T activity) throws IOException {
        first();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                taskRun();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        taskEnd();
                    }
                });
            }
        });
    }

    public void execute(T activity) throws IOException {
        startService(activity);
    }

    public abstract void first() throws IOException;
    public abstract void taskRun();
    public abstract void taskEnd();
}












