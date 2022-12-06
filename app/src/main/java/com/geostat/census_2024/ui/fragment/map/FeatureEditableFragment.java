package com.geostat.census_2024.ui.fragment.map;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;

import com.geostat.census_2024.R;
import com.geostat.census_2024.data.model.LayerModel;
import com.geostat.census_2024.architecture.inter.ThatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FeatureEditableFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FeatureEditableFragment extends Fragment {

    public interface EditableFeature{
        void edit(LayerModel layerModel);
    }

    public interface IfRedirectListener {
        void redirectInAddressing(LayerModel model);
    }

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "LayerModel";

    // TODO: Rename and change types of parameters
    private LayerModel layerModel;

    private ThatActivity<AppCompatActivity> activity;

    public FeatureEditableFragment() {
        // Required empty public constructor
    }

    private IfRedirectListener redirectListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param layerModel Parameter 1.
     * @return A new instance of fragment BlankFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FeatureEditableFragment newInstance(LayerModel layerModel) {
        FeatureEditableFragment fragment = new FeatureEditableFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1, layerModel);

        fragment.setArguments(args);
        return fragment;
    }

    public void setRedirectListener(IfRedirectListener redirectListener) {
        this.redirectListener = redirectListener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            layerModel = (LayerModel) getArguments().getParcelable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_blank, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        activity.getDrawerLayout().openDrawer(GravityCompat.START);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(LayerModel model, String name) {
        return model.getProperty(name) != null ? (T) model.getProperty(name) : (T) activity.getUser().getProperty(name);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        TextInputEditText fid = view.findViewById(R.id.fid);
        TextInputEditText region = view.findViewById(R.id.region_id);
        TextInputEditText municipal = view.findViewById(R.id.municipal_);
        TextInputEditText instr = view.findViewById(R.id.instr_id_fr);
        TextInputEditText district = view.findViewById(R.id.district_i);

        fid.setText(String.valueOf( (Long) layerModel.getProperty("fid")));

        String MunicipalKey = "municipal_";
        String municipalText = get(layerModel, MunicipalKey);
        municipal.setText(String.valueOf(municipalText));
        municipal.addTextChangedListener(new TextWatch(MunicipalKey, layerModel));

        String regionKey = "region_id";
        String regionText = get(layerModel, regionKey);
        region.setText(regionText);
        region.addTextChangedListener(new TextWatch(regionKey, layerModel));

        String instrKey = "instr_id";
        String instText = get(layerModel, instrKey);
        instr.setText(instText);
        instr.addTextChangedListener(new TextWatch(instrKey, layerModel));

        String districtKey = "district_i";
        String districtText = get(layerModel, districtKey);
        Log.d("TAG", "onViewCreated: " + layerModel);
        district.setText(districtText);
        district.addTextChangedListener(new TextWatch(districtKey, layerModel));

        MaterialButton materialButton = view.findViewById(R.id.create);
        materialButton.setOnClickListener(v -> {
            if (((ThatActivity<AppCompatActivity>) requireActivity()) instanceof EditableFeature) {
                ((EditableFeature) ((ThatActivity<AppCompatActivity>) requireActivity())).edit(layerModel);
            }
        });

        MaterialButton addressing = view.findViewById(R.id.addressing);
        addressing.setOnClickListener(v -> {
            if (redirectListener != null) redirectListener.redirectInAddressing(layerModel);
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onAttach(@NonNull Context activity) {
        super.onAttach(activity);
        if (activity instanceof ThatActivity) {
            this.activity = (ThatActivity<AppCompatActivity>) activity;
        } else {
            throw new RuntimeException(activity + "must implement ThatActivity<AppCompatActivity>");
        }
    }

    public static class TextWatch implements TextWatcher {

        private final String KEY_NAME;
        LayerModel layerModel;

        public TextWatch(String keyName, LayerModel layerModel) {
            this.KEY_NAME = keyName;
            this.layerModel = layerModel;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            Class<?> clazz = layerModel.getProperty(KEY_NAME) != null ? layerModel.getProperty(KEY_NAME).getClass() : Object.class;
//                s = (s != null) ? s : (Editable) Class.forName(clazz.getName()).cast(new Object());
            layerModel.setProperty(KEY_NAME, s, clazz);
        }
    }
}