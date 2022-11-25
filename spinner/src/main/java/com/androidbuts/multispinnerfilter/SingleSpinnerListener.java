package com.androidbuts.multispinnerfilter;

import androidx.annotation.Nullable;

public interface SingleSpinnerListener {
    void onItemsSelected(KeyPairBoolData selectedItem, @Nullable String clear);
    void onClear();
}