package com.geostat.census_2024.ui.map;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;

import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.GeoElement;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.geostat.census_2024.IndexActivity;
import com.geostat.census_2024.R;
import com.geostat.census_2024.architecture.manager.ArcgisManager;
import com.geostat.census_2024.architecture.task.AsyncTask;
import com.geostat.census_2024.architecture.task.AsyncTaskEasy;
import com.geostat.census_2024.data.repository.MapRepository;
import com.geostat.census_2024.data.LoginDataSource;
import com.geostat.census_2024.data.repository.LoginRepository;
import com.geostat.census_2024.data.model.LayerModel;
import com.geostat.census_2024.data.model.UserModel;
import com.geostat.census_2024.architecture.inter.ThatActivity;
import com.geostat.census_2024.architecture.service.SyncService;
import com.geostat.census_2024.ui.addressing.AddressingActivity;
import com.geostat.census_2024.ui.map.event.listener.MapTouchListener;
import com.geostat.census_2024.ui.fragment.map.MapFeatureAlert;
import com.geostat.census_2024.ui.fragment.map.FeatureEditableFragment;
import com.geostat.census_2024.ui.map.model.MapViewModel;
import com.geostat.census_2024.architecture.task.map.RunnableRaster;
import com.geostat.census_2024.architecture.task.map.RunnableVector;
import com.geostat.census_2024.architecture.widjet.Callout;
import com.geostat.census_2024.utility.SharedPref;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class MapActivity extends IndexActivity implements ThatActivity<AppCompatActivity>, RunnableRaster.IfRasterLoadListener, MapTouchListener.MapTouchHandler, MapFeatureAlert.OnButtonClickedListener, FeatureEditableFragment.EditableFeature, Callout.NonResidentClickHandler {

    private MapViewModel mapViewModel;
    private MapView mapView;
    private UserModel userModel;
    private ArcgisManager arcgisManager;


    private final ActivityResultLauncher<Intent> mapActivityResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {

                Feature feature = arcgisManager.getMapService().findRollbackQuestionnaireLocation(Objects.requireNonNull(result.getData()).getStringExtra("house_code"), mapViewModel);
                if (feature != null) {
                    Objects.requireNonNull(getMapViewModel().getTouchableLayer().getValue()).clearSelection();
                    Objects.requireNonNull(getMapViewModel().getTouchableLayer().getValue()).selectFeature(feature);
                    Callout.exit();
                    mapView.setViewpointCenterAsync(feature.getGeometry().getExtent().getCenter());
                }
            }
        }
    });

    /// Life

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mapView = findViewById(R.id.mapView);
        mapViewModel = new ViewModelProvider(MapActivity.this).get(MapViewModel.class);

        try {
            new AsyncTask<>() {
                @Override
                public void first() {
                    arcgisManager = ArcgisManager.getInstance(MapActivity.this);
                    arcgisManager.setFeatureManager(userArea -> {
                        mapViewModel.setUserArea(userArea);
                        userModel = LoginRepository.getInstance(new LoginDataSource(getApplication())).updateUserProps(userArea.getAttributes());
                    });
                }

                @Override
                public void taskRun() {
                    if (arcgisManager.getGeoPackage() == null) arcgisManager.initPackage();
                    if (arcgisManager.getGeoPackage().getLoadStatus() == LoadStatus.NOT_LOADED) {
                        arcgisManager.getGeoPackage().loadAsync();
                        arcgisManager.getGeoPackage().addDoneLoadingListener(() -> mapViewModel.setIsLoadPkg(true));
                    }
                }

                @Override
                public void taskEnd() {}
            }.execute(this);

        } catch (IOException e) {
            e.printStackTrace();
        }

        mapViewModel.getIsLoadPkg().observe(MapActivity.this, aBoolean -> {
            if (aBoolean) {
                new AsyncTaskEasy().execute(new RunnableRaster(arcgisManager.getGeoPackage(), MapActivity.this));
            }
        });

        mapViewModel.getIsLoadRaster().observe(MapActivity.this, aBoolean -> {
            if (aBoolean) {
                UserModel userModel = LoginRepository.getInstance(new LoginDataSource(getApplication())).getUser();
                Integer userLocation = Integer.valueOf(Objects.toString(userModel.getDistrictNum()));
                new AsyncTaskEasy().execute(new RunnableVector(userLocation, arcgisManager.getGeoPackage(), MapActivity.this));
            }
        });

        ArcGISMap map = new ArcGISMap(); map.setBackgroundColor(Color.WHITE); map.setMinScale(170000);
        mapView.setMap(map);

        if (getSupportActionBar() != null ) getSupportActionBar().setTitle("რუკა");

    }

    @Override
    protected void onPause() {
        mapView.pause();
        super.onPause();
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    protected void onResume() {
        super.onResume();
        mapView.resume();

        if (!(mapView.getOnTouchListener() instanceof MapTouchListener)) {
            mapView.setOnTouchListener(new MapTouchListener(getApplicationContext(), this));
        }

//        if (getArcgisController() == null) { arcgisController = ArcgisController.getInstance(MapActivity.this);}
//        if (getArcgisController().getGeoPackage() == null) { getArcgisController().getMapRepository().initPkg(); }

        if (SharedPref.read("house-start", false)){ Callout.exit(); arcgisManager.getMapService().updateHouseStatus(1); SharedPref.remove("house-start");}
        if (SharedPref.read("house-clear", false)){ Callout.exit(); arcgisManager.getMapService().updateHouseStatus(0); SharedPref.remove("house-clear");}
        if (SharedPref.read("is_end", 0) != 0){
            Callout.exit();
            Feature feature = arcgisManager.getMapService().updateHouseStatus(SharedPref.read("is_end", 0), 1);
            SharedPref.remove("is_end");
            new Handler().postDelayed(() -> editFeatureListener(feature), 1);

        }

    }

    @Override
    protected void onDestroy() {

        Callout.remove();
        arcgisManager.clear();
        arcgisManager = null;
        mapView.dispose();
        SyncService.clear();

        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle savedState) {
        super.onSaveInstanceState(savedState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        // super.onRestoreInstanceState(savedInstanceState);
    }

    ///

    /// Feature

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    ///

    /// Listeners
    @Override
    public void rasterLoad(Basemap baseMap) {
        mapViewModel.setIsLoadRaster(true);
        mapView.getMap().setBasemap(baseMap);
    }

    @Override
    public void IfNonResidentListener(Feature feature, Callout.TryUpdate tryUpdate) {
        String text = ((Number) Objects.requireNonNull(feature.getAttributes().get("sacx_stat"))).intValue() == 1 ? "საცხოვრებელს" : "არასაცხოვრებელს , რომელშიც არავინ ცხოვრობს მუდმივად";
        new MaterialAlertDialogBuilder(this)
            .setTitle("შეტყობინება")
            .setMessage("დარწმუნდით, რომ ობიექტი განეკუთვნება "+ text)
            .setNeutralButton("არა", (dialogInterface, i) -> dialogInterface.cancel())
            .setPositiveButton("დიახ", (dialogInterface, i) -> {
                ((ArcgisManager) getArcgisController()).getMapService().updateSetNonResident(feature);
                tryUpdate.update();
            }).create().show();
    }

    @Override
    public void ifSelectedHouse(Point tapPoint, List<GeoElement> selectedFeatures) {

        Feature selectedFeature = (Feature) selectedFeatures.get(0);
        getMapViewModel().setSelectedFeatureFromMapClickedArea(selectedFeature);

        Callout callout = Callout.getInstance(mapView, MapActivity.this);
        callout.setEditFeatureListener(this);
        callout.setNonResidentClickHandler(this);

        Point wgs84Point = (Point) GeometryEngine.project(selectedFeature.getGeometry(), SpatialReference.create(32638));

        callout.show(selectedFeature, wgs84Point);

        mapView.setViewpointCenterAsync(tapPoint);

        Objects.requireNonNull(getMapViewModel().getTouchableLayer().getValue()).clearSelection();
        getMapViewModel().getTouchableLayer().getValue().selectFeature(selectedFeature);
    }

    @Override
    public void editFeatureListener(Feature feature) {
        LayerModel layerModel = getArcgisController().getMapService().edit(feature);

        Intent intent = new Intent(MapActivity.this, AddressingActivity.class);
        intent.putExtra("m", layerModel);
        intent.putExtra("house-status", ((Integer) Objects.requireNonNull(feature.getAttributes().get("status"))).intValue());
        startActivity(intent);

        //        FeatureEditableFragment fragment = FeatureEditableFragment.newInstance(layerModel);
        //        fragment.setRedirectListener(model -> {
        //            Intent intent = new Intent(MapActivity.this, AddressingActivity.class);
        //            intent.putExtra("m", model);
        //            startActivity(intent);
        //        });

        //        getSupportFragmentManager().beginTransaction().add(R.id.test_frame, fragment).addToBackStack("MAP").commit();
    }

    @Override
    public void newFeatureInsertListener(Point tap) throws Exception {

        getArcgisController().getMapService().create(() -> {
            MapFeatureAlert.setActionType("insert");
            MapFeatureAlert.setGraphics(tap);
            MapFeatureAlert.setTitle(MapActivity.this.getString(R.string.dialog_confirm_insert_essage));

            MapFeatureAlert.newInstance(null).show(getSupportFragmentManager(), MapFeatureAlert.class.getSimpleName());
            return null;
        });
    }

    @Override
    public void onDeleteFeatureClicked(Feature feature) {
        try {
            com.esri.arcgisruntime.concurrent.ListenableFuture<java.lang.Void> delete = getArcgisController().getMapService().waitDestroyPermission(feature, getMapViewModel().getTouchableLayer().getValue());
            getMapViewModel().updateAddressingStatus(feature, 5);
            delete.addDoneListener(Callout::exit);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            e.printStackTrace();
        }
    }

    @Override
    public void edit(LayerModel layerModel) {
        getArcgisController().getMapService().update(layerModel, () -> {
            new Thread(() -> { getDrawerLayout().closeDrawer(GravityCompat.START); runOnUiThread(Callout::exit); }).start();
            getMapViewModel().setSelectedFeatureFromMapClickedArea(null);
            return null;

        });
    }


    @Override
    public void onInsertFeatureClicked(Point tapPoint) {
        getArcgisController().getMapService().store(tapPoint, getMapViewModel().getTouchableLayer().getValue());
        Callout.exit();
    }

    ///

    /// Shared
    @Override
    public ThatActivity<AppCompatActivity> getActivity() { return MapActivity.this; }

    @Override
    public int getLayoutResource() { return R.layout.activity_map; }

    @Override
    public ActivityResultLauncher<Intent> getActivityResultLauncher() {
        return mapActivityResult;
    }

    @Override
    public MapActivity init() { return MapActivity.this; }

    @Override
    public MapViewModel getMapViewModel() { return mapViewModel; }

    @Override
    public DrawerLayout getDrawerLayout() { return drawerLayout; }

    @Override
    public UserModel getUser() { return userModel; }

    @Override
    public MapView getMapView() { return mapView; }

    @Override
    public ArcgisManager getArcgisController() { return arcgisManager; }


    ///
}


//                Set<String> keys = attr.keySet();
//                for (String key : keys) {
//                    Object value = attr.get(key);
//                    Log.d("TEST", "run: KEY: " + key + " VALUE: " + value);
//                }

//copyFile(
//        new File(getExternalFilesDir(null).getPath() + "/nadzaladevi.gpkg"), new File(getExternalFilesDir(null).getPath() + "/abc/nadzaladevi.gpkg")
//);


//            Map<String, Object> attr = mSelectedRelatedFeature.getAttributes();
//            Set<String> keys = attr.keySet();
//            for (String key : keys) {
//                Object value = attr.get(key);
//                Log.d("TEST", "run: KEY: " + key + " VALUE: " + value);
//            }


//    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//    boolean newI = preferences.getBoolean("newStep", false);
//                          if (newI) { arcgisController.getMapRepository().newI(); preferences.edit().clear().apply(); }