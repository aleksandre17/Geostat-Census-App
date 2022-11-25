package com.geostat.census_2024.ui.map.event;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.geostat.census_2024.R;
import com.geostat.census_2024.inter.ThatActivity;
import com.geostat.census_2024.ui.map.widjet.Callout;

public class ListenDrawer implements DrawerLayout.DrawerListener {

    private final ThatActivity<AppCompatActivity> thatActivity;

    public ListenDrawer(ThatActivity<AppCompatActivity> thatActivity) {
        this.thatActivity = thatActivity;
    }

    @Override
    public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

    }

    @Override
    public void onDrawerOpened(@NonNull View drawerView) {

    }

    @Override
    public void onDrawerClosed(@NonNull View drawerView) {

        if (((AppCompatActivity) thatActivity).getSupportFragmentManager().getBackStackEntryCount() > 0) {
            ((AppCompatActivity) thatActivity).getSupportFragmentManager().popBackStack();
        }

        Fragment fr1 = ((AppCompatActivity) thatActivity).getSupportFragmentManager().findFragmentById(R.id.test_frame);
        Fragment fr = ((AppCompatActivity) thatActivity).getSupportFragmentManager().findFragmentById(R.id.setting);

        if (fr1 != null) {
            ((AppCompatActivity) thatActivity).getSupportFragmentManager().beginTransaction().remove(fr1).commit();
            Callout.exit();
        } else if (fr != null) {
            ((AppCompatActivity) thatActivity).getSupportFragmentManager().beginTransaction().remove(fr).commit();
        }
    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }
}
