package com.geostat.census_2024.architecture;

import androidx.annotation.NonNull;

import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureCollectionTable;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.FeatureTable;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.GeometryType;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.layers.Layer;
import com.geostat.census_2024.architecture.inter.ArcgisFeatureInter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ArcgisFeatureManager {

    ArcgisFeatureInter arcgisFeatureInter;

    public interface IfFindUserAreaListener {
        void findUserArea(Feature userArea);
    }

    IfFindUserAreaListener userAreaListener;

    public ArcgisFeatureManager(IfFindUserAreaListener userAreaListener) {
        this.userAreaListener = userAreaListener;
    }

    public ArcgisFeatureManager setArcgisFeatureInter(ArcgisFeatureInter arcgisFeatureInter) {
        this.arcgisFeatureInter = arcgisFeatureInter;
        return this;
    }

    public Layer create(FeatureTable table) {
        return arcgisFeatureInter.create(table);
    }

    @NonNull
    public FeatureCollectionTable createFeatureCollectionTable(@NonNull FeatureQueryResult features, Integer userLocation) {

        Feature userFeature;
        Map<String, Feature> featureMap = new HashMap<>();

        FeatureCollectionTable featureCollectionTable = new FeatureCollectionTable(features.getFields(), GeometryType.POLYGON, SpatialReference.create(32638));

        for (Feature feature : features) {
            // feature.setGeometry(GeometryEngine.project(feature.getGeometry(), SpatialReference.create(32638)));
            Feature addedFeature = featureCollectionTable.createFeature(feature.getAttributes(), GeometryEngine.project(feature.getGeometry(), SpatialReference.create(32638)));
            featureMap.put(Objects.toString(feature.getAttributes().get("distr_code")), addedFeature);
        }

        userFeature = featureMap.remove(Objects.toString(userLocation));
        featureCollectionTable.addFeaturesAsync(new ArrayList<Feature>(featureMap.values()));

        if (userAreaListener != null && userFeature != null) { userAreaListener.findUserArea(userFeature);}
        return featureCollectionTable;
    }
}


//    public <E extends ArcgisFeatureInter> ArcgisFeatureInter setArcgisFeatureInter(Class<E> instance) throws IllegalAccessException, InstantiationException {
//        this.arcgisFeatureInter = instance.newInstance();
//        return this.arcgisFeatureInter;
//    }


//            Map<String, Object> attr = feature.getAttributes();
//            Set<String> keys = attr.keySet();
//            for (String key : keys) {
//                Object value = attr.get(key);
//                Log.d("TEST", "run: KEY: " + key + " VALUE: " + value);
//            }






