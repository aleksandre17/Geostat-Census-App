package com.geostat.census_2024.ui.fragment.stepper.adapter;

import android.content.Context;
import android.util.Log;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

import com.stepstone.stepper.Step;
import com.stepstone.stepper.adapter.StepAdapter;
import com.stepstone.stepper.viewmodel.StepViewModel;


abstract class AbstractFragmentStepperAdapter extends FragmentStatePagerAdapter implements StepAdapter {

    @NonNull
    protected final Context context;

    public AbstractFragmentStepperAdapter(@NonNull FragmentManager fm, @NonNull Context context) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.context = context;
    }

    @Override
    public final Fragment getItem(@IntRange(from = 0) int position) {
        Log.d("FRFR", "getItem: " + ((Fragment)createStep(position)));
//        Fragment fr = new Fragment();
//        try {
//            if (createStep(position) != null) {
//                fr = (Fragment) createStep(position);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        return (Fragment) createStep(position);
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public Step findStep(@IntRange(from = 0) int position) {
//        Log.d("TAG", "findStep: " + position);
//        //String fragmentTag =  "android:switcher:" + R.id.ms_stepPager + ":" + this.getItemId(position);
//        Log.d("TAG", "findStep: " + mFragmentManager.getFragments());
//        Step step = null;
//        try {
//            step = (Step) getItem(position);
//        } catch (ClassCastException | Fragment.InstantiationException e) {
//            e.printStackTrace();
//        }
//        return step;

        return this.getRegisteredFragment(position);
    }

    /** {@inheritDoc} */
    @NonNull
    @Override
    public StepViewModel getViewModel(@IntRange(from = 0) int position) {
        return new StepViewModel.Builder(context).create();
    }

    /** {@inheritDoc} */
    @Override
    public final PagerAdapter getPagerAdapter() {
        return this;
    }

    public abstract Step getRegisteredFragment(int position);
}
