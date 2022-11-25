package com.geostat.census_2024.ui.custom;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseBindingAdapter;
import androidx.databinding.InverseBindingListener;

import com.geostat.census_2024.data.local.entities.ISpinner;
import com.geostat.census_2024.handlers.IfInterSpinnerClickHandler;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class InterSpinner extends androidx.appcompat.widget.AppCompatAutoCompleteTextView {

    @Nullable
    private Integer selected = null;
    private IfInterSpinnerClickHandler clickListener;

    public InterSpinner(Context context) {
        super(context);
    }

    public InterSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InterSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setClickListener(IfInterSpinnerClickHandler listener) {
        this.clickListener = listener;
    }

    @BindingAdapter("valueAttrChanged")
    public static void setListener(InterSpinner view, InverseBindingListener listener) {
        view.setText("");
        view.setOnItemClickListener((adapterView, view2, i, l) -> {
            if (adapterView.getItemAtPosition(i) instanceof ISpinner){
                if (!Objects.equals(view.selected, ((ISpinner) adapterView.getItemAtPosition(i)).getId())){
                    view.selected = ((ISpinner) adapterView.getItemAtPosition(i)).getId();

                    /// NEW
                    if (view.getParent().getParent() instanceof TextInputLayout) { ((TextInputLayout) view.getParent().getParent()).setError(null); }
                    ///

                    listener.onChange();
                    new Handler().postDelayed(() -> {
                        if (view.clickListener != null) {
                            view.clickListener.interSpinnerClick((ISpinner) adapterView.getItemAtPosition(i));
                        }
                    }, 1000);
                }
            }
        });
    }

    @BindingAdapter(value = { "value" }, requireAll = false)
    public static void setValue(InterSpinner view, @Nullable Integer result) {
        // Log.d("TAGTAG", "setValue: " + result);
        if (!Objects.equals(result, view.selected)) view.selected = result;

        if (view.getAdapter() != null && result != null) {
            for (int i = 0; i < view.getAdapter().getCount(); i++) {
                if (view.getAdapter().getItem(i) instanceof ISpinner) {
                    ISpinner item = (ISpinner) view.getAdapter().getItem(i);
                    if (Objects.equals(item.getId(), view.selected)){
                        view.setText(item.getName());
                        view.getFilter().filter(null);
                        view.setSelection(view.getText().length());
                        break;
                    }
                }
            }
        }
    }

    @InverseBindingAdapter(attribute = "value", event = "valueAttrChanged")
    public static Integer getValue(InterSpinner view) {
        // Log.d("TAGTAG", "getValue: " + view.selected);
        return view.selected != null ? view.selected : null;
    }
}
