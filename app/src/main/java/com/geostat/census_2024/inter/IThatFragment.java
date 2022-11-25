package com.geostat.census_2024.inter;

import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;

public interface IThatFragment<T extends Fragment, A extends ViewDataBinding> {

    T getFragment();

    A getBinding();
}
