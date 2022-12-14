package com.geostat.census_2024.data.repository;

import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.geostat.census_2024.data.model.LayerModel;

import java.util.concurrent.Callable;

public interface MapRepository {

    void create(Callable<Void> create) throws Exception;
    void store(Point tapPoint, FeatureLayer mainFeatureLayer);
    LayerModel edit(Feature feature);
    void update(LayerModel layerModel, Callable<Void> callable);
    void updateSetNonResident(Feature feature);
    com.esri.arcgisruntime.concurrent.ListenableFuture<java.lang.Void> waitDestroyPermission(Feature feature, FeatureLayer mainFeatureLayer);

}
