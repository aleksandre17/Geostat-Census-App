package com.geostat.census_2024.architecture.task.map;

import android.annotation.SuppressLint;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.esri.arcgisruntime.arcgisservices.LabelDefinition;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.GeoPackage;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.layers.FeatureCollectionLayer;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.symbology.Symbol;
import com.esri.arcgisruntime.symbology.UniqueValueRenderer;
import com.geostat.census_2024.architecture.inter.ThatActivity;
import com.geostat.census_2024.ui.map.model.MapViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class RunnableVector implements Runnable {

    Integer userLocation;
    GeoPackage geoPackageVector;
    ThatActivity<AppCompatActivity> activity;

    public RunnableVector(Integer userLocation, GeoPackage geoPackageVector, @NonNull ThatActivity<AppCompatActivity> activity) {
        this.userLocation = userLocation;

        this.activity = activity;
        this.geoPackageVector = geoPackageVector;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void run() {

        if (geoPackageVector.getLoadStatus() == LoadStatus.FAILED_TO_LOAD) {
            // String error = "Geopackage failed to load: " + geoPackageVector.getLoadError();
            // Log.e("Geopackage", error);
            return;
        }

        activity.getArcgisController().getMapService().setFeatureLayersNames(geoPackageVector.getGeoPackageFeatureTables());

        geoPackageVector.getGeoPackageFeatureTables().forEach(geoPackageFeatureTable -> {
//                Log.d("TEST", "accept: " + geoPackageFeatureTable.getTableName());
        });

        FeatureLayer line = activity.getArcgisController().getMapService().createLayer("road_cl_napr");
        line.setLabelsEnabled(true);
        line.setRenderer(activity.getArcgisController().getMapService().featureRender("line"));
        LabelDefinition lineLabel = activity.getArcgisController().getMapService().getLabel();
        line.getLabelDefinitions().add(lineLabel);

        FeatureLayer mainFeatureLayer = activity.getArcgisController().getMapService().createLayer("house_point");
        mainFeatureLayer.setRenderer(initMarkers());

        activity.getMapViewModel().setTouchableLayer(mainFeatureLayer, 1);

        ///

        try {
            if (activity.getArcgisController() == null) return;
            FeatureCollectionLayer maskEffectLayer = activity.getArcgisController().getMapService().createLayer(userLocation);
            maskEffectLayer.getFeatureCollection()
                    .getTables().get(0).setRenderer(activity.getArcgisController().getMapService().featureRender("featureCollectionTable"));

            Envelope envelope;
            MapViewModel mapViewModel = activity.getMapViewModel();
            Feature selectedFeature = mapViewModel.getSelectedFeatureFromMapClickedArea();
            if (selectedFeature != null) {
                envelope = selectedFeature.getGeometry().getExtent();
                if (mapViewModel.getTouchableLayer().getValue() != null) mapViewModel.getTouchableLayer().getValue().selectFeature(selectedFeature);
            } else {
                envelope = activity.getMapViewModel().getUserArea().getGeometry().getExtent();
            }

            Viewpoint viewpoint = new Viewpoint(envelope.getCenter(), 3000);
            maskEffectLayer.addDoneLoadingListener(() -> activity.getMapView().setViewpointAsync(viewpoint));

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

//        mainFeatureLayer.addDoneLoadingListener(() ->
//                gisMap.getMapView().setViewpointAsync(new Viewpoint(mainFeatureLayer.getFullExtent()))
//        );

        geoPackageVector = null;
        activity.getMapViewModel().setIsLoadRaster(false);

    }

    private UniqueValueRenderer initMarkers() {

        UniqueValueRenderer uniqueValueRenderer = new UniqueValueRenderer();
        uniqueValueRenderer.getFieldNames().add("status");

        Symbol symbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CROSS, Color.RED, 12);

        uniqueValueRenderer.setDefaultSymbol(symbol);
        uniqueValueRenderer.setDefaultLabel("Other");

        Map<Integer, Integer> uniqueValueRendererMap =
                Map.ofEntries(Map.entry(0, 1), Map.entry(1, 2), Map.entry(2, 3), Map.entry(3, 4), Map.entry(4, 5), Map.entry(5, 7));
        List<SimpleMarkerSymbol.Style> styles =
                List.of(SimpleMarkerSymbol.Style.CROSS, SimpleMarkerSymbol.Style.CROSS, SimpleMarkerSymbol.Style.DIAMOND, SimpleMarkerSymbol.Style.CROSS, SimpleMarkerSymbol.Style.CROSS, SimpleMarkerSymbol.Style.DIAMOND);
        List<Integer> colors =
                List.of(Color.YELLOW, Color.GREEN, Color.RED, Color.BLACK, Color.BLACK, Color.GREEN);

        uniqueValueRendererMap.entrySet().parallelStream().forEach(integerIntegerEntry -> {
            Integer v = integerIntegerEntry.getValue();
            SimpleMarkerSymbol.Style style = styles.get(integerIntegerEntry.getKey());
            Integer color = colors.get(integerIntegerEntry.getKey());

            Symbol californiaFillSymbol = new SimpleMarkerSymbol(style, color, !integerIntegerEntry.getValue().equals(3) ? 12 : 15);
            List<Object> californiaValue = new ArrayList<>();
            californiaValue.add(v);
            uniqueValueRenderer.getUniqueValues().add(new UniqueValueRenderer.UniqueValue("start", "Is start", californiaFillSymbol, californiaValue));
        });

        return uniqueValueRenderer;

    }
}

//    FeatureLayer line = activity.getArcgisController().createLayer("gza");
//        line.setLabelsEnabled(true);
//                line.setRenderer(activity.getArcgisController().featureRender("line"));
//                LabelDefinition lineLabel = activity.getArcgisController().getLabel();
//                line.getLabelDefinitions().add(lineLabel);
//
//                FeatureLayer mainFeatureLayer = activity.getArcgisController().createLayer("shenoba");
//                mainFeatureLayer.setRenderer(activity.getArcgisController().featureRender("cross"));
//
//                activity.getMapViewModel().setTouchableLayer(mainFeatureLayer);