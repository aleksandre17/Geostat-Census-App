package com.geostat.census_2024.ui.custom;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseBindingAdapter;
import androidx.databinding.InverseBindingListener;

import com.geostat.census_2024.data.model.SpinerModel;
import com.geostat.census_2024.architecture.inter.handler.ISpinnerItemClickHandler;

import java.util.List;

public class InterCompleteTextView extends androidx.appcompat.widget.AppCompatAutoCompleteTextView {

    private ISpinnerItemClickHandler iSpinnerItemClickHandler;


    public InterCompleteTextView(@NonNull Context context) {
        super(context);
    }

    public InterCompleteTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
    public InterCompleteTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setIfSpinnerItemClickListener(ISpinnerItemClickHandler handler)
    {
        iSpinnerItemClickHandler = handler;
    }

//    public void trigger(Object item) {
//        Log.d("HY", "trigger2: ");
//        if (mOnStopTrackEventListener != null) {
//            Log.d("HY", "trigger: ");
//            mOnStopTrackEventListener.onStopTrack(item);
//        }
//    }

    public void newText(String text) {
        this.replaceText(text);
    }

    @BindingAdapter(value = { "ttext", "nextIndex",  "viewList", "isMunicipal", "ttextAttrChanged"}, requireAll = false)
    public static void setTtext(InterCompleteTextView view, Integer result, String nexIndex, List<? extends View> viewList, @Nullable Boolean isMunicipal, InverseBindingListener listener) {

        Log.d("afterTextChanged", "setTtext: " + result);
        if (result != null) {

            /// view.getTag()
            if (view.getTag() == null && view.getAdapter() != null) {
                for (int i=0; i<view.getAdapter().getCount(); i++) {
                    SpinerModel m = (SpinerModel) view.getAdapter().getItem(i);
                    if (m.getKey().equals(result)) {
                        view.setText(m.getValue());
                        break;
                    }
                }
            } else if (view.getTag() != null && view.getTag() instanceof SpinerModel) {
                SpinerModel m = (SpinerModel) view.getTag();
                if (!view.getText().toString().equals(m.getValue())) {
                    result = m.getKey();
                    view.setText(m.getValue());
                }
            }
        }

        view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View NView, int i, long l) {
                Log.d("afterTextChanged", "setOnItemClickListener: ");

                Object item = adapterView.getItemAtPosition(i); view.setTag(item);

                Log.d("ITEMMM", "onItemClick: " + ((SpinerModel) item).getUrbanTypeId());

                if (view.iSpinnerItemClickHandler != null) {
                    view.iSpinnerItemClickHandler.ifSpinnerItemClick(adapterView, view, i, l, nexIndex, viewList, item);
                    if (isMunicipal) {
                        view.iSpinnerItemClickHandler.ifSpinnerNesItemClick(adapterView, view, i, l, nexIndex, viewList, item);
                    }
                }

                 listener.onChange();
            }
        });

        final String[] test = {""};

        view.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                test[0] = charSequence.toString();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Log.d("afterTextChanged", "afterTextChanged: " + editable.toString());
//                // listener.onChange();
                if (editable.toString().isEmpty() && !test[0].isEmpty()) {
                    view.setTag(new Object());
                    view.setText("");
                    listener.onChange();

                    Log.d("afterTextChanged", "afterTextChanged: " + editable.toString());
                }
            }
        });

//        view.setOnStopTrackEventListener(new InterCompleteTextView.OnStopTrackEventListener() {
//            @Override
//            public void onStopTrack(Object item) {
//                if (item instanceof SpinerModel) {
//
//                    Log.d("TESTI", "onStopTrack: ");
//                    view.setTag(item);
//                    listener.onChange();
//                }
//
//            }
//        });

    }

    @InverseBindingAdapter(attribute = "ttext", event = "ttextAttrChanged")
    public static Integer getTtext(InterCompleteTextView view) {
        // Log.d("afterTextChanged", "getTtext: ");
        Integer value = 0;
        if (view.getTag() instanceof SpinerModel) {
            SpinerModel m = (SpinerModel) view.getTag();
            value = m.getKey();
            Log.d("afterTextChanged", "getText: " +  value);
        }
        return value;
    }

//    @BindingAdapter("ttextAttrChanged")
//    public static void setListeners(InterCompleteTextView view, InverseBindingListener listener) {
//        Log.d("TESTI", "setTextListener: ");
//
//        view.setOnStopTrackEventListener(new InterCompleteTextView.OnStopTrackEventListener() {
//            @Override
//            public void onStopTrack(Object item) {
//                if (item instanceof SpinerModel) {
//
//                    Log.d("TESTI", "onStopTrack: ");
//                    view.setTag(item);
//                    listener.onChange();
//                }
//
//            }
//        });
//
//    }
}
