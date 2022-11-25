package com.geostat.census_2024.architecture;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.geostat.census_2024.data.LoginDataSource;
import com.geostat.census_2024.data.LoginRepository;
import com.geostat.census_2024.ui.login.LoginActivity;
import com.geostat.census_2024.utility.SharedPref;

import java.util.Date;

public class App extends Application {

    public final String SYNC_URL = "http://192.168.1.58:8080/addressing";
    public final String LGN_URL = "http://192.168.1.58:8080/users/login";

    Date dif;

    @Override
    public void onCreate() {
        super.onCreate();

        SharedPref.init(getApplicationContext());
        registerActivityLifecycleCallbacks(new appLifecycle());
    }


    class appLifecycle implements Application.ActivityLifecycleCallbacks {

        private Integer numStarted = 0;

        @Override
        public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {

        }

        @Override
        public void onActivityStarted(@NonNull Activity activity) {

            if (numStarted == 0) {
                if (dif != null && (!activity.getLocalClassName().contains("LoginActivity") || !activity.getLocalClassName().contains("FileActivity"))) {
                    if (new Date().getTime() - dif.getTime() >= 3 * 60 * 1000) {

                        activity.finish();

                        LoginRepository.getInstance(new LoginDataSource(App.this)).logout();
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }

                }
                // app went to foreground
                // Log.d("LISTENER", "onActivityStarted: foreground");
            }
            numStarted++;
        }

        @Override
        public void onActivityResumed(@NonNull Activity activity) {
        }

        @Override
        public void onActivityPaused(@NonNull Activity activity) {
        }

        @Override
        public void onActivityStopped(@NonNull Activity activity) {
            numStarted--;
            if (numStarted == 0) {
                dif = new Date();
                // app went to background
                //                Log.d("LISTENER", "onActivityStopped background: ");
                //                LoginRepository.getInstance(new LoginDataSource(App.this)).logout();
            }
        }

        @Override
        public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

        }

        @Override
        public void onActivityDestroyed(@NonNull Activity activity) {

        }
    }
}
