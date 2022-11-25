package com.geostat.census_2024.ui.map.custom;

import com.esri.arcgisruntime.internal.jni.CoreFeature;

public class Feature extends com.esri.arcgisruntime.data.Feature {

    protected Feature(CoreFeature coreFeature) {
        super(coreFeature);
    }
}
