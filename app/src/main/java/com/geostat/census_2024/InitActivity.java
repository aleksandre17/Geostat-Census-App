package com.geostat.census_2024;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.geostat.census_2024.ui.fileManager.FileActivity;
import com.geostat.census_2024.ui.map.MapActivity;

import java.io.File;

public class InitActivity extends AppCompatActivity {

    Integer map = 1;

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK) {
                map = 3;
                mapActivity();
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(null);
        map = 2;
        if (!new File(getExternalFilesDir(null).getPath() + "/nadzaladevi.gpkg").exists()) {
            fileActivity();
        } else {
            mapActivity();
        }
    }

    public void fileActivity() {
        Intent intent = new Intent(InitActivity.this, FileActivity.class);
        // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activityResultLauncher.launch(intent);
    }

    public void mapActivity() {
        Intent intent = new Intent(InitActivity.this, MapActivity.class);
        // intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (map.equals(2)) {
            // Log.d("TAG", "onResume: ");
            fileActivity();
        }
    }
}