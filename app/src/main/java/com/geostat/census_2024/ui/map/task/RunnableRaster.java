package com.geostat.census_2024.ui.map.task;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.esri.arcgisruntime.data.GeoPackage;
import com.esri.arcgisruntime.layers.RasterLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.Basemap;
import com.geostat.census_2024.inter.ThatActivity;

public class RunnableRaster implements Runnable {

    public interface IfRasterLoadListener {
        void rasterLoad(Basemap baseMap);
    }

    GeoPackage geoPackage;
    IfRasterLoadListener loadListener;
    ThatActivity<AppCompatActivity> activity;

    public RunnableRaster(GeoPackage geoPackage, @NonNull ThatActivity<AppCompatActivity> activity) {
        this.geoPackage = geoPackage;
        this.loadListener = (IfRasterLoadListener) activity;
        this.activity = activity;
    }

    @Override
    public void run() {
        // activity.getMapView().getMap().getOperationalLayers().stream().filter(layer -> layer.getName().equals(layerName)).findFirst().orElse(null);
        Basemap basemap = new Basemap();
        if (activity.getMapView().getMap().getLoadStatus() != LoadStatus.LOADED) {
            RasterLayer rasterLayer = new RasterLayer(geoPackage.getGeoPackageRasters().get(0));
            basemap.getBaseLayers().add(rasterLayer);
            if (loadListener != null) { loadListener.rasterLoad(basemap); geoPackage = null; activity.getMapViewModel().setIsLoadPkg(false); }
        }

    }
}


//        Log.d("TAG", "run: ");
//        Basemap basemap = new Basemap();
//
//        mapView.getMap().getOperationalLayers().clear();
//        mapView.getMap().getBasemap().getBaseLayers().clear();
//        // mapView.getMap().getBasemap().getBaseLayers().get(0)
//
//        if (mapView.getMap().getLoadStatus() != LoadStatus.LOADED) {
//        Log.d("TAG", "run: " + geoPackage.getGeoPackageRasters().get(0));
//
//        if (Objects.equals(mapView.getMap().getBasemap().getBaseLayers().size(), 0)) {
//        Log.d("TAG", "rasterLayer: " + mapView.getMap().getBasemap().getBaseLayers().size());
//        RasterLayer rasterLayer = new RasterLayer(geoPackage.getGeoPackageRasters().get(0));
//
//        // basemap = new Basemap(rasterLayer);
//        basemap.getBaseLayers().add(rasterLayer);
//
//        mapView.getMap().setBasemap(basemap);
//
//        Log.d("TRL", "run: " +  mapView.getMap().getBasemap().getBaseLayers().get(0));
//        }
//        } else {
//        mapView.getMap().getBasemap().getBaseLayers().remove(0);
//        }
//
//        geoPackageVector.addDoneLoadingListener(this.runnableVector);
