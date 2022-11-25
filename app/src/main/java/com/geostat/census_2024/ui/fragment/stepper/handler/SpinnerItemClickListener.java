package com.geostat.census_2024.ui.fragment.stepper.handler;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.androidbuts.multispinnerfilter.KeyPairBoolData;
import com.geostat.census_2024.data.local.entities.Address;
import com.geostat.census_2024.databinding.FragmentFirstStepBinding;
import com.geostat.census_2024.inter.IThatFragment;
import com.geostat.census_2024.inter.IfSpinnerClickHandler;
import com.geostat.census_2024.ui.fragment.stepper.FirstStepFragment;
import com.geostat.census_2024.ui.fragment.stepper.model.FragmentStepViewModel;

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

        LiveData<List<Address>> r = getRegions(selectedItem, nextIndex);
        r.observeForever(new Observer<>() {
            @Override
            public void onChanged(List<Address> addresses) {

                List<KeyPairBoolData> entries = addresses
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
        LiveData<List<Address>> r = getRegionsAlter(selectedItem, isTbilisi);


        r.observeForever(new Observer<>() {
            @Override
            public void onChanged(List<Address> addresses) {

                Map<Integer, List<Address>> splitedData = addresses.stream().collect(Collectors.groupingBy(Address::getUrbanTypeId, Collectors.toList()));

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


    public LiveData<List<Address>> getRegions (KeyPairBoolData selectedItem, Integer nextIndex) {
        return fragment.getFragment().getAddressViewModel().getRegions((int) selectedItem.getId(), nextIndex);
    }

    public LiveData<List<Address>> getRegionsAlter (KeyPairBoolData selectedItem, Integer urbanType) {
        return fragment.getFragment().getAddressViewModel().getRegionsAlter((int) selectedItem.getId(), urbanType);
    }
}
