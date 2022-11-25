package com.geostat.census_2024.architecture.inter;

import com.esri.arcgisruntime.data.FeatureTable;
import com.esri.arcgisruntime.layers.Layer;

public interface ArcgisFeatureInter {
    Layer create(FeatureTable table);
}
