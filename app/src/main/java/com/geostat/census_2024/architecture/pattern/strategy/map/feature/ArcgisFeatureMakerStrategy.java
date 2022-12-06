package com.geostat.census_2024.architecture.pattern.strategy.map.feature;

import com.esri.arcgisruntime.data.FeatureTable;
import com.esri.arcgisruntime.layers.Layer;

public interface ArcgisFeatureMakerStrategy {
    Layer create(FeatureTable table);
}
