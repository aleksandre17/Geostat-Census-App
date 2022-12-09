package com.geostat.census_2024;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.geostat.census_2024.data.LoginDataSource;
import com.geostat.census_2024.data.repository.LoginRepository;
import com.geostat.census_2024.architecture.inter.ThatActivity;
import com.geostat.census_2024.architecture.service.SyncService;
import com.geostat.census_2024.ui.login.LoginActivity;
import com.geostat.census_2024.ui.map.event.ListenDrawer;
import com.geostat.census_2024.ui.rollback.RollbackActivity;
import com.geostat.census_2024.ui.settings.SettingsFragment;
import com.geostat.census_2024.utility.SharedPref;

public abstract class IndexActivity extends AppCompatActivity {

    protected DrawerLayout drawerLayout;

    int PERMISSION_ALL = 1;

    String[] PERMISSIONS = {
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isLoggedIn();
        setContentView(getLayoutResource());
        _lockOrientation();

        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

        ArcGISRuntimeEnvironment.setApiKey("AAPK2f8df106b30b4ac49addfb588f6c022aZhAODDJtnwbqR5KxedVZiRpgSBNQkTi8YTckMtTtFCE6v-FQUsd4LfX4P--9dOdV");

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.addDrawerListener(new ListenDrawer(getActivity()));
//        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.setDrawerIndicatorEnabled(false);
        toggle.syncState();
    }

    private void isLoggedIn () {
        if(!SharedPref.read("isLoggedIn", false)) {
            Intent intent = new Intent(getActivity().init(), LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    public void _lockOrientation() {
        if (super.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT);
        } else {
            super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE);
        }
    }

    public void _unlockOrientation() {
        super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    public static boolean hasPermissions(AppCompatActivity app, String... permissions) {
        if (app != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(app, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }



    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        if (menu instanceof MenuBuilder){
            ((MenuBuilder) menu).setOptionalIconsVisible(true);
        }

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.settings) {
            if(!drawerLayout.isDrawerOpen(GravityCompat.END)) {
                drawerLayout.openDrawer(GravityCompat.END);
                getSupportFragmentManager().beginTransaction().add(R.id.setting, SettingsFragment.newInstance()).commit();
            }
            return true;
        } else if (item.getItemId() == R.id.exit) {

            logout();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            finishAffinity();
            startActivity(intent);

        } else if (item.getItemId() == R.id.needEnd) {
            Intent intent = new Intent(getActivity().init(), RollbackActivity.class);
            // startActivity(intent);
            getActivityResultLauncher().launch(intent);
        } else if (item.getItemId() == R.id.action_settings) {
            // HousesSentService.getInstance(getActivity()).sync();
            SyncService.getInstance(getActivity()).sync();
        }

        return super.onOptionsItemSelected(item);

    }

    public void logout () {
        LoginRepository.getInstance(new LoginDataSource(getApplication())).logout();
    }


    public abstract ThatActivity<AppCompatActivity> getActivity();
    public abstract int getLayoutResource();
    public abstract ActivityResultLauncher<Intent>  getActivityResultLauncher();

}
