package com.geostat.census_2024.handlers;

public interface IndexAdapterItemClickHandlersWithMap extends IndexAdapterItemClickHandlers {

    void btnClickListener(int id, int index, String houseCode);
    void mapClickListener(String houseCode);
    void removeClickListener(int id, int index);

    @Override
    default void btnClickListener(int id, int index) {}
}
