package com.geostat.census_2024.ui.inquire_v1.stepper.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.ViewGroup;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.geostat.census_2024.architecture.inter.IfFragmentMoveListener;
import com.geostat.census_2024.ui.address.AddressViewModel;
import com.geostat.census_2024.ui.inquire_v1.stepper.InquireActivityV1;
import com.geostat.census_2024.ui.inquire_v1.stepper.factory.StepFactory;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.viewmodel.StepViewModel;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class StepperAdapter extends AbstractFragmentStepperAdapter {

    private static final String CURRENT_STEP_POSITION_KEY = "0";
    private final SparseArray<WeakReference<Fragment>> items = new SparseArray<>();

    public List<String> mFragmentListNames = new ArrayList<>(List.of("home", "first", "second", "end"));

    private final IfFragmentMoveListener fragmentInteractionListener;
    private final Integer isEnd;

    public StepperAdapter(FragmentManager fm, Context context, AddressViewModel viewModelProvider, StepFactory stepFactory) {
        super(fm, context);
        fragmentInteractionListener = (IfFragmentMoveListener) context;
        isEnd = ((InquireActivityV1) context).houseStatus;
        // FragmentStepViewModel fragmentStepViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(FragmentStepViewModel.class);

    }

    @Override
    @SuppressWarnings("unchecked")
    public int getItemPosition(@NonNull Object object) {
        int index = items.indexOfValue((WeakReference<Fragment>) object);
        if (index == -1) { return POSITION_NONE;} else { return index; }
    }

    @Override
    public Step createStep(int position) throws Fragment.InstantiationException {
        WeakReference<Fragment> fr = items.get(position);
        if (fr != null && fr.get() != null) {
            return (Step) fr.get();
        } else {
            return (Step) StepFactory.Init.create(mFragmentListNames.get(position), position);
        }
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        Fragment fr = (Fragment) super.instantiateItem(container, position);
        if (mFragmentListNames.size() > items.size() && items.get(position) == null) {
            items.put(position, new WeakReference<>(fr));
        }
//        Log.d("FRFR", "instantiateItem: " + items);
        return fr;
    }

    @Override
    public int getCount() {
        return mFragmentListNames.size();
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        WeakReference<Fragment> fr = items.get(position);
        if (fr != null && fr.get() != null) {
            fr.clear();
            items.remove(position);
        }
        super.destroyItem(container, position, object);
    }

    public Step getRegisteredFragment(int index) {
        return items.get(index) != null ? (Step) items.get(index).get() : createStep(index);
    }

    @NonNull
    @Override
    public StepViewModel getViewModel(@IntRange(from = 0) int position) {
        //Override this method to set Step title for the Tabs, not necessary for other stepper types
        StepViewModel.Builder builder = new StepViewModel.Builder(context);
        String title;

        switch (position) {
            case 0:

                title = "ყდა";
                builder.setTitle("ყდა").setEndButtonLabel("წინ").setBackButtonLabel("გაუქმება").setBackButtonStartDrawableResId(StepViewModel.NULL_DRAWABLE);
                break;

            case 1:
            case 2:

                title = (position == 1 )? "სამისამართე ნაწილი" : "შინამეურნეობის ნაწილი";
                builder.setTitle("სამისამართე ნაწილი").setEndButtonLabel("წინ").setBackButtonLabel("უკან");
                break;

            case 3:

                title = (isEnd != null && isEnd == 2) ? "რუკა" : "დასრულება";
                builder.setTitle(title).setEndButtonLabel(title).setBackButtonLabel("უკან");
                break;

            default: throw new IllegalArgumentException("Unsupported position: " + position);
        }

        if (fragmentInteractionListener != null) { fragmentInteractionListener.moveSetTitle(title); }

        return builder.create();
    }

    public void clear () {
        for (int key = 0; key < items.size(); key++) {
            WeakReference<Fragment> fr = items.get(key);
            if (fr != null ) {
                items.get(key).clear();
            }
        }
        items.clear();
    }
}


//    @Override
//    public long getItemId(int position) {
//        return super.getItemId(position);
//    }
//    @Override
//    public Step tryFind(int position) {
//        return (Step) mFragmentList.get(position);
//    }
//
//    public void addFragment(Fragment fragment) {
//        mFragmentList.add(fragment);
//        notifyDataSetChanged();
//    }
//
//    public void addFragment(Fragment fragment,int position) {
//        mFragmentList.add(position,fragment);
//        notifyDataSetChanged();
//    }
//
//
//    public void RemoveFragment(Fragment fragment) {
//        mFragmentList.remove(fragment);
//        notifyDataSetChanged();
//    }
//
//    public void RemoveFragment(int Id) {
//        mFragmentList.remove(Id);
//        notifyDataSetChanged();
//    }