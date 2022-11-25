package com.geostat.census_2024.data.local.dai;

import androidx.room.Dao;
import androidx.room.Query;

import com.geostat.census_2024.data.local.entities.BuildingType;

import java.util.List;

@Dao
public interface BuildingTypeDao {

    @Query("SELECT * FROM cl_building_types")
    List<BuildingType> getAllBuildingTypes();
}
