package com.geostat.census_2024.ui.custom;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseBindingAdapter;
import androidx.databinding.InverseBindingListener;

import com.androidbuts.multispinnerfilter.KeyPairBoolData;
import com.androidbuts.multispinnerfilter.SingleSpinnerListener;
import com.androidbuts.multispinnerfilter.SingleSpinnerSearch;
import com.geostat.census_2024.inter.IfSpinnerClickHandler;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

public class CustomSpinner extends SingleSpinnerSearch {

    private IfSpinnerClickHandler clickListener;

    public CustomSpinner(Context context) {
        super(context);
    }

    public CustomSpinner(Context arg0, AttributeSet arg1) {
        super(arg0, arg1);
    }

    public CustomSpinner(Context arg0, AttributeSet arg1, int arg2) {
        super(arg0, arg1, arg2);
    }

    public CustomSpinner setClickListener(IfSpinnerClickHandler listener) {
        this.clickListener = listener;
        return this;
    }

    @BindingAdapter(value = {"entries"}, requireAll = false)
    public static void setEntries(CustomSpinner spinner, List<KeyPairBoolData> ldata ) {
        if (ldata != null && ldata.size() > 0) {
            spinner.setItems(ldata);
        }
    }

    @BindingAdapter(value = {"selectedItemPosition", "modelId", "viewList", "nextIndex", "isMunicipal",  "selectedItemPositionAttrChanged" }, requireAll = false)
    public static void setSelectedItemPosition(CustomSpinner spinner, Integer selected, Integer modelId, @Nullable List<? extends String > viewList, @Nullable Integer nexIndex, @Nullable Boolean isMunicipal, InverseBindingListener listener) {

        if (spinner.getListener() == null) {

//            if (nexIndex != null) {
//                if (nexIndex.equals(3) || nexIndex.equals(4)) {
//                    spinner.setEnabled(false);
//                }
//            }

            spinner.setListener(new SingleSpinnerListener() {
                @Override
                public void onItemsSelected(KeyPairBoolData selectedItem, String clear) {

                    spinner.setSetSelectedId(Math.toIntExact(selectedItem.getId()));
                    listener.onChange();

                    /// NEW
                    if (spinner.getParent().getParent() instanceof TextInputLayout) { ((TextInputLayout) spinner.getParent().getParent()).setError(null); ((TextInputLayout) spinner.getParent().getParent()).setErrorEnabled(false); }
                    ///

                    new Handler().postDelayed(() -> {
                        if (spinner.clickListener != null) {
                            if (selectedItem.getObject() instanceof Integer) {
                                if (!selectedItem.getObject().equals(8)) {
                                    spinner.clickListener.spinnerClick(selectedItem, viewList, nexIndex, clear);
                                }
                            }
                            if (isMunicipal != null && isMunicipal) {
                                spinner.clickListener.spinnerNesItemClick(selectedItem, viewList, nexIndex, clear);
                            }
                        }
                    }, 100);
                }

                @Override
                public void onClear() {
                    spinner.clickListener.Erase(viewList);
                }
            });
        }

        // Log.d("TAG", "setSelectedItemPosition: " + selected);
        //        if (ldata != null) {
        //            Log.d("THATDAA", "setEntries: " + ldata);
        //
        //            spiner.setItems(ldata, new SingleSpinnerListener() {
        //                @Override
        //                public void onItemsSelected(KeyPairBoolData selectedItem) {
        //                    spiner.setTag(selectedItem);
        //                    listener.onChange();
        //                }
        //
        //                @Override
        //                public void onClear() {
        //
        //                }
        //            });
        //        }
    }

    @InverseBindingAdapter(attribute = "selectedItemPosition", event = "selectedItemPositionAttrChanged")
    public static Integer getSelectedItemPosition(CustomSpinner spinner) {
        Integer id = null;
        //        if (spinner.getTag() instanceof KeyPairBoolData) {
        //            id = Math.toIntExact(((KeyPairBoolData) spinner.getTag()).getId());
        //            Log.d("THATDAA", "getSelectedItemPosition: " + id);
        //        }
        if (spinner.getSetSelectedId() != null) {
            id = spinner.getSetSelectedId();
            // Log.d("THATDAA", "getSelectedItemPosition: " + id);
        }
        return id;
    }
}
