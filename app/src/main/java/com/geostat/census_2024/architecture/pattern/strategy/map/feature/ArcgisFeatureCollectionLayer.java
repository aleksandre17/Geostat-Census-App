package com.geostat.census_2024.architecture.pattern.strategy.map.feature;

import com.esri.arcgisruntime.data.FeatureCollection;
import com.esri.arcgisruntime.data.FeatureCollectionTable;
import com.esri.arcgisruntime.data.FeatureTable;
import com.esri.arcgisruntime.internal.jni.CoreFeatureLayer;
import com.esri.arcgisruntime.internal.jni.CoreRequest;
import com.esri.arcgisruntime.layers.FeatureCollectionLayer;
import com.esri.arcgisruntime.layers.Layer;

public class ArcgisFeatureCollectionLayer extends Layer implements ArcgisFeatureMakerStrategy {

    public ArcgisFeatureCollectionLayer() {
        super(new CoreFeatureLayer());
    }

    @Override
    protected void onRequestRequired(CoreRequest coreRequest) {

    }

    public Layer create(FeatureTable table) {
        FeatureCollectionTable featureCollectionTable = (FeatureCollectionTable) table;
        FeatureCollection featureCollection = new FeatureCollection();
        featureCollection.getTables().add(featureCollectionTable);

        return new FeatureCollectionLayer(featureCollection);
    }
}
