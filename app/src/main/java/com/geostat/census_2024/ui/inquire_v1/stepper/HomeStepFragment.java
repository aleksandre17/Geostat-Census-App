package com.geostat.census_2024.ui.inquire_v1.stepper;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.geostat.census_2024.R;
import com.geostat.census_2024.data.model.UserModel;
import com.geostat.census_2024.ui.inquire_v1.stepper.factory.StepFactory;
import com.geostat.census_2024.ui.inquire_v1.stepper.model.FragmentStepViewModel;
import com.stepstone.stepper.BlockingStep;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeStepFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeStepFragment extends Fragment implements BlockingStep {


    FragmentStepViewModel fragmentStepViewModel;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String STEP_NAME = "STEP_NAME_INDEX";

    // TODO: Rename and change types of parameters
    private Integer index;

    public HomeStepFragment() {
        // Required empty public constructor
        // this.fragmentStepViewModel = StepFactory.getStepViewModel();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param index Parameter 1.
     * @return A new instance of fragment HomeStepFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeStepFragment newInstance(Integer index) {
       return initFr(index);
    }

    @NonNull
    private static HomeStepFragment initFr (int index) {
        HomeStepFragment fragment = new HomeStepFragment();
        Bundle args = new Bundle();
        args.putInt(STEP_NAME, index);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            index = getArguments().getInt(STEP_NAME);
        }

        fragmentStepViewModel = StepFactory.initViewModel(requireActivity(), FragmentStepViewModel.class);
    }

    public Integer getIndex() {
        return index;
    }

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return LayoutInflater.from(getContext()).inflate(R.layout.fragment_home_step, null, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        UserModel userModel = fragmentStepViewModel.getUser();

        TextView instId = view.findViewById(R.id.instr_id);
        TextView distrId = view.findViewById(R.id.distr_id);
        TextView houseCode = view.findViewById(R.id.house_code);

        instId.setText(userModel.getProperty("instr_id"));
        distrId.setText(userModel.getProperty("distr_id"));
        houseCode.setText(Objects.requireNonNull(fragmentStepViewModel.getVm().getValue()).getHouseCode());

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    @Override
    public void onNextClicked(StepperLayout.OnNextClickedCallback callback) {
        callback.goToNextStep();
    }

    @Override
    public void onCompleteClicked(StepperLayout.OnCompleteClickedCallback callback) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                callback.complete();
            }
        }, 300);
    }

    @Override
    public void onBackClicked(StepperLayout.OnBackClickedCallback callback) {
        callback.goToPrevStep();
    }

    @Nullable
    @Override
    public VerificationError verifyStep() {
        return null;
    }

    @Override
    public void onSelected() {}

    @Override
    public void onError(@NonNull VerificationError error) {}

}