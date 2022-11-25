package com.geostat.census_2024.ui.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.geostat.census_2024.R;
import com.geostat.census_2024.inter.ThatActivity;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    private ThatActivity<AppCompatActivity> activity;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
//        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

    }

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("PAUSE", "onPause: ");
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroy() {
        SwitchPreferenceCompat switchPreferenceCompat = findPreference("sync");
        if (switchPreferenceCompat != null) {
            switchPreferenceCompat.setChecked(false);
        }

        SwitchPreferenceCompat switchPreferenceCompat2 = findPreference("initLocation");
        if (switchPreferenceCompat2 != null) {
            switchPreferenceCompat2.setChecked(false);
        }

        super.onDestroy();
    }

    @Override
    public boolean onPreferenceTreeClick(@NonNull Preference preference) {
        return super.onPreferenceTreeClick(preference);
    }

    @SuppressWarnings("unchecked")
    public void onAttach(@NonNull Context activity) {
        super.onAttach(activity);
        if (activity instanceof ThatActivity) {
            this.activity = (ThatActivity<AppCompatActivity>) activity;
        } else {
            throw new RuntimeException(activity.toString() + "must implement ThatActivity<AppCompatActivity>");
        }
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d("TAG", "onSharedPreferenceChanged: ");
        if (key.equals("sync")) {
            SwitchPreferenceCompat switchPreferenceCompat = findPreference(key);
            if (switchPreferenceCompat != null) {
                if (switchPreferenceCompat.isChecked()) {
                    LocationDisplay locationDisplay = activity.getMapView().getLocationDisplay();

                    locationDisplay.setShowLocation(true);
                    locationDisplay.setShowAccuracy(true);
                    locationDisplay.setShowPingAnimation(true);
                    locationDisplay.setUseCourseSymbolOnMovement(true);
                    locationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.COMPASS_NAVIGATION);
                    locationDisplay.startAsync();

                } else if (!switchPreferenceCompat.isChecked()){
                    activity.getMapView().getLocationDisplay().stop();
                }
            }
        }

        if (key.equals("initLocation")) {
            SwitchPreferenceCompat switchPreferenceCompat = findPreference(key);
            if (switchPreferenceCompat != null) {
                if (switchPreferenceCompat.isChecked()) {

                    Point r;
                    if (activity.getMapViewModel().getSelectedFeatureFromMapClickedArea() != null) {
                        r = activity.getMapViewModel().getMapClickedArea().getExtent().getCenter();
                    } else {
                        r = activity.getMapViewModel().getUserArea().getGeometry().getExtent().getCenter();
                    }

                    activity.getMapView().setViewpointAsync(new Viewpoint(r, 3000));

                } else if (!switchPreferenceCompat.isChecked()){

                }
            }
        }
    }
}