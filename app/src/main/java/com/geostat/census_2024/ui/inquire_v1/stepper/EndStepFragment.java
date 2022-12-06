package com.geostat.census_2024.ui.inquire_v1.stepper;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.geostat.census_2024.R;
import com.geostat.census_2024.databinding.FragmentEndStepBinding;
import com.geostat.census_2024.ui.inquire_v1.stepper.factory.StepFactory;
import com.geostat.census_2024.ui.inquire_v1.stepper.model.FragmentStepViewModel;
import com.stepstone.stepper.BlockingStep;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EndStepFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EndStepFragment extends Fragment implements BlockingStep {

    private FragmentEndStepBinding binding;
    private FragmentStepViewModel fragmentStepViewModel;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String STEP_NAME = "STEP_NAME_INDEX";

    // TODO: Rename and change types of parameters
    private Integer index;

    public EndStepFragment() {
        // Required empty public constructor
        // this.addressViewModel = addressViewModel;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param index Parameter 1.
     * @return A new instance of fragment EndStepFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EndStepFragment newInstance(Integer index) {
        EndStepFragment fragment = new EndStepFragment();
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
    }

    public Integer getIndex() {
        return index;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentStepViewModel = StepFactory.initViewModel(requireActivity(), FragmentStepViewModel.class);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_end_step, null, false);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        return binding.getRoot();
    }

    @Override
    public void onNextClicked(StepperLayout.OnNextClickedCallback callback) {

    }

    @Override
    public void onResume() {
        super.onResume();
        binding.setVm(fragmentStepViewModel.getVm().getValue());
    }

    @Override
    public void onDestroy() {
        binding = null;
        super.onDestroy();
    }

    @Override
    public void onCompleteClicked(StepperLayout.OnCompleteClickedCallback callback) {
        new Handler().postDelayed(callback::complete, 300);
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