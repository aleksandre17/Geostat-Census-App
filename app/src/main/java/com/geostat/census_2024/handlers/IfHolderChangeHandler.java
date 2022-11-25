package com.geostat.census_2024.handlers;

import com.geostat.census_2024.data.model.HouseHoldModel;

public interface IfHolderChangeHandler {
    void ifHolderChange(Integer index, HouseHoldModel holder);
}
