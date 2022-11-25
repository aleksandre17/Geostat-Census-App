package com.geostat.census_2024.inter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.esri.arcgisruntime.mapping.view.MapView;
import com.geostat.census_2024.controller.ArcgisController;
import com.geostat.census_2024.data.model.User;
import com.geostat.census_2024.ui.map.model.MapViewModel;

public interface ThatActivity<T extends AppCompatActivity> {

    T init();

    DrawerLayout getDrawerLayout();

    User getUser();

    MapView getMapView();

    ArcgisController getArcgisController();

    MapViewModel getMapViewModel();
}
