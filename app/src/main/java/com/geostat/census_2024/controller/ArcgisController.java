package com.geostat.census_2024.controller;

import android.graphics.Color;
import android.os.FileUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.esri.arcgisruntime.arcgisservices.LabelDefinition;
import com.esri.arcgisruntime.arcgisservices.LabelingPlacement;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureCollectionTable;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.FeatureTable;
import com.esri.arcgisruntime.data.GeoPackage;
import com.esri.arcgisruntime.data.GeoPackageFeatureTable;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.layers.FeatureCollectionLayer;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.layers.Layer;
import com.esri.arcgisruntime.mapping.labeling.ArcadeLabelExpression;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.symbology.Renderer;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleRenderer;
import com.esri.arcgisruntime.symbology.Symbol;
import com.esri.arcgisruntime.symbology.TextSymbol;
import com.geostat.census_2024.R;
import com.geostat.census_2024.architecture.ArcgisFeatureManager;
import com.geostat.census_2024.architecture.feature.ArcgisFeatureCollectionLayer;
import com.geostat.census_2024.architecture.feature.ArcgisFeatureLayer;
import com.geostat.census_2024.architecture.inter.ArcgisFeatureInter;
import com.geostat.census_2024.data.model.LayerModel;
import com.geostat.census_2024.data.repository.MapRepository;
import com.geostat.census_2024.inter.ThatActivity;
import com.geostat.census_2024.ui.map.model.MapViewModel;
import com.tapadoo.alerter.Alerter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

public class ArcgisController implements IMapController {

    private static volatile ArcgisController instance;

    private GeoPackage geoPackage;
    private static String geoPackagePath;

    private final MapRepository mapRepository;
    private ArcgisFeatureManager featureManager;

    public GraphicsOverlay graphicsOverlay;
    public List<GeoPackageFeatureTable> featureLayersNames;

    private final ThatActivity<AppCompatActivity> activity;

    public static void setPackagePath(String path) { geoPackagePath = path; }

    public static ArcgisController getInstance(ThatActivity<AppCompatActivity> thatActivity)
    {
        // Log.d("LIFE", "ArcgisController: ");
        if (instance == null) { synchronized (ArcgisController.class) { if( instance == null ) { instance = new ArcgisController(thatActivity); } } }
        return instance;
    }

    public void initPackage() {
        if (geoPackage == null) {
            geoPackage = mapRepository.initPkg();
        }
    }

    public void destroyPackage () { geoPackage = null; }

    public GeoPackage getGeoPackage() { return geoPackage; }

    private ArcgisController(@NonNull ThatActivity<AppCompatActivity> activity)
    {

        this.activity = activity;

        MapViewModel mapViewModel = activity.getMapViewModel();
        mapRepository = new MapRepository(activity.getMapView(), mapViewModel, geoPackagePath);

        // this.featureManager = new ArcgisFeatureManager();
        graphicsOverlay = new GraphicsOverlay();
        activity.getMapView().getGraphicsOverlays().add(graphicsOverlay);
    }

    public MapRepository getMapRepository () { return mapRepository; }

    public List<GeoPackageFeatureTable> getFeatureLayersNames() { return featureLayersNames; }

    public void setFeatureLayersNames(List<GeoPackageFeatureTable> featureLayersNames) { this.featureLayersNames = featureLayersNames; }

    public FeatureTable findFeatureByName(String name) {
        return geoPackage
                .getGeoPackageFeatureTables()
                .stream()
                .filter(geoPackageFeatureTable -> geoPackageFeatureTable.getTableName().equals(name)).findFirst().orElse(null);
    }

    public ListenableFuture<FeatureQueryResult> queryResult(String tableName, String query) {
        //create query parameters
        QueryParameters queryParams = new QueryParameters();

        // 1=1 will give all the features from the table
        queryParams.setWhereClause(query);

        //query feature from the table
        return findFeatureByName(tableName).queryFeaturesAsync(queryParams);
    }

    private Layer initFeatureLayer(String layerName, FeatureTable table) {
        Layer findLayer;
        if (activity.getMapView().getMap() == null) findLayer = null;
        else findLayer = activity.getMapView().getMap().getOperationalLayers().stream().filter(layer -> layer.getName().equals(layerName)).findFirst().orElse(null);

        if (findLayer == null) {
            ArcgisFeatureInter featureInter = new ArcgisFeatureLayer();
            if (table instanceof FeatureCollectionTable) {
                featureInter = new ArcgisFeatureCollectionLayer();
            }
            findLayer = featureManager.setArcgisFeatureInter(featureInter).create(table);
            if (activity.getMapView().getMap() != null) activity.getMapView().getMap().getOperationalLayers().add(findLayer);
        }

        return findLayer;
    }

    public FeatureLayer createLayer(String name) {
        return (FeatureLayer) initFeatureLayer(name, findFeatureByName(name));
    }


    public FeatureCollectionLayer createLayer(Integer userLocation) throws ExecutionException, InterruptedException {
        FeatureQueryResult select = queryResult("saregistracio", "1=1").get();
        return (FeatureCollectionLayer) initFeatureLayer("saregistracio", featureManager.createFeatureCollectionTable(select, userLocation));
    }

    public Renderer featureRender (@Nullable String type) {
        Symbol symbol;
        assert type != null;
        if (type.equals("line")) {
            symbol = new SimpleLineSymbol(SimpleLineSymbol.Style.DASH, 0x7F00FF00, 2);
        } else if (type.equals("cross")) {
            symbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CROSS, Color.RED, 12);
        } else {
            SimpleLineSymbol lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, 0xFF0000FF, 2);
            symbol = new SimpleFillSymbol(SimpleFillSymbol.Style.SOLID, -0x5fa8a8cd, lineSymbol);
        }
        return new SimpleRenderer(symbol);
    }

    public LabelDefinition getLabel() {
        // create text symbol for styling the label
        TextSymbol textSymbol = new TextSymbol();
        textSymbol.setSize(12);
        textSymbol.setColor(Color.WHITE);
        textSymbol.setFontWeight(TextSymbol.FontWeight.BOLD);


        // create a label definition with an Arcade expression script
        ArcadeLabelExpression arcadeLabelExpression = new ArcadeLabelExpression("$feature.NAMN1");
        LabelDefinition labelDefinition = new LabelDefinition(arcadeLabelExpression, textSymbol);
        labelDefinition.setPlacement(LabelingPlacement.LINE_CENTER_ALONG);
        labelDefinition.setMinScale(10000);
        labelDefinition.setWhereClause("NAMN1 <> 'N_A' And NAMN1 <> 'UNK'");

        return labelDefinition;
    }

    public Feature findRollbackQuestionnaireLocation (String houseCode, MapViewModel mapViewModel) {
        Feature feature = null;
        Map<String, Object> areaAttributes = mapViewModel.getUserArea().getAttributes();
        ListenableFuture<FeatureQueryResult> select = queryResult("house_point", "house_code = '" + houseCode + "' AND distr_id = '" + areaAttributes.get("distr_id") + "' AND instr_id = '" + areaAttributes.get("instr_id") + "'");

        try {
            FeatureQueryResult featureQueryResult = select.get();
            if (featureQueryResult.iterator().next() != null) {
                feature = featureQueryResult.iterator().next();
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            Alerter.create(activity.init())
                    .setTitle("შეტყობინება").setBackgroundColorRes(R.color.yellow).setText("შენობა ვერ მოიძებნა").setDuration(5000).show();
        }
        return feature;
    }


    /// CRUD

    @Override
    public void create(Callable<Void> create) throws Exception {
        mapRepository.create(create);
    }

    @Override
    public void store(Point tapPoint, FeatureLayer mainFeatureLayer) {
        mapRepository.store(tapPoint, mainFeatureLayer);
    }

    @Override
    public LayerModel edit(Feature feature){
        return mapRepository.edit(feature);
    }



    @Override
    public void update(LayerModel layerModel, Callable<Void> callable) {
        mapRepository.update(layerModel, callable);
    }

    @Override
    public void updateSetNonResident(Feature feature) {
        mapRepository.updateSetNonResident(feature);
    }

    @Override
    public com.esri.arcgisruntime.concurrent.ListenableFuture<java.lang.Void> destroy(Feature feature, FeatureLayer mainFeatureLayer) {
        return mapRepository.destroy(feature, mainFeatureLayer);
    }

    ///

    public interface TryUpdateMap {
        void updateMap();
    }

    public void copyFile(File source, File destination) throws IOException {
        FileUtils.copy(new FileInputStream(source), new FileOutputStream(destination));
    }

    public ArcgisFeatureManager getFeatureManager() {
        return this.featureManager;
    }

    public void setFeatureManager (ArcgisFeatureManager manager) {
        this.featureManager = manager;
    }

    public void clear() {
        destroyPackage();
        instance = null;
    }


}
