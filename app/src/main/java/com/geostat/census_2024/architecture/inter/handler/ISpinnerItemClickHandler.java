package com.geostat.census_2024.architecture.inter.handler;

import android.view.View;
import android.widget.AdapterView;

import androidx.annotation.Nullable;

import java.util.List;

public interface ISpinnerItemClickHandler {

    void ifSpinnerItemClick(AdapterView<?> adapterView, View nView, int i, long l, String nexIndex, @Nullable List<? extends View> viewList, Object item);

    void ifSpinnerNesItemClick(AdapterView<?> adapterView, View nView, int i, long l, String nextIndex, @Nullable List<? extends View> viewList, Object item);
}
