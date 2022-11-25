package com.geostat.census_2024.architecture.feature;

import com.esri.arcgisruntime.data.FeatureCollection;
import com.esri.arcgisruntime.data.FeatureCollectionTable;
import com.esri.arcgisruntime.data.FeatureTable;
import com.esri.arcgisruntime.internal.jni.CoreFeatureLayer;
import com.esri.arcgisruntime.internal.jni.CoreRequest;
import com.esri.arcgisruntime.layers.FeatureCollectionLayer;
import com.esri.arcgisruntime.layers.Layer;
import com.geostat.census_2024.architecture.inter.ArcgisFeatureInter;

public class ArcgisFeatureCollectionLayer extends Layer implements ArcgisFeatureInter {

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
