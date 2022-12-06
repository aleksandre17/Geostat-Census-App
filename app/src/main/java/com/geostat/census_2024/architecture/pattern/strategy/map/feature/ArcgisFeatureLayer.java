package com.geostat.census_2024.architecture.pattern.strategy.map.feature;

import com.esri.arcgisruntime.data.FeatureTable;
import com.esri.arcgisruntime.internal.jni.CoreFeatureLayer;
import com.esri.arcgisruntime.internal.jni.CoreRequest;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.layers.Layer;

public class ArcgisFeatureLayer extends Layer implements ArcgisFeatureMakerStrategy {

    public ArcgisFeatureLayer() {
        super(new CoreFeatureLayer());
    }

    @Override
    protected void onRequestRequired(CoreRequest coreRequest) {

    }

    public Layer create(FeatureTable table) {
        return new FeatureLayer(table);
    }
}
