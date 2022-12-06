package com.geostat.census_2024.archive;

import android.util.Log;

import androidx.annotation.NonNull;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.GeoPackage;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.view.IdentifyLayerResult;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.geostat.census_2024.data.model.LayerModel;
import com.geostat.census_2024.ui.map.model.MapViewModel;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;


public class MapRepository implements com.geostat.census_2024.data.repository.MapRepository {

    @NonNull
    private final MapView mapView;
    @NonNull
    private final String packagePath;

    private GeoPackage geoPackage;

    @NonNull private final MapViewModel mapViewModel;

    public MapRepository(@NonNull MapView mapView, @NonNull MapViewModel mapViewModel, @NonNull String packagePath) {

        this.mapView = mapView;
        this.packagePath = packagePath;
        this.mapViewModel = mapViewModel;
    }

    @NonNull
    @org.jetbrains.annotations.Contract(" -> new")
    private GeoPackage packageInstance() { return new GeoPackage(packagePath); }

    public GeoPackage getGeoPackage () {
        return geoPackage;
    }

    public void initPkg () {
        if (geoPackage == null) {
            // Log.d("LIFE", "initPkg: ");
            geoPackage = packageInstance();
        } else {
            geoPackage = null;
        }
    }

    public IdentifyLayerResult selectFeaturesFromMapTouch(android.graphics.Point screenPoint, double tolerance, boolean returnPopupsOnly, int maximumResults) throws ExecutionException, InterruptedException {
        ListenableFuture<IdentifyLayerResult> result = null;
        if (mapViewModel.getTouchableLayer().getValue() != null) {
            FeatureLayer layer = mapViewModel.getTouchableLayer().getValue();
            result = mapView.identifyLayerAsync(layer, screenPoint, tolerance, returnPopupsOnly, maximumResults);
        }
        return result != null ? result.get() : null;
    }


    @Override
    public void create(Callable<Void> create) throws Exception {
        create.call();
    }

    @Override
    public void store(Point tapPoint, FeatureLayer mainFeatureLayer) {
        Long featureNum = mainFeatureLayer.getFeatureTable().getTotalFeatureCount();

        Map<String, Object> attributes = new HashMap<>();
        Feature feature =  mainFeatureLayer.getFeatureTable().createFeature(attributes, new Point(tapPoint.getX(), tapPoint.getY(), SpatialReference.create(32638)));

        Map<String, Object> attr = (Map<String, Object>) feature.getAttributes();
        LayerModel layerModel = new LayerModel(attr instanceof HashMap ? (HashMap<String, Object>) attr : new HashMap<>(attr));
        layerModel.getProperties().remove("fid");
        feature.getAttributes().putAll(layerModel.getProperties());

        mainFeatureLayer.clearSelection();

        ForeceInsert(featureNum, feature, tapPoint, mainFeatureLayer);
    }

    void ForeceInsert(Long featureNum, Feature feature, Point tapPoint, FeatureLayer mainFeatureLayer) {

        mainFeatureLayer.getFeatureTable().addFeatureAsync(feature).addDoneListener(() -> {
            if (featureNum.equals(mainFeatureLayer.getFeatureTable().getTotalFeatureCount())) {
                ForeceInsert(featureNum, feature, tapPoint, mainFeatureLayer);
            } else {
                Log.d("YEP", "onInsertFeatureClicked: ");
                mapView.setViewpointCenterAsync(tapPoint);
                mainFeatureLayer.selectFeature(feature);
            }
        });
    }

    @Override
    public LayerModel edit(Feature feature) {

        Map<String, Object> attr = (Map<String, Object>) feature.getAttributes();
        LayerModel layerModel = new LayerModel(attr instanceof HashMap ? (HashMap<String, Object>) attr : new HashMap<>(attr));

        for (String key : layerModel.getAll().keySet()) {
            Object value = layerModel.getProperty(key) != null ? layerModel.getProperty(key) : new Object();
            Log.d("TEST", "run: KEY: " + value.getClass().getTypeName() + " VALUE: " + key);
        }

        return layerModel;
    }

    @Override
    public void update(LayerModel layerModel, Callable<Void> callable) {
        if (mapViewModel.getSelectedFeatureFromMapClickedArea() != null && mapViewModel.getTouchableLayer().getValue() != null) {

            Feature selectedFeature = mapViewModel.getSelectedFeatureFromMapClickedArea();
            FeatureLayer touchableLayer = mapViewModel.getTouchableLayer().getValue();

            layerModel.getProperties().remove("fid");
            // selectedFeature.getAttributes().put("status", 1);
            selectedFeature.getAttributes().putAll(layerModel.getProperties());

            touchableLayer.getFeatureTable().updateFeatureAsync(selectedFeature).addDoneListener(() -> {
                try { callable.call(); } catch (Exception e) { e.printStackTrace(); }
            });
        }
    }

    @Override
    public void updateSetNonResident(Feature feature) {

    }

    public void newI() {
        Feature selectedFeature = mapViewModel.getSelectedFeatureFromMapClickedArea();
        if (selectedFeature != null) {
            selectedFeature.getAttributes().put("status", 1);
            FeatureLayer touchableLayer = mapViewModel.getTouchableLayer().getValue();
            touchableLayer.getFeatureTable().updateFeatureAsync(selectedFeature).addDoneListener(new Runnable() {
                @Override
                public void run() {

                }
            });
        }
    }

    @Override
    public ListenableFuture<Void> waitDestroyPermission(Feature feature, FeatureLayer mainFeatureLayer) {
        return mainFeatureLayer.getFeatureTable().deleteFeatureAsync(feature);
    }
}
