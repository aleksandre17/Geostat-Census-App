package com.geostat.census_2024.architecture.module;

import android.annotation.SuppressLint;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.geostat.census_2024.R;
import com.geostat.census_2024.inter.ThatActivity;

public class LoaderModule {

    boolean isProgressShowing;
    ViewGroup progressView;

    ThatActivity<AppCompatActivity> activity;

    public LoaderModule(ThatActivity<AppCompatActivity> activity) {
        this.activity = activity;
    }

    @SuppressLint("InflateParams")
    public void showProgressingView() {

        if (!isProgressShowing) {
            isProgressShowing = true;
            progressView = (ViewGroup) activity.init().getLayoutInflater().inflate(R.layout.progressbar_layout, null);
            View v = activity.init().findViewById(android.R.id.content).getRootView();
            ViewGroup viewGroup = (ViewGroup) v;
            viewGroup.addView(progressView);
        }
    }

    public void hideProgressingView() {
        View v = activity.init().findViewById(android.R.id.content).getRootView();
        ViewGroup viewGroup = (ViewGroup) v;
        viewGroup.removeView(progressView);
        isProgressShowing = false;
    }
}
