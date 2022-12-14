package com.geostat.census_2024.ui.inquire_v1.stepper.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.Observable;
import androidx.databinding.ObservableList;
import androidx.recyclerview.widget.RecyclerView;

import com.geostat.census_2024.BR;
import com.geostat.census_2024.R;
import com.geostat.census_2024.architecture.inter.handler.IfHolderRemItemHandler;
import com.geostat.census_2024.data.model.AddressingModel;
import com.geostat.census_2024.data.model.HouseHoldModel;
import com.geostat.census_2024.databinding.TestItemBinding;
import com.github.florent37.expansionpanel.ExpansionLayout;

import java.util.List;

public class HolderAdapter extends RecyclerView.Adapter<HolderAdapter.ItemViewHolder> {

    public List<HouseHoldModel> m;

    public AddressingModel mmm;

    private IfHolderRemItemHandler Listener;

    public HolderAdapter(Activity activity) {
        setHasStableIds(true);
    }

    public void setRemListener (IfHolderRemItemHandler Listener) {
        this.Listener = Listener;
    }

    @Override
    public long getItemId(int position) {
        return m.get(position).getId();
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TestItemBinding testItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.test_item, parent, false);

        return new ItemViewHolder(testItemBinding, Listener);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void submit(final ObservableList<HouseHoldModel> model, AddressingModel mm) {
        mmm = mm;
        this.m = model;
        notifyDataSetChanged();
        notifyItemRangeInserted(0, model.size());
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {

        HouseHoldModel houseHoldModel = m.get(position);
        HouseHoldModel tmp = null;

        try {
            tmp = (HouseHoldModel) houseHoldModel.clone();
            holder.bindItem(houseHoldModel, mmm, m, position);

            holder.binding.setVm(houseHoldModel);
            holder.binding.executePendingBindings();

        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return this.m != null ? this.m.size() : 0;
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull ItemViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
    }


    /// ITEM

    public static class ItemViewHolder extends RecyclerView.ViewHolder{

        TestItemBinding binding;
        IfHolderRemItemHandler remListener;

        public ItemViewHolder(TestItemBinding binding, IfHolderRemItemHandler remListener) {
            super(binding.getRoot());
            this.binding = binding;
            this.remListener = remListener;

        }

        @SuppressLint("SetTextI18n")
        void bindItem(HouseHoldModel item, AddressingModel mmm, List<HouseHoldModel> m, int index) {

            if (m.size() > 1) { binding.append.setText("??????????????????????????????????????? " + (index + 1)); } else { binding.append.setText("???????????????????????????????????????");}

            ExpansionLayout layout = binding.expansionLayout;

            new Handler().postDelayed(() -> {
                if (m.size() == index + 1) {
                    if (!layout.isExpanded()) { layout.expand(false); }
                } else {
                    if (layout.isExpanded()) { layout.collapse(false); }
                }
            }, 100);

            binding.trash.setOnClickListener(view -> remListener.holderRemItemExec(index));

            item.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
                @Override
                public void onPropertyChanged(Observable sender, int propertyId) {
                    if(propertyId == BR.membersNum) {
                        HouseHoldModel houseHoldModel= (HouseHoldModel) sender;
                        if (houseHoldModel.getMembersNum() != null && houseHoldModel.getMembersNum() > 9) {
                            binding.membersNumFiled.setErrorTextColor(ColorStateList.valueOf(Color.BLACK));
                            binding.membersNumFiled.setError("?????????????????????????????? ????????????????????? ???????????????????????????");
                            binding.membersNumFiled.setErrorEnabled(true);
                        } else {
                            binding.membersNumFiled.setError(null);
                            binding.membersNumFiled.setErrorEnabled(false);
                        }
                    }
                }
            });

        }
    }
}


//return newProduct.getLastName() == oldProduct.getLastName() && newProduct.getFirstName() == oldProduct.getFirstName() && newProduct.getFatherName() == oldProduct.getFatherName() && newProduct.getId() == oldProduct.getId();