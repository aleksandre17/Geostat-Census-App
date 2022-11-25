package com.geostat.census_2024.ui.map.event.listener;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.mapping.GeoElement;
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.IdentifyLayerResult;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.geostat.census_2024.R;
import com.geostat.census_2024.data.repository.MapRepository;
import com.geostat.census_2024.inter.ThatActivity;
import com.geostat.census_2024.ui.map.widjet.Callout;
import com.tapadoo.alerter.Alerter;

import java.util.List;
import java.util.Objects;

public class MapTouchListener extends DefaultMapViewOnTouchListener {

    public interface ToucheHandler {
        void editFeatureListener (Feature feature);
    }

    public interface MapTouchHandler extends ToucheHandler {
        void ifSelectedHouse(Point selected, List<GeoElement> selectedFeatures);
        void newFeatureInsertListener (Point tap) throws Exception;
    }

    private final MapTouchHandler mapTouchHandler;

    private final MapView mapView;
    private final ThatActivity<AppCompatActivity> thatActivity;

    public MapTouchListener(Context context, ThatActivity<AppCompatActivity> thatActivity) {
        super(context, thatActivity.getMapView());

        this.thatActivity = thatActivity;
        this.mapView = thatActivity.getMapView();
        this.mapTouchHandler = (MapTouchHandler) thatActivity;
    }
    
    public Boolean exit (Feature first, Geometry second) {
        Geometry t = GeometryEngine.project(second.getExtent(), SpatialReference.create(32638));
        return GeometryEngine.contains(first.getGeometry(), t);
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        Callout.exit();
        return super.onScroll(e1, e2, distanceX, distanceY);
    }

    @Override
    public boolean onRotate(MotionEvent event, double rotationAngle) {
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        Point tapPoint = mapView.screenToLocation(new android.graphics.Point((int) e.getX(), (int) e.getY()));
        android.graphics.Point mClickPoint = new android.graphics.Point((int) e.getX(), (int) e.getY());

        if (!exit(thatActivity.getMapViewModel().getUserArea(), tapPoint)) {
            Toast.makeText(((AppCompatActivity) thatActivity).getApplicationContext(), "არ გაქვთ ნებართვა, მოცემულ სააღწერო უბანზე!", Toast.LENGTH_LONG).show();
        } else {
            MapRepository mapRepository = thatActivity.getArcgisController().getMapRepository();

            try {
                IdentifyLayerResult result = mapRepository.selectFeaturesFromMapTouch(mClickPoint, 10, false, 1);
                List<GeoElement> resultGeoElements = result.getElements();
                if (resultGeoElements.isEmpty()) {
                    mapTouchHandler.newFeatureInsertListener(tapPoint);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                Log.e("FEATURE", "Select feature failed: " + ex.getMessage());
            }

        }
        super.onLongPress(e);
    }

    /**
     * Overrides the onSingleTapConfirmed gesture on the MapView, showing formatted coordinates of the tapped location.
     * @param e the motion event
     * @return true if the listener has consumed the event; false otherwise
     */
    @Override
    public boolean onSingleTapConfirmed(@NonNull MotionEvent e) {
        if (thatActivity.getMapViewModel().getUserArea() == null) return super.onSingleTapConfirmed(e);
        Point tapPoint = mapView.screenToLocation(new android.graphics.Point((int) e.getX(), (int) e.getY()));
        android.graphics.Point mClickPoint = new android.graphics.Point((int) e.getX(), (int) e.getY());
        
        if (!exit(thatActivity.getMapViewModel().getUserArea(), tapPoint)) {
            Alerter.create(thatActivity.init())
                    .setTitle("შეტყობინება").setBackgroundColorRes(R.color.yellow).setText("არ გაქვთ ნებართვა, მოცემულ სააღწერო უბანზე!").setDuration(5000).show();
            return true;
        } else {

            thatActivity.getMapViewModel().setMapClickedArea(tapPoint);
            MapRepository mapRepository = thatActivity.getArcgisController().getMapRepository();

            try {
                IdentifyLayerResult result = mapRepository.selectFeaturesFromMapTouch(mClickPoint, 10, false, 1);
                List<GeoElement> resultGeoElements = result.getElements();
                if (!resultGeoElements.isEmpty()) {
                    Feature feature = (Feature) result.getElements().get(0);
                    if (Objects.equals(feature.getAttributes().get("status"), 4)) {
                        Alerter.create(thatActivity.init())
                                .setTitle("შეტყობინება").setBackgroundColorRes(R.color.yellow).setText("დაელოდეთ წაშლის დადასტურებას!").setDuration(5000).show();
                    } else {
                        mapTouchHandler.ifSelectedHouse(tapPoint, resultGeoElements);
                    }
                }
//                else {
//                    if (newFeatureInsertListener != null)
//                        newFeatureInsertListener.newFeatureInsertListener(tapPoint);
//                }
            } catch (Exception ex) {
                ex.printStackTrace();
                Alerter.create(thatActivity.init())
                        .setTitle("შეტყობინება").setBackgroundColorRes(R.color.red).setText("შეცდომა, შენობის არჩევის დროს").setDuration(5000).show();
                Log.e("FEATURE", "Select feature failed: " + ex.getMessage());
            }

            return super.onSingleTapConfirmed(e);
        }
    }

}






//    @Override
//    public void edit(@NonNull LayerModel model) {
//        Toast.makeText(context, "FIND" + model.getProperty("MUNICIPAL_"), Toast.LENGTH_SHORT).show();
//        queryRelatedFeatures(mSelectedRelatedFeature, model);
//    }
//
//    private void queryRelatedFeatures(Feature feature, @NonNull LayerModel model) {
//
//        model.getProperties().remove("fid");
//        feature.getAttributes().putAll(model.getProperties());
//
//        thatActivity.getMapViewModel().getTouchableLayer().getValue().getFeatureTable().updateFeatureAsync(feature).addDoneListener(new Runnable() {
//            @Override
//            public void run() {
//
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        ((ThatActivity<AppCompatActivity>)thatActivity).getDrawerLayout().closeDrawer(GravityCompat.START);
//                    }
//                }, 200);
//
//                Map<String, Object> attr = feature.getAttributes();
//                Set<String> keys = attr.keySet();
//                for (String key : keys) {
//                    Object value = attr.get(key);
//                    Log.d("TEST", "run: KEY: " + key + " VALUE: " + value);
//                }
//            }
//        });
//
//    }

//
//    private void confirmDeletion(Feature feature) {
//
//        MapFeatureAlert.setActionType("delete");
//        MapFeatureAlert.setFeature(feature);
//        MapFeatureAlert.setTitle(((AppCompatActivity)thatActivity).getString(R.string.dialog_confirm_delete_message));
//
//        String id = (feature.getAttributes().get("fid") != null) ? String.valueOf(feature.getAttributes().get("fid")) : null;
//        MapFeatureAlert.newInstance(id)
//                .show(((AppCompatActivity)thatActivity).getSupportFragmentManager(), MapFeatureAlert.class.getSimpleName());
//    }
//
//    private void confirmInsert(Point tapPoint) {
//
//        Map<String, Object> attributes = new HashMap<>();
//        Feature feature =  featureLayer.getFeatureTable().createFeature(attributes, tapPoint);
//
//        MapFeatureAlert.setActionType("insert");
//        MapFeatureAlert.setFeature(feature);
//        MapFeatureAlert.setTitle(((AppCompatActivity)thatActivity).getString(R.string.dialog_confirm_insert_essage));
//
//        MapFeatureAlert
//                .newInstance(null).show(((AppCompatActivity)thatActivity).getSupportFragmentManager(), MapFeatureAlert.class.getSimpleName());
//    }
//
////                try {
////
////                    Feature feature = (Feature) resultGeoElements.get(0);
//////                    mSelectedRelatedFeature = (Feature) feature;
//////                    Point wgs84Point = (Point) GeometryEngine.project(feature.getGeometry(), SpatialReference.create(32638));
////
//////                    inflateCallout(mapView, feature, wgs84Point).show();
////
//////                    mapView.setViewpointCenterAsync(tapPoint);
////
////                    // featureLayer.clearSelection();
////                    // featureLayer.selectFeature(feature);
////
////                    if (selectedHouseListener != null) {
////                        selectedHouseListener.ifSelectedHouse(tapPoint, feature, resultGeoElements);
////                    }
////
////                } catch (Exception exc) {
////                    exc.printStackTrace();
////                }
//
//    private Callout inflateCallout(MapView mapView, Feature feature, Point point) {
//
////        if (callout != null) callout.dismiss();
////        callout = mapView.getCallout();
//
////        @SuppressLint("InflateParams") View view = LayoutInflater.from(context).inflate(R.layout.view_callout, null);
////
////        view.findViewById(R.id.delete).setOnClickListener(item -> confirmDeletion(feature));
//
////        view.findViewById(R.id.edit).setOnClickListener(item -> {
//
////            Map<String, Object> attr = (Map<String, Object>) feature.getAttributes();
////            LayerModel layerModel = new LayerModel(attr instanceof HashMap ? (HashMap<String, Object>) attr : new HashMap<>(attr));
////
////            for (String key : layerModel.getAll().keySet()) {
////                Object value = layerModel.getProperty(key) != null ? layerModel.getProperty(key) : new Object();
////                Log.d("TEST", "run: KEY: " + value.getClass().getTypeName() + " VALUE: " + key);
////            }
////
////            Fragment fragment = BlankFragment.newInstance(layerModel);
////
////            ((AppCompatActivity)thatActivity)
////                    .getSupportFragmentManager()
////                    .beginTransaction().add(R.id.test_frame, fragment).addToBackStack(null).commit();
//
////                thatActivity.getDrawerLayout().openDrawer(GravityCompat.START);
////        });
//
////        view.findViewById(R.id.exit).setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                callout.dismiss();
////            }
////        });
//
////        callout.setContent(view);
////        callout.setLocation(point);
//
//        return callout;
//    }
