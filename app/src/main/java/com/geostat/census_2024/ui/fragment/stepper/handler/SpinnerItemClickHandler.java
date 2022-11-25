package com.geostat.census_2024.ui.fragment.stepper.handler;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.geostat.census_2024.R;
import com.geostat.census_2024.data.local.entities.Address;
import com.geostat.census_2024.data.model.SpinerModel;
import com.geostat.census_2024.databinding.FragmentFirstStepBinding;
import com.geostat.census_2024.handlers.ISpinnerItemClickHandler;
import com.geostat.census_2024.inter.IThatFragment;
import com.geostat.census_2024.ui.custom.InterCompleteTextView;
import com.geostat.census_2024.ui.fragment.stepper.FirstStepFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SpinnerItemClickHandler implements ISpinnerItemClickHandler {

    FirstStepFragment fragment;
    FragmentFirstStepBinding fragmentFirstStepBinding;

    public SpinnerItemClickHandler(IThatFragment<FirstStepFragment, FragmentFirstStepBinding> fragment) {
        this.fragment = fragment.getFragment();
        this.fragmentFirstStepBinding = fragment.getBinding();
    }

    @Override
    public void ifSpinnerItemClick(AdapterView<?> adapterView, View nView, int i, long l, String nextIndex, @Nullable List<? extends View> viewList, Object item) {
        InterCompleteTextView that = (InterCompleteTextView) nView;

        Log.d("TAGIF", "ifSpinnerItemClick: " + nextIndex);

        if (viewList != null) {
            viewList.forEach(new Consumer<View>() {
                @Override
                public void accept(View view) {
                    if (view instanceof InterCompleteTextView) {
                        ((InterCompleteTextView) view).newText("");
                        ((InterCompleteTextView) view).setAdapter(new ArrayAdapter<>(fragment.getActivity(), R.layout.item, new ArrayList<>()));
                    }
                }
            });
        }
        if (item instanceof SpinerModel) {
            LiveData<List<Address>> listLiveData;
            listLiveData = fragment.getAddressViewModel().getRegions(((SpinerModel) item).getKey(), Integer.parseInt(nextIndex));
            listLiveData.observeForever(new Observer<List<Address>>() {
                @Override
                public void onChanged(List<Address> addresses) {
                    List<SpinerModel> spinerModels = new ArrayList<>();
                    ArrayAdapter<SpinerModel> arrayAdapter = new ArrayAdapter<>(fragment.getActivity(), R.layout.item,spinerModels);

                    for (Address single: addresses) {
                        Log.d("TAGIF", "onChanged: " + single.getLocationName());
                        SpinerModel m = new SpinerModel(single.getId(), single.getLocationName());
                        m.setUrbanTypeId(single.getUrbanTypeId());
                        m.setLocationTypeId(single.getLocationTypeId());
                        spinerModels.add(m);
                    }

                    if (viewList.get(0) instanceof InterCompleteTextView) {
                        ((InterCompleteTextView) viewList.get(0)).setAdapter(arrayAdapter);
                        arrayAdapter.notifyDataSetChanged();
                    }

                    listLiveData.removeObserver(this);
                }
            });
        }
    }

    @Override
    public void ifSpinnerNesItemClick(AdapterView<?> adapterView, View nView, int i, long l, String nexIndex, @Nullable List<? extends View> viewList, Object item) {

        if (item instanceof SpinerModel) {
            LiveData<List<Address>> listLiveData;
            listLiveData = fragment.getAddressViewModel().getRegionsAlter(((SpinerModel) item).getKey(), 5);
            listLiveData.observeForever(new Observer<List<Address>>() {
                @Override
                public void onChanged(List<Address> addresses) {
                    List<SpinerModel> spinerModels = new ArrayList<>();
                    ArrayAdapter<SpinerModel> arrayAdapter = new ArrayAdapter<>(fragment.getActivity(), R.layout.item,spinerModels);

                    for (Address single: addresses) {
                        Log.d("CITY", "onChanged: " + single.getLocationName());
                         spinerModels.add(new SpinerModel(single.getId(), single.getLocationName()));
                    }

                    if (viewList.get(1) instanceof InterCompleteTextView) {
                        ((InterCompleteTextView) viewList.get(1)).setAdapter(arrayAdapter);
                        arrayAdapter.notifyDataSetChanged();
                    }

                    listLiveData.removeObserver(this);
                }
            });

//            if (((SpinerModel) item).getUrbanTypeId() == 1) {
//
//
//                LiveData<List<Address>> listLiveData2;
//                listLiveData2 = fragment.getAddressViewModel().getRegionsAlter(((SpinerModel) item).getKey(), 5, 2);
//                listLiveData2.observeForever(new Observer<List<Address>>() {
//                    @Override
//                    public void onChanged(List<Address> addresses) {
//                        List<SpinerModel> spinerModels2 = new ArrayList<>();
//                        ArrayAdapter<SpinerModel> arrayAdapter2 = new ArrayAdapter<>(fragment.getActivity(), R.layout.item,spinerModels2);
//
//                        for (Address single: addresses) {
//                            Log.d("CITY", "onChanged: " + single.getLocationName());
//
//                            spinerModels2.add(new SpinerModel(single.getId(), single.getLocationName()));
//
//                        }
//
//                        if (viewList.get(2) instanceof InterCompleteTextView) {
//                            ((InterCompleteTextView) viewList.get(2)).setAdapter(arrayAdapter2);
//                            arrayAdapter2.notifyDataSetChanged();
//                        }
//
//                        listLiveData.removeObserver(this);
//                    }
//                });
//            }
        }
    }
}
