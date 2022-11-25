package com.geostat.census_2024.data.repository;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.annotation.NonNull;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.FeatureTable;
import com.esri.arcgisruntime.data.GeoPackage;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.view.IdentifyLayerResult;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.geostat.census_2024.controller.IMapController;
import com.geostat.census_2024.data.model.LayerModel;
import com.geostat.census_2024.ui.map.model.MapViewModel;
import com.geostat.census_2024.ui.map.model.UserAreaModel;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;


public class MapRepository implements IMapController {

    @NonNull
    private final MapView mapView;
    @NonNull
    private final String packagePath;

    @NonNull private final MapViewModel mapViewModel;

    public MapRepository(@NonNull MapView mapView, @NonNull MapViewModel mapViewModel, @NonNull String packagePath) {
        this.mapView = mapView;
        this.packagePath = packagePath;
        this.mapViewModel = mapViewModel;
    }

    @NonNull
    @org.jetbrains.annotations.Contract(" -> new")
    private GeoPackage packageInstance() { return new GeoPackage(packagePath); }

    public GeoPackage initPkg () { return packageInstance(); }

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

    public Feature getLastFeature(FeatureQueryResult queryResult) {
        Iterator<Feature> iterable = queryResult.iterator();
        Feature lastElm = iterable.next();
        while (iterable.hasNext()) {
            lastElm = iterable.next();
        }
        return lastElm;
    }

    public Feature query(FeatureTable featureTable) throws ExecutionException, InterruptedException {
        UserAreaModel areaModel = mapViewModel.getUserAreaModel();

        QueryParameters query = new QueryParameters();
        query.setWhereClause("region_id = " + areaModel.getRegion_id() + " AND munic_id = " + areaModel.getMunic_id() + " AND distr_id = " + areaModel.getDistr_id() + " AND instr_id = '" + areaModel.getInstr_id() + "'");
        FeatureQueryResult featureQueryResult = featureTable.queryFeaturesAsync(query).get();

        return getLastFeature(featureQueryResult);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> generateHouseAttributes(Feature lastR) {

        Gson gson = new Gson();
        Map<String, Object> res = gson.fromJson(gson.toJson(mapViewModel.getUserAreaModel(), UserAreaModel.class), Map.class);
        res.remove("distr_code");

        int integer = Integer.parseInt(lastR.getAttributes().get("house_id").toString());

        @SuppressLint("DefaultLocale") String house =  String.format("%04d", integer + 1);
        String houseId = Objects.requireNonNull(res.get("region_id")).toString() + Objects.requireNonNull(res.get("munic_id")).toString() + Objects.requireNonNull(res.get("instr_id")).toString() + Objects.requireNonNull(res.get("distr_id")).toString() + house;

        res.put("house_id", house);
        res.put("house_code", houseId);
        res.put("status", 0);
        res.put("sacx_stat", 0);
        return res;
    }

    @Override
    public void store(Point tapPoint, FeatureLayer mainFeatureLayer) {

        try {
            Feature lastR = query(mainFeatureLayer.getFeatureTable());

            Feature feature =  mainFeatureLayer.getFeatureTable().createFeature(new HashMap<>(), new Point(tapPoint.getX(), tapPoint.getY(), SpatialReference.create(32638)));
            feature.getAttributes().putAll(generateHouseAttributes(lastR));

            mainFeatureLayer.clearSelection();
            ForeceInsert(mainFeatureLayer.getFeatureTable().getTotalFeatureCount(), feature, tapPoint, mainFeatureLayer);

        } catch (ExecutionException | InterruptedException e) {
            e.getMessage();
        }
    }

    void ForeceInsert(Long featureNum, Feature feature, Point tapPoint, FeatureLayer mainFeatureLayer) {

        mainFeatureLayer.getFeatureTable().addFeatureAsync(feature).addDoneListener(() -> {
            if (featureNum.equals(mainFeatureLayer.getFeatureTable().getTotalFeatureCount())) {
                ForeceInsert(featureNum, feature, tapPoint, mainFeatureLayer);
            } else {
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

        if (mapViewModel.getSelectedFeatureFromMapClickedArea() != null && mapViewModel.getTouchableLayer().getValue() != null) {
            FeatureLayer touchableLayer = mapViewModel.getTouchableLayer().getValue();

            if (Objects.equals(feature.getAttributes().get("sacx_stat"), 1)) {
                feature.getAttributes().put("sacx_stat", 0);
                feature.getAttributes().put("status", 0);
            } else {
                feature.getAttributes().put("sacx_stat", 1);
                feature.getAttributes().put("status", 7);
            }

            touchableLayer.getFeatureTable().updateFeatureAsync(feature).addDoneListener(() -> {});
        }
    }

    public void updateHouseStatus(Integer status) {
        Feature selectedFeature = mapViewModel.getSelectedFeatureFromMapClickedArea();
        if (selectedFeature != null) {
            selectedFeature.getAttributes().put("status", status);
            FeatureLayer touchableLayer = mapViewModel.getTouchableLayer().getValue();
            touchableLayer.getFeatureTable().updateFeatureAsync(selectedFeature).addDoneListener(() -> {});
        }
    }

    public Feature updateHouseStatus(Integer status, int reset) {
        Feature selectedFeature = mapViewModel.getSelectedFeatureFromMapClickedArea();
        if (selectedFeature != null) {
            selectedFeature.getAttributes().put("status", status);
            FeatureLayer touchableLayer = mapViewModel.getTouchableLayer().getValue();
            touchableLayer.getFeatureTable().updateFeatureAsync(selectedFeature).addDoneListener(() -> {});
        }
        return selectedFeature;
    }

    @Override
    public ListenableFuture<Void> destroy(Feature feature, FeatureLayer mainFeatureLayer) {
        // mainFeatureLayer.getFeatureTable().deleteFeatureAsync(feature);
        FeatureLayer touchableLayer = mapViewModel.getTouchableLayer().getValue();
        feature.getAttributes().put("status", 4);
        touchableLayer.getFeatureTable().updateFeatureAsync(feature).addDoneListener(() -> {});

        return touchableLayer.getFeatureTable().updateFeatureAsync(feature);
    }
}


//            Map<String, Object> attr = (Map<String, Object>) feature.getAttributes();
//        LayerModel layerModel = new LayerModel(attr instanceof HashMap ? (HashMap<String, Object>) attr : new HashMap<>(attr));
//        layerModel.getProperties().remove("fid");