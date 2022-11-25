package com.geostat.census_2024.ui.fragment.stepper;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.geostat.census_2024.R;
import com.geostat.census_2024.data.local.entities.BuildingType;
import com.geostat.census_2024.data.local.entities.LivingStatus;
import com.geostat.census_2024.data.local.entities.Tag;
import com.geostat.census_2024.data.model.AddressingModel;
import com.geostat.census_2024.databinding.FragmentFirstStepBinding;
import com.geostat.census_2024.inter.IThatFragment;
import com.geostat.census_2024.ui.address.AddressViewModel;
import com.geostat.census_2024.ui.custom.InterAdapter;
import com.geostat.census_2024.ui.custom.InterSpinner;
import com.geostat.census_2024.ui.custom.StreetSpinner;
import com.geostat.census_2024.ui.fragment.stepper.factory.StepFactory;
import com.geostat.census_2024.ui.fragment.stepper.handler.SpinnerItemClickListener;
import com.geostat.census_2024.ui.fragment.stepper.inter.Stepper;
import com.geostat.census_2024.ui.fragment.stepper.model.FragmentStepViewModel;
import com.stepstone.stepper.BlockingStep;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
//


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FirstStepFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FirstStepFragment extends Fragment implements BlockingStep, Stepper {

    private static volatile FirstStepFragment INSTANCE;

    private final AddressViewModel addressViewModel;
    private FragmentFirstStepBinding binding;
    private FragmentStepViewModel fragmentStepViewModel;

    private Observer<AddressingModel> FirstTrigger;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String STEP_NAME = "STEP_NAME_INDEX";

    // TODO: Rename and change types of parameters
    private Integer index;

    public FirstStepFragment() {
        // Required empty public constructor
        this.addressViewModel = StepFactory.getAddressViewModel();
        //this.fragmentStepViewModel = StepFactory.getStepViewModel();
    }

    public IThatFragment<FirstStepFragment, FragmentFirstStepBinding>  fr;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param index Parameter 1.
     * @return A new instance of fragment FirstStepFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FirstStepFragment newInstance(Integer index) {
        if (INSTANCE == null) { synchronized (HomeStepFragment.class) { if (INSTANCE == null) { INSTANCE = initFr(index); } } }
        return INSTANCE;

    }

    @NonNull
    private static FirstStepFragment initFr (int index) {
        FirstStepFragment fragment = new FirstStepFragment();
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

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_first_step, null, false);
        //binding.setVm(fragmentStepViewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        // fragmentStepViewModel = new ViewModelProvider(requireActivity()).get(FragmentStepViewModel.class);

        AddressingModel fr = fragmentStepViewModel.getVm().getValue();

        assert fr != null;
        binding.regionId
                .setClickListener(new SpinnerItemClickListener(getFragmentInstance(), fragmentStepViewModel)).setSetSelectedId(fr.getRegionId());
        binding.municipal
                .setClickListener(new SpinnerItemClickListener(getFragmentInstance(), fragmentStepViewModel)).setSetSelectedId(fr.getMunicipalId());
        binding.cityAddress
                .setClickListener(new SpinnerItemClickListener(getFragmentInstance(), fragmentStepViewModel)).setSetSelectedId(fr.getCityId());
        binding.unity
                .setClickListener(new SpinnerItemClickListener(getFragmentInstance(), fragmentStepViewModel)).setSetSelectedId(fr.getUnityId());
        binding.village
                .setClickListener(new SpinnerItemClickListener(getFragmentInstance(), fragmentStepViewModel)).setSetSelectedId(fr.getVillageId());

        InterSpinner buildingType = binding.BuildingType;
        InterSpinner livingStatusInput = binding.houseStatus;

        try {
            List<BuildingType> types = addressViewModel.getAllBuildingType();
            buildingType.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.item, R.id.textView, types));
            buildingType.setClickListener(selected -> livingStatusInput.setText(""));

            List<LivingStatus> types2 = addressViewModel.getAllLivingStatus();
            livingStatusInput.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.item, R.id.textView, types2));

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        //        binding.setVm(fragmentStepViewModel);
        //        binding.setLifecycleOwner(getViewLifecycleOwner());
        //        binding.executePendingBindings();

    }

    @Override
    public void onResume() {
        super.onResume();

        LiveData<AddressingModel> addressingModelLiveData = fragmentStepViewModel.getVm();

        FirstTrigger = addressingModel -> {

            if (binding.getVm() == null) {
                binding.setVm(fragmentStepViewModel);
            }
        };


        addressingModelLiveData.observe(getViewLifecycleOwner(), FirstTrigger);


        StreetSpinner streetSpinner = binding.streetAddress;
        try {
            List<Tag> types = addressViewModel.getTags();
            Collections.reverse(types);

            if (!types.isEmpty()) {
                InterAdapter<Tag> interAdapter = new InterAdapter<>(requireActivity(), R.layout.item_alternative, types, addressViewModel::removeTag);
                streetSpinner.setAdapter(interAdapter);
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.executePendingBindings();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        // super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        fragmentStepViewModel.getVm().removeObservers(getViewLifecycleOwner());
        super.onPause();
    }

    @Override
    public void onStop() {
        fragmentStepViewModel.getVm().removeObservers(getViewLifecycleOwner());
        FirstTrigger = null;
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        this.fragmentStepViewModel.getVm().removeObservers(getViewLifecycleOwner());
        FirstTrigger = null;
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getParentFragmentManager().beginTransaction().remove(FirstStepFragment.this).commitAllowingStateLoss();
    }

    ///

    @Override
    public void onNextClicked(StepperLayout.OnNextClickedCallback callback) {
        this.fragmentStepViewModel.getVm().removeObserver(FirstTrigger);
        fragmentStepViewModel.getVm().removeObservers(getViewLifecycleOwner());

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                callback.goToNextStep();
//            }
//        }, 300);

        callback.goToNextStep();
    }

    @Override
    public void onCompleteClicked(StepperLayout.OnCompleteClickedCallback callback) {
        new Handler().postDelayed(callback::complete, 300);
    }

    @Override
    public void onBackClicked(StepperLayout.OnBackClickedCallback callback) {
        this.fragmentStepViewModel.getVm().removeObserver(FirstTrigger);
        fragmentStepViewModel.getVm().removeObservers(getViewLifecycleOwner());

        callback.goToPrevStep();
    }

    @Nullable
    @Override
    public VerificationError verifyStep() {
        AddressingModel m = fragmentStepViewModel.getVm().getValue();
        if (m != null) {

            if (m.getRegionId() == null || m.getMunicipalId() == null || m.getBuildingType() == null) {
                return new VerificationError("სავალდებულო ველი!");
            }

            if (Arrays.asList(1, 2).contains(m.getBuildingType()) && m.getLivingStatus() == null) {
                return new VerificationError("სავალდებულო ველი!");
            }

//            if (m.getBuildingType() != null && m.getBuildingType() != 5 && m.getBuildingType() != 6 && m.getLivingStatus() == null) {
//                return new VerificationError("");
//            }
        }

        return null;
    }

    @Override
    public void onSelected() {

    }

    @Override
    public void onError(@NonNull VerificationError error) {
        AddressingModel m = fragmentStepViewModel.getVm().getValue();
        if (m != null) {
            if (m.getRegionId() == null) { binding.regionIdFeald.setError("სავალდებულო ველი!"); }
            if (m.getMunicipalId() == null) { binding.municipalFeald.setError("სავალდებულო ველი!"); }
            if (m.getBuildingType() == null) { binding.BuildingTypeFiled.setError("სავალდებულო ველი!"); }

            if (Arrays.asList(1, 2).contains(m.getBuildingType()) && m.getLivingStatus() == null) {
                binding.houseStatusField.setError("სავალდებულო ველი!!");
            }

//            if (m.getBuildingType() != null && m.getBuildingType() != 5 && m.getBuildingType() != 6 && m.getLivingStatus() == null) {
//                binding.houseStatusField.setError("სავალდებულო ველი!");
//            }
        }
    }

    public AddressViewModel getAddressViewModel () {
        return FirstStepFragment.this.addressViewModel;
    }

    public IThatFragment<FirstStepFragment, FragmentFirstStepBinding> getFragmentInstance() {
        return new IThatFragment<>() {
            @Override
            public FirstStepFragment getFragment() {
                return FirstStepFragment.this;
            }

            @Override
            public FragmentFirstStepBinding getBinding() {
                return FirstStepFragment.this.binding;
            }
        };
    }

    @Override
    public void clear() {
        INSTANCE = null;
    }
}


//                    SpinerModel m = new SpinerModel(address.getId(), address.getLocationName());
//                    m.setUrbanTypeId(address.getUrbanTypeId());
//                    m.setLocationTypeId(address.getLocationTypeId());
//                    regionAddresses.add(m);

//if (binding.streetName.isFocused()) {
//        Log.d("UPS", "invoke: isDirty" + addressingModel.getStreet());
//        Validator validator = new Validator(addressingModel.getStreet() != null ? addressingModel.getStreet() : "").addRule(new NonEmptyRule("შეავსეთ!"));
//        validator.addSuccessCallback(new Function0<Unit>() {
//@Override
//public Unit invoke() {
//        Log.d("UPS", "invoke: 1");
//        binding.streetNameField.setError(null);
//        return null;
//        }
//        }).addErrorCallback(new Function1<String, Unit>() {
//@Override
//public Unit invoke(String s) {
//        Log.d("UPS", "invoke: 2");
//        binding.streetNameField.setError(s);
//        return null;
//        }
//        }).check();
//        }

//        Validator validator = new Validator(binding.streetName.getText().toString()).addRule(new NonEmptyRule("Ups"));
//        validator.addErrorCallback(new Function1<String, Unit>() {
//            @Override
//            public Unit invoke(String s) {
//                return null;
//            }
//        }).check();

//
//public class FirstStepFragment extends Fragment implements BlockingStep {
//
//    private static volatile FirstStepFragment INSTANCE;
//
//    private AddressViewModel addressViewModel;
//    private FragmentFirstStepBinding binding;
//    private FragmentStepViewModel fragmentStepViewModel;
//
//    private Observer<AddressingModel> FirstTrigger;
//
//    // TODO: Rename parameter arguments, choose names that match
//    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String STEP_NAME = "STEP_NAME_INDEX";
//
//    // TODO: Rename and change types of parameters
//    private Integer index;
//
//    public FirstStepFragment() {
//        // Required empty public constructor
//        this.addressViewModel = StepFactory.getAddressViewModel();
//        //this.fragmentStepViewModel = StepFactory.getStepViewModel();
//    }
//
//    public IThatFragment<FirstStepFragment, FragmentFirstStepBinding>  fr;
//
//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param index Parameter 1.
//     * @return A new instance of fragment FirstStepFragment.
//     */
//    // TODO: Rename and change types and number of parameters
//    public static FirstStepFragment newInstance(Integer index) {
//
//        if (INSTANCE == null) {
//            synchronized (HomeStepFragment.class) {
//                if (INSTANCE == null) {
//
//                    FirstStepFragment fragment = new FirstStepFragment();
//                    Bundle args = new Bundle();
//                    args.putInt(STEP_NAME, index);
//                    fragment.setArguments(args);
//
//                    INSTANCE = fragment;
//                }
//            }
//        }
//        return INSTANCE;
//
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            index = getArguments().getInt(STEP_NAME);
//        }
//    }
//
//    public Integer getIndex() {
//        return index;
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//
//        fragmentStepViewModel = StepFactory.initViewModel(requireActivity(), FragmentStepViewModel.class);
////        fragmentStepViewModel.setVm(fragmentStepViewModel.getVm().getValue());
//
//        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_first_step, null, false);
//        //binding.setVm(fragmentStepViewModel);
//        binding.setLifecycleOwner(getViewLifecycleOwner());
//
//        return binding.getRoot();
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//
//        super.onViewCreated(view, savedInstanceState);
//
//        // fragmentStepViewModel = new ViewModelProvider(requireActivity()).get(FragmentStepViewModel.class);
//
//
//        List<SpinerModel> municipalAddresses = new ArrayList<>();
//        ArrayAdapter municipalAdapter = new ArrayAdapter(getActivity(), R.layout.item, municipalAddresses);
//
//        List<SpinerModel> unityAddresses = new ArrayList<>();
//        ArrayAdapter unityAdapter = new ArrayAdapter(getActivity(), R.layout.item, unityAddresses);
//
//        binding.regionId.setIfSpinnerItemClickListener(new SpinnerItemClickHandler(getFragmentInstance()));
//
//        binding.municipal.setIfSpinnerItemClickListener(new SpinnerItemClickHandler(getFragmentInstance()));
//        binding.municipal.setAdapter(municipalAdapter);
//
//        binding.unity.setIfSpinnerItemClickListener(new SpinnerItemClickHandler(getFragmentInstance()));
//        binding.unity.setAdapter(unityAdapter);
//
////        binding.setVm(fragmentStepViewModel);
////        binding.setLifecycleOwner(getViewLifecycleOwner());
////        binding.executePendingBindings();
//
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//
//        List<SpinerModel> regionAddresses = new ArrayList<>();
//        ArrayAdapter regionAdapter = new ArrayAdapter(getActivity(), R.layout.item, regionAddresses);
//
//        LiveData<AddressingModel> addressingModelLiveData = fragmentStepViewModel.getVm();
//        LiveData<List<AddressModel>> listLiveData = addressViewModel.getRegions(1, 2);
//
//        FirstTrigger = new Observer<AddressingModel>() {
//            @Override
//            public void onChanged(AddressingModel addressingModel) {
//                Log.d("LLIII", "onChanged: 1");
//
//                binding.setVm(fragmentStepViewModel);
////                            binding.setLifecycleOwner(getViewLifecycleOwner());
////                            binding.executePendingBindings();
//            }
//        };
//
//        addressingModelLiveData.observe(getViewLifecycleOwner(), FirstTrigger);
//
//        listLiveData.observeForever(new Observer<List<AddressModel>>() {
//            @Override
//            public void onChanged(List<AddressModel> addressModels) {
//
//                Log.d("HOLDER", "onChanged: YES CHANGE YESY YESY");
//
//                regionAddresses.clear();
//                for (AddressModel address : addressModels) {
//
//                    SpinerModel m = new SpinerModel(address.getId(), address.getLocationName());
//                    m.setUrbanTypeId(address.getUrbanTypeId());
//                    m.setLocationTypeId(address.getLocationTypeId());
//                    regionAddresses.add(m);
//
//                }
//
//                binding.regionId.setAdapter(regionAdapter);
//                regionAdapter.notifyDataSetChanged();
//
//                listLiveData.removeObserver(this);
//            }
//        });
//
//        binding.executePendingBindings();
//    }
//
//    @Override
//    public void onSaveInstanceState(@NonNull Bundle outState) {
//        // super.onSaveInstanceState(outState);
//    }
//
//    @Override
//    public void onPause() {
//        fragmentStepViewModel.getVm().removeObservers(getViewLifecycleOwner());
//        super.onPause();
//    }
//
//    @Override
//    public void onStop() {
//        fragmentStepViewModel.getVm().removeObservers(getViewLifecycleOwner());
//        FirstTrigger = null;
//        super.onStop();
//    }
//
//    @Override
//    public void onDestroyView() {
//        this.fragmentStepViewModel.getVm().removeObservers(getViewLifecycleOwner());
//        FirstTrigger = null;
//        super.onDestroyView();
//    }
//
//
//    ///
//
//    @Override
//    public void onNextClicked(StepperLayout.OnNextClickedCallback callback) {
//        this.fragmentStepViewModel.getVm().removeObserver(FirstTrigger);
//        fragmentStepViewModel.getVm().removeObservers(getViewLifecycleOwner());
//
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                callback.goToNextStep();
//            }
//        }, 300);
//    }
//
//    @Override
//    public void onCompleteClicked(StepperLayout.OnCompleteClickedCallback callback) {
//
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                callback.complete();
//            }
//        }, 300);
//    }
//
//    @Override
//    public void onBackClicked(StepperLayout.OnBackClickedCallback callback) {
//        this.fragmentStepViewModel.getVm().removeObserver(FirstTrigger);
//        fragmentStepViewModel.getVm().removeObservers(getViewLifecycleOwner());
//
//        callback.goToPrevStep();
//    }
//
//    @Nullable
//    @Override
//    public VerificationError verifyStep() {
//        return null;
//    }
//
//    @Override
//    public void onSelected() {
//
//    }
//
//    @Override
//    public void onError(@NonNull VerificationError error) {
//
//    }
//
//    public AddressViewModel getAddressViewModel () {
//        return FirstStepFragment.this.addressViewModel;
//    }
//
//    public IThatFragment<FirstStepFragment, FragmentFirstStepBinding> getFragmentInstance() {
//        return new IThatFragment<>() {
//            @Override
//            public FirstStepFragment getFragment() {
//                return FirstStepFragment.this;
//            }
//
//            @Override
//            public FragmentFirstStepBinding getBinding() {
//                return FirstStepFragment.this.binding;
//            }
//        };
//    }
//
//}