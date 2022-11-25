package com.geostat.census_2024.inter;

import androidx.annotation.Nullable;

import com.androidbuts.multispinnerfilter.KeyPairBoolData;

import java.util.List;

public interface IfSpinnerClickHandler {
    void spinnerClick(KeyPairBoolData selectedItem, @Nullable List<? extends String> viewList, @Nullable Integer nextIndex, @Nullable String clear);
    void spinnerNesItemClick(KeyPairBoolData selectedItem, List<? extends String> viewList, @Nullable Integer nextIndex, @Nullable String clear);
    void Erase(List<? extends String> viewList);
}
