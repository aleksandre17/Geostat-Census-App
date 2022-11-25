package com.geostat.census_2024.ui.fragment.stepper.factory;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.geostat.census_2024.ui.address.AddressViewModel;
import com.geostat.census_2024.ui.fragment.stepper.EndStepFragment;
import com.geostat.census_2024.ui.fragment.stepper.FirstStepFragment;
import com.geostat.census_2024.ui.fragment.stepper.HomeStepFragment;
import com.geostat.census_2024.ui.fragment.stepper.SecondStepFragment;
import com.geostat.census_2024.ui.fragment.stepper.model.FragmentStepViewModel;

import java.lang.ref.WeakReference;

interface Init<I> {
    I instance();
}
public class StepFactory {

    private static WeakReference<AddressViewModel> addressViewModel;
    private static WeakReference<FragmentStepViewModel> stepViewModel;

    public static  <T extends ViewModel> T initViewModel(ViewModelStoreOwner that, Class<T> clazz) {
        return new ViewModelProvider(that).get(clazz);
    }

    public static class Init {

        public static Fragment create (String name, Integer index) throws Fragment.InstantiationException {
            switch (name) {
                case "home":
                    return HomeStepFragment.newInstance(index);
                case "first":
                    return FirstStepFragment.newInstance(index);
                case "second":
                    return SecondStepFragment.newInstance(index);
                case "end":
                    return EndStepFragment.newInstance(index);
                default: throw new Fragment.InstantiationException("Not DINED", null);
            }
        }
    }

    public static AddressViewModel getAddressViewModel() {
        return addressViewModel.get();
    }

    public static void setAddressViewModel(AddressViewModel addressViewModel) {
        StepFactory.addressViewModel = new WeakReference<>(addressViewModel);
    }

    public static FragmentStepViewModel getStepViewModel() {
        return stepViewModel.get();
    }

    public static void setStepViewModel(FragmentStepViewModel stepViewModel) {
        StepFactory.stepViewModel = new WeakReference<>(stepViewModel);
    }

    public static void clear () {
        StepFactory.stepViewModel.clear();
        StepFactory.addressViewModel.clear();
    }

    //    public static <I extends Fragment & Step & Init<I>> I create (I fragment, Class<I> clazz) {
//        if (clazz.isInstance(fragment)) {
//            return fragment.instance();
//        }
//    }
}
