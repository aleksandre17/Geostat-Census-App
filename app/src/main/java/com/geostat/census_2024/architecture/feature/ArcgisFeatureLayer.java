package com.geostat.census_2024.architecture.feature;

import com.esri.arcgisruntime.data.FeatureTable;
import com.esri.arcgisruntime.internal.jni.CoreFeatureLayer;
import com.esri.arcgisruntime.internal.jni.CoreRequest;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.layers.Layer;
import com.geostat.census_2024.architecture.inter.ArcgisFeatureInter;

public class ArcgisFeatureLayer extends Layer implements ArcgisFeatureInter {

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
