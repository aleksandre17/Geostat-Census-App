package com.geostat.census_2024.architecture.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class AppService extends Service {

    @Override
    public void onTaskRemoved(Intent rootIntent) {

        super.onTaskRemoved(rootIntent);
        //here you will get call when app close.

        Log.d("onTaskRemoved", "onTaskRemoved: ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d("onTaskRemoved", "onTaskRemoved: ");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
