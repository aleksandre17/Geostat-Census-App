package com.geostat.census_2024.ui.fragment.stepper;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.geostat.census_2024.R;
import com.geostat.census_2024.data.model.AddressingModel;
import com.geostat.census_2024.data.model.HouseHoldModel;
import com.geostat.census_2024.databinding.FragmentSecondStepBinding;
import com.geostat.census_2024.databinding.TestItemBinding;
import com.geostat.census_2024.ui.fragment.stepper.adapter.HolderAdapter;
import com.geostat.census_2024.ui.fragment.stepper.factory.StepFactory;
import com.geostat.census_2024.ui.fragment.stepper.inter.Stepper;
import com.geostat.census_2024.ui.fragment.stepper.model.FragmentStepViewModel;
import com.stepstone.stepper.BlockingStep;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SecondStepFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SecondStepFragment extends Fragment implements BlockingStep, Stepper {

    private static volatile SecondStepFragment INSTANCE;

    private FragmentSecondStepBinding binding;
    private FragmentStepViewModel fragmentStepViewModel;


    private List<HouseHoldModel> items;

    private HolderAdapter listItemAdapter;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String STEP_NAME = "STEP_NAME_INDEX";

    // TODO: Rename and change types of parameters
    private Integer index;

    // ListItemAdapter listItemAdapter;

    public SecondStepFragment() {
        // Required empty public constructor
//        fragmentStepViewModel = StepFactory.getStepViewModel();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param index Parameter 1.
     * @return A new instance of fragment SecondStepFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SecondStepFragment newInstance(Integer index) {

        if (INSTANCE == null) { synchronized (SecondStepFragment.class) { if (INSTANCE == null) { INSTANCE = initFr(index); } } }
        return INSTANCE;
    }

    @NonNull
    private static SecondStepFragment initFr (int index) {
        SecondStepFragment fragment = new SecondStepFragment();
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

        fragmentStepViewModel = StepFactory.initViewModel(requireActivity(), FragmentStepViewModel.class);

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_second_step, null, false);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = binding.recyclerView;

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        listItemAdapter = new HolderAdapter(requireActivity());

        listItemAdapter.setRemListener(index -> {
            AlertDialog.Builder may = new AlertDialog.Builder(requireActivity());
            may.setTitle("შეტყობინება");
            may.setMessage("გსურთ, შინამეურნეობის წაშლა?");
            may.setPositiveButton("დიახ", new DialogInterface.OnClickListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Objects.requireNonNull(fragmentStepViewModel.getVm().getValue()).remItem(index);
                    listItemAdapter.notifyDataSetChanged();
                }
            });
            may.setNegativeButton("არა", (dialogInterface, i) -> dialogInterface.cancel());
            may.create();may.show();
        });

        recyclerView.setAdapter(listItemAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();

        LiveData<AddressingModel> addressingModelLiveData = fragmentStepViewModel.getVm();
        Observer<AddressingModel> secondTrigger = addressingModel -> {

            if (Arrays.asList(6, 7).contains(addressingModel.getBuildingType()) && addressingModel.getHouseHold().isEmpty()) {
                binding.txtt.setVisibility(View.VISIBLE);
                binding.ttttt.setVisibility(View.VISIBLE);
            } else {
                if (!Arrays.asList(6, 7).contains(addressingModel.getBuildingType())) {
                    binding.txtt.setVisibility(View.GONE);
                }
                binding.ttttt.setVisibility(View.GONE);
            }

            listItemAdapter.submit(addressingModel.getHouseHold(), addressingModel);
        };

        addressingModelLiveData.observe(getViewLifecycleOwner(), secondTrigger);

        binding.button2.setOnClickListener(view -> {
            AlertDialog.Builder may = new AlertDialog.Builder(requireActivity());
            may.setTitle("შეტყობინება");
            may.setMessage("გსურთ ახალი შინამეურნეობის დამატება?");
            may.setPositiveButton("დიახ", (dialogInterface, i) -> {
                int newIndex = Objects.requireNonNull(addressingModelLiveData.getValue()).getHouseHold().size();
                int index = LocalDateTime.now().getSecond();
                fragmentStepViewModel.trigger(newIndex, index);
            });
            may.setNegativeButton("არა", (dialogInterface, i) -> dialogInterface.cancel());
            may.create();may.show();
        });

        if (addressingModelLiveData.getValue() != null && Arrays.asList(6, 7).contains(addressingModelLiveData.getValue().getBuildingType())) {
            String nonresidentialPlacesNum = Objects.requireNonNullElse(addressingModelLiveData.getValue().getInstitutionSpaceNum(), "").toString();
            binding.nonresidentialPlacesNum.setText(nonresidentialPlacesNum);
            binding.nonresidentialPlacesNum.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                @Override
                public void afterTextChanged(Editable editable) {
                    try {
                        if (!editable.toString().isEmpty()) binding.nonresidentialPlacesNumFiled.setError(null); binding.nonresidentialPlacesNumFiled.setErrorEnabled(false);
                        Integer nonresidentialPlacesNum = Integer.parseInt(editable.toString());
                        addressingModelLiveData.getValue().setInstitutionSpaceNum(nonresidentialPlacesNum);
                    } catch (NumberFormatException e) {
                        addressingModelLiveData.getValue().setInstitutionSpaceNum(null);
                        e.printStackTrace();
                    }
                }
            });
        }

        binding.executePendingBindings();
    }

    @Override
    public void onNextClicked(StepperLayout.OnNextClickedCallback callback) {
        fragmentStepViewModel.getVm().removeObservers(getViewLifecycleOwner());
        //new Handler().postDelayed(callback::goToNextStep, 300);

        callback.goToNextStep();
    }

    @Override
    public void onCompleteClicked(StepperLayout.OnCompleteClickedCallback callback) {
        fragmentStepViewModel.getVm().removeObservers(getViewLifecycleOwner());
        new Handler().postDelayed(callback::complete, 300);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        // super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentStepViewModel.getVm().removeObservers(getViewLifecycleOwner());
    }

    @Override
    public void onBackClicked(StepperLayout.OnBackClickedCallback callback) {
        callback.goToPrevStep();
    }

    @Nullable
    @Override
    public VerificationError verifyStep() {

        AddressingModel m = fragmentStepViewModel.getVm().getValue();
        if (m != null) {
            if ((Arrays.asList(6, 7).contains(m.getBuildingType())) && (m.getInstitutionSpaceNum() == null || m.getInstitutionSpaceNum().equals(0))) {
                return new VerificationError("სავალდებულო ველი!");
            }
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
            if ((m.getInstitutionSpaceNum() == null || m.getInstitutionSpaceNum().equals(0))) {
                binding.nonresidentialPlacesNumFiled.setError("სავალდებულო ველი!");
            }
        }
    }

    @Override
    public void clear() {
        INSTANCE = null;
    }


}


//listItemAdapter.setListener(new IfHolderChangeHandler() {
//@Override
//public void ifHolderChange(Integer index, HouseHoldModel holder) {
//        Log.d("TRYTRY", "nesItemChange: " + index);
//        fragmentStepViewModel.setFlag(true);
//        fragmentStepViewModel.setHouseHold(index, holder);
//        }
//        });
// new Handler().postDelayed(new Runnable() { @Override public void run() { callback.goToNextStep(); } }, 19);