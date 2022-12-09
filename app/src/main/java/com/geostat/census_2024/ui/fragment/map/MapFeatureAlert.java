package com.geostat.census_2024.ui.fragment.map;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.geometry.Point;
import com.geostat.census_2024.R;
import com.geostat.census_2024.architecture.inter.ThatActivity;
import com.tapadoo.alerter.Alerter;

import java.util.Objects;

public class MapFeatureAlert extends DialogFragment {

    public interface OnButtonClickedListener {
        void onDeleteFeatureClicked(Feature feature, String motive);
        void onInsertFeatureClicked(Point feature);
    }

    private static final String ARG_FEATURE_ID = MapFeatureAlert.class.getSimpleName() + "_feature_id";

    @Nullable
    private String featureId;

    private static Feature feature;

    public static String title;
    public static String actionType;

    public static Point tap;

    @SuppressWarnings("unchecked")
    private final DialogInterface.OnClickListener mOnClickListener = (dialog, which) -> {

        ThatActivity<AppCompatActivity> activity = (ThatActivity<AppCompatActivity>) getContext();

        if (activity instanceof OnButtonClickedListener) {

            if (which == DialogInterface.BUTTON_POSITIVE) {
                if (getActionType().equals("delete")) {
                    EditText editText = this.getDialog().findViewById(R.id.reason);

                    if (editText.getText().toString().equals("") && Objects.equals(getFeature().getAttributes().get("status"), 1)) {
                        Alerter.create(activity.init()).setTitle("შეტყობინება").setBackgroundColorRes(R.color.red).setText("დაწერეთ წაშლის მიზეზი!").setDuration(2000).show();
                    } else {
                        ((OnButtonClickedListener) activity).onDeleteFeatureClicked(getFeature(), editText.getText().toString());
                    }
                } else if (getActionType().equals("insert")) {
                    ((OnButtonClickedListener) activity).onInsertFeatureClicked(getGraphics());
                }
            } else {
                dismiss();
            }
        }
    };

    public static Point getGraphics() {
        return tap;
    }

    public static void setGraphics(Point tap) {
        MapFeatureAlert.tap = null;
        MapFeatureAlert.tap = tap;
    }

    public static Feature getFeature() {
        return feature;
    }

    public static void setFeature(Feature feature) {
        MapFeatureAlert.feature = feature;
    }

    public static String getActionType() {
        return actionType;
    }

    public static void setActionType(String type) {
        actionType = type;
    }

    public static String getTitle() {
        return title;
    }

    public static void setTitle(String t) {
       title = t;
    }


    public static MapFeatureAlert newInstance(@Nullable String featureId) {
        MapFeatureAlert fragment = new MapFeatureAlert();
        Bundle args = new Bundle();
        args.putString(ARG_FEATURE_ID, featureId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.featureId = getArguments().getString(ARG_FEATURE_ID);
    }


    @SuppressLint("InflateParams")
    @NonNull
    @Override public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setMessage(getTitle());
        builder.setPositiveButton(R.string.dialog_confirm_delete_positive, mOnClickListener);
        builder.setNeutralButton(R.string.dialog_confirm_delete_negative, mOnClickListener);

        if (getActionType().equals("delete") && Objects.equals(getFeature().getAttributes().get("status"), 1)) {
            View view = requireActivity().getLayoutInflater().inflate(R.layout.fragment_sample_dialog, null, false);
            EditText editText = view.findViewById(R.id.reason);
            if (editText != null) {
                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        Log.d("getClass", "afterTextChanged: " + editable.toString());
                    }
                });
            }
            builder.setView(view);
        }

        return builder.create();
    }
}
