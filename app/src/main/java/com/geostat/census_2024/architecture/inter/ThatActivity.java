package com.geostat.census_2024.architecture.inter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.esri.arcgisruntime.mapping.view.MapView;
import com.geostat.census_2024.architecture.manager.ArcgisManager;
import com.geostat.census_2024.data.model.UserModel;
import com.geostat.census_2024.ui.map.model.MapViewModel;

public interface ThatActivity<T extends AppCompatActivity> {

    T init();

    DrawerLayout getDrawerLayout();

    UserModel getUser();

    MapView getMapView();

    ArcgisManager getArcgisController();

    MapViewModel getMapViewModel();
}
