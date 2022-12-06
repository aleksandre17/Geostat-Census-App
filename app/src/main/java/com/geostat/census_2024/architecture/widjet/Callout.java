package com.geostat.census_2024.architecture.widjet;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.geostat.census_2024.R;
import com.geostat.census_2024.ui.map.event.listener.MapTouchListener;
import com.geostat.census_2024.ui.fragment.map.MapFeatureAlert;

import java.util.Arrays;
import java.util.Objects;

public class Callout {

    public interface NonResidentClickHandler {
        void IfNonResidentListener(Feature feature, TryUpdate update);
    }

    public interface TryUpdate {
        void update();
    }

    private static volatile Callout instance;
    private final AppCompatActivity activity;
    public com.esri.arcgisruntime.mapping.view.Callout callout;

    public Callout(MapView mapView, AppCompatActivity activity) {
        callout = mapView.getCallout();
        this.activity = activity;
    }

    MapTouchListener.ToucheHandler editFeatureListener;
    NonResidentClickHandler nonResidentClickHandler;

    public static Callout getInstance (MapView mapView, AppCompatActivity activity) {
        if (instance == null) { synchronized (Callout.class) { if( instance == null ) { instance = new Callout(mapView, activity); } } }
        return instance;
    }

    public void setEditFeatureListener(MapTouchListener.ToucheHandler editFeatureListener) {
        this.editFeatureListener = editFeatureListener;
    }

    public void setNonResidentClickHandler(NonResidentClickHandler nonResidentClickHandler) {
        this.nonResidentClickHandler = nonResidentClickHandler;
    }

    public void show (Feature feature, Point wgs84Point) {

        if (callout != null) callout.dismiss();
        @SuppressLint("InflateParams") View view = this.activity.getLayoutInflater().inflate(R.layout.view_callout, null);

        view.findViewById(R.id.delete).setOnClickListener(item -> confirmDeletion(feature));


        view.findViewById(R.id.edit).setOnClickListener(item -> {
            if (editFeatureListener != null) { editFeatureListener.editFeatureListener(feature); }
        });

        TextView editableText = view.findViewById(R.id.edit);

        if (((Number) Objects.requireNonNull(feature.getAttributes().get("sacx_stat"))).intValue() == 1) {
            editableText.setClickable(false);
            editableText.setTextColor(Color.parseColor("#b8b2a2"));
            ((TextView) view.findViewById(R.id.nonResident)).setText("საცხოვრებელი");
        } else {
            editableText.setClickable(true);
            editableText.setTextColor(Color.parseColor("#252323"));
            ((TextView) view.findViewById(R.id.nonResident)).setText("არასაცხოვრებელი");
        }

        view.findViewById(R.id.nonResident).setOnClickListener(view1 -> nonResidentClickHandler.IfNonResidentListener(feature, () -> {
            if (((Number) Objects.requireNonNull(feature.getAttributes().get("sacx_stat"))).intValue() == 1) {
                editableText.setClickable(false);
                editableText.setTextColor(Color.parseColor("#b8b2a2"));
                ((TextView) view1.findViewById(R.id.nonResident)).setText("საცხოვრებელი");
            } else {
                editableText.setClickable(true);
                editableText.setTextColor(Color.parseColor("#252323"));
                ((TextView) view1.findViewById(R.id.nonResident)).setText("არასაცხოვრებელი");
            }
        }));

        if (feature.getAttributes().get("status") != null && Arrays.asList(1, 2, 3).contains(Integer.parseInt(Objects.requireNonNull(feature.getAttributes().get("status")).toString()))){
            ((Button) view.findViewById(R.id.nonResident)).setEnabled(false);
            if(!Arrays.asList(0,1).contains(Integer.parseInt(Objects.requireNonNull(feature.getAttributes().get("status")).toString()))) ((Button) view.findViewById(R.id.delete)).setEnabled(false);
        }

//        view.findViewById(R.id.exit).setOnClickListener(v -> callout.dismiss());

        callout.setContent(view);
        callout.setLocation(wgs84Point); callout.show();
    }

    public static void exit() {
        if (instance != null) instance.callout.dismiss();
    }

    private void confirmDeletion(Feature feature) {

        MapFeatureAlert.setActionType("delete");
        MapFeatureAlert.setFeature(feature);
        MapFeatureAlert.setTitle(activity.getString(R.string.dialog_confirm_delete_message));

        String id = (feature.getAttributes().get("fid") != null) ? String.valueOf(feature.getAttributes().get("fid")) : null;
        MapFeatureAlert.newInstance(id).show(activity.getSupportFragmentManager(), MapFeatureAlert.class.getSimpleName());
    }

    public static void remove() {
        instance = null;
    }
}
