package com.geostat.census_2024.ui.inquire_v1.stepper.handler;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.androidbuts.multispinnerfilter.KeyPairBoolData;
import com.geostat.census_2024.data.local.entities.AddressEntity;
import com.geostat.census_2024.databinding.FragmentFirstStepBinding;
import com.geostat.census_2024.architecture.inter.IThatFragment;
import com.geostat.census_2024.architecture.inter.IfSpinnerClickHandler;
import com.geostat.census_2024.ui.inquire_v1.stepper.FirstStepFragment;
import com.geostat.census_2024.ui.inquire_v1.stepper.model.FragmentStepViewModel;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SpinnerItemClickListener implements IfSpinnerClickHandler {

    private final FragmentStepViewModel fragmentStepViewModel;
    private final IThatFragment<FirstStepFragment, FragmentFirstStepBinding> fragment;

    public SpinnerItemClickListener(IThatFragment<FirstStepFragment, FragmentFirstStepBinding> fragment, FragmentStepViewModel fragmentStepViewModel) {
        this.fragmentStepViewModel = fragmentStepViewModel;
        this.fragment = fragment;
    }

    public String checkIfIsTbilisi (@Nullable KeyPairBoolData selectedItem) {
        String isTbilisi = "FALSE"; if (selectedItem != null && selectedItem.getObject() instanceof Integer && selectedItem.getObject().equals(8)) return "TRUE";
        return isTbilisi;
    }

    @Override
    public void spinnerClick(KeyPairBoolData selectedItem, @Nullable List<? extends String> viewList, Integer nextIndex, @Nullable String clear) {

        if (nextIndex == null) return;

        LiveData<List<AddressEntity>> r = getRegions(selectedItem, nextIndex);
        r.observeForever(new Observer<>() {
            @Override
            public void onChanged(List<AddressEntity> addressEntities) {

                List<KeyPairBoolData> entries = addressEntities
                        .stream()
                        .map(address ->
                                new KeyPairBoolData(address.getId(), address.getLocationName(), false, address.getLocationTypeId()))
                        .collect(Collectors.toList());

                try {

                    if (viewList != null) {
                        Method method = fragmentStepViewModel.getClass().getMethod(viewList.get(1), List.class);
                        method.invoke(fragmentStepViewModel, entries);

                        if (clear != null) {
                            Method rm = fragmentStepViewModel.getClass().getMethod(viewList.get(1) + "R", Integer.class);
                            rm.invoke(fragmentStepViewModel, 0);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("THATDAA", "onChangedERRR: " + e);
                }

                r.removeObserver(this);
            }
        });

    }

    @Override
    public void spinnerNesItemClick(KeyPairBoolData selectedItem, List<? extends String> viewList, Integer nextIndex, @Nullable String clear) {
        if (nextIndex == null) return;
        int isTbilisi = (selectedItem.getObject().equals(8)) ? 2 : 0;
        LiveData<List<AddressEntity>> r = getRegionsAlter(selectedItem, isTbilisi);


        r.observeForever(new Observer<>() {
            @Override
            public void onChanged(List<AddressEntity> addressEntities) {

                Map<Integer, List<AddressEntity>> splitedData = addressEntities.stream().collect(Collectors.groupingBy(AddressEntity::getUrbanTypeId, Collectors.toList()));

                if (viewList != null && isTbilisi != 0 && clear != null) {
                    try {
                        Method rm = fragmentStepViewModel.getClass().getMethod(viewList.get(0) + "R", Integer.class);
                        rm.invoke(fragmentStepViewModel, 0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                splitedData.forEach((integer, addresses1) -> {

                    int index = integer == 1 ? 2 : 3;

                    List<KeyPairBoolData> entries = addresses1
                            .stream()
                            .map(address -> new KeyPairBoolData(address.getId(), address.getLocationName(), false, address.getLocationTypeId()))
                            .collect(Collectors.toList());

                    try {

                        if (viewList != null) {
                            Method method = fragmentStepViewModel.getClass().getMethod(viewList.get(index), List.class);
                            method.invoke(fragmentStepViewModel, entries);

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d("THATDAA", "onChangedERRR: " + e);
                    }
                });

                r.removeObserver(this);
            }
        });

    }

    @Override
    public void Erase(List<? extends String> viewList) {

        try {
            if (viewList != null) {
                Method rm = fragmentStepViewModel.getClass().getMethod(viewList.get(0) + "R", Integer.class);
                rm.invoke(fragmentStepViewModel, 1);

            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.d("THATDAA", "onChangedERRR: " + e);
        }
    }


    public LiveData<List<AddressEntity>> getRegions (KeyPairBoolData selectedItem, Integer nextIndex) {
        return fragment.getFragment().getAddressViewModel().getRegions((int) selectedItem.getId(), nextIndex);
    }

    public LiveData<List<AddressEntity>> getRegionsAlter (KeyPairBoolData selectedItem, Integer urbanType) {
        return fragment.getFragment().getAddressViewModel().getRegionsAlter((int) selectedItem.getId(), urbanType);
    }
}
