package com.geostat.census_2024.architecture.inter.handler;

import com.geostat.census_2024.architecture.inter.handler.IndexAdapterItemClickHandlers;

public interface IndexAdapterItemClickHandlersWithMap extends IndexAdapterItemClickHandlers {

    void btnClickListener(int id, int index, String houseCode);
    void mapClickListener(String houseCode);
    void removeClickListener(int id, int index);

    @Override
    default void btnClickListener(int id, int index) {}
}
